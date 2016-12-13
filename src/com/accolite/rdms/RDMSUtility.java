package com.accolite.rdms;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.accolite.orient.OrientMain;

public class RDMSUtility {

	public static String FIELD_SEPERATOR = ":";
	final static Logger logger = Logger.getLogger(RDMSUtility.class);
	
	public static Connection getConnection() {
		
		logger.info("Fetching JDBC Connection Details .... ");
		Properties props = new Properties();
		FileInputStream fis = null;
		Connection con = null;
		try {
			URL url = OrientMain.class.getClassLoader().getResource("database.properties");
			logger.info("Database properties : "+url.getPath());

			fis = new FileInputStream(url.getPath());
			props.load(fis);

			// load the Driver Class
			Class.forName(props.getProperty("DRIVER_CLASS"));

			// create the connection now
			con = DriverManager.getConnection(props.getProperty("URL"),
					props.getProperty("USERNAME"),
					props.getProperty("PASSWORD"));
			logger.info("JDBC Connection established successfully..");
		} catch (IOException | ClassNotFoundException | SQLException e) {
			logger.error(" Exception : "+e.getStackTrace());
		}
		return con;
	}
}
