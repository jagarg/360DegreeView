package com.accolite.datamodel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class TableMapping {

	private List<Table> tables = new ArrayList<Table>();
	private Set<String> mappings = new HashSet<String>();

	public List<Table> getTables() {
		return tables;
	}

	public void setTables(List<Table> tableName) {
		this.tables = tableName;
	}

	public Set<String> getMappings() {
		return mappings;
	}

	public void setMappings(Set<String> mappings) {
		this.mappings = mappings;
	}

	@JsonIgnore
	public List<String> getTableNames() {
		Set<String> tableNames = new HashSet<String>();
		for (String mapping : mappings) {
			String[] mappingValues = mapping.split(" ");
			if (!mappingValues[0].equalsIgnoreCase("NULL"))
				tableNames.add(mappingValues[0]);
			if (!mappingValues[2].equalsIgnoreCase("NULL"))
				tableNames.add(mappingValues[2]);
		}
		return new ArrayList<String>(tableNames);
	}

}
