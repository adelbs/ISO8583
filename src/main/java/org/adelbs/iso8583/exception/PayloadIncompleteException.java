package org.adelbs.iso8583.exception;

public class PayloadIncompleteException extends ParseException {

	private static final long serialVersionUID = 2L;

	private int lastParsedBit = 0;
	
	public PayloadIncompleteException(String message, int lastParsedBit) {
		super(message + " (last parsed bit: "+ lastParsedBit +")");
		this.lastParsedBit  = lastParsedBit;
	}

	public int getLastParsedBit() {
		return lastParsedBit;
	}

}
