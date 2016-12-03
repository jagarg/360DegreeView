package com.accolite.datamodel;

public class Column {

	private String columnName;
	private boolean isPrimaryKey;
	private boolean isForeignKey;
	private String foreignTable;
	private String foreignColumn;
	
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public boolean isPrimaryKey() {
		return isPrimaryKey;
	}
	public void setPrimaryKey(boolean isPrimaryKey) {
		this.isPrimaryKey = isPrimaryKey;
	}
	public boolean isForeignKey() {
		return isForeignKey;
	}
	public void setForeignKey(boolean isForeignKey) {
		this.isForeignKey = isForeignKey;
	}
	public String getForeignTable() {
		return foreignTable;
	}
	public void setForeignTable(String foreignTable) {
		this.foreignTable = foreignTable;
	}
	public String getForeignColumn() {
		return foreignColumn;
	}
	public void setForeignColumn(String foreignColumn) {
		this.foreignColumn = foreignColumn;
	}
}
