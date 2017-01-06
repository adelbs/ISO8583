package org.adelbs.iso8583.vo;

import java.util.ArrayList;

import org.adelbs.iso8583.constants.EncodingEnum;
import org.adelbs.iso8583.constants.TypeEnum;
import org.adelbs.iso8583.constants.TypeLengthEnum;
import org.adelbs.iso8583.gui.FrmMain;

public class FieldVO extends GenericIsoVO {

	private String name;
	
	private String subFieldName;
	
	private Integer bitNum;
	
	private TypeEnum type;
	
	private TypeLengthEnum typeLength;
	
	private Integer length;
	
	private EncodingEnum encoding;
	
	private String dynaCondition;
	
	//Valor a ser populado durante a execução do ISO
	private boolean isPresent = false;
	private String typeValue = "";
	private String lenValue = "";
	private String value = "";
	
	private ArrayList<FieldVO> fieldList = new ArrayList<FieldVO>();

	public FieldVO(String name, String subFieldName, Integer bitNum, TypeEnum type, TypeLengthEnum typeLength, Integer length, EncodingEnum encoding, String dynaCondition) {
		this.name = name;
		this.subFieldName = subFieldName;
		this.bitNum = bitNum;
		this.type = type;
		this.length = length;
		this.typeLength = typeLength;
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
		return (FrmMain.getInstance().getPnlGuiConfig().isShowBitNum() ? "[" + bitNum + "] " : "") + fieldName + getValidationMessage();
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

	public TypeLengthEnum getTypeLength() {
		return typeLength;
	}

	public void setTypeLength(TypeLengthEnum typeLength) {
		this.typeLength = typeLength;
	}

	public boolean isPresent() {
		return isPresent;
	}

	public void setPresent(boolean isPresent) {
		this.isPresent = isPresent;
	}

	public String getTypeValue() {
		return typeValue;
	}

	public void setTypeValue(String typeValue) {
		this.typeValue = typeValue;
	}

	public String getLenValue() {
		return lenValue;
	}

	public void setLenValue(String lenValue) {
		this.lenValue = lenValue;
	}
	
	//********** runtime
	
	public String getPayloadValue() {
		return getPayloadValue(null);
	}
	
	public String getPayloadValue(FieldVO superFieldVO) {
		String payload = "";

		String newValue = value;
		int sizeTypeTLV = 2;
		
		if (type != TypeEnum.TLV) {
			length = (fieldList.size() > 0) ? 0 : length;
			for (FieldVO fieldVO : fieldList)
				newValue += fieldVO.getPayloadValue(this);
		}
		else if (type == TypeEnum.TLV) {
			if (superFieldVO == null) {
				sizeTypeTLV = 2;
				length = 3;
			}
			else {
				sizeTypeTLV = Integer.parseInt(superFieldVO.getValue().substring(0, 1));
				length = Integer.parseInt(superFieldVO.getValue().substring(1, 2));
			}
		}
		
		if (type == TypeEnum.ALPHANUMERIC)
			payload = getPayloadValue(typeLength, newValue, length);
		else if (type == TypeEnum.TLV) {
			String size = getMaxSizeStr(lenValue, length);

			typeValue = (typeValue == null) ? "" : typeValue;
			payload = getMaxSizeStr(typeValue, sizeTypeTLV);
			payload += size;
			payload += getPayloadValue(TypeLengthEnum.FIXED, newValue, Integer.parseInt(size));
		}
		
		if (superFieldVO != null && superFieldVO.getType() != TypeEnum.TLV)
			superFieldVO.setLength(superFieldVO.getLength().intValue() + payload.length());
		
		if (type == TypeEnum.TLV && fieldList.size() > 0) {
			for (FieldVO fieldVO : fieldList)
				payload += fieldVO.getPayloadValue(this);
		}
		
		return payload;
	}
	
	private String getPayloadValue(TypeLengthEnum typeLength, String value, int length) {
		String payload = "";
		String size;
		int maxSize = length;

		if (typeLength == TypeLengthEnum.NVAR) {
			size = getMaxSizeStr(String.valueOf(value.length()), length);
			payload = size;
			maxSize = Integer.parseInt(size);
		}
		
		if (type == TypeEnum.TLV)
			payload += getMaxSizeStr(value, maxSize);
		else
			payload += getMaxSpacesValue(value, maxSize);
		
		return payload;
	}

	private String getMaxSizeStr(String value, int numBits) {
		String maxSize = value;
		if (maxSize.length() > numBits) {
			maxSize = "";
			for (int i = 0; i < numBits; i++)
				maxSize += "9";
		}
		else {
			while (maxSize.length() < numBits)
				maxSize = "0" + maxSize;
		}
		
		return maxSize;
	}
	
	private String getMaxSpacesValue(String value, int maxLen) {
		String result = "";
		
		if (value.length() < maxLen) {
			result = value;
			for (int i = 0; i < (maxLen - value.length()); i++)
				result += " ";
		}
		else {
			result = value.substring(0, maxLen);
		}
		
		return result;
	}
}
