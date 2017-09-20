package org.adelbs.iso8583.constants;

public enum NodeValidationError {

	RESERVED_BIT_NUMBER("Reserved bit number"),
	INVALID_CONDITION("Invalid dynamic condition"),
	INVALID_BIT_NUMBER("Invalid bit number"),
	DUPLICATED_BIT("Duplicated bit number"),
	DUPLICATED_MESSAGE_TYPE("Duplicated message type"),
	CHILD_ERRORS("There are errors at some child node");
	
	private String message;
	
	NodeValidationError(String message) {
		this.message = message;
	}
	
	public String toString() {
		return message;
	}
}
