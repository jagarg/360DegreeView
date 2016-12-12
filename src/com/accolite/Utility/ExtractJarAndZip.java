package com.accolite.Utility;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ExtractJarAndZip implements Runnable {

	File file;
	String unJarPath;
	String fileType;
	String classPath;

	public ExtractJarAndZip(String unJarPath, String classPath, File fileName, String fileType) {
		this.unJarPath = unJarPath;
		this.classPath = classPath;
		this.file = fileName;
		this.fileType = fileType;
	}

	@Override
	public void run() {
		System.out.println("Thread started processing : " + file.getName());
		if (file.isFile() && (file.getName().endsWith("jar") || file.getName().endsWith("zip"))) {
			try {
				ZipFile compressedFile = null;
				String fileName = file.toString();
				String jarOrzip = null;
				if(fileName.contains(".jar")){
					compressedFile = new JarFile(file);
					jarOrzip = ".jar";
				}
				else if(fileName.contains(".zip")){
					compressedFile	= new ZipFile(file);
					jarOrzip = ".zip";
				}
				File classDirectory = new File(classPath);
				File unJarDirectory = new File(unJarPath
						+ "/"
						+ file
								.toString()
								.substring(file.toString().lastIndexOf("\\"))
								.replaceAll(jarOrzip, ""));
				if (!unJarDirectory.exists())
					unJarDirectory.mkdir();
				Enumeration<? extends ZipEntry> entries = compressedFile.entries();
				boolean isZipPresent =false;
				while (entries.hasMoreElements()) {
					ZipEntry entry = entries.nextElement();

					if (entry.getName().contains(fileType) || entry.getName().contains(".jar") || entry.getName().contains(".class") ) {
						java.io.File fl = null;
						if(entry.getName().contains(".class")){
							fl = new java.io.File(classDirectory,
									entry.getName());
							if (!fl.exists()) {
								fl.getParentFile().mkdirs();
								fl = new java.io.File(classDirectory,
										entry.getName());
							}
						}else{
							fl = new java.io.File(unJarDirectory,
									entry.getName());
							if (!fl.exists()) {
								fl.getParentFile().mkdirs();
								fl = new java.io.File(unJarDirectory,
										entry.getName());
							}
						}
						
						if (entry.isDirectory()) {
							continue;
						}
						
						java.io.InputStream is = compressedFile.getInputStream(entry);
						java.io.FileOutputStream fo = new java.io.FileOutputStream(fl);
						
						byte[] buffer = new byte[2048];
				        int    tmp    = 0;
				  
				        while ((tmp = is.read(buffer)) != -1) 
				        {
				          fo.write(buffer, 0, tmp);
				        }
				        
						fo.close();
						is.close();
						
						if (entry.getName().contains(".jar") || entry.getName().contains(".zip") ) {
							isZipPresent = true;
						}
						
					} else
						continue;

				}
				if(isZipPresent){
					Utility.extractJarAndZipFiles(unJarDirectory.toString(),unJarPath,classPath,fileType,false);
				}
				// delete empty directory
				if (unJarDirectory.list().length == 0)
					unJarDirectory.delete();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}

