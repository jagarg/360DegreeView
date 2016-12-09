package com.accolite.orient;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.accolite.controller.ERDController;
import com.accolite.datamodel.ColumnDetail;
import com.accolite.datamodel.Model;
import com.accolite.datamodel.TableDetail;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;


public class OrientLoader {

	private static String DATABASE =ERDController.DBPATH+"testDB";
	final static Logger logger = Logger.getLogger(OrientLoader.class);
	
	public static void initiateLoad(Model model) {
		
		if(null != model)
		{
			try 
			{
				OrientGraphFactory factory = factory(DATABASE,null,null);
				
				System.out.println("Data load initiated !!");
				
				System.out.println("Drop Existing classes ...");
				dropClass(factory);
				
				System.out.println("Create classes ...");
				createClass(factory);
				
				System.out.println("Populate data into Table & Column ....");
				addTableColumnVertex(factory, model);		
				
				System.out.println("Establish Edges between Table & Column ...");
				addEdge(factory, model);
				
				System.out.println("Data load Complete !!");				
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		else
		{
			logger.info("Model is NULL !!");
		}
	}
	
	public static OrientGraphFactory factory(String Database,String username,String password) {
		
		String defaultName = "admin";
		String defaultPassword = "admin";
		
		if(null != username && !username.isEmpty())
			defaultName = username;
		
		if(null != password && !password.isEmpty())
			defaultPassword = password;
		
		OrientGraphFactory factory = new OrientGraphFactory(Database,defaultName,defaultPassword).setupPool(1,10);
        
		return factory;
    }
	
	public static void nonTrnasactional(OrientGraphFactory factory,String query) {
    	OrientGraphNoTx gph = factory.getNoTx();
    	try
    	{			
	        OCommandSQL tableQR = new OCommandSQL(query);
	        logger.debug("\nExecute : "+tableQR.getText());
	        
			Object result = gph.command(tableQR).execute();
			logger.debug("\nReturn : "+result.toString());
			
			gph.commit();
    	}catch (Exception e) {
			logger.error(e.getMessage());
		}finally {
			gph.shutdown();
		}
    }
    
    @SuppressWarnings("finally")
	public static boolean trnasactional(OrientGraphFactory factory,String query) {
    	OrientGraph graph = factory.getTx();
    	boolean result = false;
    	try
    	{			
	        OCommandSQL tableQR = new OCommandSQL(query);
	        logger.debug("\nExecute : "+tableQR.getText());

	        Object tmp = graph.command(tableQR).execute();
	        logger.debug("\nReturn : "+tmp.toString());
			
			graph.commit();
			result=true;
    	}catch (Exception e) {
    		logger.error(e.getMessage()+"\t"+e.getCause());
    		result=true;
		}finally {
			graph.shutdown();
			return result;
		}
    }
    
	private static void dropClass(OrientGraphFactory factory) {

    	logger.info("======================================= ");
		logger.info("\n Dropping Class .. ");
				
		/**
		nonTrnasactional(factory, fw, "TRUNCATE CLASS COLUMN unsafe");
		nonTrnasactional(factory, fw, "TRUNCATE CLASS TABLE unsafe");
		nonTrnasactional(factory, fw, "TRUNCATE CLASS hasColumn unsafe");
		nonTrnasactional(factory, fw, "TRUNCATE CLASS isForeignKey unsafe");
		**/
		        
		nonTrnasactional(factory, "DROP CLASS TABLE IF EXISTS UNSAFE");
		       
		nonTrnasactional(factory, "DROP CLASS COLUMN IF EXISTS UNSAFE");
		      
		nonTrnasactional(factory, "DROP CLASS hasColumn IF EXISTS UNSAFE");
		        
		nonTrnasactional(factory, "DROP CLASS isForeignKey IF EXISTS UNSAFE");
		        
		logger.info("\n Dropping Class Completed !!! ");
		logger.info("\n=======================================\n\n");    	
    }
    
    private static void createClass(OrientGraphFactory factory) {

		logger.info("======================================= ");
		logger.info("\n Creating Classes .. ");
		
        nonTrnasactional(factory, "CREATE CLASS TABLE EXTENDS V");
        
        nonTrnasactional(factory, "CREATE CLASS COLUMN EXTENDS V");
        
        nonTrnasactional(factory, "CREATE CLASS hasColumn EXTENDS E");
        
        nonTrnasactional(factory, "CREATE CLASS isForeignKey EXTENDS E");
     		        
		logger.info("\n Creating Classes Completed !!! ");
		logger.info("\n=======================================\n\n");
    }
    
    private static void addTableColumnVertex(OrientGraphFactory factory,Model model) {

    	try {
    		
			logger.info("\n\n=============== Table & Column data load starts =======================\n\n");
			
			Gson gson = new Gson();
			String query = "";
						
			Iterator iter = model.getTableMap().entrySet().iterator();
			while(iter.hasNext())
			{
				Map.Entry pair = (Map.Entry)iter.next();
		        TableDetail td = (TableDetail)pair.getValue();
		        
				logger.debug("\n\n ==========<< TableName : "+td.getTableName()+" >>================= \n\n");
		        
		        // process Table
		        JsonObject objT = gson.toJsonTree(td).getAsJsonObject();
		        objT.remove("columns");
		        String tdJson  = gson.toJson(objT);
		        
		        query = "CREATE VERTEX TABLE CONTENT "+tdJson;
		        trnasactional(factory, query);
		        
		        HashMap<String, ColumnDetail> columns = td.getColumns();
		        
		        // process Columns
				Iterator iterCol = columns.entrySet().iterator();
				while(iterCol.hasNext())
				{
					Map.Entry col = (Map.Entry)iterCol.next();
			        ColumnDetail cd = (ColumnDetail) col.getValue();
				
			        if(null != cd.getColumnName())
			        {
			        	logger.debug("\n\n ==========<< ColumnName : "+cd.getColumnName()+" >>================= \n\n");
			        	JsonObject objC = gson.toJsonTree(cd).getAsJsonObject();
				        objC.addProperty("localTableName", objT.get("tableName").getAsString());
				        
				        JsonElement sourceTable = objT.get("tableName");
				        JsonElement sourceColumn = objC.get("columnName");
				        if(objC.get("foreignKey").getAsBoolean() && null != objC.get("foreignKeyTable") && null != objC.get("foreignKeyColumn"))
				        {
				        	JsonElement destTable = objC.get("foreignKeyTable");
				        	JsonElement destColumn = objC.get("foreignKeyColumn");
				        	objC.addProperty("details",sourceTable.getAsString()+" "+sourceColumn.getAsString()+" "+destTable.getAsString()+" "+destColumn.getAsString());
				        }
				        else
				        {
				        	objC.addProperty("details",sourceTable.getAsString()+" "+sourceColumn.getAsString()+" NULL NULL");
				        }

				        String tdJson1  = gson.toJson(objC);
				        
				        query = "CREATE VERTEX COLUMN CONTENT "+tdJson1;
				        trnasactional(factory, query);
				        
				     }
			        else
			        {
			        	logger.debug("\n\n ==========<< ColumnNamne is Null !! \t LocalFiled : "+cd.getLocalFieldName()+" >>================= \n\n");
			        }
				}
				
				logger.debug("\n===============Table Complete ========================\n\n");
			}
			
			logger.info("\n");
			logger.info("\n=============== Table & Column data load ends ========================\n\n");
			
		} catch (Exception e) {
			logger.error(e.getMessage()+"\t"+e.getCause());
		}
    }
    
    private static void addEdge(OrientGraphFactory factory, Model model) {

    	try {
    		
    		logger.info("\n\n===============Edges Creation Begins========================\n\n");
			Gson gson = new Gson();
			String query = "";

			
			Iterator iter = model.getTableMap().entrySet().iterator();
			while(iter.hasNext())
			{
				logger.debug("\n\n ======================== Create Edge hasColumn ================================ \n\n");

				Map.Entry pair = (Map.Entry)iter.next();
		        TableDetail td = (TableDetail)pair.getValue();
		        HashMap<String, ColumnDetail> columns = td.getColumns();
		        				
				// process Columns
				Iterator iterCol = columns.entrySet().iterator();
				while(iterCol.hasNext())
				{
					Map.Entry col = (Map.Entry)iterCol.next();
			        ColumnDetail cd = (ColumnDetail) col.getValue();
			
			        if(null == cd.getColumnName())
			        {
			        	logger.debug("\n\n Error !!!  ColumnName is NULL ---> TableName : "+td.getTableName()+" <<----- \n\n");
					}
			        else
			        {
			        	logger.debug("\n ===== << TableName : "+td.getTableName()+" >> -------hasColumn-------> << ColumnName : "+cd.getColumnName()+" >> ====== ");
			        	query = "CREATE EDGE hasColumn FROM (SELECT FROM TABLE WHERE tableName = '"+td.getTableName()+"') TO (SELECT FROM COLUMN WHERE columnName = '"+cd.getColumnName()+"' AND localTableName = '"+td.getTableName()+"') ";
			        	trnasactional(factory,query);
			        	
						// Handle Foreign Key
						if(cd.isForeignKey())
						{
							if(null == cd.getForeignKeyTable())
					        {
								logger.debug("\n\n Error !!!  ForeignKeyTable is NULL ---> TableName : "+td.getTableName()+" , ColumnName : "+cd.getColumnName()+"<<----- \n\n");
							}
							else if(null == cd.getForeignKeyColumn())
							{
								logger.debug("\n\n Error !!!  ForeignKeyColumn is NULL ---> TableName : "+td.getTableName()+" , ColumnName : "+cd.getColumnName()+"<<----- \n\n");
							}
					        else
					        {
								query = "CREATE EDGE isForeignKey FROM (SELECT FROM COLUMN WHERE columnName = '"+cd.getColumnName()+"' AND localTableName='"+td.getTableName()+"') TO (SELECT FROM TABLE WHERE tableName = '"+cd.getForeignKeyTable()+"') SET foreignKeyColumn = '"+cd.getForeignKeyColumn()+"'";
								trnasactional(factory, query);
								
								logger.debug("\n ===== << ColumnName : "+cd.getColumnName()+" >> -------isForeignKey-------> << TableName : "+td.getTableName()+" >> ====== ");

					        }
						}
					}
				}
				
				logger.debug("\n\n =========================== \n\n");
			}			
			logger.info("\n===============Table and Column Complete========================\n\n");
		
    	} catch (Exception e) {
			logger.error(e.getMessage());
		}    	
    }
    
}
