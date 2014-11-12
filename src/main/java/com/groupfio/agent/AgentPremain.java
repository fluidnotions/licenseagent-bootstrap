package com.groupfio.agent;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
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

import com.groupfio.agent.actions.ActionResultHandler;
import com.groupfio.agent.actions.LicFileActions;
import com.groupfio.agent.config.Config;
import com.groupfio.agent.stomp.Websocket;
import com.groupfio.agent.transformers.StartupTransformer;

public class AgentPremain {

	private ValidationState validation;
	private String agentArguments;
	private Instrumentation instrumentation;
	
	private static Logger logger = Logger.getLogger(AgentPremain.class);
	private String urlString;
	private Websocket socket;
	private WebSocketClient client;
	private ActionResultHandler actionResultHandler;

	public AgentPremain(String agentArguments, Instrumentation instrumentation) {
		this.validation = new ValidationState();
		this.actionResultHandler = new ActionResultHandler(validation);
		this.agentArguments = agentArguments;
		this.instrumentation = instrumentation;
		this.urlString = Config.getProp("wsbase")
				+ Config.getProp("wsconnect");
	}

	public static void premain(String agentArguments,
			Instrumentation instrumentation) {

		System.out.println("premain");
		AgentPremain agent = new AgentPremain(agentArguments, instrumentation);
		agent.setUpTransformers();
		agent.startupChecks();
		agent.startWebsocketConnection();

	}
	
	//this is for test purposes within eclipse
	public static void main(String[] args){
		AgentPremain agent = new AgentPremain(null, null);
		agent.startupChecks();
		agent.startWebsocketConnection();
	}

	private void setUpTransformers() {
		ClassFileTransformer trans1 = new StartupTransformer(validation);
		instrumentation.addTransformer(trans1);
		ClassFileTransformer trans2 = new StartupTransformer(validation);
		instrumentation.addTransformer(trans2);
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
			cookies.add(new HttpCookie("user", Config
					.getProp("serialnum")));
			request.setCookies(cookies);
			client.connect(socket, url, request);
			logger.debug("Connecting to : " + url);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	private void startupChecks() {
		// do some basic checks before app starts
		// check that the lic file is in the expected location
		String licFileLocationProperty = Config.getProp("licfile");
		File licFile = new File(licFileLocationProperty);
		if(!licFile.exists()){
			String message = "The license file was not found at configured path ["+licFileLocationProperty+"] Shutting down...";
			logger.fatal(message);
			System.err.println(message);
			System.exit(0);
		}else{
			logger.debug("The license file was found at configured path ["+licFileLocationProperty+"]");
		}
		
	}

	public void runPeriodicOperations() {
		
		String fixeddelay = Config.getProp("fixedelay");
		
		ScheduledExecutorService scheduledExecutorService = Executors
				.newScheduledThreadPool(5);

		// run lic file operation every 5 sec
		LicFileActions lfo = new LicFileActions(socket);
		ScheduledFuture scheduledFuture = scheduledExecutorService
				.scheduleWithFixedDelay(lfo, 5, (new Integer(fixeddelay)), TimeUnit.SECONDS);

	}

	public ActionResultHandler getActionResultHandler() {
		return actionResultHandler;
	}
}
