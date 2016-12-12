package com.accolite.datamodel;

import java.util.List;

public class Table {
	private String type;
	private String rid;
	private String tableName;
	private List<Column> columns;
	public Table(){
		type = "Tables";
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
		this.rid = "#"+tableName;
	}	

	public List<Column> getColumns() {
		return columns;
	}
	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getRid() {
		return rid;
	}
	public void setRid(String rid) {
		this.rid = rid;
	}

}
