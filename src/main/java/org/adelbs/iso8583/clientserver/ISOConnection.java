package org.adelbs.iso8583.clientserver;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

import org.adelbs.iso8583.exception.ConnectionException;
import org.adelbs.iso8583.exception.ParseException;
import org.adelbs.iso8583.helper.Iso8583Config;


public class ISOConnection {

	private final static int SLEEP_TIME = 50;
	
	private boolean running = false;
	private long lastAction = 0;
	
	private Iso8583Config isoConfig;
	private CallbackAction callback;

	private final PayloadQueue payloadQueue = new PayloadQueue();
	private Sender sender = new Sender();

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
	
	public void connect() throws IOException, ConnectionException {
		if (isoConfig != null && callback != null) {
			if(isoConfig.getXmlFilePath()!=null){
				registerActionTimeMilis();
				if (isServer) 
					this.isoServer = new ISOServer(this, host, port, isoConfig, payloadQueue, callback);
				else 
					this.isoClient = new ISOClient(host, port, isoConfig, payloadQueue, callback);
				
				running = true;
				sender.start();
			}
			else{
				throw new ConnectionException("ISOConfig is missing");
			}
		}
		else {
			throw new ConnectionException("ISOConfig or Callback is missing");
		}
	}
	
	public boolean isConnected() {
		return running;
	}
	
	public void endConnection() {
		running = false;
		lastAction = 0;
		
		if (isoClient != null) isoClient.closeConnection();
		if (isoServer != null) isoServer.closeConnection();

		isoClient = null;
		isoServer = null;
		sender = null;
	}
	
	private void waitRequest(int keepaliveTimeout) throws InterruptedException {
		while (running && !payloadQueue.hasMorePayloadIn()) {
			Thread.sleep(SLEEP_TIME);
			
			if (keepaliveTimeout > 0 && (System.currentTimeMillis() - startOfWaitNextRequest) > (keepaliveTimeout * 1000)) {
				startOfWaitNextRequest = System.currentTimeMillis();
				callback.keepalive();
			}
			
		}
	}

	private long startOfWaitNextRequest = 0;
	public void processNextPayload(boolean waitIfThereIsNothingAtQueue, int keepaliveTimeout) throws ParseException, InterruptedException {
		
		if (waitIfThereIsNothingAtQueue) {
			startOfWaitNextRequest = System.currentTimeMillis();
			waitRequest(keepaliveTimeout);
		}
		
		if (payloadQueue.hasMorePayloadIn()) {
			synchronized(this) {
				callback.log("Parsing bytes...");
				callback.dataReceived(payloadQueue.getNextPayloadIn());
			}
		}
	}
	
	public void setIsoConfig(Iso8583Config isoConfig) {
		this.isoConfig = isoConfig;
	}
	
	public void setCallback(CallbackAction callback) {
		this.callback = callback;
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
		
		Sender() {
			setName("Sender");
		}
		
		public void run() {
			try {
				while (running) {
					try {
						if (payloadQueue.hasMorePayloadOut())
					        send(payloadQueue.getNextPayloadOut());

						sleep(SLEEP_TIME);
					}
					catch (SocketException se) {
						callback.log("Client disconnected...");
					}
					
					if (timeout < (System.currentTimeMillis() - lastAction)) {
						callback.log("Connection timeout.");
						running = false;
						lastAction = 0;
					}
				}
			}
			catch (InterruptedException | IOException x) {
				if (running) {
					x.printStackTrace();
					callback.log("ERROR:");
					callback.log(x.getMessage());
				}
			}
			finally {
				endConnection();
				
				callback.log("Connection closed.");
				callback.end();
			}
		}
		
		private void send(SocketPayload payload) throws IOException {
			synchronized(this) {
				registerActionTimeMilis();
				
				if (payload.getSocket() != null && !payload.getSocket().isClosed()) {
					OutputStream out = payload.getSocket().getOutputStream();
					
					callback.log("Sending data...");
			        out.write(payload.getData());
			        out.flush();
				}
				else {
					callback.log("Impossible to send the payload, the socket is closed!");
				}
			}
		}
	}
}