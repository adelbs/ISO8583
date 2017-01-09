package org.adelbs.iso8583.protocol;

import org.adelbs.iso8583.vo.FieldVO;
import org.adelbs.iso8583.vo.MessageVO;

import groovyjarjarcommonscli.ParseException;

public class ISOMessage {

	private int messageSize;
	private byte[] payload;
	private StringBuilder visualPayload = new StringBuilder();

	private Bitmap bitmap;
	
	public ISOMessage(MessageVO messageVO) throws ParseException {
		this(null, messageVO);
	}
	
	public ISOMessage(byte[] payload, MessageVO messageVO) throws ParseException {
		if (payload != null)
			bitmap = new Bitmap(payload, messageVO);
		else
			bitmap = new Bitmap(messageVO);
		
		StringBuilder strMessage = new StringBuilder();
		strMessage.append(messageVO.getType());
		strMessage.append(bitmap.getPayloadBitmap());
		
		visualPayload.append("Message Type: [").append(messageVO.getType()).append("]\n");
		visualPayload.append("Bitmap: [").append(bitmap.getPayloadBitmap()).append("]\n\n");
		
		for (int i = 0; i <= bitmap.getSize(); i++) {
			if (bitmap.getBit(i) != null) {
				strMessage.append(bitmap.getBit(i).getPayloadValue());
				visualPayload.append("Bit").append(i).append(": [").append(bitmap.getBit(i).getPayloadValue()).append("]\n");
			}
		}
		
		visualPayload.append("\nEntire message: [").append(strMessage).append("]\n");
		
		this.messageSize = strMessage.length();
		this.payload = strMessage.toString().getBytes();
	}
	
	public String getVisualPayload() {
		return visualPayload.toString();
	}
	
	public byte[] getPayload() {
		return payload;
	}
	
	public FieldVO getBit(int bit) {
		return bitmap.getBit(bit);
	}
	
	public int getMessageSize() {
		return messageSize;
	}
	
	public String getMessageSize(int numChars) {
		String result = String.valueOf(messageSize);
		while (result.length() < numChars)
			result = "0" + result;
		return result;
	}
}
