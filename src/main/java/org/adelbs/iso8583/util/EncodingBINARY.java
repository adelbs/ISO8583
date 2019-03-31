package org.adelbs.iso8583.util;

public class EncodingBINARY implements Encoding {

	@Override
	public String convert(byte[] bytesToConvert) {
		String result = "";
		int unitByte;
		
		for (int i = 0; i < bytesToConvert.length; i++) {
			unitByte = bytesToConvert[i];
			if (unitByte < 10) result = result.concat("0");
			result = result.concat(String.valueOf(unitByte));
		}
		
		return result;
	}

	@Override
	public byte[] convert(String strToConvert) {
		byte[] result = new byte[0];
		byte unitByte;
		
		for (int i = 0; i < strToConvert.length(); i+=2) {
			unitByte = (byte) Integer.parseInt(strToConvert.substring(i, i + 2));
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
		// TODO Auto-generated method stub
		return asciiLength;
	}

}
