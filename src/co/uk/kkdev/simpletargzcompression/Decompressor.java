package co.uk.kkdev.simpletargzcompression;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import co.uk.kkdev.simpletargzcompression.error.TarGZDecompressionException;

/**
 * A {@link File} orientated decompression wrapper for the Apache Commons Compress library.
 * Provides a simple way of decompressing files and/or directories from the .tar.gz format.
 * 
 * @author Anthony Williams (github.com/92antwilliams)
 */
public class Decompressor {
	
	/**
	 * An implementing class can register as a listener for progress, getting call backs when actions are taken.
	 */
	public interface ProgressListener {
		
		/**
		 * Called when decompression begins.
		 */
		public void onDecompressionStart();
		
		/**
		 * Called when a directory is decompressed.
		 * 
		 * @param directory The directory that was just decompressed.
		 */
		public void onDirectoryDecompressed(String directory);
		
		/**
		 * Called when a file is decompressed.
		 * 
		 * @param file The file that was just decompressed.
		 */
		public void onFileDecompressed(String file);
		
		/**
		 * Called when decompression is completed.
		 */
		public void onDecompressionComplete();
	}
	
	private static final int BUFFER_SIZE = 2048;
	
	private final File mCompressedInput;
	private final File mUncompressedOutput;
	
	private ProgressListener mListener;

	/**
	 * Create a new Decompressor object that will decompress compressedInput and put the output at the
	 * location specified by uncompressedOutput.
	 * 
	 * @param compressedInput    The archive to unpack. Must end with '.tar.gz'.
	 * @param uncompressedOutput The location to put the output.
	 * 
	 * @throws TarGZDecompressionException if the input file does not end with '.tar.gz' or doesn't exist.
	 */
	public Decompressor(File compressedInput, File uncompressedOutput) throws TarGZDecompressionException {
		mCompressedInput    = compressedInput;
		mUncompressedOutput = uncompressedOutput;
		
		if (!mCompressedInput.exists()) 
			throw new TarGZDecompressionException("Input file doesn't exist");
		if (!mCompressedInput.getPath().endsWith(".tar.gz")) 
			throw new TarGZDecompressionException("Input must end with '.tar.gz'\nPath = " 
		                                          + mCompressedInput.getPath());
		
	}
	
	/**
	 * Register for call backs, set this to null to stop listening.
	 * 
	 * @param listener The listener to set.
	 */
	public void setListener(ProgressListener listener) {
		mListener = listener;
	}

	/**
	 * Perform the decompression.
	 * 
	 * @throws TarGZDecompressionException if there is a problem decompressing the archive.
	 */
	public void decompress() throws TarGZDecompressionException {
		TarArchiveInputStream tarInput;
		
		try {
		    tarInput = new TarArchiveInputStream(new GzipCompressorInputStream(
		    		new BufferedInputStream(new FileInputStream(mCompressedInput))));   
		} catch (IOException e) {
			throw new TarGZDecompressionException("Could not create input stream", e);
		}
		
		if (mListener != null) mListener.onDecompressionStart();
		
		TarArchiveEntry tarEntry;
	    try {
			while ((tarEntry = tarInput.getNextTarEntry()) != null) {
				File destPath = new File(mUncompressedOutput, tarEntry.getName());
			    if (tarEntry.isDirectory()) {
			        destPath.mkdirs();
			        if (mListener != null) mListener.onDirectoryDecompressed(tarEntry.getName());
			    } else {
			    	destPath.createNewFile();
			    	byte [] btoRead = new byte[BUFFER_SIZE];
			    	BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(destPath));
			        int len;
			        while((len = tarInput.read(btoRead)) != -1) {
			            bout.write(btoRead,0,len);
			        }
			        bout.close();
			        if (mListener != null) mListener.onFileDecompressed(tarEntry.getName());
			    }
			}
		} catch (IOException e) {
			throw new TarGZDecompressionException("Could not create output", e);
		}
	    try {
			tarInput.close();
		} catch (IOException e) {
			throw new TarGZDecompressionException("Could not close output", e);
		}
	    if (mListener != null) mListener.onDecompressionComplete();
	}
	
	/** 
	 * Convenience for {@link Decompressor.decompress(File, File, ProgressListener)}
	 */
	public static boolean decompress(File compressedInput, File uncompressedOutput) {
		return decompress(compressedInput, uncompressedOutput, null);
	}
	
	/**
	 * Static convenience method to decompress the given files. This method does not report 
	 * Exceptions, but does report whether or not decompression happened successfully. If you 
	 * want/need more control over Exception handling, please use the non-static methods.
	 * 
	 * @param compressedInput    The file to decompress (must end in '.tar.gz')
	 * @param uncompressedOutput Where to put the decompressed files
	 * @param listener           Used for call backs, set this to null if you don't want/need this
	 * @return whether or not decompression successfully occurred 
	 */
	public static boolean decompress(File compressedInput, File uncompressedOutput, ProgressListener listener) {
		try {
			Decompressor decompressor = new Decompressor(compressedInput, uncompressedOutput);
			decompressor.setListener(listener);
			decompressor.decompress();
			return true;
		} catch (TarGZDecompressionException e) {
			e.printStackTrace();
			return false;
		}
	}
}
