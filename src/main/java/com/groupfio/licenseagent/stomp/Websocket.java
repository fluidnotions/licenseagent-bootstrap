package com.groupfio.licenseagent.stomp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.groupfio.agent.AgentPremain;
import com.groupfio.licenseagent.config.Config;
import com.groupfio.licenseagent.pojo.ActionResult;

@WebSocket(maxTextMessageSize = 64 * 1024)
public class Websocket {

	static Logger logger = Logger.getLogger(Websocket.class);

	private AgentPremain agentPremain;
	private Session session;
	private final CountDownLatch closeLatch;
	private StompHandler stompHandler;

	public Websocket(AgentPremain agentPremain) {
		this.closeLatch = new CountDownLatch(1);
		this.agentPremain = agentPremain;

	}

	

	public boolean awaitClose(int duration, TimeUnit unit)
			throws InterruptedException {
		return this.closeLatch.await(duration, unit);
	}

	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
		logger.info("Connection closed: " + statusCode + " - " + reason);
		this.session = null;
		this.closeLatch.countDown();
		logger.info("attempting auto reconnect ...");
		this.agentPremain.startWebsocketConnection();
	}

	// session.close(StatusCode.NORMAL, "I'm done");
	@OnWebSocketConnect
	public void onConnect(Session session) {
		logger.info("Got ws connect: " + session);
		this.session = session;
		this.stompHandler = new StompHandler(session);
		try {
			Map<String, String> headers = new HashMap<String, String>();
			headers.put(StompHandler.ACCEPT_VERSION_NAME,
					StompHandler.ACCEPT_VERSION);
			headers.put(StompHandler.HEART_BEAT_NAME, "10000,10000");
			logger.info("stompHandler connect...");
			stompHandler.transmit(StompHandler.COMMAND_CONNECT, headers, null);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@OnWebSocketMessage
	public void onMessage(String msg) {
		Frame f = Frame.fromString(msg);
		// logger.info("raw msg: "+msg);
		if (StompHandler.COMMAND_CONNECTED.equals(f.getCommand())) {
			logger.info("CONNECTED");
			// once connected
			// Subscribe to user results
			subscribe(Config.getProp("stdwssub"));
			// Subscribe to user errors
			subscribe(Config.getProp("errwssub"));
			// start running scheduled operations
			this.agentPremain.runPeriodicOperations();
		} else if (StompHandler.COMMAND_MESSAGE.equals(f.getCommand())) {
			logger.info("MESSAGE RECIEVED:");
			logger.info("headers:");
			for (Map.Entry<String, String> header : f.getHeaders().entrySet()) {
				logger.info(header.getKey() + " -> " + header.getValue());

			}
			logger.info("body:");
			logger.info(f.getBody());
			ActionResult result = null;
			try {
				result = new ObjectMapper().readValue(f.getBody().trim(),
						ActionResult.class);
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// logger.info("result.toString(): "+result.toString());
			// handle result
			agentPremain.getActionResultHandler().handleResponse(result);
		}
		// sendTestMessage(c++);

	}

	int c = 1;

	public void sendTestMessage(int c) {
		stompHandler.send(null, null, "{\"name\":\"msg number" + c + "\"}", 15);

	}

	public void subscribe(String dest) {
		Subscription subscription = new Subscription(dest);
		stompHandler.subscribe(subscription);
	}

	public StompHandler getStompHandler() {
		return stompHandler;
	}

	public Session getSession() {
		return session;
	}

}
