package net.crizin.webs.exception;

public class WebsResponseException extends WebsException {

	public WebsResponseException(Throwable cause) {
		super(cause);
	}

	public WebsResponseException(String message) {
		super(message);
	}
}
