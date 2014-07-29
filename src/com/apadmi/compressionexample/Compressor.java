package com.apadmi.compressionexample;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

public class Compressor {
	
	private static final int BUFFER_SIZE = 2048;

	private final File mRoot;
	private final File[] mSrc;
	private final File mDest;

	public Compressor(File root, File src, File dest) {
		this(root, new File[] {src}, dest);
	}
	
	public Compressor(File root, File[] src, File dest) {
		mRoot = root;
		mSrc = src;
		mDest = dest;
		
		// TODO: check files are okay
	}

	public void compress() {
		try {
			Utils.log("Started compression");
			TarArchiveOutputStream tarOutput = new TarArchiveOutputStream(
					new GzipCompressorOutputStream(new BufferedOutputStream(
							new FileOutputStream(mDest))));
			
			compressFiles(mSrc, tarOutput);
			
			Utils.log("Completed compression");
			tarOutput.close();
		} catch (IOException e) {
			Utils.log("Problem encountered when creating output file");
			e.printStackTrace();
		}
	}
	
	private void compressFiles(File[] files, TarArchiveOutputStream tarOutput) {
		for (File file : files) {
			if (file.isDirectory()) {
				compressDirectory(file, tarOutput);
			} else {
				compressFile(file, tarOutput);
			}
		}
	}

	private void compressDirectory(File directory, TarArchiveOutputStream tarOutput) {
		try {
			File[] files = directory.listFiles();
			if (files != null && files.length != 0) {
				Utils.log("Compressing directory " + directory.getCanonicalPath());
				TarArchiveEntry entry = new TarArchiveEntry(directory, Utils.getRelativePath(mRoot, directory));
				tarOutput.putArchiveEntry(entry);
				tarOutput.closeArchiveEntry();
				compressFiles(files, tarOutput);
			}
		} catch (IOException e) {
			Utils.log("Problem encountered when compressing directory");
			e.printStackTrace();
		}
	}
	
	private void compressFile(File file, TarArchiveOutputStream tarOutput) {
		try {
			Utils.log("Compressing file " + file.getCanonicalPath());
			BufferedInputStream sourceStream = new BufferedInputStream(new FileInputStream(file));
			TarArchiveEntry entry = new TarArchiveEntry(file, Utils.getRelativePath(mRoot, file));
			tarOutput.putArchiveEntry(entry);
			int count;
			byte data[] = new byte[BUFFER_SIZE];
			while ((count = sourceStream.read(data, 0, BUFFER_SIZE)) != -1) {
				tarOutput.write(data, 0, count);
			}
			sourceStream.close();
			tarOutput.closeArchiveEntry();
		} catch (IOException e) {
			Utils.log("Problem encountered while compressing file");
			e.printStackTrace();
		}
	}
}
