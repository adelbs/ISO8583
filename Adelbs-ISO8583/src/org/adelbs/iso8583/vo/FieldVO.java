package org.adelbs.iso8583.vo;

import java.util.ArrayList;

import org.adelbs.iso8583.constants.EncodingEnum;
import org.adelbs.iso8583.constants.TypeEnum;
import org.adelbs.iso8583.gui.PnlGuiConfig;

public class FieldVO extends GenericIsoVO {

	private String name;
	
	private String subFieldName;
	
	private Integer bitNum;
	
	private TypeEnum type;
	
	private Integer length;
	
	private EncodingEnum encoding;
	
	private String dynaCondition;
	
	//Valor a ser populado durante a execução do ISO
	private String value;
	
	private ArrayList<FieldVO> fieldList = new ArrayList<FieldVO>();

	public FieldVO(String name, String subFieldName, Integer bitNum, TypeEnum type, Integer length, EncodingEnum encoding, String dynaCondition) {
		this.name = name;
		this.subFieldName = subFieldName;
		this.bitNum = bitNum;
		this.type = type;
		this.length = length;
		this.encoding = encoding;
		this.dynaCondition = dynaCondition;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getBitNum() {
		return bitNum;
	}

	public void setBitNum(Integer bitNum) {
		this.bitNum = bitNum;
	}

	public TypeEnum getType() {
		return type;
	}

	public void setType(TypeEnum type) {
		this.type = type;
	}

	public String getDynaCondition() {
		return dynaCondition;
	}

	public void setDynaCondition(String dynaCondition) {
		this.dynaCondition = dynaCondition;
	}

	public ArrayList<FieldVO> getFieldList() {
		return fieldList;
	}

	public void setFieldList(ArrayList<FieldVO> fieldList) {
		this.fieldList = fieldList;
	}
	
	public String toString() {
		String fieldName = (subFieldName != null && !subFieldName.equals("")) ? subFieldName: name;
		return (PnlGuiConfig.isShowBitNum() ? "[" + bitNum + "] " : "") + fieldName + getValidationMessage();
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public EncodingEnum getEncoding() {
		return encoding;
	}

	public void setEncoding(EncodingEnum encoding) {
		this.encoding = encoding;
	}

	public String getSubFieldName() {
		return subFieldName;
	}

	public void setSubFieldName(String subFieldName) {
		this.subFieldName = subFieldName;
	}
}
