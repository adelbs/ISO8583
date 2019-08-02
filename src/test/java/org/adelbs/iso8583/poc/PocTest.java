package org.adelbs.iso8583.poc;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import org.adelbs.iso8583.util.AsciiTable;
import org.adelbs.iso8583.util.ISOUtils;

public class PocTest {

	public static void main(String[] args) throws IOException {
		new PocTest();
	}

	public PocTest() throws IOException {
		AsciiTable asciiTable = new AsciiTable();
		byte[] payload = new byte[0];
		
		String[] hexPayload = new String[] {"00", "90", "60", "04", "25", "00", "09", "07", "00", "a2", "38", "01", "00", "01", 
				"e0", "00", "80", "00", "00", "00", "00", "00", "00", "00", "04", "99", "99", "99", "04", "02", 
				"13", "21", "37", "00", "00", "97", "10", "21", "37", "04", "02", "00", "88", "30", "30", "30", 
				"30", "35", "35", "39", "38", "35", "30", "33", "30", "30", "30", "30", "30", "30", "30", "30", 
				"30", "39", "30", "38", "38", "30", "36", "39", "30", "33", "31", "39", "31", "31", "30", "34", 
				"37", "30", "35", "35", "39", "38", "35", "30", "33", "20", "20", "20", "20", "20", "20", "20", 
				"20", "20", "20", "20", "20", "20", "20", "20", "20", "20", "20", "20", "20", "20", "20", "00", 
				"13", "30", "33", "32", "37", "31", "32", "37", "31", "37", "32", "37", "33", "32", "00", "20", 
				"30", "55", "35", "33", "31", "30", "30", "32", "31", "30", "31", "34", "31", "30", "31", "32", 
				"31", "30", "31", "32"};
		
		for (int i = 0; i < hexPayload.length; i++)
			payload = ISOUtils.mergeArray(payload, new byte[] {asciiTable.findDecimalFromHexa(hexPayload[i])});
		
		
		Socket cliente = new Socket("localhost", 9980);
//		Socket cliente = new Socket("169.57.96.7", 8100);
		
		OutputStream dOut = cliente.getOutputStream();
//		dOut.write(	new byte[] {0, -112, 96, 4, 37, 0, 89, 7, 0, -94, 56, 1, 0, 1, -32, 0, -128, 0, 0, 0, 0, 0, 0, 0, 4, -103, -103, -103, 4, 1, 32, 69, 83, 0, 0, -120, 23, 69, 83, 4, 1, 0, -120, 48, 48, 48, 48, 53, 53, 57, 56, 53, 48, 51, 48, 48, 48, 48, 48, 48, 48, 48, 48, 57, 48, 56, 56, 48, 54, 57, 48, 51, 49, 57, 49, 49, 48, 52, 55, 48, 53, 53, 57, 56, 53, 48, 51, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 0, 19, 48, 51, 50, 55, 49, 50, 55, 49, 55, 50, 55, 51, 50, 0, 32, 48, 85, 53, 51, 49, 48, 48, 50, 49, 48, 49, 52, 49, 48, 49, 50, 49, 48, 49, 50});
		dOut.write(payload);
		
		cliente.close();
	}
}
