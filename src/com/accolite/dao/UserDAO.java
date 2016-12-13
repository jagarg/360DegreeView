package com.accolite.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.accolite.controller.ERDController;
import com.accolite.orient.AllPaths;
import com.accolite.orient.OrientLoader;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;

public class UserDAO {

	final static Logger logger = Logger.getLogger(UserDAO.class);

	@SuppressWarnings("finally")
	public static ArrayList<String> listTable(String database) {
		String dbName = ERDController.DBPATH+ database;
		OrientGraphFactory factory = OrientLoader.factory(dbName,null,null);
		OrientGraph graph = factory.getTx();

		String query = "SELECT tableName FROM TABLE";

		ArrayList<String> list = null;

		try {
			OCommandSQL tableQR = new OCommandSQL(query);
			logger.debug("\nExecute : " + tableQR.getText());

			Iterable<Vertex> dbList = graph.command(tableQR).execute();
			logger.debug("\n Return : " + database.toString());
			list = new ArrayList<>();

			for (Vertex vertex : dbList) {
				list.add(vertex.getProperty("tableName"));
			}

			graph.commit();
		} catch (Exception e) {
			logger.error(e.getMessage() + "\t" + e.getCause());
			list = null;
		} finally {
			graph.shutdown();
			factory.close();
			return list;
		}
	}

	@SuppressWarnings("finally")
	public static ArrayList<ArrayList<Object>> getTable(String database,
			String table) {
		String dbName = ERDController.DBPATH+ database;
		OrientGraphFactory factory = OrientLoader.factory(dbName,null,null);
		OrientGraph graph = factory.getTx();

		String query = "SELECT" + " out().columnName as columns,"
				+ " out().primaryKey as primaryKey,"
				+ " out().foreignKey as foreignKey,"
				+ " out().foreignKeyTable as foreignTable,"
				+ " out().foreignKeyColumn as foreignColumn" + " FROM TABLE "
				+ " where tableName = '" + table + "'";

		ArrayList<ArrayList<Object>> list = null;

		try {
			OCommandSQL tableQR = new OCommandSQL(query);
			logger.debug("\nExecute : " + tableQR.getText());

			Iterable<Vertex> dbList = graph.command(tableQR).execute();
			logger.debug("\n Return : " + database.toString());
			list = new ArrayList<>();

			for (Vertex vertex : dbList) {
				list.add(vertex.getProperty("columns"));
				list.add(vertex.getProperty("primaryKey"));
				list.add(vertex.getProperty("foreignKey"));
				list.add(vertex.getProperty("foreignTable"));
				list.add(vertex.getProperty("foreignColumn"));
			}

			graph.commit();
		} catch (Exception e) {
			logger.error(e.getMessage() + "\t" + e.getCause());
			list = null;
		} finally {
			graph.shutdown();
			factory.close();
			return list;
		}
	}

	@SuppressWarnings("finally")
	public static ArrayList<String> getMappings(String database,
			String tableName) {
		String dbName = ERDController.DBPATH+ database;
		OrientGraphFactory factory = OrientLoader.factory(dbName,null,null);
		OrientGraph graph = factory.getTx();
		String query = "select details as path from COLUMN "
				+ "where foreignKey = true and " + "(localTableName='"
				+ tableName + "' OR " + "foreignKeyTable = '" + tableName
				+ "')";

		ArrayList<String> list = null;

		try {
			OCommandSQL tableQR = new OCommandSQL(query);
			logger.debug("\nExecute : " + tableQR.getText());

			Iterable<Vertex> dbList = graph.command(tableQR).execute();
			logger.debug("\n Return : " + database.toString());
			list = new ArrayList<>();

			for (Vertex vertex : dbList) {
				list.add(vertex.getProperty("path"));
			}

			graph.commit();
		} catch (Exception e) {
			logger.error(e.getMessage() + "\t" + e.getCause());
			list = null;
		} finally {
			graph.shutdown();
			factory.close();
			return list;
		}
	}

	@SuppressWarnings("finally")
	public static ArrayList<String> getMappings(String database,
			String table1, String table2) {
		String dbName = ERDController.DBPATH+ database;
		OrientGraphFactory factory = OrientLoader.factory(dbName,null,null);
		OrientGraph graph = factory.getTx();
		String query = "select path.details from " + "(select shortestPath ( "
				+ "(select from TABLE where tableName = '" + table1 + "'), "
				+ "(select from TABLE where tableName = '" + table2 + "')) "
				+ "as path)";

		ArrayList<String> list = new ArrayList<>();

		try {
			OCommandSQL tableQR = new OCommandSQL(query);
			logger.debug("\nExecute : " + tableQR.getText());

			Iterable<Vertex> dbList = graph.command(tableQR).execute();
			logger.debug("\n Return : " + database.toString());
			
			for (Vertex vertex : dbList) {
				if (vertex.getProperty("path") != null){
					list = vertex.getProperty("path");
				}
			}

			graph.commit();
		} catch (Exception e) {
			logger.error(e.getMessage() + "\t" + e.getCause());
			list = null;
		} finally {
			graph.shutdown();
			factory.close();
			list.removeAll(Collections.singleton(null));
			return list;
		}
	}

	@SuppressWarnings("finally")
	public static ArrayList<String> getAllPaths(String database, String tables) {
		String dbName = ERDController.DBPATH+ database;
		OrientGraphFactory factory = OrientLoader.factory(dbName,null,null);
		OrientGraph graph = factory.getTx();

		List<String> ridList = getTableRid(database, tables);
		ArrayList<String> list = new ArrayList<String>();
		try {
			for (int i = 0; i < ridList.size() - 1; i++) {
				for (int j = i + 1; j < ridList.size(); j++) {

					List<List<Vertex>> paths1 = new AllPaths(graph).getPaths(
							ridList.get(i), ridList.get(j));
					List<List<Vertex>> paths = new AllPaths(graph).getPaths(
							ridList.get(j), ridList.get(i));
					paths.addAll(paths1);

					for (Iterable<Vertex> dbList : paths) {
						for (Vertex vertex : dbList) {
							if (vertex.getProperty("details") != null)
								list.add(vertex.getProperty("details")
										.toString());
						}
					}

				}
			}
			graph.commit();
		} catch (Exception e) {
			logger.error(e.getMessage() + "\t" + e.getCause());
			list = null;
		} finally {
			graph.shutdown();
			factory.close();
			return list;
		}
	}

	@SuppressWarnings("finally")
	public static ArrayList<String> getTableRid(String database, String tables) {
		String dbName = ERDController.DBPATH+ database;
		OrientGraphFactory factory = OrientLoader.factory(dbName,null,null);
		OrientGraph graph = factory.getTx();
		String query = "select @rid from table where tableName in [";

		String[] tableArr = tables.split(",");
		for (String tableName : tableArr)
			query = query + "'" + tableName + "',";

		int i = query.lastIndexOf(",");
		query = query.substring(0, i) + "]";

		ArrayList<String> list = new ArrayList<String>();

		try {
			OCommandSQL tableQR = new OCommandSQL(query);
			logger.debug("\nExecute : " + tableQR.getText());

			Iterable<Vertex> dbList = graph.command(tableQR).execute();
			logger.debug("\n Return : " + database.toString());

			for (Vertex vertex : dbList) {
				String id = vertex.getProperty("rid").toString();
				list.add(id.substring(id.indexOf("#"), id.indexOf("]")));
			}

			graph.commit();
		} catch (Exception e) {
			logger.error(e.getMessage() + "\t" + e.getCause());
			list = null;
		} finally {
			graph.shutdown();
			factory.close();
			return list;
		}
	}
}
