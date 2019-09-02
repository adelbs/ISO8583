package org.adelbs.iso8583.helper;

import org.adelbs.iso8583.exception.ParseException;
import org.adelbs.iso8583.protocol.ISOMessage;
import org.adelbs.iso8583.vo.MessageVO;

public class Iso8583Parser {

	private PayloadMessageConfig payload;
	
	public Iso8583Parser(String fileName) {
		payload = new PayloadMessageConfig(new Iso8583Config(fileName));
	}
	
	public byte[] parseXmlToBytes(String xml) throws ParseException {
		payload.updateFromXML(xml);
		ISOMessage isoMessage = new ISOMessage(payload.getMessageVO());
		return payload.getIsoConfig().getDelimiter().preparePayload(isoMessage, payload.getIsoConfig());
	}
	
	public String parseBytesToXml(byte[] bytes) throws ParseException {
		payload.updateFromPayload(bytes);
		return payload.getXML();
	}

	public MessageVO parseXmlToMessageVO(String xml) throws ParseException {
		return payload.buildMessageStructureFromXML(xml);
	}
}
