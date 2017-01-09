package org.adelbs.iso8583.protocol;

import java.util.HashMap;

import org.adelbs.iso8583.constants.EncodingEnum;
import org.adelbs.iso8583.constants.TypeEnum;
import org.adelbs.iso8583.constants.TypeLengthEnum;
import org.adelbs.iso8583.util.ISOUtils;
import org.adelbs.iso8583.vo.FieldVO;
import org.adelbs.iso8583.vo.MessageVO;

import groovyjarjarcommonscli.ParseException;

public class Bitmap {

	private HashMap<Integer, FieldVO> bitmap = new HashMap<Integer, FieldVO>();
	private String binaryBitmap = "";
	private String hexaBitmap = "";
	private int totalBits = 128;
	
	private String payloadBitmap;
	
	public Bitmap(byte[] payload, MessageVO messageVO) throws ParseException {
		
		String tempBitmap1 = "";
		String tempBitmap2 = "";
		int headerSize = 4;
		int bitmapSize = 0;
		
		if (messageVO.getBitmatEncoding() == EncodingEnum.BINARY)
			bitmapSize = 64;
		else if (messageVO.getBitmatEncoding() == EncodingEnum.HEXA)
			bitmapSize = 16;
		
		tempBitmap1 = new String(ISOUtils.subArray(payload, headerSize, headerSize + bitmapSize));
		
		if (messageVO.getBitmatEncoding() == EncodingEnum.HEXA)
			tempBitmap1 = ISOUtils.hexToBin(tempBitmap1);
		
		if (tempBitmap1.substring(0, 1).equals("1")) {
			
			tempBitmap2 = new String(ISOUtils.subArray(payload, headerSize + bitmapSize, headerSize + (bitmapSize * 2)));
			
			if (messageVO.getBitmatEncoding() == EncodingEnum.HEXA)
				tempBitmap2 = ISOUtils.hexToBin(tempBitmap2);
			
			bitmapSize = bitmapSize * 2;
		}
		
		binaryBitmap = tempBitmap1 + tempBitmap2;
		int startPosition = headerSize + bitmapSize;
		for (int i = 1; i < binaryBitmap.length(); i++) {
			if (binaryBitmap.substring(i, i + 1).equals("1")) {
				FieldVO foundFieldVO = null;
				for (FieldVO fieldVO : messageVO.getFieldList()) {
					if (fieldVO.getBitNum().intValue() == (i + 1)) {
						foundFieldVO = fieldVO;
						break;
					}
				}
				
				if (foundFieldVO == null) {
					throw new ParseException("Field not found.");
				}
				else {
					startPosition = foundFieldVO.setValueFromPayload(payload, startPosition);
					bitmap.put(foundFieldVO.getBitNum(), foundFieldVO);
				}
			}
		}
		
	}
	
	public Bitmap(MessageVO messageVO) {
		for (FieldVO fieldVO : messageVO.getFieldList())
			if (fieldVO.isPresent())
				bitmap.put(fieldVO.getBitNum(), fieldVO);
		
		int lastBit = 0;
		for (int i = 1; i <= 128; i++) {
			binaryBitmap = binaryBitmap.concat(bitmap.get(i) == null ? "0" : "1");
			if (bitmap.get(i) != null) lastBit = i;
		}
		
		totalBits = lastBit;
		
		if (lastBit > 64) {
			FieldVO secondBitmap = new FieldVO("Bitmap", "", 1, TypeEnum.ALPHANUMERIC, TypeLengthEnum.FIXED, 16, messageVO.getBitmatEncoding(), "true");
			binaryBitmap = "1".concat(binaryBitmap.substring(1));
			secondBitmap.setValue(getHexa(binaryBitmap.substring(64, 128)));
			bitmap.put(1, secondBitmap);
			binaryBitmap.substring(0, 64);
		}
		
		hexaBitmap = getHexa(binaryBitmap.substring(0, 64));
		
		if (messageVO.getBitmatEncoding() == EncodingEnum.HEXA)
			payloadBitmap = hexaBitmap;
		else
			payloadBitmap = binaryBitmap;
		
	}

	private String getHexa(String binaryValue) {
		String result = "";
		int decimal;
		for (int i = 4; i <= 64; i = i + 4) {
			decimal = Integer.parseInt(binaryValue.substring((i - 4), i), 2);
			result = result.concat(Integer.toString(decimal, 16)).toUpperCase();
		}
		return result;
	}
	
	public int getSize() {
		return totalBits;
	}
	
	public String getPayloadBitmap() {
		return payloadBitmap;
	}
	
	public FieldVO getBit(Integer bit) {
		return bitmap.get(bit);
	}
}
