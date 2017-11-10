package com.my.compress.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Test {
	/** 压缩单个文件*/
    public static void ZipFile(String filepath ,String zippath) {
    	try {
            File file = new File(filepath);
            File zipFile = new File(zippath);
            InputStream input = new FileInputStream(file);
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
            zipOut.putNextEntry(new ZipEntry(file.getName()));
            int temp = 0;
            while((temp = input.read()) != -1){
                zipOut.write(temp);
            }
            input.close();
            zipOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    public static void main(String[] args) {
    	String filepath = "F:\\sql.txt";
    	String zippath = "F:\\sql.zip";
    	long current = System.currentTimeMillis();
		ZipFile(filepath, zippath);
		System.out.println(System.currentTimeMillis() - current);
	}
}
