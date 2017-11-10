package com.my.compress.zip;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TestDirNoRecursion {
	public static int num; 
	public static void main(String[] args) throws IOException {
		long current = System.currentTimeMillis();
		num = 0;
		String path = "F:\\k";
		scanDirNoRecursion(path);
		System.out.println(System.currentTimeMillis() - current);
	}
	
	  //非递归  
    public static void scanDirNoRecursion(String path) throws IOException{
    	String outputPath = "F:\\k.zip";
    	ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputPath));
     LinkedList list = new LinkedList();  
        File dir = new File(path);  
        File file[] = dir.listFiles();  
        for (int i = 0; i < file.length; i++) {
            if (file[i].isDirectory()) {
            	Map<String, Object> fileInfo = new HashMap();
            	fileInfo.put("pathdeep", file[i].getName());
            	fileInfo.put("dir", file[i]);
            	list.add(fileInfo);  
            }
            else{  
            	compress(file[i].getName(), file[i].getAbsolutePath(), zos);
                //System.out.println(file[i].getName());  
                num++;  
            }  
        }  
        Map tmp;  
        while (!list.isEmpty()) {  
            tmp = (Map<String, Object>)list.removeFirst();//首个目录  
            File fileTmp = (File) tmp.get("dir");
            String pathName = (String) tmp.get("pathdeep");
            if (fileTmp.isDirectory()) {  
                file = fileTmp.listFiles();   
                if (file == null)  
                    continue;  
                for (int i = 0; i < file.length; i++) {  
                    if (file[i].isDirectory())  {
                    	Map<String, Object> fileInfo = new HashMap();
                    	fileInfo.put("pathdeep", pathName + "\\" + file[i].getName());
                    	fileInfo.put("dir", file[i]);
                    	list.add(fileInfo);//目录则加入目录列表，关键  
                    }
                    else{  
                    	compress(pathName + "\\" + file[i].getName(), file[i].getAbsolutePath(), zos);
                        //System.out.println(pathName + "\\" + file[i].getName());  
                        num++;  
                    }  
                }  
            } else {
            	compress(pathName + "\\" + fileTmp.getName(), fileTmp.getAbsolutePath(), zos);
                //System.out.println(pathName + "\\" + fileTmp.getName());  
                num++;  
            }  
        }  
        zos.close();
    }
    
    public static void compress(String dirPath,String inputFile,ZipOutputStream zos) throws IOException {
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
