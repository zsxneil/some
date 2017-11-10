package com.my.compress.zip;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringUtils;

public class TestDirAll {
	public static String inputPath = "F:\\vss";
	public static void main(String[] args) throws Exception {
		long current = System.currentTimeMillis();
    	//String inputPath = "F:\\test";
    	String outputPath = "F:\\vss.zip";
    	File inputFile = new File(inputPath);
    	ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputPath));
    	compress(inputFile, zos,null);
    	zos.close();
    	System.out.println(System.currentTimeMillis() - current);
	}
	
	public static void compress(File inputFile,ZipOutputStream zos,String dirPath) throws Exception {
		
		dirPath = (StringUtils.isBlank(dirPath) ? "" : (dirPath + File.separator)) + inputFile.getName();
		if (inputFile.isDirectory()) {
			File[] files = inputFile.listFiles();
			for (int i = 0; i < files.length; i++) {
				compress(files[i], zos, dirPath);
			}
			
		} else {
			
			// new the BuuferedInputStream
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFile));
			// the file entry ,set the file name in the zip file
			zos.putNextEntry(new ZipEntry(dirPath));
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
	
}
