package org.adelbs.iso8583.util;

public interface Encoding {

	String convert(byte[] bytesToConvert);
	
	byte[] convert(String strToConvert);
	
	/**
	 * Some encoding algorithms need more the one byte to represent an ASCII character.
	 * So its original byte size, after the conversion, may change.
	 * This method receives the ascii length and returns what would be the ammount of bytes
	 * necessary to hold this value converted
	 * 
	 * @param asciiLength length of the ascii value
	 * @return the length of the converted value
	 */
	int getEncondedByteLength(final int asciiLength);
	
	int getMinBitmapSize();
	
	
	String convertBitmap(byte[] binaryBitmap);

	byte[] convertBitmap(String binaryBitmap);
	
}
