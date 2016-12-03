package com.accolite.datamodel;

import java.util.List;

public class DatabaseDetail {

	private String databaseName;
	
	private List<String> tables;

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public List<String> getTables() {
		return tables;
	}

	public void setTables(List<String> tables) {
		this.tables = tables;
	}
}
