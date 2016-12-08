package com.accolite.Utility;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Utility {
	public static void extractJarAndZipFiles(String jarPath,String unJarPath,String fileType) {
		long start = System.currentTimeMillis();
		File directory = new File(jarPath);
		File[] fList = directory.listFiles();
		
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		for (File fileName : fList) {
			executorService.execute( new ExtractJar(unJarPath, fileName,fileType));
		}
		try {
			executorService.shutdown();
			executorService.awaitTermination(5, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		long end = System.currentTimeMillis();
		System.out.println("Decompression started at : " + start);
		System.out.println("UDecompression ended at : " + end);
		System.out.println("Time taken to decompress : " + (end - start) / 1000 + " seconds");
	}
}
