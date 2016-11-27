package com.accolite.orient;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;

import org.apache.log4j.Logger;

import com.accolite.datamodel.Model;

public class OrientMain {

	private static String FILE = "model.ser";
	final static Logger logger = Logger.getLogger(OrientMain.class);
	
	@SuppressWarnings("finally")
	private static Model deSearilizeModel()
	{
    	Model model = null;
    	URL url = OrientMain.class.getClassLoader().getResource(FILE);
    	System.out.println(url.getPath());
    	logger.info(" Serialized file : " + url.getPath());
    	
    	try(FileInputStream fileIn = new FileInputStream(url.getPath().replaceAll("%20", " "));
    		ObjectInputStream in = new ObjectInputStream(fileIn)) 
    	{
    		model = (Model)in.readObject();
    		logger.info(" De-Serialization complete");
    		
    	}catch(IOException | ClassNotFoundException e) {
    		logger.error(e.getStackTrace());
    		model = null;
    	}finally {
    		return model;
    	}
	}
	public static void main(String[] args) {
		OrientLoader.initiateLoad(deSearilizeModel());
	}
}
