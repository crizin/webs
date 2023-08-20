package net.crizin.webs.exception;

public class WebsException extends RuntimeException {

	public WebsException(Throwable cause) {
		super(cause);
	}

	public WebsException(String message) {
		super(message);
	}

	public WebsException(String message, Throwable cause) {
		super(message, cause);
	}
}
