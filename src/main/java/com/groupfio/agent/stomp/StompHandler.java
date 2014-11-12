package com.groupfio.agent.stomp;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;

import com.groupfio.agent.config.Config;

public class StompHandler {

	static Logger logger = Logger.getLogger(StompHandler.class);

	private final Session websocketSession;
	private HashMap<String, Subscription> subscriptions;
	private int counter;

	StompHandler(Session session) {
		this.websocketSession = session;
		this.subscriptions = new HashMap<String, Subscription>();
		this.counter = 0;
	}

	public void transmit(String command, Map<String, String> headers,
			String body) {
		transmit(command, headers, body, 3);
	}

	public void transmit(String command, Map<String, String> headers,
			String body, int secdelay) {
		String out = Frame.marshall(command, headers, body);
		// add content length header in if missing
		if ((body != null && body.length() > 0)
				&& !headers.containsKey(StompHandler.CONTENT_LENGTH_NAME)) {
			String bodylen = (new Integer(body.length())).toString();
			headers.put(StompHandler.CONTENT_LENGTH_NAME, bodylen);
			logger.debug(StompHandler.CONTENT_LENGTH_NAME + " of " + bodylen
					+ " added to headers");
		}
		try {
			Future<Void> fut = this.websocketSession.getRemote()
					.sendStringByFuture(out);
			fut.get(secdelay, TimeUnit.SECONDS);
			logger.debug(out);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Close the web socket connection with the server. Operations order is very
	 * important
	 */
	public void disconnect() {
		if (this.websocketSession.isOpen()) {
			transmit(COMMAND_DISCONNECT, null, null);
		}
	}

	/**
	 * Send a simple message to the server thanks to the body parameter
	 * 
	 * 
	 * @param destination
	 *            The destination through a StompHandler message will be send to
	 *            the server
	 * @param headers
	 *            headers of the message
	 * @param body
	 *            body of a message
	 */
	public void send(String destination, Map<String, String> headers,
			String body, int secdelay) {
		if (this.websocketSession.isOpen()) {
			if (headers == null)
				headers = new HashMap<String, String>();
			if (destination == null)
				destination = Config.getProp("wssend");

			if (body == null)
				body = "";

			headers.put(SUBSCRIPTION_DESTINATION, destination);

			transmit(COMMAND_SEND, headers, body);
		}
	}

	/**
	 * Allow a client to send a subscription message to the server independently
	 * of the initialization of the web socket. If connection have not been
	 * already done, just save the subscription
	 * 
	 * @param subscription
	 *            a subscription object
	 */
	public void subscribe(Subscription subscription) {
		subscription.setId(PREFIX_ID_SUBSCIPTION + this.counter++);
		this.subscriptions.put(subscription.getId(), subscription);

		if (this.websocketSession.isOpen()) {
			Map<String, String> headers = new HashMap<String, String>();
			headers.put(SUBSCRIPTION_ID, subscription.getId());
			headers.put(SUBSCRIPTION_DESTINATION, subscription.getDestination());

			subscribe(headers);
		}
	}

	/**
	 * Subscribe to a StompHandler channel, through messages will be send and
	 * received. A message send from a determine channel can not be receive in
	 * an another.
	 * 
	 */
	private void subscribe() {
		if (this.websocketSession.isOpen()) {
			for (Subscription subscription : this.subscriptions.values()) {
				Map<String, String> headers = new HashMap<String, String>();
				headers.put(SUBSCRIPTION_ID, subscription.getId());
				headers.put(SUBSCRIPTION_DESTINATION,
						subscription.getDestination());

				subscribe(headers);
			}
		}
	}

	/**
	 * Send the subscribe to the server with an header
	 * 
	 * @param headers
	 *            header of a subscribe STOMP message
	 */
	private void subscribe(Map<String, String> headers) {
		transmit(COMMAND_SUBSCRIBE, headers, null);
	}

	/**
	 * Destroy a subscription with its id
	 * 
	 * @param id
	 *            the id of the subscription. This id is automatically setting
	 *            up in the subscribe method
	 */
	public void unsubscribe(String id) {
		if (this.websocketSession.isOpen()) {
			Map<String, String> headers = new HashMap<String, String>();
			headers.put(SUBSCRIPTION_ID, id);

			this.subscriptions.remove(id);
			this.transmit(COMMAND_UNSUBSCRIBE, headers, null);
		}
	}

	public static final String CONTENT_LENGTH_NAME = "content-length";
	public static final String PREFIX_ID_SUBSCIPTION = "sub-";
	public static final String HEART_BEAT_NAME = "heart-beat";
	public static final String ACCEPT_VERSION_NAME = "accept-version";
	public static final String ACCEPT_VERSION = "1.1,1.0";
	public static final String COMMAND_CONNECT = "CONNECT";
	public static final String COMMAND_CONNECTED = "CONNECTED";
	public static final String COMMAND_MESSAGE = "MESSAGE";
	public static final String COMMAND_RECEIPT = "RECEIPT";
	public static final String COMMAND_ERROR = "ERROR";
	public static final String COMMAND_DISCONNECT = "DISCONNECT";
	public static final String COMMAND_SEND = "SEND";
	public static final String COMMAND_SUBSCRIBE = "SUBSCRIBE";
	public static final String COMMAND_UNSUBSCRIBE = "UNSUBSCRIBE";
	public static final String SUBSCRIPTION_ID = "id";
	public static final String SUBSCRIPTION_DESTINATION = "destination";
	public static final String SUBSCRIPTION_SUBSCRIPTION = "subscription";

}