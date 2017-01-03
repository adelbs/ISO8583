package org.adelbs.iso8583.protocol;

import org.adelbs.iso8583.vo.MessageVO;

public class RawMessage {

	private MessageVO messageVO;
	
	private byte[] payload;
	
	private String visualPayload;
	
	public RawMessage(MessageVO messageVO) {
		this(null, messageVO);
	}
	
	public RawMessage(byte[] payload, MessageVO messageVO) {
		this.messageVO = messageVO;
		
		StringBuilder strMessage = new StringBuilder();
		strMessage.append(messageVO.getType());
		
	}
	
}
