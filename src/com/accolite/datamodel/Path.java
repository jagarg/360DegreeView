package com.accolite.datamodel;

public class Path {
	String sourceTable;
	String sourceColumn;
	String targetTable;
	String targetColumn;
	
	public Path(){
		sourceTable="";
		sourceColumn="";
		targetTable="";
		targetColumn="";
	}
	public String getSourceTable() {
		return sourceTable;
	}
	public void setSourceTable(String sourceTable) {
		this.sourceTable = sourceTable;
	}
	public String getSourceColumn() {
		return sourceColumn;
	}
	public void setSourceColumn(String sourceColumn) {
		this.sourceColumn = sourceColumn;
	}
	public String getTargetTable() {
		return targetTable;
	}
	public void setTargetTable(String targetTable) {
		this.targetTable = targetTable;
	}
	public String getTargetColumn() {
		return targetColumn;
	}
	public void setTargetColumn(String targtColumn) {
		this.targetColumn = targtColumn;
	}
	
	@Override
	public boolean equals(Object o){
		if (o instanceof Path){
			Path obj=(Path)o;
			if(obj.sourceTable.equals(sourceTable) &&  
					obj.targetTable.equals(targetTable) &&
					obj.sourceColumn.equals(sourceColumn) &&
					obj.targetColumn.equals(targetColumn))
					return true;
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return (sourceTable.hashCode()*sourceColumn.hashCode()*targetTable.hashCode()*targetColumn.hashCode());
	}
}