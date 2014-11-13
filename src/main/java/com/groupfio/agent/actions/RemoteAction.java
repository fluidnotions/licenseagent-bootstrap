package com.groupfio.agent.actions;

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

	public abstract void run();

}
