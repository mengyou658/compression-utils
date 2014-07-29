package com.apadmi.compressionexample;

import java.io.File;

public class Main {

	public static void main(String[] args) {
		File input = new File("data/input/");
		File compressedOutput = new File("data/output/compressed/dest.tar.gz");
		File uncompressedOutput = new File("data/output/decompressed/");
		Compressor compressor = new Compressor(input, input, compressedOutput);
		compressor.compress();
		Decompressor decompressor = new Decompressor(compressedOutput, uncompressedOutput);
		decompressor.decompress();
	}

}
