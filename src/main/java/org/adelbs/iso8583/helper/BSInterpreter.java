package org.adelbs.iso8583.helper;

import groovy.lang.GroovyShell;

public class BSInterpreter {

	private static GroovyShell shell;
	private String snipetBS;

	public BSInterpreter() {
		snipetBS = "Object[] BIT = new Object[255];\n"
				+ "public boolean ignore() {return false;}\n";
	}
	
	public boolean evaluate(String dynaCondition) {
		boolean result = true;
		String cond = (dynaCondition != null) ? dynaCondition.trim() : "";
		if (cond.indexOf("return ignore();") > -1) {
			result = false;
		}
		else if (cond.length() > 0 && !cond.equals("true")) {
			final Object condition = getShell().evaluate(snipetBS + dynaCondition);
			if (!(condition instanceof Boolean)) throw new IllegalArgumentException("The expression do not generates a boolean result");
			
			result = ((boolean) condition);
		}
		
		return result;
	}
	
	public void concatSnipet(String snipet) {
		snipetBS += snipet;
	}
	
	private static GroovyShell getShell() {
		if (shell == null) {
			shell = new GroovyShell();
			shell.evaluate("true");
		}
		return shell;
	}
}
