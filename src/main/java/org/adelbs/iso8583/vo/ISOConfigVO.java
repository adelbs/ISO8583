package org.adelbs.iso8583.vo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.adelbs.iso8583.constants.DelimiterEnum;
import org.adelbs.iso8583.constants.EncodingEnum;

/**
 * Representation of a ISO8583 Config.
 */
@XmlRootElement(name="iso8583")
//@XmlType(propOrder={"delimiter", "headerEncoding", "headerSize", "tpdu", "messageList"})
@XmlType(propOrder={"delimiter", "headerEncoding", "headerSize", "messageList"})
public class ISOConfigVO {
	
    private DelimiterEnum delimiter;
    private EncodingEnum headerEncoding;
    private Integer headerSize;
    
    private boolean TPDU;
    
    private final List<MessageVO> messageList = new ArrayList<MessageVO>();
    
    public ISOConfigVO() {
    }


    /**
     * Create a new instance of a ISOConfig for a specific Delimiter
     * 
     * @param delimiter
     */
	public ISOConfigVO(DelimiterEnum delimiter) {
		super();
		this.delimiter = delimiter;
	}

	@XmlAttribute(name="delimiter")
	public DelimiterEnum getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(DelimiterEnum delimiter) {
		this.delimiter = delimiter;
	}
	
	/**
     * @return the headerEncoding
     */
    @XmlAttribute(name="headerEncoding")
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
     * @return the headerSize
     */
    @XmlAttribute(name="headerSize")
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
     * @return the TPDU
     */
    //@XmlAttribute(name="tpdu")
    public boolean TPDU() {
        return TPDU;
    }
	
	/**
     * @param TPDU the TPDU to set
     */
	public void setTPDU(boolean TPDU) {
		this.TPDU=TPDU;		
	}


	@XmlElement(name="message")
	public List<MessageVO> getMessageList() {
		return Collections.unmodifiableList(messageList);
	}
	
	/**
	 * Add a {@link MessageVO} to the list of messages of this ISOConfig
	 * @param message to be included as element of the ISO Config
	 */
	public void addMessage(final MessageVO message){
		messageList.add(message);
	}
	
	/**
	 * Add a list of {@link MessageVO} to the messages of this ISOConfig
	 * @param messages List of messages to be included as elements of the ISO Config
	 */
	public void addAllMessages(final List<MessageVO> messages){
		messageList.addAll(messages);
	}

	@Override
	public String toString() {
		return "iso8583 "+delimiter.toString();
	}
	
	
}
