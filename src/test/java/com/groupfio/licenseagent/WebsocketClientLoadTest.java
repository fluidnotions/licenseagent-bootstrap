package com.groupfio.licenseagent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.groupfio.agent.Controller;
import com.groupfio.agent.ValidationClient;

public class WebsocketClientLoadTest {
	
	private static Logger log = Logger.getLogger(WebsocketClientLoadTest.class);
	
	public static void main(String[] args) {
		String numberOfClients = null;
		if (args.length == 0) {
			log.info("Single arg indicates the numberOfClients you wish to test with ... none provided - using default 25");
			numberOfClients = "25";
		}else{
			numberOfClients = args[0];
			log.info("Single arg numberOfClients you wish to test with ... set to: "+numberOfClients);
			
		}
		loadTestWithNumberOfClients(numberOfClients);

	}

	private static void loadTestWithNumberOfClients(String numberOfClients) {

		int vol = new Integer(numberOfClients);

		ExecutorService executor = Executors.newFixedThreadPool(vol);

		for (int i = 0; i < vol; i++) {
			startNewValidationClient(i, executor);
		}
	}

	private static void startNewValidationClient(int i, ExecutorService executor) {

		executor.execute(new ValidationClient(new Controller(), "loadtest-"
				+ (new Integer(i).toString())));
	}

}
