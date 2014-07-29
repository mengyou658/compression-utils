package com.apadmi.compressionexample;

import java.io.File;

public class Utils {
	
	public static String getRelativePath(File from, File to) {
		return from.toURI().relativize(to.toURI()).getPath();
	}
	
	public static void log(String message) {
		System.out.println(message);
	}
}
