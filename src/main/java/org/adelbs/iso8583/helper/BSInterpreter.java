package org.adelbs.iso8583.helper;

import groovy.lang.GroovyShell;

public class BSInterpreter {

	private GroovyShell shell = new GroovyShell();
	private String snipetBS = "Object[] BIT = new Object[255];\n";

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
	
	
	
//	public void concatSnipet(FieldVO fieldVO) {
//		snipetBS += getSnipetBS("", fieldVO);
//	}
//	
//	private String getSnipetBS(String preSnipet, FieldVO fieldVO) {
//		String result = preSnipet.equals("") ? "BIT" : preSnipet;
//		String currentObj = "";
//				
//		if (fieldVO.getFieldList().size() > 0) {
//			currentObj = result + "["+ fieldVO.getBitNum() +"]";
//			result += "["+ fieldVO.getBitNum() +"] = new Object[255];\n";
//			
//			for (FieldVO subFieldVO : fieldVO.getFieldList()) {
//				result += getSnipetBS(currentObj, subFieldVO);
//			}
//		}
//		else {
//			result += "["+ fieldVO.getBitNum() +"] = \""+ fieldVO.getValue() + "\";\n";
//		}
//		
//		return result;
//	}
//
//	
//	
//	
//	
//	public String concatSnipetBS(String preSnipet, FieldVO fieldVO) {
//		String result = preSnipet.equals("") ? "BIT" : preSnipet;
//		String currentObj = "";
//				
//		if (fieldVO.getFieldList().size() > 0) {
//			currentObj = result + "["+ fieldVO.getBitNum() +"]";
//			result += "["+ fieldVO.getBitNum() +"] = new Object[255];\n";
//		}
//		else {
//			result += "["+ fieldVO.getBitNum() +"] = \""+ fieldVO.getValue() + "\";\n";
//		}
//		
//		snipetBS += result;
//		return currentObj;
//	}

}
