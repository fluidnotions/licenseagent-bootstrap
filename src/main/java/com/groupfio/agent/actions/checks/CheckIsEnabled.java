package com.groupfio.agent.actions.checks;

import java.util.Date;

import org.apache.log4j.Logger;

import com.groupfio.agent.actions.RemoteAction;
import com.groupfio.agent.stomp.Websocket;
import com.groupfio.message.pojo.ActionMessageConstants;
import com.groupfio.message.pojo.Message;
import com.groupfio.message.pojo.Message.Origin;

public class CheckIsEnabled  extends RemoteAction {

	private static Logger log = Logger.getLogger(CheckIsEnabled.class);

	public CheckIsEnabled(Websocket websocket) {
		super(websocket);
	}

	public void run() {
		if (super.isWebSocketSessionOpen()) {
			Message act = new Message();
			act.setOrigin(Origin.CLIENT);
			act.setAction(ActionMessageConstants.IS_ENABLED_ACTION_MSG);
			
			send(act, ActionMessageConstants.IS_ENABLED_ACTION_DESTINATION);
			log.debug("CheckIsEnabled running at " + (new Date().toString()));
		}
	}

}
