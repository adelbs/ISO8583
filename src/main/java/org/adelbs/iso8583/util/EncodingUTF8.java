package org.adelbs.iso8583.util;

public class EncodingUTF8 implements Encoding {

	@Override
	public String convert(byte[] bytesToConvert) {
		return new String(bytesToConvert);
	}

	@Override
	public byte[] convert(String strToConvert) {
		return strToConvert.getBytes();
	}

	@Override
	public String convertBitmap(byte[] binaryBitmap) {
		return new String(binaryBitmap);
	}

	@Override
	public byte[] convertBitmap(String binaryBitmap) {
		return binaryBitmap.getBytes();
	}

	@Override
	public int getMinBitmapSize() {
		return 64;
	}


}
