package com.apadmi.compressionexample;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

public class Decompressor {
	
	private static final int BUFFER_SIZE = 2048;
	
	private final File mSrc;
	private final File mDest;

	public Decompressor(File src, File dest) {
		mSrc = src;
		mDest = dest;
		
		// TODO: check files are okay
	}

	public void decompress() {
		try {
			Utils.log("Started decompression");
		    TarArchiveInputStream tarInput = new TarArchiveInputStream(new GzipCompressorInputStream(
		    		new BufferedInputStream(new FileInputStream(mSrc))));
	
		    TarArchiveEntry tarEntry;
		    while ((tarEntry = tarInput.getNextTarEntry()) != null) {
		        File destPath = new File(mDest, tarEntry.getName());
		        if (tarEntry.isDirectory()) {
		        	Utils.log("Unpacking directory " + tarEntry.getName());
		            destPath.mkdirs();
		        } else {
		        	Utils.log("Unpacking file " + tarEntry.getName());
		            destPath.createNewFile();
		            byte [] btoRead = new byte[BUFFER_SIZE];
		            BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(destPath));
		            int len = 0;
	
		            while((len = tarInput.read(btoRead)) != -1) {
		                bout.write(btoRead,0,len);
		            }
	
		            bout.close();
		            btoRead = null;
		        }
		    }
		    Utils.log("Completed decompression");
		    tarInput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
