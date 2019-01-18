package org.adelbs.iso8583.util;

public class EncodingBINARY implements Encoding {

	@Override
	public String convert(byte[] bytesToConvert) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] convert(String strToConvert) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String convertBitmap(byte[] binaryBitmap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] convertBitmap(String binaryBitmap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMinBitmapSize() {
		// TODO Auto-generated method stub
		return 64;
	}
	
	@Override
	public int getEncondedByteLength(final int asciiLength) {
		// TODO Auto-generated method stub
		return asciiLength;
	}

}
