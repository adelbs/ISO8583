package org.adelbs.iso8583.clientserver;

import java.io.IOException;

import org.adelbs.iso8583.exception.ConnectionException;
import org.adelbs.iso8583.exception.ParseException;
import org.adelbs.iso8583.helper.Iso8583Config;
import org.adelbs.iso8583.helper.PayloadMessageConfig;
import org.adelbs.iso8583.protocol.ISOMessage;
import org.adelbs.iso8583.vo.MessageVO;

public class MockServerCallback extends CallbackAction{
	private final Iso8583Config isoConfig;
	private ISOConnection isoConnection;
	
	MockServerCallback(final ISOConnection isoConnection, final Iso8583Config isoConfig) {
		this.isoConfig = isoConfig;
		this.isoConnection = isoConnection;
	}
	
	//TODO
	public void dataReceived(byte[] data){
		PayloadMessageConfig payloadMessageConfig = new PayloadMessageConfig(isoConfig);
		try {
			payloadMessageConfig.updateFromPayload(data);
			final MessageVO received = payloadMessageConfig.buildMessageStructureFromXML(payloadMessageConfig.getXML());
			respondImediately(payloadMessageConfig, received);
		} catch (ParseException e) {
			System.out.println("Server [Callback]: "+ e.getMessage());
		}
	}

	private void respondImediately(PayloadMessageConfig payloadMessageConfig, final MessageVO received)	throws ParseException {
		final MessageVO responseMessage = received.getInstanceCopy();
		
		ISOMessage isoMessage = new ISOMessage(responseMessage);
		byte[] responseData = payloadMessageConfig.getIsoConfig().getDelimiter().preparePayload(isoMessage, payloadMessageConfig.getIsoConfig());
		try {
			if (!isoConnection.isConnected()){
				isoConnection.connect();
			}
			isoConnection.sendBytes(responseData, false);
			//isoConnection.resetSocket(true);
		} catch (IOException | ConnectionException | InterruptedException e) {
			System.out.println("Server [Callback]: "+ e.getMessage());
			isoConnection.endConnection();
			this.isoConnection = null;
		}
	}

	@Override
	public void log(String log) {
		// TODO Auto-generated method stub
	}

	@Override
	public void end() {
		// TODO Auto-generated method stub
	}
}
