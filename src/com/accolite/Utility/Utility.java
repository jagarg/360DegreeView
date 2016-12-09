package com.accolite.Utility;

import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.accolite.datamodel.ColumnDetail;
import com.accolite.datamodel.Model;
import com.accolite.datamodel.TableDetail;

public class Utility {
	public static void extractJarAndZipFiles(String jarPath,String unJarPath,String fileType) {
		long start = System.currentTimeMillis();
		extractJarAndZipFiles( jarPath, unJarPath, fileType,false) ;
		long end = System.currentTimeMillis();
		System.out.println("Decompression for started at : " + start);
		System.out.println("UDecompression for ended at : " + end);
		System.out.println("Time taken to decompress : " + (end - start) / 1000 + " seconds");
	}
	
	public static void extractJarAndZipFiles(String jarPath,String unJarPath,String fileType,boolean flag) {
		File directory = new File(jarPath);
		File[] fList = directory.listFiles();
		
		ExecutorService executorService = Executors.newFixedThreadPool(10);
	
		for (File file : fList) {
			if(file.getName().contains(".jar") || file.getName().contains(".zip"))
			executorService.execute( new ExtractJarAndZip(unJarPath, file,fileType));
		}
		try {
			executorService.shutdown();
			executorService.awaitTermination(30, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static ArrayList<String> listOfFiles(String directoryName, ArrayList<String> files,String fileType) {
	    File directory = new File(directoryName);

	    // get all the files from a directory
	    File[] fList = directory.listFiles();
	    for (File file : fList) {
	        if (file.isFile()  && file.getName().endsWith(fileType)) {
	            files.add(file.getAbsolutePath());
	        } else if (file.isDirectory()) {
	        	listOfFiles(file.getAbsolutePath(), files,fileType);
	        }
	    }
		return files;
	}
	
	public static void enrichJdoModel(Model model) throws ClassNotFoundException,  SecurityException, MalformedURLException{
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
