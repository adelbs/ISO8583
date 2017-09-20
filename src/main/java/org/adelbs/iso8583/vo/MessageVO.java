package org.adelbs.iso8583.vo;

import java.util.ArrayList;

import org.adelbs.iso8583.constants.EncodingEnum;

public class MessageVO extends GenericIsoVO {

	private ArrayList<FieldVO> fieldList = new ArrayList<FieldVO>();

	private String type;
	private EncodingEnum bitmatEncoding;
	private EncodingEnum headerEncoding;
	
	public MessageVO(String type, EncodingEnum bitmatEncoding, EncodingEnum headerEncoding) {
		this.type = type;
		this.bitmatEncoding = bitmatEncoding;
		this.headerEncoding = headerEncoding;
	}

	public MessageVO getInstanceCopy() {
		MessageVO newMessageVO = new MessageVO(type, bitmatEncoding, headerEncoding);
		
		newMessageVO.setFieldList(new ArrayList<FieldVO>());
		for (FieldVO fieldVO : fieldList)
			newMessageVO.getFieldList().add(fieldVO.getInstanceCopy());
		
		return newMessageVO;
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

	public EncodingEnum getHeaderEncoding() {
		return headerEncoding;
	}

	public void setHeaderEncoding(EncodingEnum headerEncoding) {
		this.headerEncoding = headerEncoding;
	}
}
