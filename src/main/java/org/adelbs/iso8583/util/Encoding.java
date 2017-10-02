package org.adelbs.iso8583.util;

public interface Encoding {

	String convert(byte[] bytesToConvert);
	
	byte[] convert(String strToConvert);
	
	int getMinBitmapSize();
	
	String convertBitmap(byte[] binaryBitmap);

	byte[] convertBitmap(String binaryBitmap);
	
}
