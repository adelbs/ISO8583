package org.adelbs.iso8583.clientserver;

import org.adelbs.iso8583.exception.ParseException;

public abstract class CallbackAction {

	public abstract void dataReceived(byte[] data) throws ParseException;
	
	public abstract void log(String log);
	
	public abstract void end();
	
}
