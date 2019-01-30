package org.adelbs.iso8583.tests;

import groovy.util.Eval;

public class Teste {

	public static void main(String[] args) {

		//String condition = "Object[] BIT = new Object[255];\n";
		
		
		String condition = "10 == 10;";
		
		Object resultado = Eval.me(condition);

		System.out.println(resultado);
		
	}
	
}
