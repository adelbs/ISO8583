package org.adelbs.iso8583.clientserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

import org.adelbs.iso8583.helper.Iso8583Config;
import org.adelbs.iso8583.util.Out;

public class ISOServer extends Thread {

	private Iso8583Config isoConfig;
	private PayloadQueue payloadQueue;
	private CallbackAction callback;

	private ISOConnection isoConnection;
	private boolean isConnected;
	private ServerSocket listener;
	
	public ISOServer(ISOConnection isoConnection, String host, int port, Iso8583Config isoConfig, PayloadQueue payloadQueue, CallbackAction callback) throws IOException {
		this.isoConnection = isoConnection;
		this.isoConfig = isoConfig;
		this.payloadQueue = payloadQueue;
		this.callback = callback;
		isConnected = true;

		listener = new ServerSocket();
		listener.bind(new InetSocketAddress(host, port));
		setName("Server-"+ host +"-"+ port);
		
		start();
		
		Out.log("ISOServer", "Listening at " + host + ":" + port, callback);
	}
	
	public void run() {
		try {
			while (isConnected) {
				new ISOClient(this, listener.accept(), isoConfig, payloadQueue, callback);
			}
			
			listener = null;
		}
		catch (IOException exception) {
			isConnected = false;
			listener = null;
			
			Out.log("ISOServer", "Disconnected. "+ exception.getMessage(), callback);
		}
	}

	public void registerActionTimeMilis() {
		isoConnection.registerActionTimeMilis();
	}

	public ServerSocket getSocket() {
		return listener;
	}
	
	public boolean isConnected() {
		return this.isConnected;
	}

	public void closeConnection() {
		try {
			this.isConnected = false;
			this.listener.close();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			this.listener = null;
		}
	}
}
