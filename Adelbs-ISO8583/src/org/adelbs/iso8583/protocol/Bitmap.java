package org.adelbs.iso8583.protocol;

import java.util.ArrayList;
import java.util.HashMap;

import org.adelbs.iso8583.constants.TypeEnum;
import org.adelbs.iso8583.constants.TypeLengthEnum;
import org.adelbs.iso8583.exception.FieldNotFoundException;
import org.adelbs.iso8583.exception.OutOfBoundsException;
import org.adelbs.iso8583.exception.ParseException;
import org.adelbs.iso8583.exception.PayloadIncompleteException;
import org.adelbs.iso8583.util.Encoding;
import org.adelbs.iso8583.util.ISOUtils;
import org.adelbs.iso8583.vo.FieldVO;
import org.adelbs.iso8583.vo.MessageVO;


public class Bitmap {

	private HashMap<Integer, FieldVO> bitmap = new HashMap<Integer, FieldVO>();
	private String binaryBitmap = "";
	private int totalBits = 128;
	
	private byte[] payloadBitmap;
	
	private MessageVO messageVO;
	private StringBuilder visualPayload = new StringBuilder();
	
	/**
	 * Builds the bitmap and the values of each bit from the payload.
	 * @param payload
	 * @param messageVO
	 * @throws ParseException
	 */
	public Bitmap(byte[] payload, MessageVO messageVO) throws ParseException {
		
		this.messageVO = messageVO.getInstanceCopy();
		
		String tempBitmap1 = "";
		String tempBitmap2 = "";
		int headerSize = 4;
		int bitmapSize = 0;
		
		//This try block will extract the bitmap from the payload
		try {
			visualPayload.append("Message Type: [").append(messageVO.getType()).append("]\n");
			
			bitmapSize = messageVO.getBitmatEncoding().getMinBitmapSize();
			tempBitmap1 = messageVO.getBitmatEncoding().convertBitmap(ISOUtils.subArray(payload, headerSize, headerSize + bitmapSize));
			
			if (tempBitmap1.substring(0, 1).equals("1")) {
				tempBitmap2 = messageVO.getBitmatEncoding().convertBitmap(ISOUtils.subArray(payload, headerSize + bitmapSize, headerSize + (bitmapSize * 2)));
				bitmapSize = bitmapSize * 2;
			}
			
			binaryBitmap = tempBitmap1 + tempBitmap2;
	
			payloadBitmap = messageVO.getBitmatEncoding().convert(binaryBitmap.substring(0, bitmapSize));
			visualPayload.append("Bitmap: [").append(new String(payloadBitmap)).append("]\n\n");
		}
		catch (OutOfBoundsException x) {
			throw new PayloadIncompleteException("Error trying to parse the Bitmap from payload. Payload incomplete.", 0);
		}
		catch (Exception x) {
			throw new ParseException("Error parsing the bitmap.\n" + x.getMessage() + "\n" + visualPayload);
		}

		//The next try block will extract the values of each bit field from the payload
		int bitNum = 1;
		try {
			this.messageVO.setFieldList(new ArrayList<FieldVO>());
			int startPosition = headerSize + bitmapSize;
			for (; bitNum < binaryBitmap.length(); bitNum++) {
				if (binaryBitmap.substring(bitNum, bitNum + 1).equals("1")) {
					FieldVO foundFieldVO = null;
					for (FieldVO fieldVO : messageVO.getFieldList()) {
						if (fieldVO.getBitNum().intValue() == (bitNum + 1)) {
							foundFieldVO = fieldVO.getInstanceCopy();
							break;
						}
					}
					
					if (foundFieldVO == null) {
						throw new FieldNotFoundException("Field bit ("+ bitNum +") not found.");
					}
					else {
						startPosition = foundFieldVO.setValueFromPayload(payload, startPosition);
						bitmap.put(foundFieldVO.getBitNum(), foundFieldVO);
						bitmap.get(foundFieldVO.getBitNum()).setPresent(true);
						
						this.messageVO.getFieldList().add(foundFieldVO);
						visualPayload.append("Bit").append(bitNum).append(": [").append(foundFieldVO.getPayloadValue()).append("]\n");
					}
				}
			}
		}
		catch (OutOfBoundsException x) {
			throw new PayloadIncompleteException("Error trying to parse the fields from the payload. Payload incomplete.", bitNum + 1);
		}
		catch (Exception x) {
			throw new ParseException("Error parsing the message body.\n" + x.getMessage() + "\n" + visualPayload);
		}
	}
	
	public Bitmap(MessageVO messageVO) {
		
		this.messageVO = messageVO.getInstanceCopy();
		this.messageVO.setFieldList(new ArrayList<FieldVO>());
		visualPayload.append("Message Type: [").append(messageVO.getType()).append("]\n");
		
		for (FieldVO fieldVO : messageVO.getFieldList())
			if (fieldVO.isPresent()) {
				bitmap.put(fieldVO.getBitNum(), fieldVO.getInstanceCopy());
				bitmap.get(fieldVO.getBitNum()).setPresent(true);
				
				this.messageVO.getFieldList().add(bitmap.get(fieldVO.getBitNum()));
				visualPayload.append("Bit").append(fieldVO.getBitNum()).append(": [").append(fieldVO.getPayloadValue()).append("]\n");
			}
		
		int lastBit = 0;
		for (int i = 1; i <= 128; i++) {
			binaryBitmap = binaryBitmap.concat(bitmap.get(i) == null ? "0" : "1");
			if (bitmap.get(i) != null) lastBit = i;
		}
		
		totalBits = lastBit;
		
		payloadBitmap = messageVO.getBitmatEncoding().convertBitmap(binaryBitmap.substring(0, 64));
		visualPayload.append("Bitmap: [").append(new String(payloadBitmap)).append("]\n\n");
		
		if (lastBit > 64) {
			FieldVO secondBitmap = new FieldVO(null, "Bitmap", "", 1, TypeEnum.ALPHANUMERIC, TypeLengthEnum.FIXED, 16, messageVO.getBitmatEncoding(), "true");
			binaryBitmap = "1".concat(binaryBitmap.substring(1));
			payloadBitmap = messageVO.getBitmatEncoding().convertBitmap(binaryBitmap.substring(0, 64));
			secondBitmap.setPayloadValue(messageVO.getBitmatEncoding().convertBitmap(binaryBitmap.substring(64, 128)));
			secondBitmap.setPresent(true);
			bitmap.put(1, secondBitmap);
			binaryBitmap.substring(0, 64);
		}
	}

	public int getSize() {
		return totalBits;
	}
	
	public byte[] getPayloadBitmap() {
		return payloadBitmap;
	}
	
	public FieldVO getBit(Integer bit) {
		return bitmap.get(bit);
	}
	
	public String getVisualPayload() {
		return visualPayload.toString();
	}
	
	public MessageVO getMessageVO() {
		return messageVO;
	}

	public Encoding getHeaderEncoding() {
		return messageVO.getHeaderEncoding();
	}
	
	public Encoding getBitmapEncoding() {
		return messageVO.getBitmatEncoding();
	}
}
