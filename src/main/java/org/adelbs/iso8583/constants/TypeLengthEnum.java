package org.adelbs.iso8583.constants;

public enum TypeLengthEnum {

	FIXED("FIXED"), NVAR("N-VAR");

	private String value;
	
	TypeLengthEnum (String value) {
		this.value = value;
	}
	
	public String toString() {
		return value;
	}

	public String toPlainString() {
		return value.replaceAll(" ", "").replaceAll("-", "");
	}

	public static TypeLengthEnum getTypeLength(String value) {
		if ("FIXED".equals(value))
			return TypeLengthEnum.FIXED;
		else if ("NVAR".equals(value))
			return TypeLengthEnum.NVAR;

		return TypeLengthEnum.FIXED;
	}

}
