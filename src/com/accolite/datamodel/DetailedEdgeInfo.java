package com.accolite.datamodel;

import java.util.ArrayList;

public class DetailedEdgeInfo {

	String sourceTable;
	String targetTable;
	ArrayList<Edge> edges = new ArrayList<Edge>();

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

}