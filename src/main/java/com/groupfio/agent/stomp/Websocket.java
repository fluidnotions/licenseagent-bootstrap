package com.groupfio.agent.stomp;

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
import com.groupfio.agent.ValidationClient;
import com.groupfio.agent.config.Config;
import com.groupfio.agent.pojo.ActionResult;

@WebSocket(maxTextMessageSize = 64 * 1024)
public class Websocket {

	static Logger log = Logger.getLogger(Websocket.class);

	private ValidationClient client;
	private Session session;
	private final CountDownLatch closeLatch;
	private StompHandler stompHandler;

	public Websocket(ValidationClient client) {
		this.closeLatch = new CountDownLatch(1);
		this.client = client;

	}

	public boolean awaitClose(int duration, TimeUnit unit)
			throws InterruptedException {
		return this.closeLatch.await(duration, unit);
	}

	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
		log.debug("Connection closed: " + statusCode + " - " + reason);
		this.session = null;
		this.closeLatch.countDown();
		if(!client.getController().isShouldShutdown()){
			//this setter also restarts the timer
			client.getController().setHasConnectionToServer(false);
			log.debug("attempting auto reconnect ...");
			this.client.startWebsocketConnection();
		}else{
			log.debug("not attempting auto reconnect, because controller should shutdown is set to true");
		}
	}

	// session.close(StatusCode.NORMAL, "I'm done");
	@OnWebSocketConnect
	public void onConnect(Session session) {
		log.debug("Got ws connect: " + session);
		this.session = session;
		this.stompHandler = new StompHandler(session);
		try {
			Map<String, String> headers = new HashMap<String, String>();
			headers.put(StompHandler.ACCEPT_VERSION_NAME,
					StompHandler.ACCEPT_VERSION);
			headers.put(StompHandler.HEART_BEAT_NAME, "10000,10000");
			log.debug("stompHandler connect...");
			stompHandler.transmit(StompHandler.COMMAND_CONNECT, headers, null);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@OnWebSocketMessage
	public void onMessage(String msg) {
		Frame f = Frame.fromString(msg);
		// log.debug("raw msg: "+msg);
		if (StompHandler.COMMAND_CONNECTED.equals(f.getCommand())) {
			log.debug("CONNECTED");
			//set in Controller
			client.getController().setHasConnectionToServer(true);
			// once connected
			// Subscribe to user results
			subscribe(Config.getProp("stdwssub"));
			// Subscribe to user errors
			subscribe(Config.getProp("errwssub"));
			// start running scheduled operations
			this.client.runPeriodicOperations();
		} else if (StompHandler.COMMAND_MESSAGE.equals(f.getCommand())) {
			log.debug("MESSAGE RECIEVED:");
			log.debug("headers:");
			for (Map.Entry<String, String> header : f.getHeaders().entrySet()) {
				log.debug(header.getKey() + " -> " + header.getValue());

			}
			log.debug("body:");
			log.debug(f.getBody());
			//actually errors from the server are received as text/plain;charset=UTF-8
			//we do not want to attempt to map these to an object so check header
			if (!f.getHeaders().get("content-type").equals("text/plain;charset=UTF-8")) {
				
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
				// log.debug("result.toString(): "+result.toString());
				// handle result
				client.getActionResultHandler().handleResponse(result);
			}else{
				log.error(f.getBody());
			}
		}
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
