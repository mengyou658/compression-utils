package co.uk.kkdev.simpletargzcompression;

import java.io.File;

/**
 * Internal helper methods.
 * 
 * @author Anthony Williams (github.com/92antwilliams)
 */
public class Utils {
	
	// TODO: find the common root of multiple files.
	
	public static String getRelativePath(File from, File to) {
		return from.toURI().relativize(to.toURI()).getPath();
	}
}
