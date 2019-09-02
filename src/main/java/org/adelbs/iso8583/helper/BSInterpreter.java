package org.adelbs.iso8583.helper;

import groovy.lang.GroovyShell;

public class BSInterpreter {

	private GroovyShell shell = new GroovyShell();
	private String snipetBS;

	public BSInterpreter() {
		snipetBS = "Object[] BIT = new Object[255];\n"
				+ "public boolean ignore() {return false;}\n";
	}
	
	public boolean evaluate(String dynaCondition) {
		boolean result = true;
		if (dynaCondition != null && dynaCondition.length() > 0) {
			final Object condition = shell.evaluate(snipetBS + dynaCondition);
			if (!(condition instanceof Boolean)) throw new IllegalArgumentException("The expression do not generates a boolean result");
			
			result = ((boolean) condition);
		}
		
		return result;
	}
	
	public void concatSnipet(String snipet) {
		snipetBS += snipet;
	}	
}
