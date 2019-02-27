package org.adelbs.iso8583.clientserver;

import java.util.ArrayList;

final class PayloadQueue {

	private ArrayList<SocketPayload> payloadIn = new ArrayList<SocketPayload>();
	private ArrayList<SocketPayload> payloadOut = new ArrayList<SocketPayload>();

	public boolean hasMorePayloadIn() {
		return (payloadIn.size() > 0);
	}
	
	public boolean hasMorePayloadOut() {
		return (payloadOut.size() > 0);
	}
	
	public void addPayloadIn(SocketPayload payload) {
		addOrRemoveItem(payloadIn, payload);
	}
	
	public void addPayloadOut(SocketPayload payload) {
		addOrRemoveItem(payloadOut, payload);
	}
	
	public SocketPayload getNextPayloadIn() {
		return getNext(payloadIn);
	}
	
	public SocketPayload getNextPayloadOut() {
		return getNext(payloadOut);
	}
	
	private SocketPayload getNext(ArrayList<SocketPayload> list) {
		synchronized(this) {
			SocketPayload result = list.get(0);
			addOrRemoveItem(list, null);
			return result;
		}
	}
	
	private void addOrRemoveItem(ArrayList<SocketPayload> list, SocketPayload item) {
		synchronized(this) {
			if (item == null)
				list.remove(0);
			else
				list.add(item);
		}
	}
}
