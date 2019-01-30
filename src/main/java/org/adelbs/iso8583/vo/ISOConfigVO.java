package org.adelbs.iso8583.vo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.adelbs.iso8583.constants.DelimiterEnum;

/**
 * Representation of a ISO8583 Config.
 */
@XmlRootElement(name="iso8583")
public class ISOConfigVO {
	
	private DelimiterEnum delimiter;
	private final List<MessageVO> messageList = new ArrayList<MessageVO>();
	
	public ISOConfigVO(){}
	
	/**
	 * Create a new instance of a ISOConfig for a specific Delimiter
	 * @param delimiter
	 */
	public ISOConfigVO(DelimiterEnum delimiter) {
		super();
		this.delimiter = delimiter;
	}

	@XmlAttribute
	public DelimiterEnum getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(DelimiterEnum delimiter) {
		this.delimiter = delimiter;
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
		// TODO Auto-generated method stub
		return "iso8583 "+delimiter.toString();
	}
	
	
}
