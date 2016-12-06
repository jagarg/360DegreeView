package com.accolite.datamodel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class TableMapping {

	private List<Table> tables = new ArrayList<Table>();
	private Set<Path> paths = new HashSet<Path>();

	public List<Table> getTables() {
		return tables;
	}

	public void setTables(List<Table> tableName) {
		this.tables = tableName;
	}

	public Set<Path> getPaths() {
		return paths;
	}

	public void setPaths(Set<Path> mappings) {
		this.paths = mappings;
	}

	@JsonIgnore
	public List<String> getTableNames() {
		Set<String> tableNames = new HashSet<String>();
		for (Path path : paths){ 
			if(path.getSourceTable()!=null)
				tableNames.add(path.getSourceTable());
			if(path.getTargetTable()!=null)
				tableNames.add(path.getTargetTable());
		}
		return new ArrayList<String>(tableNames);
	}

}
