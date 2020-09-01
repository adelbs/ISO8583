package org.adelbs.iso8583.vo;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.adelbs.iso8583.constants.EncodingEnum;
import org.adelbs.iso8583.constants.TypeEnum;
import org.adelbs.iso8583.constants.TypeLengthEnum;
import org.adelbs.iso8583.exception.OutOfBoundsException;
import org.adelbs.iso8583.gui.PnlMain;
import org.adelbs.iso8583.helper.BSInterpreter;
import org.adelbs.iso8583.payload.PayloadTransformator;
import org.adelbs.iso8583.payload.RevertResult;
import org.adelbs.iso8583.util.ISOUtils;

@XmlRootElement(name="field")
@XmlType(propOrder={"name", "bitNum", "dynaCondition", "typeLength", "length", "type", "encoding", "fieldList"})
public class FieldVO extends GenericIsoVO {

	private String name;
	
	private String subFieldName;
	
	private Integer bitNum;
	
	private TypeEnum type;
	
	private TypeLengthEnum typeLength;
	
	private Integer length;
	
	private EncodingEnum encoding;
	
	private String dynaCondition;
	
	//Valor a ser populado durante a execucao do ISO
	private boolean isPresent = false;
	private String tlvType = "";
	private String tlvLength = "";
	private String value = "";
	private byte[] payloadValue = null;
	private ArrayList<FieldVO> fieldList = new ArrayList<FieldVO>();

	private PnlMain pnlMain;
	
	public FieldVO(){};
	
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
	
	@XmlAttribute
	public String getName() {
		return (subFieldName != null && !subFieldName.isEmpty()) ? subFieldName : name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute(name="bitnum")
	public Integer getBitNum() {
		return bitNum;
	}

	
	public void setBitNum(Integer bitNum) {
		this.bitNum = bitNum;
	}

	@XmlAttribute
	public TypeEnum getType() {
		return type;
	}

	
	public void setType(TypeEnum type) {
		this.type = type;
	}

	@XmlAttribute(name="condition")
	public String getDynaCondition() {
		return dynaCondition;
	}

	
	public void setDynaCondition(String dynaCondition) {
		this.dynaCondition = dynaCondition;
	}
	
	@XmlAttribute(name="encoding")
	public EncodingEnum getEncoding() {
		return encoding;
	}

	
	public void setEncoding(EncodingEnum encoding) {
		this.encoding = encoding;
	}
	
	@XmlAttribute
	public Integer getLength() {
		return length;
	}

	
	public void setLength(Integer length) {
		this.length = length;
	}
	
	@XmlAttribute(name="length-type")
	public TypeLengthEnum getTypeLength() {
		return typeLength;
	}
	
	public void setTypeLength(TypeLengthEnum typeLength) {
		this.typeLength = typeLength;
	}

	@XmlElement(name="field")
	public ArrayList<FieldVO> getFieldList() {
		return fieldList;
	}

	public void setFieldList(ArrayList<FieldVO> fieldList) {
		this.fieldList = fieldList;
	}
	
	public String toString() {
		String fieldName = (subFieldName != null && !subFieldName.equals("")) ? subFieldName: name;
		return (pnlMain != null && pnlMain.getPnlGuiConfig().isShowBitNum() ? "[" + bitNum + "] " : "") + fieldName;
	}


	@XmlTransient
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@XmlTransient
	public String getSubFieldName() {
		return subFieldName;
	}

	public void setSubFieldName(String subFieldName) {
		this.subFieldName = subFieldName;
	}

	@XmlTransient
	public boolean isPresent() {
		return isPresent;
	}

	public void setPresent(boolean isPresent) {
		this.isPresent = isPresent;
	}

	@XmlTransient
	public String getTlvType() {
		return tlvType;
	}

	public void setTlvType(String tlvType) {
		this.tlvType = tlvType;
	}

	@XmlTransient
	public String getTlvLength() {
		return tlvLength;
	}

	public void setTlvLength(String tlvLength) {
		this.tlvLength = tlvLength;
	}
	
	//********** runtime

	public void setPayloadValue(byte[] payloadValue) {
		this.payloadValue = payloadValue;
	}
	
	@XmlTransient
	public byte[] getPayloadValue() {
		if (payloadValue == null)
			payloadValue = getPayloadValue(null);
		return payloadValue;
	}
	
	public StringBuilder getXML() {
		return getXML(1);
	}
	
	private StringBuilder getXML(int depth) {
		StringBuilder xmlField = new StringBuilder();

		String tabs = "";
		for (int i = 0; i < depth; i++) tabs += "\t";
		
		xmlField.append("\n").append(tabs).append("<bit num=\"").append(getBitNum()).append("\"");

		if (getType() == TypeEnum.TLV) {
			xmlField.append(" tag=\"").append(getType()).
					append("\" length=\"").append(getLength()).append("\"").
					append(" value=\"").append(getValue()).append("\"");
		}
		
		if (fieldList.size() > 0){
			xmlField.append(">");
		}
		
		fieldList.forEach(subfield -> {
			xmlField.append(subfield.getXML(depth + 1));
		});
		
		if (fieldList.size() > 0) {
			xmlField.append("\n"+ tabs +"</bit>");
		}
		else if (getType() != TypeEnum.TLV) {
			xmlField.append(" value=\"").append(getValue()).append("\"/>");
		}
		else {
			xmlField.append("/>");
		}
		
		return xmlField;
	}
	
	private byte[] getPayloadValue(FieldVO superFieldVO) {
		byte[] payload = encoding.convert("");

		byte[] newValue = encoding.convert(value);
		
		if (type != TypeEnum.TLV) {
			length = (fieldList.size() > 0) ? 0 : length;
			for (FieldVO fieldVO : fieldList){
				newValue = ISOUtils.mergeArray(newValue, fieldVO.getPayloadValue(this));
			}
		}
		
		//TODO Se for binario, cada par representa o número do byte (decimal)
//		int newLength = length;
//		if (encoding == EncodingEnum.BINARY) newLength = newLength / 2;
		
		if (type == TypeEnum.ALPHANUMERIC){
			payload = getPayloadValue(typeLength, newValue, length);
		}
		else if (type == TypeEnum.TLV) {	
			payload = PayloadTransformator.getInstance(encoding).transform(this,  TypeEnum.TLV);
		}
		
		if (superFieldVO != null && superFieldVO.getType() != TypeEnum.TLV){
			superFieldVO.setLength(superFieldVO.getLength().intValue() + payload.length);
		}
		
		if (type == TypeEnum.TLV && fieldList.size() > 0) {
			for (FieldVO fieldVO : fieldList){
				payload = ISOUtils.mergeArray(payload, fieldVO.getPayloadValue(this));
			}
			
			if (superFieldVO == null){
				payload = ISOUtils.mergeArray(encoding.convert(getMaxSizeStr(String.valueOf(payload.length), 3)), payload);
			}
		}
		
		return payload;
	}
	
	private byte[] getPayloadValue(TypeLengthEnum typeLength, byte[] value, int length) {
		byte[] payload = new byte[]{};
		String size;
		int maxSize = length;

		if (typeLength == TypeLengthEnum.NVAR) {
			
			if (encoding == EncodingEnum.BCD) {
				size = getMaxSizeStr(String.valueOf(value.length), length * 2);
				maxSize = Integer.parseInt(size)*2;
			}
			else {			
				size = getMaxSizeStr(String.valueOf(value.length), length);
				maxSize = Integer.parseInt(size);
			}
			
			payload = encoding.convert(size);
			
			
		}
		
		if (type == TypeEnum.TLV)
			payload = ISOUtils.mergeArray(payload, encoding.convert(getMaxSizeStr(encoding.convert(value), maxSize)));
		else {
			if (encoding == EncodingEnum.BCD && (maxSize % 2) == 1)
				maxSize++;
			
			payload = ISOUtils.mergeArray(payload, encoding.convert(getMaxSpacesValue(encoding.convert(value), maxSize)));
		}
		
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
	
	/**
	 * Extracts the value from the payload to populate the VO.
	 * It parses the payload according to the rules of each field.
	 * @param payload
	 * @param startPosition
	 * @return
	 */	
	public int setValueFromPayload(byte[] payload, int startPosition, BSInterpreter bsInt, String preSnipet, boolean ignore) throws OutOfBoundsException {
		int endPosition = startPosition;
		String newContent = "";
		
		String snipetBS = preSnipet.equals("") ? "BIT" : preSnipet;
		String currentObjBS = "";
		
		if (type != TypeEnum.TLV && fieldList.size() > 0) {
			
			currentObjBS = snipetBS + "["+ getBitNum() +"]";
			snipetBS += "["+ getBitNum() +"] = new Object[255];\n";
			bsInt.concatSnipet(snipetBS);
			
			ArrayList<FieldVO> newFieldList = new ArrayList<FieldVO>();
			
			for (FieldVO fieldVO : fieldList){
				if ((fieldVO.getDynaCondition().indexOf("ignore()") > -1) || bsInt.evaluate(fieldVO.getDynaCondition())) {
					endPosition = fieldVO.setValueFromPayload(payload, endPosition, bsInt, currentObjBS, (fieldVO.getDynaCondition().indexOf("ignore()") > -1));
					newFieldList.add(fieldVO);
				}
			}
			
			setFieldList(newFieldList);
		}
		else {
			if (type == TypeEnum.ALPHANUMERIC) {
				if (typeLength == TypeLengthEnum.FIXED) {
					int calculatedLength = (encoding == EncodingEnum.BCD) ? length / 2 : length;
					endPosition = startPosition + encoding.getEncondedByteLength(calculatedLength);
					
					if (!ignore) newContent = encoding.convert(ISOUtils.subArray(payload, startPosition, endPosition));
				}
				else if (typeLength == TypeLengthEnum.NVAR) {
					
					String strVarValue = encoding.convert(ISOUtils.subArray(payload, startPosition, startPosition + encoding.getEncondedByteLength(length)));
					int nVarValue = Integer.valueOf(strVarValue.trim());

					if (encoding == EncodingEnum.BCD)
						endPosition = startPosition + (strVarValue.length() / 2) + nVarValue;
					else
						endPosition = startPosition + strVarValue.length() + nVarValue;
	
					if (!ignore) {
						newContent = encoding.convert(ISOUtils.subArray(payload, startPosition, endPosition));
						
						//suppressed because the field size does not need to appear 
						//if (encoding == EncodingEnum.BCD) {
						//	newContent = newContent.substring(strVarValue.length() / 2);
						//}
						//else {
							newContent = newContent.substring(strVarValue.length());
						//}
					}
				}
				
				value = newContent;
			}
			else if (type == TypeEnum.TLV) {
				final byte[] tlvPayload = ISOUtils.subArray(payload, startPosition, payload.length);
				final RevertResult revertedValue = PayloadTransformator.getInstance(encoding).revert(tlvPayload, TypeEnum.TLV);
				
				final FieldVO revertedField = (FieldVO) revertedValue.getResultantObject();
				this.tlvLength = revertedField.tlvLength;
				this.tlvType = revertedField.tlvType;
				this.value = revertedField.value;
				
				endPosition = revertedValue.getRevertEndPosition();
			}
			
			snipetBS += "["+ getBitNum() +"] = \""+ getValue() + "\";\n";
			bsInt.concatSnipet(snipetBS);
		}
		
		return endPosition;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((bitNum == null) ? 0 : bitNum.hashCode());
		result = prime * result + ((dynaCondition == null) ? 0 : dynaCondition.hashCode());
		result = prime * result + ((encoding == null) ? 0 : encoding.hashCode());
		result = prime * result + ((fieldList == null) ? 0 : fieldList.hashCode());
		result = prime * result + ((length == null) ? 0 : length.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((subFieldName == null) ? 0 : subFieldName.hashCode());
		result = prime * result + ((tlvLength == null) ? 0 : tlvLength.hashCode());
		result = prime * result + ((tlvType == null) ? 0 : tlvType.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((typeLength == null) ? 0 : typeLength.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		FieldVO other = (FieldVO) obj;
		if (bitNum == null) {
			if (other.bitNum != null)
				return false;
		} 
		else if (!bitNum.equals(other.bitNum))
			return false;
		if (dynaCondition == null) {
			if (other.dynaCondition != null)
				return false;
		} 
		else if (!dynaCondition.equals(other.dynaCondition))
			return false;
		if (encoding != other.encoding)
			return false;
		if (fieldList == null) {
			if (other.fieldList != null)
				return false;
		} 
		else if (!fieldList.equals(other.fieldList))
			return false;
		if (length == null) {
			if (other.length != null)
				return false;
		} 
		else if (!length.equals(other.length))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} 
		else if (!name.equals(other.name))
			return false;
		if (subFieldName == null) {
			if (other.subFieldName != null)
				return false;
		} 
		else if (!subFieldName.equals(other.subFieldName))
			return false;
		if (tlvLength == null) {
			if (other.tlvLength != null)
				return false;
		} 
		else if (!tlvLength.equals(other.tlvLength))
			return false;
		if (tlvType == null) {
			if (other.tlvType != null)
				return false;
		} 
		else if (!tlvType.equals(other.tlvType))
			return false;
		if (type != other.type)
			return false;
		if (typeLength != other.typeLength)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} 
		else if (!value.equals(other.value))
			return false;
		return true;
	}
}
