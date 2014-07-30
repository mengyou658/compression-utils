package co.uk.kkdev.simpletargzcompression.examples;

import co.uk.kkdev.simpletargzcompression.Compressor;
import co.uk.kkdev.simpletargzcompression.Decompressor;

import java.io.File;

public class Listener implements Compressor.ProgressListener, Decompressor.ProgressListener {

	public Listener() {}

	@Override
	public void onDecompressionStart() {
		System.out.println("Started decopmression.");
	}
	
	@Override
	public void onDirectoryDecompressed(String directory) {
		System.out.println("Decompressed directory " + directory + ".");
	}

	@Override
	public void onFileDecompressed(String file) {
		System.out.println("Decompressed file " + file + ".");
	}

	@Override
	public void onDecompressionComplete() {
		System.out.println("Finished decopmression.");
	}

	@Override
	public void onCompressionStart() {
		System.out.println("Started copmression.");
	}

	@Override
	public void onFileCompressed(File file) {
		System.out.println("Compressed " + file.getName() + ".");
	}

	@Override
	public void onCompressionComplete() {
		System.out.println("Finished copmression.");
	}

}
