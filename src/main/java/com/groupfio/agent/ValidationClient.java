package com.groupfio.agent;

import java.io.File;
import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import com.groupfio.agent.actions.RemoteActionResultHandler;
import com.groupfio.agent.actions.LicFileActions;
import com.groupfio.agent.config.Config;
import com.groupfio.agent.stomp.Websocket;

public class ValidationClient implements Runnable{

	private static Logger log = Logger.getLogger(ValidationClient.class);

	public ValidationState validation;
	public String urlString;
	public Websocket socket;
	public WebSocketClient client;
	public RemoteActionResultHandler remoteActionResultHandler;

	public ValidationClient(ValidationState validation) {
		this.validation = validation;
		this.remoteActionResultHandler = new RemoteActionResultHandler(validation);
		this.urlString = Config.getProp("wsbase")
				+ Config.getProp("wsconnect");
	}
	
	@Override
	public void run() {
		startupChecks();
		startWebsocketConnection();
		
	}

	public void startWebsocketConnection() {

		client = new WebSocketClient();
		socket = new Websocket(this);
		try {
			client.start();
			URI url = new URI(urlString);
			ClientUpgradeRequest request = new ClientUpgradeRequest();
			// set serialnum cookie to be used as
			ArrayList<HttpCookie> cookies = new ArrayList<HttpCookie>();
			cookies.add(new HttpCookie("user", Config.getProp("serialnum")));
			request.setCookies(cookies);
			client.connect(socket, url, request);
			log.debug("Connecting to : " + url);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private void startupChecks() {
		// do some basic checks before app starts
		// check that the lic file is in the expected location
		String licFileLocationProperty = Config.getProp("licfile");
		File licFile = new File(licFileLocationProperty);
		if (!licFile.exists()) {
			String message = "The license file was not found at configured path ["
					+ licFileLocationProperty + "] Shutting down...";
			log.fatal(message);
			System.err.println(message);
			System.exit(0);
		} else {
			log.debug("The license file was found at configured path ["
					+ licFileLocationProperty + "]");
		}

	}

	public void runPeriodicOperations() {

		String fixeddelay = Config.getProp("fixedelay");

		ScheduledExecutorService scheduledExecutorService = Executors
				.newScheduledThreadPool(5);

		// run lic file operation every 5 sec
		LicFileActions lfo = new LicFileActions(socket);
		ScheduledFuture scheduledFuture = scheduledExecutorService
				.scheduleWithFixedDelay(lfo, 5, (new Integer(fixeddelay)),
						TimeUnit.SECONDS);

	}

	public RemoteActionResultHandler getActionResultHandler() {
		return remoteActionResultHandler;
	}

	public ValidationState getValidation() {
		return validation;
	}

	

}