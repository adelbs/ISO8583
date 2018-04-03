package org.adelbs.iso8583.protocol;

import org.adelbs.iso8583.exception.ParseException;
import org.adelbs.iso8583.util.ISOUtils;
import org.adelbs.iso8583.vo.FieldVO;
import org.adelbs.iso8583.vo.MessageVO;


public class ISOMessage {

	private int messageSize;
	private byte[] payload;

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
		
		this.payload = bitmap.getHeaderEncoding().convert(messageVO.getType());
		this.payload = ISOUtils.mergeArray(this.payload, bitmap.getPayloadBitmap());
		
		for (int i = 0; i <= bitmap.getSize(); i++) {
			if (bitmap.getBit(i) != null) {
				this.payload = ISOUtils.mergeArray(this.payload, bitmap.getBit(i).getPayloadValue());
				strMessage.append(bitmap.getBit(i).getPayloadValue());
			}
		}
		
		this.messageSize = strMessage.length();
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
	
	public MessageVO getMessageVO() {
		return bitmap.getMessageVO();
	}
}
