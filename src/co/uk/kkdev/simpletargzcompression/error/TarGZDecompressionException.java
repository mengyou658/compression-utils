package co.uk.kkdev.simpletargzcompression.error;

/**
 * An {@link Exception} that is thrown whenever a problem is encountered during decompression. 
 * 
 * @author Anthony Williams (github.com/92antwilliams)
 */
public class TarGZDecompressionException extends Exception {

	private static final long serialVersionUID = -447923503256144198L;

	public TarGZDecompressionException() {}

	public TarGZDecompressionException(String message) {
		super(message);
	}

	public TarGZDecompressionException(Throwable cause) {
		super(cause);
	}

	public TarGZDecompressionException(String message, Throwable cause) {
		super(message, cause);
	}

}
