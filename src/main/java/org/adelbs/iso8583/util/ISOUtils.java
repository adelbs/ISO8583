package org.adelbs.iso8583.util;
import java.util.List;

import org.adelbs.iso8583.exception.OutOfBoundsException;
import org.w3c.dom.Node;

public class ISOUtils {
	/*
	 * TODO: 
	 * - implement check for TPDU
	 * - implement subArray to extract TPDU
	 * */
	public static String hexToBin(String hex){
		String bin = "";
		String binFragment = "";
		int iHex;
		hex = hex.trim();
		hex = hex.replaceFirst("0x", "");

		for(int i = 0; i < hex.length(); i++){
			iHex = Integer.parseInt(""+hex.charAt(i),16);
			binFragment = Integer.toBinaryString(iHex);

			while(binFragment.length() < 4){
				binFragment = "0" + binFragment;
			}
			bin += binFragment;
		}
		return bin;
	}

	public static String binToHex(String bin) {
		String result = "";
		int decimal;
		for (int i = 4; (i <= 64 && i <= bin.length()); i = i + 4) {
			decimal = Integer.parseInt(bin.substring((i - 4), i), 2);
			result = result.concat(Integer.toString(decimal, 16)).toUpperCase();
		}

		return result;
	}
	
	public static byte[] subArray(byte[] data, int start, int end) throws OutOfBoundsException {
		if ((end - start) <= 0) throw new OutOfBoundsException();
		if (data.length < end || data.length < start || data.length < (end - start)) throw new OutOfBoundsException();
		
		byte[] result = new byte[end - start];
	  
		for (int i = start; i < end; i++)
			result[i - start] = data[i];

		return result;
	}

	public static byte[] mergeArray(byte[] arr1, byte[] arr2) {
		byte[] result = new byte[arr1.length + arr2.length];
	  
		for (int i = 0; i < arr1.length; i++)
			result[i] = arr1[i];
	  
		for (int i = 0; i < arr2.length; i++)
			result[i + arr1.length] = arr2[i];
	  
		return result;
	}
	
	public static String getAttr(Node node, String name, String defaultValue) {
		String result = defaultValue;
		if (node != null && node.getAttributes() != null && node.getAttributes().getNamedItem(name) != null)
			result = node.getAttributes().getNamedItem(name).getNodeValue();
		
		return result;
	}

	public static byte[] listToArray(List<Byte> bytes) {
		byte[] data = new byte[bytes.size()];
		for (int i = 0; i < bytes.size(); i++)
			data[i] = bytes.get(i).byteValue();
		return data;
	}
}
