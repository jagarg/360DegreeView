package com.accolite.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class TableMapping {

	private List<Table> vertices = new ArrayList<Table>();
	private List<DetailedEdgeInfo> detailedEdgeInfoList = new ArrayList<DetailedEdgeInfo>();
	
	private Map<String,Integer> edgeRid = new HashMap<String,Integer>();
	private Set<String> tables = new HashSet<String>();

	public List<Table> getVertices() {
		return vertices;
	}

	public void setVertices(List<Table> vertices) {
		this.vertices = vertices;
	}
	
	@JsonIgnore
	public Set<String> getTables() {
		return tables;
	}

	public void setTables(Set<String> tables) {
		this.tables = tables;
	}

	public List<DetailedEdgeInfo> getDetailedEdgeInfoList() {
		return detailedEdgeInfoList;
	}

	public void setDetailedEdgeInfoList(List<DetailedEdgeInfo> detailedEdgeInfoList) {
		this.detailedEdgeInfoList = detailedEdgeInfoList;
	}
	
	@JsonIgnore
	public Map<String, Integer> getEdgeRid() {
		return edgeRid;
	}

	public void setEdgeRid(Map<String, Integer> edgeRid) {
		this.edgeRid = edgeRid;
	}
	
	@JsonIgnore
	public int generateUniqueId(){
		return ThreadLocalRandom.current().nextInt(0, 10000);
		
	}

}
