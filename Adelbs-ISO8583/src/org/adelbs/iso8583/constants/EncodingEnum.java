package org.adelbs.iso8583.constants;

public enum EncodingEnum {

	BINARY("BINARY"), EBCDIC("EBCDIC"), ISO88591("ISO 8859-1"), UTF8("UTF-8");
	
	private String value;
	
	EncodingEnum (String value) {
		this.value = value;
	}
	
	public String toString() {
		return value;
	}

	public String toPlainString() {
		return value.replaceAll(" ", "").replaceAll("-", "");
	}
	
	public static EncodingEnum getEncoding(String value) {
		if ("BINARY".equals(value))
			return EncodingEnum.BINARY;
		else if ("EBCDIC".equals(value))
			return EncodingEnum.EBCDIC;
		else if ("ISO88591".equals(value))
			return EncodingEnum.ISO88591;
		else if ("UTF8".equals(value))
			return EncodingEnum.UTF8;

		return EncodingEnum.UTF8;
	}
}
