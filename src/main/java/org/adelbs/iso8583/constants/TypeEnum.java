package org.adelbs.iso8583.constants;

public enum TypeEnum {

	ALPHANUMERIC, TLV;
	
	public static TypeEnum getType(String value) {
		if ("ALPHANUMERIC".equals(value))
			return TypeEnum.ALPHANUMERIC;
		else if ("TLV".equals(value))
			return TypeEnum.TLV;

		return TypeEnum.ALPHANUMERIC;
	}
}
