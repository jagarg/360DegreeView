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

import com.accolite.datamodel.ColumnDetail;
import com.accolite.datamodel.Model;
import com.accolite.datamodel.TableDetail;
import com.accolite.parsers.JdoXmlParser;

public class DRVisualization {
	
	public static void main(String[] a){
		Model model = new Model();
		/*
		String path = "C:/dev/gc/submodules/pacific-commerce/src/main/java/com/digitalriver";
		
		
		ArrayList<String> files = new ArrayList<String>();
		listOfFiles(path, files);
		
		//Process the JDO files
		for(String file : files)
			 JdoXmlParser.ParseJdoXml(file,model);
		
		//Enrich the model
		try {
			enrichModel(model);
		} catch (ClassNotFoundException | SecurityException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
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
		*/
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
	
	public static void listOfFiles(String directoryName, ArrayList<String> files) {
	    File directory = new File(directoryName);

	    // get all the files from a directory
	    File[] fList = directory.listFiles();
	    for (File file : fList) {
	        if (file.isFile()  && file.getName().endsWith("jdo")) {
	            files.add(file.getAbsolutePath());
	        } else if (file.isDirectory()) {
	        	listOfFiles(file.getAbsolutePath(), files);
	        }
	    }
	}
	
	public static void enrichModel(Model model) throws ClassNotFoundException,  SecurityException, MalformedURLException{
		/*File file = new File("C://dev//gc//submodules//pacific-commerce//target//classes");
		URL url = file.toURI().toURL();
		URL[] urls = new URL[]{url};

		ClassLoader cl = new URLClassLoader(urls);
		*/

		for(Entry<String,TableDetail> tableEntry: model.getTableMap().entrySet()){
			//cl.loadClass("com.digitalriver.system.BaseBusinessObject");
			TableDetail table = model.getTableMap().get(tableEntry.getKey());
			for(Entry<String,ColumnDetail> columnEntry : table.getColumns().entrySet()){
				ColumnDetail column = table.getColumns().get(columnEntry.getKey());
				if(column.isForeignKey()){
					//System.out.println(table.getTableName()+"----------------------------------"+table.getClassName());
					//System.out.println(column.getLocalFieldName());
					Class<?> localClass = Class.forName(table.getClassName());
					Field s = getField(localClass,column.getLocalFieldName());
					
					String fieldType[] = s.getType().toString().split(" ");
					String foreignKeyClassName=null;
					if(fieldType[0].equalsIgnoreCase("interface")){
						if(!model.getTableMap().containsKey(fieldType[1]))
							foreignKeyClassName = model.getClassMap().get(fieldType[1]);
						else
							foreignKeyClassName = fieldType[1];
					}
					else if(fieldType[0].equalsIgnoreCase("class")){
						foreignKeyClassName = fieldType[1];
						continue;
					}
					if(!model.getTableMap().containsKey(foreignKeyClassName)){
						System.out.println("No Table Info for "+ foreignKeyClassName);
						break;
					}
					String foreignKeyTable = null;
					String foreignKeyColumnName = null;
					if(model.getTableMap().containsKey(foreignKeyClassName))
						foreignKeyTable = model.getTableMap().get(foreignKeyClassName).getTableName();
					if(model.getTableMap().get(foreignKeyClassName).getColumns().containsKey(column.getForeignFieldName()))
						foreignKeyColumnName = model.getTableMap().get(foreignKeyClassName).getColumns().get(column.getForeignFieldName()).getColumnName();
					else
						System.out.println("column: "+ column.getForeignFieldName()+" not present in :" +foreignKeyClassName);
					
					column.setForeignKeyTable(foreignKeyTable);
					column.setForeignKeyClass(foreignKeyClassName);
					column.setForeignKeyColumn(foreignKeyColumnName);
				}
			}
		}
		
		
	}
	
	//recursively trying to find the field in the class hierarchy 
	public static Field getField(Class<?> clazz, String name) {
	    Field field = null;
	    while (clazz != null && field == null) {
	        try {
	            field = clazz.getDeclaredField(name);
	        } catch (NoSuchFieldException e) {
	        	//Do something
	        }
	        clazz = clazz.getSuperclass();
	    }
	    return field;
	}
	
}
