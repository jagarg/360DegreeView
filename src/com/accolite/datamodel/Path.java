package com.accolite.datamodel;

public class Path {
	String sourceTable;
	String sourceColumn;
	String targetTable;
	String targtColumn;
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
	public String getTargtColumn() {
		return targtColumn;
	}
	public void setTargtColumn(String targtColumn) {
		this.targtColumn = targtColumn;
	}
	
	@Override
	public boolean equals(Object o){
		if (o instanceof Path){
			Path obj=(Path)o;
			if(obj.sourceTable.equals(sourceTable) &&  
					obj.targetTable.equals(targetTable) &&
					obj.sourceColumn.equals(sourceColumn) &&
					obj.targtColumn.equals(targtColumn))
					return true;
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return (sourceTable.hashCode()*targetTable.hashCode());
	}
}