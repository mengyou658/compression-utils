package co.uk.kkdev.simpletargzcompression.examples;

import java.io.File;

import co.uk.kkdev.simpletargzcompression.Compressor;
import co.uk.kkdev.simpletargzcompression.Decompressor;

/**
 * Example of how to perform compression and decompression using the {@link SimpleTarGZCompression} library.
 * 
 * @author Anthony Williams (github.com/92antwilliams)
 */
public class CompressionExample {
	
	public static void main(String[] args) {
		Listener listener = new Listener();
		Compressor.compress(new File("test/data/"), new File("test/data/"), new File("test/out/dest.tar.gz"), listener);
		Decompressor.decompress(new File("test/out/dest.tar.gz"), new File("test/out/uncompressed"), listener);
	}

}
