package org.adelbs.iso8583.clientserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.adelbs.iso8583.exception.ConnectionException;
import org.adelbs.iso8583.exception.ParseException;
import org.adelbs.iso8583.protocol.ISOMessage;
import org.adelbs.iso8583.vo.FieldVO;
import org.adelbs.iso8583.vo.MessageVO;
import org.xml.sax.SAXException;

class MockISOClient extends MockISOConnection implements Callable<MockResult> {
	
	private MockClientCallback callback = new MockClientCallback(ISOCONFIG);
	
	public MockISOClient(final String host, final int port) throws IOException, ConnectionException {
		this.conn = new ISOConnection(false, host, port, 1000);
		conn.setIsoConfig(ISOCONFIG);
		conn.putCallback(String.valueOf(Thread.currentThread().getId()), callback);
		conn.connect(String.valueOf(Thread.currentThread().getId()));
		System.out.println("Client: Connected");
	}
	
	@Override
	public MockResult call() throws MockISOException {
		try {
			final MessageVO mockMessage = MockMessageFactory.createMockMessage(DEFAULT_MESSAGE_TYPE, ISOCONFIG);
			final MessageVO messageToBeSent = mockMessage.getInstanceCopy();
			final ArrayList<FieldVO> fieldList = messageToBeSent.getFieldList();
			for (FieldVO fieldVO : fieldList) {
				fieldVO.setValue("A");
			}
			
			final ISOMessage sendISOMessage = new ISOMessage(messageToBeSent);
			final byte[] data = ISOCONFIG.getDelimiter().preparePayload(sendISOMessage, ISOCONFIG);
			
			conn.send(new SocketPayload(data, conn.getClientSocket()));
			System.out.println("Client: Message Sent");
			
			System.out.println("Client: Waiting Response");
			conn.processNextPayload(String.valueOf(Thread.currentThread().getId()), true, 0);
			
			Thread.sleep(100);
			
			//TODO Change to TaskResult(MEssageSent, MEssageReceived)
			final MessageVO responseMessage = callback.getResponseMessage();
			return new MockResult(messageToBeSent, responseMessage);
		} 
		catch (XPathExpressionException | SAXException | 
				IOException | ParserConfigurationException | 
				ParseException | InterruptedException e) {
			System.out.println("Client: "+e.getMessage());
			throw new MockISOException(e);
		}
		finally{
			System.out.println("Client: Close Connection");
			terminate();
		}
	}

}
