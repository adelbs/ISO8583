package org.adelbs.iso8583.clientserver;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ISOServer extends GenericClientServerThread {

	private ServerSocket listener = null;
	private Socket socket = null;
	private InputStream input = null;
	private OutputStream out = null;

	private InetSocketAddress address;
	private CallbackAction callback;
	
	private byte[] responsePayload = null;
	
	public ISOServer(String host, int port, CallbackAction callback) {
		address = new InetSocketAddress(host, port);
		this.callback = callback;
	}
	
	public void run() {
		try {
			boolean running = true;
			
			while (running) {
				if (listener == null) {
					listener = new ServerSocket();
					listener.bind(address, 100);
					
					callback.log("The Server is up!");
					callback.log("Waiting the request...");
		
					socket = listener.accept();
					
					//Mensagem recebida
					input = socket.getInputStream();
					
					callback.log("Receiving bytes...");
					
					byte[] data = new byte[4];
					while (input.read(data) < 4);
					
					int messageSize = Integer.parseInt(new String(data));
					data = new byte[messageSize];
					while (input.read(data) < messageSize);
					
					callback.log("Bytes received: " + bytesToConsole(data));
					callback.log("Parsing bytes...");
					
					callback.dataReceived(data);
		
					callback.log("Select the response...");
				}
				else if (responsePayload == null) {
					sleep(500);
				}
				else {
			        //Retorno
					callback.log("Sending the response...");
					out = socket.getOutputStream();
			        out.write(responsePayload);
			        out.flush();
			        
			        running = false;
				}
			}			
		}
		catch (Exception x) {
			x.printStackTrace();
			callback.log("ERROR:");
			callback.log(x.getMessage());
		}
		finally {
			if (listener != null) try {listener.close();} catch (Exception x) {x.printStackTrace();}
			if (socket != null) try {socket.close();} catch (Exception x) {x.printStackTrace();}
			if (input != null) try {input.close();} catch (Exception x) {x.printStackTrace();}
			if (out != null) try {out.close();} catch (Exception x) {x.printStackTrace();}
			
			callback.log("Connection closed.");
			callback.end();
		}
	}
	
	public void setResponsePayload(byte[] data) {
		responsePayload = data;
	}
}
