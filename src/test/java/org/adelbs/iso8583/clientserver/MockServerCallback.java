package org.adelbs.iso8583.clientserver;

import java.io.IOException;
import java.net.Socket;

import org.adelbs.iso8583.exception.ConnectionException;
import org.adelbs.iso8583.exception.ParseException;
import org.adelbs.iso8583.helper.Iso8583Config;
import org.adelbs.iso8583.helper.PayloadMessageConfig;
import org.adelbs.iso8583.protocol.ISOMessage;
import org.adelbs.iso8583.vo.MessageVO;

public class MockServerCallback extends CallbackAction{
	private final Iso8583Config isoConfig;
	private ISOConnection isoConnection;
	
	private Socket socketToRespond;
	
	MockServerCallback(final ISOConnection isoConnection, final Iso8583Config isoConfig) {
		this.isoConfig = isoConfig;
		this.isoConnection = isoConnection;
	}
	
	public void dataReceived(SocketPayload payload){
		PayloadMessageConfig payloadMessageConfig = new PayloadMessageConfig(isoConfig);
		try {
			payloadMessageConfig.updateFromPayload(payload.getData());
			final MessageVO received = payloadMessageConfig.buildMessageStructureFromXML(payloadMessageConfig.getXML());
			respondImediately(payloadMessageConfig, received);
			
			socketToRespond = payload.getSocket();
		} 
		catch (ParseException e) {
			System.out.println("Server [Callback]: "+ e.getMessage());
		}
	}

	private void respondImediately(PayloadMessageConfig payloadMessageConfig, final MessageVO received)	throws ParseException {
		final MessageVO responseMessage = received.getInstanceCopy();
		
		ISOMessage isoMessage = new ISOMessage(responseMessage);
		byte[] responseData = payloadMessageConfig.getIsoConfig().getDelimiter().preparePayload(isoMessage, payloadMessageConfig.getIsoConfig());
		try {
			if (!isoConnection.isConnected()){
				isoConnection.connect(String.valueOf(Thread.currentThread().getId()));
			}
			isoConnection.send(new SocketPayload(responseData, socketToRespond));
			//isoConnection.resetSocket(true);
		} catch (IOException | ConnectionException | InterruptedException e) {
			System.out.println("Server [Callback]: "+ e.getMessage());
			isoConnection.endConnection(String.valueOf(Thread.currentThread().getId()));
			this.isoConnection = null;
		}
	}

	@Override
	public void log(String log) {
	}

	@Override
	public void end() {
	}
}
