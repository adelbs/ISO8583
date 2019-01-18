package org.adelbs.iso8583.clientserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.adelbs.iso8583.exception.ConnectionException;


class MockClientMessageExecutor {
	
	public final String host;
	private final Integer port;
	
	private static Map<String,MockClientMessageExecutor> INSTANCES = new HashMap<String,MockClientMessageExecutor>();
	
	MockClientMessageExecutor(String host, Integer port) {
		super();
		this.host = host;
		this.port = port;
	}
	
	public static MockClientMessageExecutor getInstance(final String host, final Integer port){
		final String key = host+":"+port.toString();
		MockClientMessageExecutor executor = INSTANCES.get(key);
		if(executor == null){
			executor = new MockClientMessageExecutor(host, port);
			INSTANCES.put(key, executor);
		}
		return executor;
	}


	public List<MockResult> executeSimultaneous(final int threadsToRun) throws MockISOException{
		ExecutorService executor = null;
		final List<MockResult> results = new ArrayList<MockResult>();
		try{
			executor = Executors.newFixedThreadPool(threadsToRun);
			final List<MockISOClient> clients = new ArrayList<MockISOClient>();
			for(int i=0;i<threadsToRun;i++){
				clients.add(new MockISOClient(host, port));
			}
			
			final List<Future<MockResult>> taskList = executor.invokeAll(clients);
			taskList.forEach(task->{
				try {
					results.add((MockResult) task.get());
				} catch (Exception e) {
					e.printStackTrace();
				}
			});	
		} catch (InterruptedException e1) {
			Thread.currentThread().interrupt();
			e1.printStackTrace();
		} catch (IOException | ConnectionException e) {
			throw new MockISOException(e.getMessage());
		}finally{
			if(executor != null){
				executor.shutdown();
			}
		}
		return results;
	}
}
