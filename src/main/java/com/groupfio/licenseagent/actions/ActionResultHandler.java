package com.groupfio.licenseagent.actions;

import org.apache.log4j.Logger;

import com.groupfio.agent.ValidationState;
import com.groupfio.licenseagent.pojo.ActionResult;
import com.groupfio.licenseagent.pojo.LicFile;

public class ActionResultHandler {

	private ValidationState validation;
	private static Logger logger = Logger.getLogger(ActionResultHandler.class);

	public ActionResultHandler(ValidationState validation) {
		this.validation = validation;
	}

	public void handleResponse(ActionResult actionResult) {
		//toString only prints obj ref for some reason
		//logger.info("inside handleResponse with actionResult: "+actionResult.toString());
		logger.info("inside handleResponse with actionResult.getAction().trim(): "+actionResult.getAction().trim());
		switch (actionResult.getAction().trim()) {
		case "ChecksumAndFileSize":
			logger.info("ChecksumAndFileSize action result: "
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
