package co.uk.kkdev.simpletargzcompression;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

import co.uk.kkdev.simpletargzcompression.error.TarGZCompressionException;

/**
 * A {@link File} orientated compression wrapper for the Apache Commons Compress library.
 * Provides a simple way of compressing files and/or directories to the .tar.gz format.
 * 
 * @author Anthony Williams (github.com/92antwilliams)
 */
public class Compressor {
	
	/**
	 * An implementing class can register as a listener for progress, getting call backs when actions are taken.
	 */
	public interface ProgressListener {
		
		/**
		 * Called when compression begins.
		 */
		public void onCompressionStart();
		
		/**
		 * Called when a file is compressed.
		 * 
		 * @param file The file that was just compressed.
		 */
		public void onFileCompressed(File file);
		
		/**
		 * Called when compression is completed.
		 */
		public void onCompressionComplete();
	}
	
	private static final int BUFFER_SIZE = 2048;

	private final File   mRoot;
	private final File[] mFilesToCompress;
	private final File   mCompressionOutput;
	
	private ProgressListener mListener;

	/**
	 * Convenience constructor for {@link Compressor(File, File[], File) you can use when you want to
	 * only compress one file or directory.
	 * 
	 * @param root              The file representing the root of the relative naming of the archived files.
	 * @param fileToCompress    The File object representing the file or directory you wish to compress.
	 * @param compressionOutput The File object representing the output file of compression, it must end in .tar.gz. 
	 *                          If this location already exists, it will be overwritten.
	 *
	 * @throws TarGZCompressionException if the output file does not end in .tar.gz or if the output 
	 *         file exists and is a directory.
	 */
	public Compressor(File root, File fileToCompress, File compressionOutput) throws TarGZCompressionException {
		this(root, new File[] {fileToCompress}, compressionOutput);
	}
	
	/**
	 * Construct a new Compressor object that will compress the list of Files (which may contain directories) and
	 * put the resulting compressed archive in dest.
	 * The folder structure of the given files will be preserved and root is the root of the relative naming that 
	 * will be used to do this. If the list of Files given are dotted around the files system, root must be a common
	 * root of all of the Files, otherwise the resulting archive may or may not behave very well.
	 * 
	 * @param root              The file representing the root of the relative naming of the archived files.
	 * @param filesToCompress   The array of Files you wish to have compressed, if these include directories, they will be 
	 *                          recursively compressed.
	 * @param compressionOutput The File object representing the output file of compression, it must end in .tar.gz. 
	 *                          If this location already exists, it will be overwritten.
	 * 
	 * @throws TarGZCompressionException if the output file does not end in .tar.gz or if the output 
	 *         file exists and is a directory.
	 */
	public Compressor(File root, File[] filesToCompress, File compressionOutput) throws TarGZCompressionException {
		mRoot = root;
		mFilesToCompress  = filesToCompress;
		mCompressionOutput = compressionOutput;
		
		if (!compressionOutput.getPath().endsWith(".tar.gz")) 
			throw new TarGZCompressionException("Output must end with '.tar.gz'.\nDest = " + compressionOutput.getPath());
		if (compressionOutput.exists() && compressionOutput.isDirectory())
			throw new TarGZCompressionException("Output cannot be a directory");
	}
	
	/**
	 * Set listener to receive call backs on compression events. Set this to null to stop listening.
	 * 
	 * @param listener The listener to set.
	 */
	public void setListener(ProgressListener listener) {
		mListener = listener;
	}

	/**
	 * Perform the compression.
	 * 
	 * @throws TarGZCompressionException if a problem is encountered.
	 */
	public void compress() throws TarGZCompressionException {
		TarArchiveOutputStream tarOutput;
		
		try {
			tarOutput = new TarArchiveOutputStream(new GzipCompressorOutputStream(
					new BufferedOutputStream(new FileOutputStream(mCompressionOutput))));
		} catch (IOException e) {
			throw new TarGZCompressionException("Could not create output stream", e);
		}
		
		if (mListener != null) mListener.onCompressionStart();
		compressFiles(mFilesToCompress, tarOutput);
		
		try {
			tarOutput.close();
		} catch (IOException e) {
			throw new TarGZCompressionException("Could not close output stream", e);
		}
		
		if (mListener != null) mListener.onCompressionComplete();
	}
	
	private void compressFiles(File[] files, TarArchiveOutputStream tarOutput) throws TarGZCompressionException {
		for (File file : files) {
			if (file.isDirectory()) {
				compressDirectory(file, tarOutput);
			} else {
				compressFile(file, tarOutput);
			}
		}
	}

	private void compressDirectory(File directory, TarArchiveOutputStream tarOutput) throws TarGZCompressionException {
		try {
			File[] files = directory.listFiles();
			if (files != null && files.length != 0) {
				TarArchiveEntry entry = new TarArchiveEntry(directory, Utils.getRelativePath(mRoot, directory));
				tarOutput.putArchiveEntry(entry);
				tarOutput.closeArchiveEntry();
				if (mListener != null) mListener.onFileCompressed(directory);
				compressFiles(files, tarOutput);
			}
		} catch (IOException e) {
			throw new TarGZCompressionException("Problem encountered while compressing directory", e);
		}
	}
	
	private void compressFile(File file, TarArchiveOutputStream tarOutput) throws TarGZCompressionException {
		try {
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
			if (mListener != null) mListener.onFileCompressed(file);
		} catch (IOException e) {
			throw new TarGZCompressionException("Problem encountered while compressing file", e);
		}
	}
	
	/** 
	 * Convenience method for {@link Compressor.compress(File, File[], File)}.
	 * 
	 * @param root           Root of the archive to create.
	 * @param fileToCompress The file or folder to compress.
	 * @param output         The output destination, must end in .tar.gz.
	 */
	public static void compress(File root, File fileToCompress, File output) {
		compress(root, new File[] { fileToCompress }, output);
	}
	
	/** 
	 * Static helper method that squelches errors for creating a compression object and calling .compress() on it.
	 * 
	 * @param root           Root of the archive to create.
	 * @param fileToCompress The files or folders to compress.
	 * @param output         The output destination, must end in .tar.gz.
	 */
	public static void compress(File root, File[] filesToCompress, File output) {
		try {
			Compressor compressor = new Compressor(root, filesToCompress, output);
			compressor.compress();
		} catch(TarGZCompressionException e) {
			e.printStackTrace();
		}
	}
}
