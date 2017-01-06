package org.adelbs.iso8583.protocol;

import org.adelbs.iso8583.vo.MessageVO;

public class ISOMessage {

	private byte[] payload;
	private StringBuilder visualPayload = new StringBuilder();

	private Bitmap bitmap;
	
	public ISOMessage(MessageVO messageVO) {
		this(null, messageVO);
	}
	
	public ISOMessage(byte[] payload, MessageVO messageVO) {
		bitmap = new Bitmap(messageVO);
		
		StringBuilder strMessage = new StringBuilder();
		strMessage.append(messageVO.getType());
		strMessage.append(bitmap.getPayloadBitmap());
		
		visualPayload.append("Message Type: [").append(messageVO.getType()).append("]\n");
		visualPayload.append("Bitmap: [").append(bitmap.getPayloadBitmap()).append("]\n\n");
		
		for (int i = 0; i < bitmap.getSize(); i++) {
			if (bitmap.getBit(i) != null) {
				strMessage.append(bitmap.getBit(i).getPayloadValue());
				visualPayload.append("Bit").append(i).append(": [").append(bitmap.getBit(i).getPayloadValue()).append("]\n");
			}
		}
		
		payload = strMessage.toString().getBytes();
	}
	
	public String getVisualPayload() {
		return visualPayload.toString();
	}
	
	public byte[] getPayload() {
		return payload;
	}
	
}
