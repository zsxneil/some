package com.my.compress.l4z;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;

public class l4z {
	public static void main(String[] args) throws Exception {
		long current = System.currentTimeMillis();
		String inputPath = "F:\\sql.txt";
    	String outputPath = "F:\\sql.zip";
		compress(inputPath, outputPath);
		System.out.println(System.currentTimeMillis() - current);
	}
	
	public static void compress(String inputPath,String outputPath) throws IOException {
		File inputFile = new File(inputPath);
		byte[] data = Files.readAllBytes(inputFile.toPath());
		LZ4Factory factory = LZ4Factory.safeInstance();
		LZ4Compressor compressor = factory.highCompressor(17);
		FileOutputStream outputStream = new FileOutputStream(outputPath);
		byte[] result = compressor.compress(data);
		outputStream.write(result);
		outputStream.close();
	}
}
