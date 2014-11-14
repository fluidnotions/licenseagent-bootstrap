package com.groupfio.agent.actions;

import org.apache.log4j.Logger;

import com.groupfio.agent.Controller;
import com.groupfio.agent.config.Config;
import com.groupfio.message.pojo.ActionMessageConstants;
import com.groupfio.message.pojo.LicFileMessage;
import com.groupfio.message.pojo.Message;

public class RemoteActionHandler {

	private Controller controller;
	private static Logger log = Logger.getLogger(RemoteActionHandler.class);

	public RemoteActionHandler(Controller controller) {
		this.controller = controller;
	}

	public void handleResponse(Message actionResult) {
		log.debug("inside handleResponse with actionResult: "+actionResult.toString());
		switch (actionResult.getAction().trim()) {
		case ActionMessageConstants.LIC_FILE_ACTION_MSG:
			log.debug("ChecksumAndFileSize action result: "
					+ actionResult.getActionMsg());
			if(ActionMessageConstants.LIC_FILE_VPASS.equals(actionResult.getActionMsg())){
				log.debug(ActionMessageConstants.LIC_FILE_VPASS+" nothing to handle");
			}else if(ActionMessageConstants.LIC_FILE_VFAIL.equals(actionResult.getActionMsg())){
				log.debug(ActionMessageConstants.LIC_FILE_VFAIL+" calling shutdown");
				controller.shutdown();
			}
			break;
		case ActionMessageConstants.TERM_ACTION_MSG:
			log.debug("Terminate calling shutdown shutdown");
			controller.shutdown();
			break;
		}

	}

}
