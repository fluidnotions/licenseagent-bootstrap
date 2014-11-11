package com.groupfio.licenseagent.actions;

import com.groupfio.licenseagent.stomp.StompHandler;
import com.groupfio.licenseagent.stomp.Websocket;

public abstract class Action implements Runnable {

	private Websocket websocket;

	public Action(Websocket websocket) {
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
