package org.adelbs.iso8583.clientserver;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ISOClient extends GenericClientServerThread {

	private Socket socket = null;
	private InputStream input;
	private OutputStream out = null;

	private String host;
	private int port;
	
	private byte[] payload;
	private CallbackAction callback;
	
	public ISOClient(String host, int port, byte[] payload, CallbackAction callback) {
		this.host = host;
		this.port = port;
		this.payload = payload;
		this.callback = callback;
	}
	
	public void run() {
		try {
			sendData();
		}
		catch (Exception x) {
			x.printStackTrace();
			callback.log("ERROR:");
			callback.log(x.getMessage());
		}
	}
	
	public void sendData() throws Exception {
		try {
			callback.log("Oppening connection at " + host + ":" + port + "...");
			socket = new Socket(host, port);
			out = socket.getOutputStream();
			
			callback.log("Sending the payload...");
	        out.write(payload);
	        out.flush();
	        
	        callback.log("Payload sent! Waiting the response...");
	        
	        //Lendo o retorno
	        input = socket.getInputStream();
	        
			byte[] data = new byte[4];
			while (input.read(data) < 4);
			
			callback.log("Receiving bytes...");
			
			int messageSize = Integer.parseInt(new String(data));
			data = new byte[messageSize];
			while (input.read(data) < messageSize);
			
			callback.log("Bytes received: " + bytesToConsole(data));
			callback.log("Parsing bytes...");
			
			callback.dataReceived(data);
		}
		catch (Exception x) {
			x.printStackTrace();
			callback.log("ERROR:");
			callback.log(x.getMessage());
			
			throw x;
		}
		finally {
			if (socket != null) try {socket.close();} catch (Exception x) {x.printStackTrace();}
			if (input != null) try {input.close();} catch (Exception x) {x.printStackTrace();}
			if (out != null) try {out.close();} catch(Exception x) {x.printStackTrace();}
			
			callback.log("Connection closed.");
			callback.end();
		}
	}
}
