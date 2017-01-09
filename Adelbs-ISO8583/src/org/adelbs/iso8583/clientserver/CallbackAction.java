package org.adelbs.iso8583.clientserver;

public interface CallbackAction {

	void dataReceived(byte[] data);
	
	void log(String log);
	
	void end();
	
}
