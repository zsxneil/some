package com.my.compress.zip;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TestDir {
	public static void main(String[] args) throws Exception {
		long current = System.currentTimeMillis();
    	String inputPath = "F:\\test";
    	String outputPath = "F:\\test.zip";
    	compressDir(inputPath, outputPath);
    	System.out.println(System.currentTimeMillis() - current);
	}
	
	public static void compressDir(String inputPath ,String outputPath) throws Exception {
		// the file path need to compress
		File inputDir = new File(inputPath);
		
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputPath));
		
		// judge the file is the directory
		if (inputDir.isDirectory()) {
			// get the every file in the directory
			File[] files = inputDir.listFiles();
			
			for(int i=0;i<files.length;i++) {
				// new the BuuferedInputStream
				BufferedInputStream bis = new BufferedInputStream(new FileInputStream(files[i]));
				// the file entry ,set the file name in the zip file
				zos.putNextEntry(new ZipEntry(inputDir.getName() + File.separator + files[i].getName()));
				while (true) {
					byte[] b = new byte[10240];
					int len = bis.read(b);
					if (len == -1) {
						break;
					}
					zos.write(b, 0, len);
				}
				// close the input stream
				bis.close();
			}
			
		}
		// close the zip output stream
		zos.close();
	}
}
