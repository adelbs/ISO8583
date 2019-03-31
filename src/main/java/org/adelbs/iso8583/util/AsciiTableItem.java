package org.adelbs.iso8583.util;

public class AsciiTableItem {
	
	private byte decimal;
	private String hexa;
	private String binary;
	private String ascii;
	private String description;
	
	public AsciiTableItem(byte decimal, String hexa, String binary, String ascii, String description) {
		this.decimal = decimal;
		this.hexa = hexa;
		this.binary = binary;
		this.ascii = ascii;
		this.description = description;
	}
	
	public AsciiTableItem(byte decimal, String hexa, String binary, String ascii) {
		this(decimal, hexa, binary, ascii, null);
	}

	public byte getDecimal() {
		return decimal;
	}

	public String getHexa() {
		return hexa;
	}

	public String getBinary() {
		return binary;
	}

	public String getAscii() {
		return ascii;
	}

	public String getDescription() {
		return description;
	}
}
