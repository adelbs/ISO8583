package org.adelbs.iso8583.constants;

public enum TypeEnum {

	ALPHA, DATE_EXP, DATE4, LVAR, LLVAR, LLLVAR, NUMERIC, TIME;
	
	public static TypeEnum getType(String value) {
		if ("ALPHA".equals(value))
			return TypeEnum.ALPHA;
		else if ("DATE_EXP".equals(value))
			return TypeEnum.DATE_EXP;
		else if ("DATE4".equals(value))
			return TypeEnum.DATE4;
		else if ("LVAR".equals(value))
			return TypeEnum.LVAR;
		else if ("LLVAR".equals(value))
			return TypeEnum.LLVAR;
		else if ("LLLVAR".equals(value))
			return TypeEnum.LLLVAR;
		else if ("NUMERIC".equals(value))
			return TypeEnum.NUMERIC;
		else if ("TIME".equals(value))
			return TypeEnum.TIME;

		return TypeEnum.ALPHA;
	}
}
