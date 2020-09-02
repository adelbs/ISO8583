package org.adelbs.iso8583.vo;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.adelbs.iso8583.constants.EncodingEnum;

@XmlRootElement(name="message")
@XmlType(propOrder={"type", "bitmatEncoding", "fieldList"})
public class MessageVO extends GenericIsoVO {

	private ArrayList<FieldVO> fieldList = new ArrayList<FieldVO>();

	private String type;
	private EncodingEnum bitmatEncoding;
    
    //Valor a ser populado durante a execucao do ISO
    private String header;
    private Integer headerSize;
    private EncodingEnum headerEncoding;
    
    private String TPDUValue;

    public MessageVO() {
    }

    /**
     * @return the headerSize
     */
    @XmlTransient
    public Integer getHeaderSize() {
        return headerSize;
    }

    /**
     * @param headerSize the headerSize to set
     */
    public void setHeaderSize(Integer headerSize) {
        this.headerSize = headerSize;
    }

    /**
     * @return the headerEncoding
     */
    @XmlTransient
    public EncodingEnum getHeaderEncoding() {
        return headerEncoding;
    }

    /**
     * @param headerEncoding the headerEncoding to set
     */
    public void setHeaderEncoding(EncodingEnum headerEncoding) {
        this.headerEncoding = headerEncoding;
    }

    /**
     * @return the header
     */
    @XmlTransient
    public String getHeader() {
        return (header == null ? "" : header);
    }

    /**
     * @param header the header to set
     */
    public void setHeader(String header) {
        this.header = header;
    }
    
    
    /**
     * @return the TPDUValue
     */
    @XmlTransient
    public String getTPDUValue() {
    	if(TPDUValue==null)
    		TPDUValue="";
    	
        return TPDUValue;
    }
    
    /**
     * @param TPDUValue the TPDUValue to set
     */
    public void setTPDUValue(String TPDUValue) {
        this.TPDUValue = TPDUValue;
    }
    
    /**
     * @return the TPDUResponseValue
     */
    @XmlTransient
    public String getTPDUResponseValue() {
    	System.out.println("TPDU:"+ TPDUValue);
    	return TPDUValue.substring(0,2).concat(TPDUValue.substring(6,10)).concat(TPDUValue.substring(2,6));
    }

    public MessageVO(String type, EncodingEnum bitmatEncoding) {
		this.type = type;
		this.bitmatEncoding = bitmatEncoding;
	}

	/**
	 * Returns a copy of this instance. It copies all values of all attributes to the new instance (copy).
	 * @return
	 */
	public MessageVO getInstanceCopy() {
		MessageVO newMessageVO = new MessageVO(type, bitmatEncoding);
		
		newMessageVO.setFieldList(new ArrayList<FieldVO>());
		for (FieldVO fieldVO : fieldList)
			newMessageVO.getFieldList().add(fieldVO.getInstanceCopy());
        
        newMessageVO.setHeader(getHeader());
        newMessageVO.setHeaderEncoding(getHeaderEncoding());
        newMessageVO.setHeaderSize(getHeaderSize());
        
        newMessageVO.setTPDUValue(getTPDUValue());
        
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
		return type;
	}

	@XmlAttribute(name="bitmap-encoding")
	public EncodingEnum getBitmatEncoding() {
		return bitmatEncoding;
	}

	public void setBitmatEncoding(EncodingEnum bitmatEncoding) {
		this.bitmatEncoding = bitmatEncoding;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((bitmatEncoding == null) ? 0 : bitmatEncoding.hashCode());
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
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
}
