package org.adelbs.iso8583.clientserver;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.adelbs.iso8583.helper.Iso8583Config;

abstract class MockISOConnection {
	protected ISOConnection conn;
	protected static Iso8583Config ISOCONFIG;
	protected static String DEFAULT_MESSAGE_TYPE = "0200";
	
	static{
		final URL location = MockISOConnection.class.getProtectionDomain().getCodeSource().getLocation();
		final Path rootClassPath = Paths.get(location.getPath().substring(1));
		final Path isoConfigXMLPath = Paths.get(rootClassPath.toString(),"resources","MockXML.xml");
		ISOCONFIG = new Iso8583Config(isoConfigXMLPath.toString());
	}
	
	public void terminate(){
		if(conn!=null){
			conn.endConnection(String.valueOf(Thread.currentThread().getId()));
			conn = null;
			System.out.println("Connection terminated");
		}
	}
}
