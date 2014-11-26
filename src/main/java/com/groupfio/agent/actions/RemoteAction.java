package com.groupfio.agent.actions;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.groupfio.agent.stomp.StompHandler;
import com.groupfio.agent.stomp.Websocket;

public abstract class RemoteAction implements Runnable {

	private Websocket websocket;

	public RemoteAction(Websocket websocket) {
		this.websocket = websocket;

	}

	public boolean isWebSocketSessionOpen() {
		return websocket.getSession().isOpen();
	}

	public StompHandler getStompHandler() {
		return websocket.getStompHandler();
	}
	
	public void send(Object value, String destination) {
		String json = null;
		try {
			json = new ObjectMapper().writeValueAsString(value);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		getStompHandler().send(destination, null, json, 15);
	}

	public abstract void run();

}
