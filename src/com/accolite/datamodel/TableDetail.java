package com.accolite.datamodel;

import java.io.Serializable;
import java.util.HashMap;

public class TableDetail implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String tableName;
	String packageName;
	String className;
	String superClassName;
	HashMap<String,ColumnDetail> columns = new HashMap<String,ColumnDetail>();
	String sequenceName;
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getSuperClassName() {
		return superClassName;
	}
	public void setSuperClassName(String superClassName) {
		this.superClassName = superClassName;
	}
	public HashMap<String,ColumnDetail> getColumns() {
		return columns;
	}
	public void setColumns(HashMap<String,ColumnDetail> columns) {
		this.columns = columns;
	}
	public String getSequenceName() {
		return sequenceName;
	}
	public void setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}
	
}
