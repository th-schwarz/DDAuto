package codes.thischwa.autodyn.rest;

public class SdkException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SdkException(String message, Throwable cause) {
		super(message, cause);
	}

	public SdkException(String message) {
		super(message);
	}

	public SdkException(Throwable cause) {
		super(cause);
	}

}
