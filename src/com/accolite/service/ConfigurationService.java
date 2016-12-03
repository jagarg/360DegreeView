package com.accolite.service;

import java.util.ArrayList;

import com.accolite.dao.ConfigurationDAO;
import com.accolite.datamodel.Configuration;
import com.tinkerpop.blueprints.Vertex;

public class ConfigurationService {

	public static boolean addConfiguration(Configuration configuration) {
		return ConfigurationDAO.addConfiguration(configuration);
	}
	
	public static ArrayList<Vertex> list() {
		return ConfigurationDAO.listConfiguration(); 
	}
	
	public static ArrayList<String> listDB() {
		return ConfigurationDAO.listDB(); 
	}
}
