package com.accolite.dao;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.accolite.orient.OrientLoader;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

public class UserDAO {

	final static Logger logger = Logger.getLogger(UserDAO.class);
	
	@SuppressWarnings("finally")
	public static ArrayList<String> listTable(String database)
	{
		String dbName = "plocal:D:\\orientdb-community-2.2.13\\databases\\"+database;
		OrientGraphFactory factory = OrientLoader.factory(dbName);
		OrientGraph graph = factory.getTx();

		String query = "SELECT tableName FROM TABLE";

		ArrayList<String> list = null;

		try
		{			
			OCommandSQL tableQR = new OCommandSQL(query);
			logger.debug("\nExecute : "+tableQR.getText());
						
			Iterable<Vertex> dbList = graph.command(tableQR).execute();
			logger.debug("\n Return : "+database.toString());
			list = new ArrayList<>();
			
			for (Vertex vertex : dbList) {
				list.add(vertex.getProperty("tableName"));
			}
	        
			graph.commit();
		}catch (Exception e) {
			logger.error(e.getMessage()+"\t"+e.getCause());
			list=null;
		}finally {
			graph.shutdown();
			factory.close();
			return list;
		}
	}	
	
	@SuppressWarnings("finally")
	public static ArrayList<ArrayList<Object>> getTable(String database,String table)
	{
		String dbName = "plocal:D:\\orientdb-community-2.2.13\\databases\\"+database;
		OrientGraphFactory factory = OrientLoader.factory(dbName);
		OrientGraph graph = factory.getTx();

		String query = "SELECT"
				+ " out().columnName as columns,"
				+ " out().primaryKey as primaryKey,"
				+ " out().foreignKey as foreignKey,"
				+ " out().foreignKeyTable as foreignTable,"
				+ " out().foreignKeyColumn as foreignColumn"
				+ " FROM TABLE "
				+ " where tableName = '"+table+"'";

		ArrayList<ArrayList<Object> >list = null;

		try
		{			
			OCommandSQL tableQR = new OCommandSQL(query);
			logger.debug("\nExecute : "+tableQR.getText());
						
			Iterable<Vertex> dbList = graph.command(tableQR).execute();
			logger.debug("\n Return : "+database.toString());
			list = new ArrayList<>();
			
			for (Vertex vertex : dbList) {
				list.add(vertex.getProperty("columns"));
				list.add(vertex.getProperty("primaryKey"));
				list.add(vertex.getProperty("foreignKey"));
				list.add(vertex.getProperty("foreignTable"));
				list.add(vertex.getProperty("foreignColumn"));
			}
	        
			graph.commit();
		}catch (Exception e) {
			logger.error(e.getMessage()+"\t"+e.getCause());
			list=null;
		}finally {
			graph.shutdown();
			factory.close();
			return list;
		}
	}		
}
