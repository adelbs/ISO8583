package org.adelbs.iso8583.clientserver;

public class GenericClientServerThread extends Thread {

	protected String bytesToConsole(byte[] data) {
		String result = "";
		
		for (int i = 0; i < data.length; i++)
			result += "byte[" + i + "]{" + data[i] + "},";
		
		return result;
	}
	
}
