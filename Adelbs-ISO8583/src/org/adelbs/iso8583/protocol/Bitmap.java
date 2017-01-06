package org.adelbs.iso8583.protocol;

import java.util.HashMap;

import org.adelbs.iso8583.constants.EncodingEnum;
import org.adelbs.iso8583.vo.FieldVO;
import org.adelbs.iso8583.vo.MessageVO;

public class Bitmap {

	private HashMap<Integer, FieldVO> bitmap = new HashMap<Integer, FieldVO>();
	private String binaryBitmap = "";
	private String hexaBitmap = "";
	private int totalBits = 128;
	
	private String payloadBitmap;
	
	public Bitmap(MessageVO messageVO) {
		for (FieldVO fieldVO : messageVO.getFieldList())
			if (fieldVO.isPresent())
				bitmap.put(fieldVO.getBitNum(), fieldVO);
		
		int lastBit = 0;
		for (int i = 1; i <= 128; i++) {
			binaryBitmap = binaryBitmap.concat(bitmap.get(i) == null ? "0" : "1");
			if (bitmap.get(i) != null) lastBit = i;
		}
		
		if (lastBit <= 64) {
			totalBits = 64;
			binaryBitmap = binaryBitmap.substring(0, 64);
		}
		
		int decimal;
		for (int i = 4; i <= totalBits; i = i + 4) {
			decimal = Integer.parseInt(binaryBitmap.substring((i - 4), i), 2);
			hexaBitmap = hexaBitmap.concat(Integer.toString(decimal, 16)).toUpperCase();
		}
		
		if (messageVO.getBitmatEncoding() == EncodingEnum.HEXA)
			payloadBitmap = hexaBitmap;
		else
			payloadBitmap = binaryBitmap;
		
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
