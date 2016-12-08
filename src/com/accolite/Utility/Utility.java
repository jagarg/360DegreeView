package com.accolite.Utility;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
			executorService.awaitTermination(5, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
