package org.adelbs.iso8583.vo;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.adelbs.iso8583.constants.EncodingEnum;



@XmlRootElement(name="message")
@XmlType(propOrder={"type", "headerEncoding", "bitmatEncoding", "fieldList"})
public class MessageVO extends GenericIsoVO {

	private ArrayList<FieldVO> fieldList = new ArrayList<FieldVO>();

	private String type;
	private EncodingEnum bitmatEncoding;
	private EncodingEnum headerEncoding;
	
	public MessageVO(){}
	
	public MessageVO(String type, EncodingEnum bitmatEncoding, EncodingEnum headerEncoding) {
		this.type = type;
		this.bitmatEncoding = bitmatEncoding;
		this.headerEncoding = headerEncoding;
	}

	/**
	 * Returns a copy of this instance. It copies all values of all attributes to the new instance (copy).
	 * @return
	 */
	public MessageVO getInstanceCopy() {
		MessageVO newMessageVO = new MessageVO(type, bitmatEncoding, headerEncoding);
		
		newMessageVO.setFieldList(new ArrayList<FieldVO>());
		for (FieldVO fieldVO : fieldList)
			newMessageVO.getFieldList().add(fieldVO.getInstanceCopy());
		
		return newMessageVO;
	}
	
	@XmlElement(name="field")
	public ArrayList<FieldVO> getFieldList() {
		return fieldList;
	}

	public void setFieldList(ArrayList<FieldVO> fieldList) {
		this.fieldList = fieldList;
	}

	@XmlAttribute
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String toString() {
		return type + getValidationMessage();
	}

	@XmlAttribute(name="bitmap-encoding")
	public EncodingEnum getBitmatEncoding() {
		return bitmatEncoding;
	}

	public void setBitmatEncoding(EncodingEnum bitmatEncoding) {
		this.bitmatEncoding = bitmatEncoding;
	}

	@XmlAttribute(name="header-encoding")
	public EncodingEnum getHeaderEncoding() {
		return headerEncoding;
	}

	public void setHeaderEncoding(EncodingEnum headerEncoding) {
		this.headerEncoding = headerEncoding;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((bitmatEncoding == null) ? 0 : bitmatEncoding.hashCode());
		result = prime * result + ((headerEncoding == null) ? 0 : headerEncoding.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		MessageVO other = (MessageVO) obj;
		if (bitmatEncoding != other.bitmatEncoding)
			return false;
		if (headerEncoding != other.headerEncoding)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
}
