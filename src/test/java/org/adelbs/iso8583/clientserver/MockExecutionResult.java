package org.adelbs.iso8583.clientserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.adelbs.iso8583.vo.MessageVO;

public class MockExecutionResult {
	
	private List<MessageVO> messagesSent = new ArrayList<MessageVO>();
	private List<MessageVO> messagesReceived = new ArrayList<MessageVO>();

	public MockExecutionResult() {}

	public List<MessageVO> getMessagesSent() {
		return Collections.unmodifiableList(messagesSent);
	}

	public List<MessageVO> getMessagesReceived() {
		return Collections.unmodifiableList(messagesReceived);
	}
	
	public void addMessageSent(final MessageVO messageVO){
		this.messagesSent.add(messageVO);
	}
	
	public void addMessageReceived(final MessageVO messageVO){
		this.messagesReceived.add(messageVO);
	}
}
