package com.accolite.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.accolite.datamodel.Configuration;
import com.accolite.datamodel.DatabaseDetail;
import com.accolite.datamodel.Table;
import com.accolite.datamodel.TableMapping;
import com.accolite.orient.OrientLoader;
import com.accolite.service.ConfigurationService;
import com.accolite.service.UserService;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

@Controller
@EnableAutoConfiguration
public class ERDController {
	
	final static Logger logger = Logger.getLogger(ERDController.class);

	@RequestMapping("/")
	public String welcome() {
		return "index";
	}

 
	@RequestMapping(value = "/config/add", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	 public @ResponseBody boolean addConfiguration(@RequestBody Configuration configuration) 
	 {
		return ConfigurationService.addConfiguration(configuration);
	 }
	
	@RequestMapping(value = "/config/list", method = RequestMethod.GET)
	 public @ResponseBody List<Map<String,Object>> listConfiguration() 
	 {
		List<Vertex> list = ConfigurationService.list();
		
		List<Map<String,Object>> map = new ArrayList<>();
		

		for (Vertex vertex : list) {
			map.add(((OrientVertex)vertex).getProperties());
		}
		return map;
	 }
		
	@RequestMapping(value = "/user/listdb", method = RequestMethod.GET)
	 public @ResponseBody List<DatabaseDetail> listDB() 
	 {
		List<String> list = ConfigurationService.listDB();
		
		List<DatabaseDetail> details = new ArrayList<>();
		
		for (String database : list) {
			
			DatabaseDetail dd = new DatabaseDetail();
			dd.setDatabaseName(database);
			
			dd.setTables(UserService.listTable(database));
			details.add(dd);
		}
		return details;
	 }
	
	@RequestMapping(value = "/user/gettable/{database}/{table}", method = RequestMethod.GET)
	 public @ResponseBody Table getTable(@PathVariable String database,@PathVariable String table) 
	 {
		return UserService.getTable(database,table);
	 }	
	
	@RequestMapping(value = "/user/getmappings/{database}/{tables}", method = RequestMethod.GET)
	 public @ResponseBody TableMapping getTableMapping(@PathVariable String database,@PathVariable String tables) 
	 {
		return UserService.getTableMapping(database,tables);
	 }
	
	 private static void init() {
		 logger.info("Init ....");

			OrientGraphFactory factory = OrientLoader.factory("plocal:D:\\orientdb-community-2.2.13\\databases\\appDB");			
			OrientGraphNoTx gph = factory.getNoTx();
	    	try
	    	{			
		        OCommandSQL tableQR = new OCommandSQL("CREATE CLASS CONFIGURATION IF NOT EXISTS EXTENDS V");
		        logger.debug("\nExecute : "+tableQR.getText());
		        
				Object result = gph.command(tableQR).execute();
				logger.debug("\nReturn : "+result.toString());
				
				gph.commit();
	    	}catch (Exception e) {
				logger.error(e.getMessage());
			}finally {
				gph.shutdown();
				factory.close();
			}
		}
	 

    public static void main(String[] args) throws Exception {
    	init();
        SpringApplication.run(ERDController.class, args);
    }
}
