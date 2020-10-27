package org.adelbs.iso8583.protocol;

import java.util.List;

import org.adelbs.iso8583.exception.InvalidPayloadException;
import org.adelbs.iso8583.exception.OutOfBoundsException;
import org.adelbs.iso8583.exception.PayloadIncompleteException;
import org.adelbs.iso8583.helper.Iso8583Config;
import org.adelbs.iso8583.util.ISOUtils;

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
		return isoMessage.getPayload();
	}

	@Override
	public boolean isPayloadComplete(List<Byte> bytes, Iso8583Config isoConfig) throws InvalidPayloadException {
		boolean isComplete = false;
		
		//The minimum size = first bitmap (64) + message type (4)
		if (bytes.size() > 68) {
			try {
				new Bitmap(ISOUtils.listToArray(bytes), 
						isoConfig.getMessageVOAtTree(
								isoConfig.findMessageVOByPayload(ISOUtils.listToArray(bytes)).getType()));
				isComplete = true;
			}
			catch (PayloadIncompleteException x) {
				isComplete = false;
			} 
			catch (Exception e) {
				throw new InvalidPayloadException(e.getMessage(), e);
			}
		}
		
		return isComplete;
	}

	@Override
	public byte[] clearPayload(byte[] data, Iso8583Config isoConfig) {
		return data;
	}

	@Override
	public int getMessageSize(List<Byte> bytes) throws OutOfBoundsException {
		return 0;
	}

	@Override
	public byte[] preparePayload(ISOMessage isoMessage, boolean stxEtx) {
		return isoMessage.getPayload();
	}

}
