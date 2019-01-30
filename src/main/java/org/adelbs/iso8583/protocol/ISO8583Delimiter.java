package org.adelbs.iso8583.protocol;

import java.util.List;

import org.adelbs.iso8583.exception.InvalidPayloadException;
import org.adelbs.iso8583.exception.OutOfBoundsException;
import org.adelbs.iso8583.helper.Iso8583Config;

public interface ISO8583Delimiter {

	String getName();
	
	String getDesc();
	
	byte[] preparePayload(ISOMessage isoMessage, Iso8583Config isoConfig);
	
	byte[] clearPayload(byte[] data, Iso8583Config isoConfig);
	
	boolean isPayloadComplete(List<Byte> bytes, Iso8583Config isoConfig) throws InvalidPayloadException;

	int getMessageSize(List<Byte> bytes) throws OutOfBoundsException;
	
}
