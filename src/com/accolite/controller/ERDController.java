package com.accolite.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.accolite.Utility.Utility;
import com.accolite.datamodel.Configuration;
import com.accolite.datamodel.DataModelDetail;
import com.accolite.datamodel.JDBCConnection;
import com.accolite.datamodel.Model;
import com.accolite.datamodel.Table;
import com.accolite.datamodel.TableMapping;
import com.accolite.orient.OrientLoader;
import com.accolite.parsers.JdoXmlParser;
import com.accolite.rdms.MySQLUtility;
import com.accolite.rdms.OracleUtility;
import com.accolite.rdms.RDMSUtility;
import com.accolite.service.ConfigurationService;
import com.accolite.service.UserService;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

@CrossOrigin(origins = "*")
@Controller
@EnableAutoConfiguration
public class ERDController {
	
	final static Logger logger = Logger.getLogger(ERDController.class);
	public static String DBPATH = "plocal:F:\\orientdb-community-2.2.12\\databases\\";
	//"plocal:C:\\Users\\hsareen\\Desktop\\db\\orientdb-community-2.2.13\\databases\\";
	public static String ADMIN_DATABSE = DBPATH+"appDB";
	
	static HashMap<String, String> config_DB = null;
	
	@RequestMapping("/")
	public String welcome() {
		return "index";
	}

	@RequestMapping(value="/testJDBC/", method=RequestMethod.POST,consumes=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Boolean testJDBC(@RequestBody JDBCConnection jdbcConnection)
	{  
		boolean result = false;
		// load the Driver Class
		try
		{
			Class.forName(jdbcConnection.getDriver());
			Connection conn =  DriverManager.getConnection(jdbcConnection.getUrl(),jdbcConnection.getUsername(),jdbcConnection.getPassword());
			result = true;
		} catch (ClassNotFoundException | SQLException e) {
			logger.error(" Exception : "+e.getStackTrace());
			result = false;
		}
		return result;
	}
	
	@RequestMapping(value="/startProcess/", method=RequestMethod.POST,consumes=MediaType.APPLICATION_JSON_VALUE,produces=MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Boolean handleFileUpload(@RequestBody Configuration configuration){    
		
		// Process this directory for zip and jar files
		File fileDirectory = new File(configuration.getConfigName()+"//");
	    
	    // Create new entry for Configuration
	    // generate Database for user
	    String dbName = configuration.getConfigName()+"_"+System.currentTimeMillis();
	    configuration.setDatabaseName(dbName);
	    configuration.setDbUserName("admin");
	    configuration.setDbPassword("admin");
	    
	    boolean result =  ConfigurationService.addConfiguration(configuration);

	    Model model = new Model();
	    
	    // Process the data and create a DataBase with details provided in configuration
	    switch(configuration.getSchemaName())
	    {
	    	case "JDO":
	    		logger.info("Processing JDO files ...");
	    		
	    		//create unjar/unip folder
	    		File unJarDirectory = new File(fileDirectory.getAbsolutePath()+"//unjar");
	    		unJarDirectory.mkdir();
	    		
	    		//create unjar/unip folder
	    		File classDirectory = new File(fileDirectory.getAbsolutePath()+"//classes");
	    		classDirectory.mkdir();
	    		
	    		//calling unjar utility
	    		Utility.extractJarAndZipFiles(fileDirectory.getAbsolutePath(),unJarDirectory.getAbsolutePath(),classDirectory.getAbsolutePath(),".jdo");
	    		
	    		//parse the config files
	    		model = JdoXmlParser.ParseJdoXml(Utility.listOfFiles(unJarDirectory.getAbsolutePath(), new ArrayList<String>(),"jdo"),classDirectory.getAbsolutePath());
	    		
	    		//OrientLoader.initiateLoad(model,configuration);
	    		
	    		logger.info("Processing JDO files Completes !!");
	    	break;
	    	
	    	case "HIBERNATE":
				// To Do - Ankit
	    		// Process fileDirectory for zip
	    		logger.info("Processing HIBERNATE files ...");
	    	break;
	    	
	    	case "ORACLE":
	    		/**
	    		 *  Steps
	    		 *  1. Provide Database Details and Connection details using <a>Configuration</a>
	    		 *  2. Call process() from <a>OracleUtility</a> and it will return instance of <a>Model</a>
	    		 *  3. Call initiateLoad(Model model) from <a>OrientLoader</a> to start loading.
	    		 */
	    		logger.info("Processing ORACLE ...");
	    		
	    		model = OracleUtility.process(configuration.getConnection());
	    		OrientLoader.initiateLoad(model,configuration);
	    		
	    		logger.info("Processing ORACLE Completes !!");
	    	break;	 
	    	
	    	case "MYSQL":
	    		/**
	    		 *  Steps
	    		 *  1. Provide Database Details and Connection details using <a>Configuration</a>
	    		 *  2. Call process() from <a>MySQLUtility</a> and it will return instance of <a>Model</a>
	    		 *  3. Call initiateLoad(Model model) from <a>OrientLoader</a> to start loading.
	    		 */
	    		logger.info("Processing MYSQL ...");
	    		
	    		//model = MySQLUtility.process(configuration.getConnection());
	    		//OrientLoader.initiateLoad(model,configuration);
	    		
	    		logger.info("Processing MYSQL Completes !!");
	    	break;	  

	    	case "CUSTOM JAR":
				// To Do 
	    		// Process fileDirectory for jar
	    		logger.info("Processing CUSTOM JAR ...");
	    	break;
	    }	    
	    	    
	    // Delete fileDirectory in case of JDO or HIBERNATE
	    try
	    {
	    	if(configuration.getSchemaName().equals("JDO") || configuration.getSchemaName().equals("HIBERNATE"))
	    	{
			    if(removeDirectory(fileDirectory))
			    	System.out.println("Directory "+fileDirectory+" deleted !!");
			    else
			    	System.out.println("Directory "+fileDirectory+" not deleted !!");
			}
	    }
	    catch(Exception e)
		{
			logger.error(e);
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
		
	@RequestMapping(value = "/user/listConfig", method = RequestMethod.GET)
	 public @ResponseBody List<DataModelDetail> listDB() 
	 {
		List<String> list = ConfigurationService.listDB();
		List<DataModelDetail> details = new ArrayList<>();
		
		config_DB = new HashMap<>();
		
		for (String entry : list) {
			
			String[] arr = entry.split(RDMSUtility.FIELD_SEPERATOR);
			
			config_DB.put(arr[0], arr[1]);
			DataModelDetail dd = new DataModelDetail();
			dd.setDataModelName(arr[0]);
			
			dd.setTables(UserService.listTable(arr[1]));
			details.add(dd);
		}
		return details;
	 }
	
	@RequestMapping(value = "/user/gettable/{configName}/{table}", method = RequestMethod.GET)
	 public @ResponseBody Table getTable(@PathVariable String configName,@PathVariable String table) 
	 {
		if(config_DB.containsKey(configName))
			return UserService.getTable(config_DB.get(configName),table);
		else
			return new Table();
	 }	
	
	@RequestMapping(value = "/user/getmappings/{configName}/{tables}", method = RequestMethod.GET)
	 public @ResponseBody TableMapping getTableMapping(@PathVariable String configName,@PathVariable String tables) 
	 {
		if(config_DB.containsKey(configName))
			return UserService.getTableMapping(config_DB.get(configName),tables);
		else
			return new TableMapping();
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
