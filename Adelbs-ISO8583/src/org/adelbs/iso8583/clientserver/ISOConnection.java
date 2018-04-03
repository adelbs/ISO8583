package org.adelbs.iso8583.clientserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.adelbs.iso8583.constants.ForceCloseConnection;
import org.adelbs.iso8583.exception.ConnectionException;
import org.adelbs.iso8583.exception.InvalidPayloadException;
import org.adelbs.iso8583.exception.ParseException;
import org.adelbs.iso8583.helper.Iso8583Config;
import org.adelbs.iso8583.util.ISOUtils;


public class ISOConnection {

	private final static int SLEEP_TIME = 500;
	
	private boolean running = false;
	private long lastAction = 0;
	
	private ServerSocket listener = null;
	private Socket socket = null;
	private InputStream input = null;
	private OutputStream out = null;

	private InetSocketAddress address;
	private Iso8583Config isoConfig;
	private CallbackAction callback;

	private final PayloadQueue payloadQueue = new PayloadQueue();
	private Receiver receiver = new Receiver();
	private Sender sender = new Sender();

	private boolean isServer;
	private String host;
	private int port;
	private int timeout;
	
	public ISOConnection(boolean isServer, String host, int port, int timeout) {
		address = new InetSocketAddress(host, port);
		this.isServer = isServer;
		this.host = host;
		this.port = port;
		this.timeout = (timeout * 1000);
	}
	
	public void connect() throws IOException, ConnectionException {
		if (isoConfig != null && callback != null) {
			if(isoConfig.getXmlFilePath()!=null){
				registerActionTimeMilis();
				if (isServer) {
					listener = new ServerSocket();
					listener.bind(address, 100);
					
					callback.log("The Server is up!");
					callback.log("Waiting the request...");
				}
				else {
					callback.log("Opening connection at " + host + ":" + port + "...");
					socket = new Socket(host, port);
				}
				
				running = true;
				receiver.start();
				sender.start();
			}else{
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
	
	public void resetSocket() {
		if (socket != null) try {socket.close();} catch (Exception x) {x.printStackTrace();}
		socket = null;
	}
	
	public void endConnection() {
		running = false;
		lastAction = 0;
		
		if (listener != null) try {listener.close();} catch (Exception x) {x.printStackTrace();}
		if (socket != null) try {socket.close();} catch (Exception x) {x.printStackTrace();}
		if (input != null) try {input.close();} catch (Exception x) {x.printStackTrace();}
		if (out != null) try {out.close();} catch (Exception x) {x.printStackTrace();}

		listener = null;
		socket = null;
		input = null;
		out = null;

		receiver = null;
		sender = null;
	}
	
	private boolean checkForceCloseConnection(List<Byte> bytes) {
		boolean isClose = false;
		
		try {
			isClose = new String(ForceCloseConnection.CLOS.bytes).equals(new String(ISOUtils.listToArray(bytes)));
		}
		catch (Exception e) { }
		
		if (isClose)
			endConnection();

		return isClose;
	}
	
	private String bytesToConsole(byte[] data) {
		String result = "";
		
		for (int i = 0; i < data.length; i++)
			result += "byte[" + i + "]{" + data[i] + "},\n";
		
		return result;
	}
	
	private long startOfWaitNextRequest = 0;
	public void processNextPayload(boolean waitIfThereIsNothingAtQueue, int keepaliveTimeout) throws ParseException, InterruptedException {
		
		if (waitIfThereIsNothingAtQueue) {
			startOfWaitNextRequest = System.currentTimeMillis();
			receiver.waitRequest(keepaliveTimeout);
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
	
	public void sendBytes(byte[] data, boolean waitReponse) throws IOException, ParseException, InterruptedException {
		payloadQueue.addPayloadOut(data);
		
		//TODO INVESTIGAR
		//if (resetSocket)
		//	resetSocket();
		
		if (waitReponse)
			receiver.waitRequest(0);
	}
	
	private void registerActionTimeMilis() {
		lastAction = System.currentTimeMillis();
	}
	
	private abstract class DefThread extends Thread {
		public void run() {
			try {
				while (running) {
					try {
						exec();
						sleep(SLEEP_TIME);
					}
					catch (SocketException se) {
						callback.log("Client disconnected...");
						resetSocket();
					}
					
					if (timeout < (System.currentTimeMillis() - lastAction)) {
						callback.log("Connection timeout.");
						running = false;
						lastAction = 0;
					}
				}
			}
			catch (InterruptedException | ParseException | IOException x) {
				if (running) {
					x.printStackTrace();
					callback.log("ERROR:");
					callback.log(x.getMessage());
				}
			}
			finally {
				if (listener != null) try {listener.close();} catch (Exception x) {x.printStackTrace();}
				if (socket != null) try {socket.close();} catch (Exception x) {x.printStackTrace();}
				if (input != null) try {input.close();} catch (Exception x) {x.printStackTrace();}
				if (out != null) try {out.close();} catch (Exception x) {x.printStackTrace();}
				
				running = false;
				
				endConnection();
				
				callback.log("Connection closed.");
				callback.end();
			}
		}
		
		protected abstract void exec() throws IOException, ParseException;
	}
	
	private class Receiver extends DefThread {
		
		private List<Byte> bytes;
		
		protected void exec() throws IOException, ParseException {
			if (socket == null) {
				if (isServer) {
					socket = listener.accept();
					callback.log("Client connected!");
					socket.sendUrgentData(1);
					registerActionTimeMilis();
				}
				else {
					socket = new Socket(host, port);
				}
			}
			
			callback.log("Waiting for the bytes...");
			input = socket.getInputStream();
		
			try {
				bytes = new ArrayList<Byte>();
				while (!socket.isClosed() && !checkForceCloseConnection(bytes) && !isoConfig.getDelimiter().isPayloadComplete(bytes, isoConfig))
					bytes.add(new Byte((byte) input.read()));
				
				byte[] data = isoConfig.getDelimiter().clearPayload(ISOUtils.listToArray(bytes), isoConfig);
				
				/*test*/
				//callback.log("Bytes received: " + bytesToConsole(data));
				callback.log("---");
				callback.log("Total Bytes received: " + bytesToConsole(ISOUtils.listToArray(bytes)));
				callback.log("---");
				/*test*/
				registerActionTimeMilis();
				
				payloadQueue.addPayloadIn(data);
			}
			catch (InvalidPayloadException e) {
				callback.log("Invalid Payload ("+ e.getMessage() +")");
				resetSocket();
			}
		}
		
		public void waitRequest(int keepaliveTimeout) throws InterruptedException {
			while (running && !payloadQueue.hasMorePayloadIn()) {
				sleep(SLEEP_TIME);
				
				if (keepaliveTimeout > 0 && (System.currentTimeMillis() - startOfWaitNextRequest) > (keepaliveTimeout * 1000)) {
					startOfWaitNextRequest = System.currentTimeMillis();
					callback.keepalive();
				}
				
			}
		}
	}
	
	private class Sender extends DefThread {
		protected void exec() throws IOException {
			if (payloadQueue.hasMorePayloadOut())
		        send(payloadQueue.getNextPayloadOut());
		}
		
		public void send(byte[] data) throws IOException {
			synchronized(this) {
				registerActionTimeMilis();
				
				if (socket != null && !socket.isClosed()) {
					out = socket.getOutputStream();
					
					callback.log("Sending the payload...");
			        out.write(data);
			        out.flush();
				}
				else {
					callback.log("Impossible to send the payload, the socket is closed!");
				}
			}
		}
	}
}
