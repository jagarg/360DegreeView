package com.accolite.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.accolite.dao.UserDAO;
import com.accolite.datamodel.Column;
import com.accolite.datamodel.DetailedEdgeInfo;
import com.accolite.datamodel.Edge;
import com.accolite.datamodel.Table;
import com.accolite.datamodel.TableMapping;

public class UserService {

	public static ArrayList<String> listTable(String database) {
		return UserDAO.listTable(database);
	}

	public static Table getTable(String database, String table) {
		ArrayList<ArrayList<Object>> data = UserDAO.getTable(database, table);

		Table t = new Table();
		t.setTableName(table);

		List<Column> columns = new ArrayList<>();

		// fetch column Names
		for (Object str : data.get(0)) {
			Column c = new Column();
			c.setColumnName((String) str);

			columns.add(c);
		}

		// fetch Primary Keys
		int i = -1;
		for (Object bool : data.get(1))
			columns.get(++i).setPrimaryKey((Boolean) bool);

		// fetch foreign Keys
		i = -1;
		for (Object bool : data.get(2))
			columns.get(++i).setForeignKey((Boolean) bool);

		// fetch foreign Table
		i = -1;
		for (Object str : data.get(3))
			columns.get(++i).setForeignTable((String) str);

		// fetch foreign Column
		i = -1;
		for (Object str : data.get(4))
			columns.get(++i).setForeignColumn((String) str);

		t.setColumns(columns);

		return t;
	}

	public static TableMapping getTableMapping(String database, String tables) {
		TableMapping tableMapping = new TableMapping();
		String[] tableList = tables.split(",");

		if (tableList.length == 1) {
			ArrayList<String> data = UserDAO.getMappings(database, tableList[0]);

			// fetch path info
			for (String path : data)
				if (path != null && !path.contains("NULL")) {
					Edge newPath = new Edge();
					String[] pathArr = path.toString().split(" ");
					if (!pathArr[0].equalsIgnoreCase("NULL"))
						newPath.setOut(pathArr[0]);
					if (!pathArr[1].equalsIgnoreCase("NULL"))
						newPath.setSourceColumn(pathArr[1]);
					if (!pathArr[2].equalsIgnoreCase("NULL"))
						newPath.setIn(pathArr[2]);
					if (!pathArr[3].equalsIgnoreCase("NULL"))
						newPath.setTargetColumn(pathArr[3]);
					newPath.setRid("#"+data.indexOf(path));
					tableMapping.getEdges().add(newPath);
				}
		} else {
			for (int i = 0; i < tableList.length - 1; i++) {
				for (int j = i + 1; j < tableList.length; j++) {

					ArrayList<String> data = UserDAO.getMappings(
							database, tableList[i], tableList[j]);

					// fetch path info
					DetailedEdgeInfo detailEdgeInfo = new DetailedEdgeInfo(tableList[i], tableList[j]);
					for (String path : data) {
						if (path != null && !path.contains("NULL")) {
							Edge newPath = new Edge();
							String[] pathArr = path.toString().split(" ");
							if (!pathArr[0].equalsIgnoreCase("NULL"))
								newPath.setOut(pathArr[0]);
							if (!pathArr[1].equalsIgnoreCase("NULL"))
								newPath.setSourceColumn(pathArr[1]);
							if (!pathArr[2].equalsIgnoreCase("NULL"))
								newPath.setIn(pathArr[2]);
							if (!pathArr[3].equalsIgnoreCase("NULL"))
								newPath.setTargetColumn(pathArr[3]);
							newPath.setRid("#"+data.indexOf(path));
							if(!pathArr[0].equalsIgnoreCase("NULL") && !pathArr[2].equalsIgnoreCase("NULL"))
								detailEdgeInfo.getEdges().add(newPath);
							tableMapping.getEdges().add(newPath);
						}
					}
					tableMapping.getDetailedEdgeInfoList().add(detailEdgeInfo);
				}
				
			}
		}
		// fetch table info
		for (String table : tableMapping.getTableNames())
			tableMapping.getVertices().add(getTable(database, table));
		return tableMapping;
	}

	public static TableMapping getTableAllPaths(String database, String tables) {
		TableMapping tableMapping = new TableMapping();
		if (tables == null || tables.isEmpty())
			return tableMapping;

		ArrayList<String> data = UserDAO.getAllPaths(database, tables);
		// fetch path info
		Set<Edge> paths = new HashSet<Edge>();
		for (String path1 : data) {
			String path = path1.toString();
			if (path != null && !path.contains("NULL")) {
				Edge newPath = new Edge();
				String[] pathArr = path.toString().split(" ");
				if (!pathArr[0].equalsIgnoreCase("NULL"))
					newPath.setOut(pathArr[0]);
				if (!pathArr[1].equalsIgnoreCase("NULL"))
					newPath.setSourceColumn(pathArr[1]);
				if (!pathArr[2].equalsIgnoreCase("NULL"))
					newPath.setIn(pathArr[2]);
				if (!pathArr[3].equalsIgnoreCase("NULL"))
					newPath.setTargetColumn(pathArr[3]);
				newPath.setRid("#"+data.indexOf(path1));
				paths.add(newPath);
			}
		}
		tableMapping.setEdges(paths);

		// fetch table info
		for (String table : tableMapping.getTableNames())
			tableMapping.getVertices().add(getTable(database, table));
		return tableMapping;
	}

}
