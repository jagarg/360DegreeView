package com.accolite.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

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
	public static String DBPATH = "plocal:D:\\orientdb-community-2.2.13\\databases\\";
	public static String ADMIN_DATABSE = DBPATH+"appDB";
	
	@RequestMapping("/")
	public String welcome() {
		return "index";
	}

	@RequestMapping(value="/startProcess/", method=RequestMethod.POST,consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Boolean handleFileUpload(@RequestBody Configuration configuration){    
		
		// Process this directory for zip and jar files
		File fileDirectory = new File(configuration.getConfigName()+"//");
	    System.out.println(fileDirectory.getAbsolutePath());
	    
	    // Create new entry for Configuration

	    boolean result =  ConfigurationService.addConfiguration(configuration);
	    
	    
	    // Process the data and create a DataBase with details provided in configuration
	    switch(configuration.getSchemaName())
	    {
	    	case "JDO":
	    		// To Do - Jay
	    		// Process fileDirectory for zip and jar
	    		logger.info("Processing JDO files ...");
	    	break;
	    	
	    	case "HIBERNATE":
				// To Do - Ankit
	    		// Process fileDirectory for zip
	    		logger.info("Processing HIBERNATE files ...");
	    	break;
	    	
	    	case "ORACLE":
				// To Do - Himanshu
	    		logger.info("Processing ORACLE ...");
	    	break;	 
	    	
	    	case "MYSQL":
				// To Do - Himanshu
	    		logger.info("Processing MYSQL ...");
	    	break;	  

	    	case "CUSTOM JAR":
				// To Do 
	    		// Process fileDirectory for jar
	    		logger.info("Processing CUSTOM JAR ...");
	    	break;
	    }	    
	    
	    
	    // Delete fileDirectory
	    try
	    {
	    if(removeDirectory(fileDirectory))
	    	System.out.println("Directory "+fileDirectory+" deleted !!");
	    else
	    	System.out.println("Directory "+fileDirectory+" not deleted !!");
	    }catch(Exception e)
	    {
	    	System.out.println(e);
	    }
	    
		return result;   
    }
	
	private static boolean removeDirectory(File dir) {
	    if (dir.isDirectory()) {
	        File[] files = dir.listFiles();
	        if (files != null && files.length > 0) {
	            for (File aFile : files) {
	                removeDirectory(aFile);
	            }
	        }
	        return dir.delete();
	    } else {
	        return dir.delete();
	    }
	}
	
	@RequestMapping(value="/upload", method=RequestMethod.POST, consumes = {"multipart/form-data"}, produces = "application/json")
    public @ResponseBody String handleFileUpload(@RequestParam(value = "configName",required=true) String configName,@RequestParam(value = "fileOne",required=true) MultipartFile zip,           
            MultipartHttpServletRequest request, ModelAndView modelAndView){    
		
	    //String filePath = request.getServletContext().getRealPath("/"+configName+"/");
		String filePath = configName+"//";
	    //Creating directory to store uploaded files
	    if((new File(filePath)).exists())
	    	(new File(filePath)).delete();
	    if((new File(filePath)).mkdir())
	    	System.out.println("Directory created !! "+filePath);
	    else
	    	System.out.println("Directory not created !!");
	   
		String jdoFile = filePath+zip.getName()+".zip";
		
	    if (!zip.isEmpty()) {
	        try {
	            byte[] bytes = zip.getBytes();
	            File f = new File(jdoFile);
		    	f.createNewFile();
	            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(f));
	            stream.write(bytes);
	            stream.close();
	            return JSONObject.quote("You successfully uploaded " + jdoFile + " !");
	        } catch (Exception e) {
	            System.out.println("You failed to upload " + jdoFile + "  => " + e.getMessage());;
	            return "You failed to upload " + jdoFile + e.getMessage();
	        }
	    } else {
	        System.out.println("You failed to upload " + jdoFile + " because the file was empty.");
	        return "You failed to upload files because one of the file was empty.";
	    } 
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
	
	@RequestMapping(value = "/user/getallpaths/{database}/{tables}", method = RequestMethod.GET)
	 public @ResponseBody TableMapping getTableAllPaths(@PathVariable String database,@PathVariable String tables) 
	 {
		return UserService.getTableAllPaths(database,tables);
	 }
	
	 private static void init() {
		 logger.info("Init ....");

			OrientGraphFactory factory = OrientLoader.factory(ADMIN_DATABSE,null,null);			
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
