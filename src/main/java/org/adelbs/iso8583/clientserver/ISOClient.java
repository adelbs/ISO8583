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
import org.adelbs.iso8583.util.Out;

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
		long parsingTime = 0;
		int lx;
		int ix;
		Out.log("ISOClient", "Client connected " + clientName, callback);
		
		try {
			while (isConnected && (isoServer == null || isoServer.isConnected())) {
				
				input = socket.getInputStream();
			
				try {
					bytes = new ArrayList<Byte>();
					
					byte bRead;
					parsingTime = 0;
					lx=0;
					
					while (this.isConnected) {
						ix=input.read();
						if(ix>=0){
							bRead = new Byte((byte) ix);
							//bRead = new Byte((byte) input.read());
							//if (bRead == -1) {
								//this.isConnected = false;
								//break;
							//}
							
							//if (bRead != -1) bytes.add(new Byte(bRead));
							bytes.add(new Byte(bRead));
							if (isoConfig.getDelimiter().isPayloadComplete(bytes, isoConfig)) {
								//Message complete.
								break;
							}
							
							if (parsingTime == 0) parsingTime = System.currentTimeMillis();
						}
					}
					
					if (this.isConnected) {
						byte[] data = isoConfig.getDelimiter().clearPayload(ISOUtils.listToArray(bytes), isoConfig);
						
						
						registerActionTimeMilis();
						if(data.length!=65537) {
							payloadQueue.addPayloadIn(new SocketPayload(data, socket));
						}
					}
				}
				catch (InvalidPayloadException e) {
					Out.log("ISOClient", "Invalid Payload ("+ e.getMessage() +")", callback);
				}
				catch (Exception ex) {
					System.out.println("2Catch Unknown error ("+ ex.getMessage() +")");
				}
			}
		}
		catch (Exception x) {
			Out.log("ISOClient", "1catch Error "+ x.getMessage(), callback);
			Out.log("ISOClient", "IsConnected = "+ isConnected, callback);
		}
		finally {
			try {
				socket.close();
			}
			catch (Exception x) {
				System.out.println(x);
			}
			finally {
				System.out.println("2finally");
				socket = null;
			}
		}
		
		Out.log("ISOClient", "Client disconnected " + clientName, callback);
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
