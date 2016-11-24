package com.accolite.datamodel;

import java.io.Serializable;

public class ColumnDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String columnName;
	boolean primaryKey;
	boolean foreignKey;
	String localFieldName;
	String foreignFieldName;
	String foreignKeyClass;
	String foreignKeyTable;
	String foreignKeyColumn;
	
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	
	public boolean isPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}
	public boolean isForeignKey() {
		return foreignKey;
	}
	public void setForeignKey(boolean foreignKey) {
		this.foreignKey = foreignKey;
	}
	public String getLocalFieldName() {
		return localFieldName;
	}
	public void setLocalFieldName(String localFieldName) {
		this.localFieldName = localFieldName;
	}
	public String getForeignKeyClass() {
		return foreignKeyClass;
	}
	public void setForeignKeyClass(String foreignKeyClass) {
		this.foreignKeyClass = foreignKeyClass;
	}
	public String getForeignFieldName() {
		return foreignFieldName;
	}
	public void setForeignFieldName(String foreignFieldName) {
		this.foreignFieldName = foreignFieldName;
	}
	public String getForeignKeyTable() {
		return foreignKeyTable;
	}
	public void setForeignKeyTable(String foreignKeyTable) {
		this.foreignKeyTable = foreignKeyTable;
	}
	public String getForeignKeyColumn() {
		return foreignKeyColumn;
	}
	public void setForeignKeyColumn(String foreignKeyColumn) {
		this.foreignKeyColumn = foreignKeyColumn;
	}
	
}
