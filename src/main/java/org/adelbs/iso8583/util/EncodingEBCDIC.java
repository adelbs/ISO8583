package org.adelbs.iso8583.util;

import java.io.UnsupportedEncodingException;

public class EncodingEBCDIC implements Encoding {
/*
 * ASCII -> Java:   new String(bytes, "ASCII")
 * EBCDIC -> Java:  new String(bytes, "Cp1047")
 * Java -> ASCII:   string.getBytes("ASCII")
 * Java -> EBCDIC:  string.getBytes("Cp1047")
 * */
	
	@Override
	public String convert(byte[] bytesToConvert) {
		try {
			return new String(bytesToConvert, "Cp1047");
		} 
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public byte[] convert(String strToConvert) {
		try {
			return strToConvert.getBytes("Cp1047");
		} 
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	//TODO: Check the code
	public String convertBitmap(byte[] binaryBitmap) {
		String strByte;
		String result = "";
		
		for (byte bt : binaryBitmap) {
			strByte = convert(new byte[]{bt});
			result = result.concat(ISOUtils.hexToBin(strByte));
		}
		
		return result;
	}

	@Override
	//TODO: This code is not validated under the requirements of an ISO bitmap
	public byte[] convertBitmap(String binaryBitmap) {
		byte[] result = new byte[]{};

		for (int i = 4; (i <= 64 && i <= binaryBitmap.length()); i = i + 4) {
			result = ISOUtils.mergeArray(result, convert(ISOUtils.binToHex(binaryBitmap.substring((i - 4), i))));
		}

		return result;
	}

	@Override
	public int getMinBitmapSize() {
		return 16;
	}
	
	
	@Override
	public int getEncondedByteLength(final int asciiLength) {
		return asciiLength;
	}
}
