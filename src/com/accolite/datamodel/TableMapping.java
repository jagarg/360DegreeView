package com.accolite.datamodel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class TableMapping {

	private List<Table> vertices = new ArrayList<Table>();
	private Set<Edge> edges = new HashSet<Edge>();

	public List<Table> getVertices() {
		return vertices;
	}

	public void setVertices(List<Table> vertices) {
		this.vertices = vertices;
	}

	public Set<Edge> getEdges() {
		return edges;
	}

	public void setEdges(Set<Edge> edges) {
		this.edges = edges;
	}

	@JsonIgnore
	public List<String> getTableNames() {
		Set<String> tableNames = new HashSet<String>();
		for (Edge path : edges){ 
			if(path.getOut()!=null && !path.getOut().isEmpty())
				tableNames.add(path.getOut());
			if(path.getIn()!=null && !path.getIn().isEmpty())
				tableNames.add(path.getIn());
		}
		return new ArrayList<String>(tableNames);
	}

}
