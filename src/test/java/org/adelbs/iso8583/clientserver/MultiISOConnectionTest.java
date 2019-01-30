package org.adelbs.iso8583.clientserver;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.adelbs.iso8583.exception.ConnectionException;
import org.adelbs.iso8583.vo.MessageVO;
import org.junit.BeforeClass;
import org.junit.Test;


public class MultiISOConnectionTest {
	
	private static ExecutorService THREAD_POOL;
	
	@BeforeClass
	public static void setUpBefore(){
		THREAD_POOL = Executors.newCachedThreadPool();
	}
	
	@Test
	public void test() throws MockISOException, IOException, ConnectionException{
		final String host = "localhost";
		final int port = 9010;
		
		final MockISOServer server = new MockISOServer(host, port);
		THREAD_POOL.execute(new Runnable(){
			 public void run(){
				 while(true){
					 try {
						server.process();
						Thread.sleep(1000);
					} catch (MockISOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						e.printStackTrace();
					}
				 }
			 }
		});
		
		final MockClientMessageExecutor executor = MockClientMessageExecutor.getInstance(host, port);
		try {
			final List<MockResult> results = executor.executeSimultaneous(2);
			results.forEach(result->{
				final MessageVO receivedMessage = result.getReceived();
				final MessageVO sentMessage = result.getSent();
				System.out.println("Check: "+receivedMessage.equals(sentMessage));
				//assertEquals("Message Sent should be received back",receivedMessage, sentMessage);
			});
		}finally {
			server.terminate();
		}
	}
}
