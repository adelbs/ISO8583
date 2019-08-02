package org.adelbs.iso8583.clientserver;

@SuppressWarnings("serial")
public class MockISOException extends Exception {

	public MockISOException() {
	}

	public MockISOException(String message) {
		super(message);
	}

	public MockISOException(Throwable cause) {
		super(cause);
	}

	public MockISOException(String message, Throwable cause) {
		super(message, cause);
	}

	public MockISOException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
