package com.accolite.Utility;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ExtractJar implements Runnable {

	File fileName;
	String unJarPath;
	String fileType;

	public ExtractJar(String unJarPath, File fileName,String fileType) {
		this.unJarPath = unJarPath;
		this.fileName = fileName;
		this.fileType = "."+fileType;
	}

	@Override
	public void run() {
		System.out.println("Thread started processing : " + fileName.getName());
		if (fileName.isFile() && fileName.getName().endsWith("jar")) {
			try {
				@SuppressWarnings("resource")
				JarFile jar = new JarFile(fileName);
				File unJarDirectory = new File(unJarPath
						+ "/"
						+ fileName
								.toString()
								.substring(
										fileName.toString().lastIndexOf("\\"))
								.replaceAll(".jar", ""));

				if (!unJarDirectory.exists())
					unJarDirectory.mkdir();
				Enumeration<JarEntry> entries = jar.entries();
				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();

					if (entry.getName().contains(fileType)) {

						java.io.File fl = new java.io.File(unJarDirectory,
								entry.getName());
						if (!fl.exists()) {
							fl.getParentFile().mkdirs();
							fl = new java.io.File(unJarDirectory,
									entry.getName());
						}
						if (entry.isDirectory()) {
							continue;
						}
						java.io.InputStream is = jar.getInputStream(entry);
						java.io.FileOutputStream fo = new java.io.FileOutputStream(
								fl);
						while (is.available() > 0) {
							fo.write(is.read());
						}
						fo.close();
						is.close();
					} else
						continue;

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

