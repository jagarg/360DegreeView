package com.accolite.Utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.lang.StringUtils;

public class ExtractJarUtil {
	
	// instead of writing java code we can use jar utility to unjar.
	public static void extractJarFiles(String jarPath, String unJarPath, String type){
		File directory = new File(jarPath);
	    // get all the files from a directory
	    File[] fList = directory.listFiles();
	    for (File fileName : fList) {
	        if (fileName.isFile()  && fileName.getName().endsWith("jar")) {
	        	try {
					@SuppressWarnings("resource")
					JarFile jar = new JarFile(fileName);
					File unJarDirectory = new File(unJarPath+"/"+ fileName.toString().substring(fileName.toString().lastIndexOf("\\")).replaceAll(".jar",""));
					if(!unJarDirectory.exists())
						unJarDirectory.mkdir();
					Enumeration<JarEntry> entries = jar.entries();
					 while (entries.hasMoreElements()){
						 JarEntry entry = entries.nextElement();
						 if(!entry.getName().contains(type))
							 continue;
						 java.io.File fl = new java.io.File(unJarDirectory, entry.getName());
					        if(!fl.exists())
					        {
					            fl.getParentFile().mkdirs();
					            fl = new java.io.File(unJarDirectory, entry.getName());
					        }
					        if(entry.isDirectory())
					        {
					            continue;
					        }
					        java.io.InputStream is = jar.getInputStream(entry);
					        java.io.FileOutputStream fo = new java.io.FileOutputStream(fl);
					        while(is.available()>0)
					        {
					            fo.write(is.read());
					        }
					        fo.close();
					        is.close();
						 
					 }
					 //delete empty directory
					 if(unJarDirectory.list().length == 0)
							unJarDirectory.delete();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        } 
	    }
	}
	
	// we can use this command to grep all class file names from jar: jar tf HibernateSampleAnnotations.jar | grep.class
	
	public static void listOfFiles(String directoryName, ArrayList<String> files, String type) {
	    File directory = new File(directoryName);

	    // get all the files from a directory
	    File[] fList = directory.listFiles();
	    for (File file : fList) {
	        if (file.isFile()  && file.getName().endsWith(type)) {
	            files.add(file.getName());
	        } else if (file.isDirectory()) {
	        	listOfFiles(file.getAbsolutePath(), files, type);
	        }
	    }
	}
	
	public static void readTextFile(String filename, ArrayList<String> files){
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				if(sCurrentLine.contains(".class")){
					files.add(StringUtils.replace(StringUtils.remove(sCurrentLine, ".class"), "/", "."));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}