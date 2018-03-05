package org.adelbs.iso8583.exception;

public class FieldNotFoundException extends ParseException {

	private static final long serialVersionUID = 2L;

	public FieldNotFoundException(String message) {
		super(message);
	}
}
