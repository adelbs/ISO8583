package org.adelbs.iso8583.bsparser;

import java.io.IOException;
import java.io.OutputStream;

/**
 * The OutPutStream implementation used at the com.ca.smartresponse.parser.Printer implementation.
 * @author CA - AD
 *
 */
public class PrinterOutputStream extends OutputStream {

	private StringBuffer target;
	private boolean supressNewLine = true;
	
	/**
	 * Creates a new PrinterOutputStream instance.
	 * @param target StringBuffer to have the values printed
	 */
	public PrinterOutputStream(StringBuffer target) {
		this.target = target;
	}
	
	/**
	 * Writes each byte at the StringBuffer. This method will ignore the bytes of carriage and new line (\\r and \\n).
	 */
	@Override
	public void write(int b) throws IOException {
		if (supressNewLine) {
			if (b != "\n".getBytes()[0] && b != "\r".getBytes()[0])
				target.append(new String(new byte[]{(byte)b}));
		}
		else
			target.append(new String(new byte[]{(byte)b}));
	}
	
	public void setSupressNewLine(boolean value) {
		this.supressNewLine = value;
	}
	
	public boolean isSupressNewLine() {
		return supressNewLine;
	}
}
