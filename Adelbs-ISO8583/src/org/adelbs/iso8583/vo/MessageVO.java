package org.adelbs.iso8583.vo;

import java.util.ArrayList;

import org.adelbs.iso8583.constants.EncodingEnum;

public class MessageVO extends GenericIsoVO {

	private ArrayList<FieldVO> fieldList = new ArrayList<FieldVO>();

	private String type;
	private EncodingEnum bitmatEncoding;

	public MessageVO(String type, EncodingEnum bitmatEncoding) {
		this.type = type;
		this.bitmatEncoding = bitmatEncoding;
	}
	
	public ArrayList<FieldVO> getFieldList() {
		return fieldList;
	}

	public void setFieldList(ArrayList<FieldVO> fieldList) {
		this.fieldList = fieldList;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String toString() {
		return type + getValidationMessage();
	}

	public EncodingEnum getBitmatEncoding() {
		return bitmatEncoding;
	}

	public void setBitmatEncoding(EncodingEnum bitmatEncoding) {
		this.bitmatEncoding = bitmatEncoding;
	}
}
