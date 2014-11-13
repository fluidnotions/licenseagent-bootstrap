package com.groupfio.agent.actions;

import org.apache.log4j.Logger;

import com.groupfio.agent.Controller;
import com.groupfio.agent.config.Config;
import com.groupfio.agent.pojo.ActionResult;
import com.groupfio.agent.pojo.LicFile;

public class RemoteActionResultHandler {

	private Controller controller;
	private static Logger log = Logger.getLogger(RemoteActionResultHandler.class);

	public RemoteActionResultHandler(Controller controller) {
		this.controller = controller;
	}

	public void handleResponse(ActionResult actionResult) {
		//toString only prints obj ref for some reason
		//log.debug("inside handleResponse with actionResult: "+actionResult.toString());
		log.debug("inside handleResponse with actionResult.getAction().trim(): "+actionResult.getAction().trim());
		switch (actionResult.getAction().trim()) {
		case "ChecksumAndFileSize":
			log.debug("ChecksumAndFileSize action result: "
					+ actionResult.getActionResultMsg());
			if(LicFile.VPASS.equals(actionResult.getActionResultMsg())){
				
			}else if(LicFile.VFAIL.equals(actionResult.getActionResultMsg())){
				log.debug(LicFile.VFAIL+" calling shutdown shutdown");
				controller.shutdown();
			}
			break;
		}

	}

}
