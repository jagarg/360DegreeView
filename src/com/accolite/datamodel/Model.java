package com.accolite.datamodel;

import java.io.Serializable;
import java.util.HashMap;

public class Model implements Serializable {
	
	private static final long serialVersionUID = 1L;

	//To hold table information
	HashMap<String,TableDetail> tableMap = new HashMap<String,TableDetail>();
	
	//to store mapping of class/interface and the implementation class
	HashMap<String,String> classMap = new HashMap<String,String>();

	public HashMap<String, TableDetail> getTableMap() {
		return tableMap;
	}

	public void setTableMap(HashMap<String, TableDetail> tableMap) {
		this.tableMap = tableMap;
	}

	public HashMap<String, String> getClassMap() {
		return classMap;
	}

	public void setClassMap(HashMap<String, String> classMap) {
		this.classMap = classMap;
	}
	
}
