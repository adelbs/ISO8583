package org.adelbs.iso8583.clientserver;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;

import org.adelbs.iso8583.exception.ConnectionException;
import org.adelbs.iso8583.exception.ParseException;
import org.adelbs.iso8583.helper.Iso8583Config;
import org.adelbs.iso8583.util.Out;


public class ISOConnection {

	private final static int SLEEP_TIME = 50;
	
	private boolean running = false;
	private long lastAction = 0;
	
	private Iso8583Config isoConfig;
	private HashMap<String, CallbackAction> callbackMap = new HashMap<String, CallbackAction>();
//	private CallbackAction callback;

	private final PayloadQueue payloadQueue = new PayloadQueue();
	private HashMap<String, Sender> senderMap = new HashMap<String, ISOConnection.Sender>();
//	private Sender sender = new Sender();

	private boolean isServer;
	private String host;
	private int port;
	private int timeout;
	
	private ISOServer isoServer;
	private ISOClient isoClient;
	
	public ISOConnection(boolean isServer, String host, int port, int timeout) {
		this.isServer = isServer;
		this.host = host;
		this.port = port;
		this.timeout = (timeout * 1000);
	}
	
	public void connect(String threadName) throws IOException, ConnectionException {
		if (isoConfig != null && callbackMap.containsKey(threadName)) {
			if(isoConfig.getXmlFilePath()!=null){
				registerActionTimeMilis();
				if (isServer) 
					this.isoServer = new ISOServer(this, host, port, isoConfig, payloadQueue, callbackMap.get(threadName));
				else 
					this.isoClient = new ISOClient(host, port, isoConfig, payloadQueue, callbackMap.get(threadName));
				
				registerSender(threadName);
			}
			else{
				throw new ConnectionException("ISOConfig is missing");
			}
		}
		else {
			throw new ConnectionException("ISOConfig or Callback is missing");
		}
	}
	
	public void registerSender(String threadName) {
		running = true;
		if (!senderMap.containsKey(threadName)) {
			senderMap.put(threadName, new Sender(threadName));
			senderMap.get(threadName).start();
		}
	}
	
	public boolean isConnected() {
		return running;
	}
	
	public void endConnection(String threadName) {
		running = false;
		lastAction = 0;
		
		if (isoClient != null) isoClient.closeConnection();
		if (isoServer != null) isoServer.closeConnection();

		isoClient = null;
		isoServer = null;
//		senderMap.remove(threadName);
	}
	
	private void waitRequest(String threadName, int keepaliveTimeout) throws InterruptedException {
		while (running && !payloadQueue.hasMorePayloadIn()) {
			Thread.sleep(SLEEP_TIME);
			
			if (keepaliveTimeout > 0 && (System.currentTimeMillis() - startOfWaitNextRequest) > (keepaliveTimeout * 1000)) {
				startOfWaitNextRequest = System.currentTimeMillis();
				callbackMap.get(threadName).keepalive();
			}
			
		}
	}

	private long startOfWaitNextRequest = 0;
	public synchronized void processNextPayload(String threadName, boolean waitIfThereIsNothingAtQueue, int keepaliveTimeout) throws ParseException, InterruptedException {
		
		if (waitIfThereIsNothingAtQueue) {
			startOfWaitNextRequest = System.currentTimeMillis();
			waitRequest(threadName, keepaliveTimeout);
		}
		
		if (payloadQueue.hasMorePayloadIn()) {
			Out.log("ISOConnection", "Parsing bytes for Thread ["+threadName+"]...", callbackMap.get(threadName));
			callbackMap.get(threadName).dataReceived(payloadQueue.getNextPayloadIn());
		}
	}
	
	public void setIsoConfig(Iso8583Config isoConfig) {
		this.isoConfig = isoConfig;
	}
	
	public void putCallback(String threadName, CallbackAction callback) {
		callbackMap.put(threadName, callback);
	}

	public void send(SocketPayload payload) throws IOException, ParseException, InterruptedException {
		payloadQueue.addPayloadOut(payload);
	}
	
	public void registerActionTimeMilis() {
		lastAction = System.currentTimeMillis();
	}
	
	public String getHost() {
		return host;
	}
	
	public int getPort() {
		return port;
	}
	
	public Socket getClientSocket() {
		Socket socket = null;
		if (isoClient != null)
			socket = isoClient.getSocket();
		return socket;
	}
	
	private class Sender extends Thread {
		
		private String threadName;
		
		Sender(String threadName) {
			setName("Sender");
			this.threadName = threadName;
		}
		
		public void run() {
			try {
				while (running) {
					try {
						if (payloadQueue.hasMorePayloadOut()) {
							send(payloadQueue.getNextPayloadOut());
						}

						sleep(SLEEP_TIME);
					}
					catch (SocketException se) {
						Out.log("ISOConnection", "Client disconnected...", callbackMap.get(threadName));
					}
					
					if (timeout > 0 && timeout < (System.currentTimeMillis() - lastAction)) {
						Out.log("ISOConnection", "Connection timeout.", callbackMap.get(threadName));
						running = false;
						lastAction = 0;
					}
				}
			}
			catch (Exception x) {
				if (running) {
					x.printStackTrace();
					Out.log("ISOConnection", "ERROR "+ x.getMessage(), callbackMap.get(threadName));
				}
			}
			finally {
				Out.log("ISOConnection", "Connection closed.", callbackMap.get(threadName));
				callbackMap.get(threadName).end();
				endConnection(threadName);
			}
		}
		
		private void send(SocketPayload payload) throws IOException {
			synchronized(this) {
				registerActionTimeMilis();
				
				if (payload == null) {
					Out.log("ISOConnection", "Null payload", callbackMap.get(threadName));
				}
				else if (payload.getSocket() != null && !payload.getSocket().isClosed()) {
					OutputStream out = payload.getSocket().getOutputStream();
					
					Out.log("ISOConnection", "Sending data...", callbackMap.get(threadName));
					
					out.write(payload.getData());
			        out.flush();
				}
				else {
					Out.log("ISOConnection", "Impossible to send the payload, the socket is closed!", callbackMap.get(threadName));
				}
			}
		}

	}
}