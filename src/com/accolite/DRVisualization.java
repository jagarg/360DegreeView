package com.accolite;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Map.Entry;


import com.accolite.Utility.Utility;
import com.accolite.datamodel.ColumnDetail;
import com.accolite.datamodel.Model;
import com.accolite.parsers.JdoXmlParser;

public class DRVisualization {
	static Model model = new Model();
	static ArrayList<File> unProcessedFiles = new ArrayList<File>();
	static String unJarPath="C:/Users/hsareen/Desktop/Jay/unjar";
	//static String unJarPath = "C:/dev/gc/submodules/pacific-commerce/src/main/java/com/digitalriver";
	
	
	public static void main(String[] a){
		
		File fileDirectory = new File("C://Users//JAGARG//git//DR_Visualizer//subs//");//C:/dev/gc/submodules/runtime/lib/common";
		//create unjar/unip folder
		File unJarDirectory = new File(fileDirectory.getAbsolutePath()+"//unjar");
		//unJarDirectory.mkdir();
		
		//create unjar/unip folder
		File classDirectory = new File(fileDirectory.getAbsolutePath()+"//classes");
		classDirectory.mkdir();
		
		//calling unjar utility
		//Utility.extractJarAndZipFiles(fileDirectory.getAbsolutePath(),unJarDirectory.getAbsolutePath(),classDirectory.getAbsolutePath(),".jdo");
		
		try{
		//parse the config files
		System.out.println("Creating Model");
		model = JdoXmlParser.ParseJdoXml(Utility.listOfFiles(unJarDirectory.getAbsolutePath(), new ArrayList<String>(),"jdo"),classDirectory.getAbsolutePath());
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		//Enrich the model
		
		
		// Serializing the model
		try {
	         FileOutputStream fileOut = new FileOutputStream("src\\model.ser");
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(model);
	         out.close();
	         fileOut.close();
	         System.out.println("Serialized data is saved in src\\model.ser");
	     }catch(IOException i) {
	         i.printStackTrace();
	     }
		
		model=null;
		
		 //Deserializing the model
		try {
	         FileInputStream fileIn = new FileInputStream("src\\model.ser");
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         model = (Model)in.readObject();
	         in.close();
	         fileIn.close();
	         System.out.println("Serialized data is read from src\\model.ser");
	     }catch(IOException | ClassNotFoundException i) {
	         i.printStackTrace();
	     }
		
		model=null;
	}
	
}
