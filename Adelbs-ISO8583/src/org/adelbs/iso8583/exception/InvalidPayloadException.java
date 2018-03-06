package org.adelbs.iso8583.exception;

public class InvalidPayloadException extends Exception {

	private static final long serialVersionUID = 2L;

	public InvalidPayloadException(String message, Exception cause) {
		super(message, cause);
	}

}
