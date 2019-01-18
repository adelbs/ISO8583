package org.adelbs.iso8583.vo;

import java.util.ArrayList;
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
	
	public ArrayList<FieldVO> getFieldList() {
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((errorList == null) ? 0 : errorList.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GenericIsoVO other = (GenericIsoVO) obj;
		if (errorList == null) {
			if (other.errorList != null)
				return false;
		} else if (!errorList.equals(other.errorList))
			return false;
		return true;
	}
}
