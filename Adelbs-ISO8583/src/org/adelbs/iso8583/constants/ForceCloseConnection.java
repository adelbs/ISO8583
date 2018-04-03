package org.adelbs.iso8583.constants;

public enum ForceCloseConnection {

	CLOS("CLOS");
	
	public byte[] bytes;
	
	ForceCloseConnection(String name) {
		this.bytes = name.getBytes();
	}
	
}
