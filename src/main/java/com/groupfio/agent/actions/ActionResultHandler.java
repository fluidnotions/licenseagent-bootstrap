package com.groupfio.agent.actions;

import org.apache.log4j.Logger;

import com.groupfio.agent.ValidationState;
import com.groupfio.agent.pojo.ActionResult;
import com.groupfio.agent.pojo.LicFile;

public class ActionResultHandler {

	private ValidationState validation;
	private static Logger logger = Logger.getLogger(ActionResultHandler.class);

	public ActionResultHandler(ValidationState validation) {
		this.validation = validation;
	}

	public void handleResponse(ActionResult actionResult) {
		//toString only prints obj ref for some reason
		//logger.debug("inside handleResponse with actionResult: "+actionResult.toString());
		logger.debug("inside handleResponse with actionResult.getAction().trim(): "+actionResult.getAction().trim());
		switch (actionResult.getAction().trim()) {
		case "ChecksumAndFileSize":
			logger.debug("ChecksumAndFileSize action result: "
					+ actionResult.getActionResultMsg());
			if(LicFile.VPASS.equals(actionResult.getActionResultMsg())){
				validation.setShouldShutdown(false);
			}else if(LicFile.VFAIL.equals(actionResult.getActionResultMsg())){
				 validation.setShouldShutdown(true);
			}
			break;
		}

	}

}
