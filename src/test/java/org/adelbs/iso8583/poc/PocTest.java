package org.adelbs.iso8583.poc;

import java.util.ArrayList;

import org.adelbs.iso8583.util.EncodingBINARY;

public class PocTest {

	public static void main(String[] args) {
		new PocTest();
	}
	
	public PocTest() {
		String value = "0700";
		byte[] bValue = new byte[] {(byte) 7, (byte) 0};

		EncodingBINARY encoding = new EncodingBINARY();
		
//		byte[] result = encoding.convert(value);
		String result = encoding.convert(bValue);
		
		System.out.println(result);

	}
}
