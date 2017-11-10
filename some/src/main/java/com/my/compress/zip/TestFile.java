package com.my.compress.zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TestFile
{
    public static void main ( String [ ] args ) throws IOException
    {
    	long current = System.currentTimeMillis();
    	String inputPath = "F:\\EAS服务云平台_进度反馈表2016.xlsx";
    	String outputPath = "F:\\EAS服务云平台_进度反馈表2016.zip";
/*    	String inputPath = "F:\\sql.txt";
    	String outputPath = "F:\\sql.zip";
*/    	File inputFile = new File(inputPath);
        // new a file input stream
        FileInputStream fis = new FileInputStream (inputFile) ;
        BufferedInputStream bis = new BufferedInputStream ( fis ) ;

        // new a zipPutputStream
        // /home/liangruihua/ziptest/1.zip -- the out put file path and
        // name
        ZipOutputStream zos = new ZipOutputStream (
                new FileOutputStream (outputPath) ) ;
        BufferedOutputStream bos = new BufferedOutputStream ( zos ) ;

        // set the file name in the .zip file
        zos.putNextEntry ( new ZipEntry (inputFile.getName()) ) ;

        // set the declear
        zos.setComment ( "by  test!" ) ;

        byte [ ] b = new byte [ 10240 ] ;
        while ( true )
        {
            int len = bis.read ( b ) ;
            if ( len == - 1 )
                break ;
            bos.write ( b , 0 , len ) ;
        }
        fis.close ( ) ;
        zos.close ( ) ;
        
        System.out.println(System.currentTimeMillis() - current);
    }
}