package org.adelbs.iso8583.vo;

import java.util.ArrayList;

import org.adelbs.iso8583.constants.EncodingEnum;
import org.adelbs.iso8583.constants.TypeEnum;
import org.adelbs.iso8583.constants.TypeLengthEnum;
import org.adelbs.iso8583.gui.PnlMain;
import org.adelbs.iso8583.util.ISOUtils;

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
	private String tlvType = "";
	private String tlvLength = "";
	private String value = "";
	
	private ArrayList<FieldVO> fieldList = new ArrayList<FieldVO>();

	private PnlMain pnlMain;
	
	public FieldVO(PnlMain pnlMain, String name, String subFieldName, Integer bitNum, TypeEnum type, TypeLengthEnum typeLength, Integer length, EncodingEnum encoding, String dynaCondition) {
		this.name = name;
		this.subFieldName = subFieldName;
		this.bitNum = bitNum;
		this.type = type;
		this.length = length;
		this.typeLength = typeLength;
		this.encoding = encoding;
		this.dynaCondition = dynaCondition;
		this.pnlMain = pnlMain;
	}
	
	public FieldVO getInstanceCopy() {
		FieldVO newFieldVO = new FieldVO(pnlMain, name, subFieldName, bitNum, type, typeLength, length, encoding, dynaCondition);
		newFieldVO.setPresent(isPresent);
		newFieldVO.setTlvType(tlvType);
		newFieldVO.setTlvLength(tlvLength);
		newFieldVO.setValue(value);

		newFieldVO.setFieldList(new ArrayList<FieldVO>());
		for (FieldVO fieldVO : fieldList)
			newFieldVO.getFieldList().add(fieldVO.getInstanceCopy());
		
		return newFieldVO;
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
		return (pnlMain != null && pnlMain.getPnlGuiConfig().isShowBitNum() ? "[" + bitNum + "] " : "") + fieldName + getValidationMessage();
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

	public String getTlvType() {
		return tlvType;
	}

	public void setTlvType(String tlvType) {
		this.tlvType = tlvType;
	}

	public String getTlvLength() {
		return tlvLength;
	}

	public void setTlvLength(String tlvLength) {
		this.tlvLength = tlvLength;
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
				sizeTypeTLV = (superFieldVO.getValue().equals("") ? 0 : Integer.parseInt(superFieldVO.getValue().substring(0, 1)));
				length = (superFieldVO.getValue().equals("") ? 0 : Integer.parseInt(superFieldVO.getValue().substring(1, 2)));
			}
		}
		
		if (type == TypeEnum.ALPHANUMERIC)
			payload = getPayloadValue(typeLength, newValue, length);
		else if (type == TypeEnum.TLV) {
			String size = getMaxSizeStr(tlvLength, length);

			tlvType = (tlvType == null) ? "" : tlvType;
			payload = getMaxSizeStr(tlvType, sizeTypeTLV);
			payload += size;
			payload += getPayloadValue(TypeLengthEnum.FIXED, newValue, (size.equals("") ? 0 : Integer.parseInt(size)));
		}
		
		if (superFieldVO != null && superFieldVO.getType() != TypeEnum.TLV)
			superFieldVO.setLength(superFieldVO.getLength().intValue() + payload.length());
		
		if (type == TypeEnum.TLV && fieldList.size() > 0) {
			for (FieldVO fieldVO : fieldList)
				payload += fieldVO.getPayloadValue(this);
			
			if (superFieldVO == null)
				payload = getMaxSizeStr(String.valueOf(payload.length()), 3) + payload;
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
	
	
	public int setValueFromPayload(byte[] payload, int startPosition) {
		int endPosition = startPosition;
		String newContent = "";
		
		if (type != TypeEnum.TLV && fieldList.size() > 0) {
			for (FieldVO fieldVO : fieldList)
				endPosition = fieldVO.setValueFromPayload(payload, endPosition);
		}
		else {
			if (type == TypeEnum.ALPHANUMERIC) {
				if (typeLength == TypeLengthEnum.FIXED) {
					endPosition = startPosition + length;
					newContent = new String(ISOUtils.subArray(payload, startPosition, endPosition));
				}
				else if (typeLength == TypeLengthEnum.NVAR) {
					String strVarValue = new String(ISOUtils.subArray(payload, startPosition, startPosition + length));
					int nVarValue = Integer.valueOf(strVarValue);
					endPosition = startPosition + strVarValue.length() + nVarValue;
	
					newContent = new String(ISOUtils.subArray(payload, startPosition, endPosition));
					newContent = newContent.substring(String.valueOf(nVarValue).length());
				}
				
				value = newContent;
			}
			else if (type == TypeEnum.TLV) {
				String strVarValue = new String(ISOUtils.subArray(payload, startPosition, startPosition + 3));
				int nVarValue = Integer.valueOf(strVarValue);
				int endTlvPosition = startPosition + strVarValue.length() + nVarValue;
				
				endPosition = populateTLVFromPayload(2, 3, payload, startPosition + 3);
				fieldList = new ArrayList<FieldVO>();
				FieldVO tlvSubfield;
				int count = 1;
				while (endPosition < endTlvPosition) {
					tlvSubfield = getInstanceCopy();
					tlvSubfield.setFieldList(new ArrayList<FieldVO>());
					tlvSubfield.setBitNum(count++);
					endPosition = tlvSubfield.populateTLVFromPayload(Integer.parseInt(value.substring(0, 1)), Integer.parseInt(value.substring(1, 2)), payload, endPosition);
					fieldList.add(tlvSubfield);
				}
			}
		}
		
		return endPosition;
	}
	
	public int populateTLVFromPayload(int lenType, int lenLen, byte[] payload, int startPosition) {
		int endPosition = startPosition + lenType;
		tlvType = new String(ISOUtils.subArray(payload, startPosition, endPosition));
		
		endPosition = endPosition + lenLen;
		tlvLength = new String(ISOUtils.subArray(payload, startPosition + lenType, endPosition));

		endPosition = endPosition + Integer.parseInt(tlvLength);
		value = new String(ISOUtils.subArray(payload, startPosition + lenType + lenLen, endPosition));
		
		return endPosition;
	}
}
