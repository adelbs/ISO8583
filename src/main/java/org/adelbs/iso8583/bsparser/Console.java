package org.adelbs.iso8583.bsparser;

import java.io.PrintStream;
import java.io.Reader;

import bsh.ConsoleInterface;

/**
 * Implementation of the Console to be used at the BeanShell parser. Instead of print the content at the System Console, 
 * it will print at a StringBuffer instance.
 * @author CA - AD
 *
 */
public class Console implements ConsoleInterface {
	
	private StringBuffer result = new StringBuffer();
	private Printer printter;
	
	/**
	 * Returns the StringBuffer with all printed content.
	 * @return StringBuffer instance with all printed content
	 */
	public StringBuffer getResult(){ return result; }
	
	/**
	 * Return the Printer instance.
	 */
	public Console() {
		printter = new Printer(result);
	}
	
	/**
	 * Prints a new line at the StringBuffer result instance.
	 */
	@Override
	public void println(Object arg0) {
		result.append("\n").append(arg0);
	}
	
	/**
	 * Prints a new content at the StringBuffer result instance.
	 */
	@Override
	public void print(Object arg0) {
		result.append(arg0);
	}
	
	/**
	 * Returns the PrintStream instance.
	 */
	@Override
	public PrintStream getOut() {
		return printter;
	}
	
	/**
	 * Return null. There is no Reader.
	 */
	@Override
	public Reader getIn() {
		return null;
	}
	
	/**
	 * Return null. There is no PrintStream specific for errors prints.
	 */
	@Override
	public PrintStream getErr() {
		return null;
	}
	
	/**
	 * Empty implementation.
	 */
	@Override
	public void error(Object arg0) {}
	
	public void setSupressNewLine(boolean value) {
		printter.setSupressNewLine(value);
	}
	
	public boolean isSupressNewLine() {
		return printter.isSupressNewLine();
	}
}
