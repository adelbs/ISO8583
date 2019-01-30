package org.adelbs.iso8583.clientserver;

import org.adelbs.iso8583.vo.MessageVO;

class MockResult {
	private MessageVO sent;
	private MessageVO received;
	
	
	public MockResult(MessageVO sent, MessageVO received) {
		super();
		this.sent = sent;
		this.received = received;
	}
	
	public MessageVO getSent() {
		return sent;
	}
	public MessageVO getReceived() {
		return received;
	}
}
