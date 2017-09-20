package org.adelbs.iso8583.vo;

public class CmbItemVO {

	private String display;
	
	private Object value;

	public CmbItemVO(String display, Object value) {
		this.display = display;
		this.value = value;
	}
	
	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	public String toString() {
		return display;
	}
}
