package org.adelbs.iso8583.bsparser;

import java.io.PrintStream;

/**
 * The PrinterStream implementation, used at the com.ca.smartresponse.parser.Console implementation.
 * @author CA - AD
 *
 */
public class Printer extends PrintStream {
	
	/**
	 * Constructs a new Printer.
	 * @param target StringBuffer to have the values printed in
	 */
	public Printer(StringBuffer target) {
		super(new PrinterOutputStream(target));
	}

	@Override
	public void print(char c) {
		super.print(c);
	}

	@Override
	public void print(char[] s) {
		super.print(s);
	}

	@Override
	public void print(double d) {
		super.print(d);
	}

	@Override
	public void print(float f) {
		super.print(f);
	}

	@Override
	public void print(int i) {
		super.print(i);
	}

	@Override
	public void print(long l) {
		super.print(l);
	}

	@Override
	public void print(Object obj) {
		super.print(obj);
	}

	@Override
	public void print(String s) {
		super.print(s);
	}

	@Override
	public void println() {
		super.println();
	}

	@Override
	public void println(boolean x) {
		super.println(x);
	}

	@Override
	public void println(char x) {
		super.println(x);
	}

	@Override
	public void println(char[] x) {
		super.println(x);
	}

	@Override
	public void println(double x) {
		super.println(x);
	}

	@Override
	public void println(float x) {
		super.println(x);
	}

	@Override
	public void println(int x) {
		super.println(x);
	}

	@Override
	public void println(long x) {
		super.println(x);
	}

	@Override
	public void println(Object x) {
		super.println(x);
	}

	@Override
	public void println(String x) {
		super.println(x);
	}
	
	public void setSupressNewLine(boolean value) {
		((PrinterOutputStream) this.out).setSupressNewLine(value);
	}
	
	public boolean isSupressNewLine() {
		return ((PrinterOutputStream) this.out).isSupressNewLine();
	}
}
