package org.adelbs.iso8583.clientserver;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.adelbs.iso8583.exception.InvalidPayloadException;
import org.adelbs.iso8583.helper.Iso8583Config;
import org.adelbs.iso8583.util.ISOUtils;

public class ISOClient extends Thread {

	private Iso8583Config isoConfig;
	private PayloadQueue payloadQueue;
	private CallbackAction callback;
	
	private ISOServer isoServer;
	private Socket socket;
	private InputStream input;
	private List<Byte> bytes;
	
	private boolean isConnected;

	public ISOClient(ISOServer isoServer, Socket cliSocket, Iso8583Config isoConfig, PayloadQueue payloadQueue, CallbackAction callback) {
		this.isoServer = isoServer;
		this.socket = cliSocket;
		this.isConnected = true;
		this.isoConfig = isoConfig;
		this.payloadQueue = payloadQueue;
		this.callback = callback;
		
		setName("Client-"+ cliSocket.getInetAddress().getHostAddress() + "-" + cliSocket.getPort());
		
		start();
	}

	public ISOClient(String host, int port, Iso8583Config isoConfig, PayloadQueue payloadQueue, CallbackAction callback) throws UnknownHostException, IOException {
		this(null, new Socket(host, port), isoConfig, payloadQueue, callback);
	}

	public void run() {
		String clientName = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
		callback.log("Client connected " + clientName);
		
		try {
			while (isConnected && (isoServer == null || isoServer.isConnected())) {
				
				input = socket.getInputStream();
			
				try {
					bytes = new ArrayList<Byte>();
					
					byte bRead;
					while (this.isConnected) {
						bRead = new Byte((byte) input.read());
						if (bRead == -1) {
							this.isConnected = false;
							break;
						}

						if (bRead != -1) bytes.add(new Byte(bRead));
						if (isoConfig.getDelimiter().isPayloadComplete(bytes, isoConfig)) break;
					}
					
					if (this.isConnected) {
						byte[] data = isoConfig.getDelimiter().clearPayload(ISOUtils.listToArray(bytes), isoConfig);
						callback.log("Bytes received ("+ clientName +"): " + bytesToConsole(ISOUtils.listToArray(bytes)));
						registerActionTimeMilis();
						payloadQueue.addPayloadIn(new SocketPayload(data, socket));
					}
				}
				catch (InvalidPayloadException e) {
					callback.log("Invalid Payload ("+ e.getMessage() +")");
				}
			}
		}
		catch (Exception x) {
			if (isConnected) callback.log(x.getMessage());
		}
		finally {
			try {
				socket.close();
			}
			catch (Exception x) {
				System.out.println(x);
			}
			finally {
				socket = null;
			}
		}
		
		callback.log("Client disconnected " + clientName);
	}

	private String bytesToConsole(byte[] data) {
		String result = "";
		
		for (int i = 0; i < data.length; i++)
			result += "byte[" + i + "]{" + data[i] + "},\n";
		
		return result;
	}
	
	public Socket getSocket() {
		return socket;
	}

	public void registerActionTimeMilis() {
		if (isoServer != null) isoServer.registerActionTimeMilis();
	}

	public void closeConnection() {
		this.isConnected = false;
		try {
			socket.close();
		}
		catch (Exception x) {
			System.out.println(x);
		}
		finally {
			socket = null;
		}
	}
}
