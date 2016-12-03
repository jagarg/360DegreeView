package com.accolite.service;

import java.util.ArrayList;
import java.util.List;

import com.accolite.dao.UserDAO;
import com.accolite.datamodel.Column;
import com.accolite.datamodel.Table;

public class UserService {

	public static ArrayList<String> listTable(String database) {
		return UserDAO.listTable(database);
	}
	
	public static Table getTable(String database,String table) {
		 ArrayList<ArrayList<Object>> data = UserDAO.getTable(database, table);
		 
		 Table t = new Table();
		 t.setTableName(table);
		 
		 List<Column> columns = new ArrayList<>();

		 // fetch column Names
		 for (Object str : data.get(0)) {
			 Column c = new Column();
			 c.setColumnName((String)str);
			 
			 columns.add(c);
		}
		 
		 // fetch Primary Keys
		 int i=-1;
		 for (Object bool : data.get(1))
			 columns.get(++i).setPrimaryKey((Boolean)bool);
		 
		 // fetch foreign Keys
		 i=-1;
		 for (Object bool : data.get(2))
			 columns.get(++i).setForeignKey((Boolean)bool);		 
		 
		 // fetch foreign Table
		 i=-1;
		 for (Object str : data.get(3))
			 columns.get(++i).setForeignTable((String)str);
		 
		 // fetch foreign Column
		 i=-1;
		 for (Object str : data.get(4))
			 columns.get(++i).setForeignColumn((String)str);
		 
		 t.setColumns(columns);
		 
		 return t;
	}	
}
