package org.adelbs.iso8583.constants;

public enum OperatorEnum {

	EQUAL("=="), DIFFERENT("!="), GRATER_THAN(">"), SMALLER_THAN("<"), OPEN_PAR("("), CLOSE_PAR(")"), 
	PLUS("+"), MINUS("-"), AND("&&"), OR("||");

	private String value;
	
	OperatorEnum(String value) {
		this.value = value;
	}
	
	public String toString() {
		return value;
	}
}
