package com.accolite.dao;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.accolite.controller.ERDController;
import com.accolite.datamodel.Configuration;
import com.accolite.orient.OrientLoader;
import com.accolite.rdms.RDMSUtility;
import com.google.gson.Gson;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

public class ConfigurationDAO {

	final static Logger logger = Logger.getLogger(ConfigurationDAO.class);
	
	@SuppressWarnings("finally")
	public static boolean addConfiguration(Configuration configuration)
	{
		OrientGraphFactory factory = OrientLoader.factory(ERDController.ADMIN_DATABSE,null,null);
		OrientGraph graph = factory.getTx();
		Gson gson = new Gson();
		
		String query = "CREATE VERTEX CONFIGURATION CONTENT "+gson.toJson(configuration);
		
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
			factory.close();
			return result;
		}
	}
	
	@SuppressWarnings("finally")
	public static ArrayList<Vertex> listConfiguration()
	{
		OrientGraphFactory factory = OrientLoader.factory(ERDController.ADMIN_DATABSE,null,null);
		OrientGraph graph = factory.getTx();

		String query = "SELECT configName,schemaName FROM CONFIGURATION";

		ArrayList<Vertex> list = null;

		try
		{			
			OCommandSQL tableQR = new OCommandSQL(query);
			logger.debug("\nExecute : "+tableQR.getText());
						
			Iterable<Vertex> vertices = graph.command(tableQR).execute();
			logger.debug("\n Return : "+vertices.toString());
			list = new ArrayList<>();
			
			for (Vertex vertex : vertices) {
				list.add(vertex);
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
	public static ArrayList<String> listDB()
	{
		OrientGraphFactory factory = OrientLoader.factory(ERDController.ADMIN_DATABSE,null,null);
		OrientGraph graph = factory.getTx();

		String query = "SELECT configName,databaseName FROM CONFIGURATION";

		ArrayList<String> list = null;

		try
		{			
			OCommandSQL tableQR = new OCommandSQL(query);
			logger.debug("\nExecute : "+tableQR.getText());
						
			Iterable<Vertex> database = graph.command(tableQR).execute();
			logger.debug("\n Return : "+database.toString());
			list = new ArrayList<>();
			
			for (Vertex vertex : database) {
				list.add(vertex.getProperty("configName")+RDMSUtility.FIELD_SEPERATOR+vertex.getProperty("databaseName"));
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