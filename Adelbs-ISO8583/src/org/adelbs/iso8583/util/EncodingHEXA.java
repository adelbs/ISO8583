package org.adelbs.iso8583.util;

public class EncodingHEXA implements Encoding {

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
		String tempBM = new String(binaryBitmap);
		tempBM = ISOUtils.hexToBin(tempBM);
		return tempBM;
	}

	@Override
	public byte[] convertBitmap(String binaryBitmap) {
		return ISOUtils.binToHex(binaryBitmap).getBytes();
	}

	@Override
	public int getMinBitmapSize() {
		return 16;
	}


}
