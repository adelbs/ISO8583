package org.adelbs.iso8583.clientserver;

import java.util.ArrayList;

final class PayloadQueue {

	private ArrayList<byte[]> payloadIn = new ArrayList<byte[]>();
	private ArrayList<byte[]> payloadOut = new ArrayList<byte[]>();

	public boolean hasMorePayloadIn() {
		return (payloadIn.size() > 0);
	}
	
	public boolean hasMorePayloadOut() {
		return (payloadOut.size() > 0);
	}
	
	public void addPayloadIn(byte[] payload) {
		addOrRemoveItem(payloadIn, payload);
	}
	
	public void addPayloadOut(byte[] payload) {
		addOrRemoveItem(payloadOut, payload);
	}
	
	public byte[] getNextPayloadIn() {
		return getNext(payloadIn);
	}
	
	public byte[] getNextPayloadOut() {
		return getNext(payloadOut);
	}
	
	private byte[] getNext(ArrayList<byte[]> list) {
		synchronized(this) {
			byte[] result = list.get(0);
			addOrRemoveItem(list, null);
			return result;
		}
	}
	
	private void addOrRemoveItem(ArrayList<byte[]> list, byte[] item) {
		synchronized(this) {
			if (item == null)
				list.remove(0);
			else
				list.add(item);
		}
	}
}
