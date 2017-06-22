package org.adelbs.iso8583.protocol;

import java.util.List;

import org.adelbs.iso8583.helper.Iso8583Config;

public class ISO8583GenericConfigDelimiter implements ISO8583Delimiter {

	@Override
	public String getName() {
		return "Generic, Based on Configuration";
	}

	@Override
	public String getDesc() {
		return "This delimiter is based on the configuration file to find out the length of each message. It " +
				"may has a poor performance, depending of the complexity and size of the messages.";
	}

	@Override
	public byte[] preparePayload(ISOMessage isoMessage, Iso8583Config isoConfig) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPayloadComplete(List<Byte> bytes, Iso8583Config isoConfig) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public byte[] clearPayload(byte[] data, Iso8583Config isoConfig) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMessageSize(List<Byte> bytes) {
		// TODO Auto-generated method stub
		return 0;
	}

}
