package codes.thischwa.ddauto.service;

public class ZoneSdkException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ZoneSdkException(String message, Throwable cause) {
		super(message, cause);
	}

	public ZoneSdkException(String message) {
		super(message);
	}

	public ZoneSdkException(Throwable cause) {
		super(cause);
	}

}
