package com.accolite.datamodel;

import java.util.List;

public class DataModelDetail {

	private String dataModelName;
	
	private List<String> tables;

	public String getDataModelName() {
		return dataModelName;
	}

	public void setDataModelName(String dataModelName) {
		this.dataModelName = dataModelName;
	}

	public List<String> getTables() {
		return tables;
	}

	public void setTables(List<String> tables) {
		this.tables = tables;
	}
}
