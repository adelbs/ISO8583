package org.adelbs.iso8583.clientserver;

import org.adelbs.iso8583.exception.ParseException;
import org.adelbs.iso8583.helper.Iso8583Config;
import org.adelbs.iso8583.helper.PayloadMessageConfig;
import org.adelbs.iso8583.vo.MessageVO;

public class MockClientCallback extends CallbackAction{
	private Iso8583Config isoConfig;
	private MessageVO responseMessage = null;
	
	MockClientCallback(Iso8583Config isoConfig) {
		this.isoConfig = isoConfig;
	}
	
	public void dataReceived(byte[] data) throws ParseException {
		try{
			PayloadMessageConfig payloadMessageConfig = new PayloadMessageConfig(isoConfig);
			payloadMessageConfig.updateFromPayload(data);
			this.responseMessage = payloadMessageConfig.buildMessageStructureFromXML(payloadMessageConfig.getXML());
			System.out.println("Client [Callback]: Message Received!");
		}catch (Exception e){
			System.out.println("Client [Callback]: "+e.getMessage());
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

	public MessageVO getResponseMessage() {
		return responseMessage;
	}

}
