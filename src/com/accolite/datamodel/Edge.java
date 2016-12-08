package com.accolite.datamodel;

public class Edge {
	String out;
	String sourceColumn;
	String in;
	String targetColumn;
	
	public Edge(){
		out="";
		sourceColumn="";
		in="";
		targetColumn="";
	}
	public String getOut() {
		return out;
	}
	public void setOut(String out) {
		this.out = out;
	}
	public String getSourceColumn() {
		return sourceColumn;
	}
	public void setSourceColumn(String sourceColumn) {
		this.sourceColumn = sourceColumn;
	}
	public String getIn() {
		return in;
	}
	public void setIn(String in) {
		this.in = in;
	}
	public String getTargetColumn() {
		return targetColumn;
	}
	public void setTargetColumn(String targtColumn) {
		this.targetColumn = targtColumn;
	}
	
	@Override
	public boolean equals(Object o){
		if (o instanceof Edge){
			Edge obj=(Edge)o;
			if(obj.out.equals(out) &&  
					obj.in.equals(in) &&
					obj.sourceColumn.equals(sourceColumn) &&
					obj.targetColumn.equals(targetColumn))
					return true;
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return (out.hashCode()*sourceColumn.hashCode()*in.hashCode()*targetColumn.hashCode());
	}
}