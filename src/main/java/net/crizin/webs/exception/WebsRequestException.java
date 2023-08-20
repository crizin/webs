package net.crizin.webs.exception;

public class WebsRequestException extends WebsException {

	public WebsRequestException(Throwable cause) {
		super(cause);
	}

	public WebsRequestException(String message) {
		super(message);
	}

	public WebsRequestException(String message, Throwable cause) {
		super(message, cause);
	}
}
