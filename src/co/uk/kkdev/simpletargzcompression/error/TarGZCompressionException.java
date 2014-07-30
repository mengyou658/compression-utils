package co.uk.kkdev.simpletargzcompression.error;

/**
 * An {@link Exception} that is thrown whenever a problem is encountered during compression. 
 * 
 * @author Anthony Williams (github.com/92antwilliams)
 */
public class TarGZCompressionException extends Exception {

	private static final long serialVersionUID = 1992405307406151288L;

	public TarGZCompressionException() {}

	public TarGZCompressionException(String message) {
		super(message);
	}

	public TarGZCompressionException(Throwable cause) {
		super(cause);
	}

	public TarGZCompressionException(String message, Throwable cause) {
		super(message, cause);
	}

}
