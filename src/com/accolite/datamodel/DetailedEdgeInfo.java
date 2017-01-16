package com.accolite.datamodel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DetailedEdgeInfo {

	String sourceTable;
	String targetTable;
	ArrayList<Edge> edges = new ArrayList<Edge>();
	private Set<String> intermediaryTables = new HashSet<String>();

	public DetailedEdgeInfo() {
		sourceTable = "";
		targetTable = "";
	}

	public DetailedEdgeInfo(String sourceTable, String targetTable) {
		this.sourceTable = sourceTable;
		this.targetTable = targetTable;
	}

	public String getSourceTable() {
		return sourceTable;
	}

	public void setSourceTable(String sourceTable) {
		this.sourceTable = sourceTable;
	}

	public String getTargetTable() {
		return targetTable;
	}

	public void setTargetTable(String targetTable) {
		this.targetTable = targetTable;
	}

	public ArrayList<Edge> getEdges() {
		return edges;
	}

	public void setEdges(ArrayList<Edge> edges) {
		this.edges = edges;
	}
	
	public Set<String> getIntermediaryTables() {
		for(Edge edge : edges){
			intermediaryTables.add(edge.getOut());
			intermediaryTables.add(edge.getIn());
		}
		intermediaryTables.remove(sourceTable);
		intermediaryTables.remove(targetTable);
		return intermediaryTables;
	}

	public void setIntermediaryTables(Set<String> tables) {
		this.intermediaryTables = tables;
	}
}