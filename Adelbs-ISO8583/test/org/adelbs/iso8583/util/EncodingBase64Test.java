package org.adelbs.iso8583.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class EncodingBase64Test {
	
	private final String readableValue = "TestValue";
	private static final EncodingBASE64 encoder = new EncodingBASE64();

	@Test
	public void testConvert() {
		final byte[] base64ConvertedValue = encoder.convert(readableValue);
		final String convertedStringValue = encoder.convert(base64ConvertedValue);
		assertEquals("The original String value must be maintaned through the convertions",readableValue, convertedStringValue);
	}
	
	@Test
	public void testGetEncondedByteLength_LessThanThreeChars(){
		assertEncodedSize("A");
	}
	
	@Test
	public void testGetEncondedByteLength_MultipleOfThreeChars(){
		assertEncodedSize("A1x");
		assertEncodedSize("12r6hd");
	}
	
	@Test
	public void testGetEncondedByteLength_NonMultipleOfThreeChars(){
		assertEncodedSize("A1x3");
		assertEncodedSize("12r6he56");
	}
	
	
	private void assertEncodedSize(final String value){
		final byte[] base64ConvertedValue = encoder.convert(value);
		final int encondedByteLength = encoder.getEncondedByteLength(value.length());
		assertEquals("Calculated encoded size should match the converted byte array size", base64ConvertedValue.length, encondedByteLength);
	}
}
