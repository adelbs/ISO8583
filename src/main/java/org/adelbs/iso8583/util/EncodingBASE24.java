package org.adelbs.iso8583.util;

import java.util.Base64;

/**
 * Helper class to handle encoding and decoding of Base64 data.
 * 
 * @see Encoding 
 */
public class EncodingBASE24 implements Encoding {
	
	/**
	 * Returns the converted base64 array of bytes to string.
	 * 
	 * @param bytesToConvert array of bytes, in Base64, to be converted.
	 * @return Converted bytes into string.
	 */
	@Override
	public String convert(byte[] bytesToConvert) {
		try {
			final byte[] decodeBytes = Base64.getDecoder().decode(bytesToConvert);
			return new String(decodeBytes);
		} 
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns the converted string into a array of bytes, in base64.
	 * 
	 * @param strToConvert String value to be converted to Base64
	 * @return A newly converted Base64 array of bytes.
	 */
	@Override
	public byte[] convert(String strToConvert) {
		return Base64.getEncoder().encode(strToConvert.getBytes());
	}

	@Override
	public String convertBitmap(byte[] binaryBitmap) {
		String strByte;
		String result = "";
		
		for (byte bt : binaryBitmap) {
			strByte = this.convert(new byte[]{bt});
			result = result.concat(ISOUtils.hexToBin(strByte));
		}
		
		return result;
	}

	@Override
	public byte[] convertBitmap(String binaryBitmap) {
		byte[] result = new byte[]{};
		final int MTIBitLength = 4;
		for (int i = MTIBitLength; (i <= 64 && i <= binaryBitmap.length()); i = i + MTIBitLength) {
			result = ISOUtils.mergeArray(result, convert(ISOUtils.binToHex(binaryBitmap.substring((i - MTIBitLength), i))));
		}
		return result;
	}

	@Override
	public int getMinBitmapSize() {
		return 64;
	}
	
	@Override
	/**
	 * From left to right, these bytes are organized into 24-bit groups. 
	 * Each group is treated as four concatenated 6-bit groups. Each 6-bit group indexes into an array of the 64 printable characters; 
	 * The resulting character is output.
	 * 
	 * When fewer than 24 bits are available at the end of the data being encoded, zero bits are added (on the right)
	 *  to form an integral number of 6-bit groups. Then, one or two = pad characters may be output.
	 *  
	 *  So, for each 3 ASCII char We have 4 Base64 bytes, Padding is added to have a multiple of 4 Base64 bytes
	 */
	public int getEncondedByteLength(final int asciiLength) {
		final int asciiGroupSize = 3;
		final int base64GroupSize = 4;
		final int  reminder = asciiLength % asciiGroupSize;
		final int totalGroups = asciiLength / asciiGroupSize;
		
		return base64GroupSize * totalGroups + (reminder != 0 ? base64GroupSize : 0);
	}
}
