package org.adelbs.iso8583.util;

public class EncodingBCD implements Encoding {

	@Override
	public String convert(byte[] bytesToConvert) {
		String result = "";
		String unitHexa;
		
		AsciiTable asciiTable = new AsciiTable();
		
		for (int i = 0; i < bytesToConvert.length; i++) {
			unitHexa = asciiTable.findHexaFromByte(bytesToConvert[i]);
			result = result.concat(unitHexa);
		}
		
		return result;
	}

	@Override
	public byte[] convert(String strToConvert) {
		byte[] result = new byte[0];
		String unitHexa;
		byte unitByte;
		
		AsciiTable asciiTable = new AsciiTable();
		
		for (int i = 0; i < strToConvert.length(); i+=2) {
			unitHexa = strToConvert.substring(i, i + 2);
			unitByte = asciiTable.findDecimalFromHexa(unitHexa);
			result = ISOUtils.mergeArray(result, new byte[] {unitByte});
		}
		
		return result;
	}

	@Override
	public String convertBitmap(byte[] binaryBitmap) {
		String result = "";
		AsciiTable table = new AsciiTable();
		
		for (int i = 0; i < binaryBitmap.length; i++) {
			result += table.findBinaryFromByte(binaryBitmap[i]); 
		}
		
		return result;
	}

	@Override
	public byte[] convertBitmap(String binaryBitmap) {
		byte[] data = new byte[0];
		String byteToFind = "";
		AsciiTable table = new AsciiTable();
		
		for (int i = 0; i < binaryBitmap.length(); i += 8) {
			byteToFind = binaryBitmap.substring(i, i + 8);
			data = ISOUtils.mergeArray(data, new byte[] {table.findByteFromBinary(byteToFind)});
		}
		
		return data;
	}

	@Override
	public int getMinBitmapSize() {
		return 8;
	}
	
	@Override
	public int getEncondedByteLength(final int asciiLength) {
		return asciiLength;
	}

}
