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
		
		String jarPath = "C:/Users/hsareen/Desktop/Jay/jar";//C:/dev/gc/submodules/runtime/lib/common";
		
		Utility.extractJarAndZipFiles(jarPath,unJarPath,".jdo");
		
		
		
		JdoXmlParser.ParseJdoXml(Utility.listOfFiles(unJarPath, new ArrayList<String>(),"jdo"),model);
		
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
