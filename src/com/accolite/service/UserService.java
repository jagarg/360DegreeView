package com.accolite.service;

import java.util.ArrayList;
import java.util.List;

import com.accolite.dao.UserDAO;
import com.accolite.datamodel.Column;
import com.accolite.datamodel.Path;
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
			ArrayList<String> data = UserDAO.getMappings(database,
					tableList[0]);

			// fetch path info
			for(String path : data)
				if (path != null) {
					Path newPath = new Path();
					String[] pathArr = path.toString().split(" ");
					if(!pathArr[0].equalsIgnoreCase("NULL"))							
						newPath.setSourceTable(pathArr[0]);
					if(!pathArr[1].equalsIgnoreCase("NULL"))							
						newPath.setSourceColumn(pathArr[1]);
					if(!pathArr[2].equalsIgnoreCase("NULL"))							
						newPath.setTargetTable(pathArr[2]);
					if(!pathArr[3].equalsIgnoreCase("NULL"))							
						newPath.setTargtColumn(pathArr[3]);
					tableMapping.getPaths().add(newPath);
				}
		} else {
			for (int i = 0; i < tableList.length - 1; i++) {
				for (int j = i + 1; j < tableList.length; j++) {

					ArrayList<ArrayList<Object>> data = UserDAO.getMappings(
							database, tableList[i], tableList[j]);

					// fetch path info
					for (Object path : data.get(0)) {
						if (path != null) {
							Path newPath = new Path();
							String[] pathArr = path.toString().split(" ");
							if(!pathArr[0].equalsIgnoreCase("NULL"))							
								newPath.setSourceTable(pathArr[0]);
							if(!pathArr[1].equalsIgnoreCase("NULL"))							
								newPath.setSourceColumn(pathArr[1]);
							if(!pathArr[2].equalsIgnoreCase("NULL"))							
								newPath.setTargetTable(pathArr[2]);
							if(!pathArr[3].equalsIgnoreCase("NULL"))							
								newPath.setTargtColumn(pathArr[3]);
							tableMapping.getPaths().add(newPath);
						}
					}
				}
			}
		}
		// fetch table info
		for (String table : tableMapping.getTableNames())
			tableMapping.getTables().add(getTable(database, table));
		return tableMapping;
	}

}
