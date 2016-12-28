package org.adelbs.iso8583.vo;

import java.util.HashSet;

import org.adelbs.iso8583.constants.NodeValidationError;

public abstract class GenericIsoVO {

	private HashSet<NodeValidationError> errorList = new HashSet<NodeValidationError>();
	
	public boolean isValid() {
		return errorList.size() == 0;
	}

	public void addValidationError(NodeValidationError error) {
		errorList.add(error);
	}
	
	public void removeValidationError(NodeValidationError error) {
		errorList.remove(error);
	}
	
	public String getValidationMessage() {
		String message = "";
		for (NodeValidationError error : errorList)
			message += ", "+ error.toString();
		
		message = !message.equals("") ? " ["+ message.substring(2) +"]" : "";
		return message;
	}
}
