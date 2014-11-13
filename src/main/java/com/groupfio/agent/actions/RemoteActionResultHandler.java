package com.groupfio.agent.actions;

import org.apache.log4j.Logger;

import com.groupfio.agent.ValidationState;
import com.groupfio.agent.config.Config;
import com.groupfio.agent.pojo.ActionResult;
import com.groupfio.agent.pojo.LicFile;

public class RemoteActionResultHandler {

	private ValidationState validation;
	private static Logger log = Logger.getLogger(RemoteActionResultHandler.class);

	public RemoteActionResultHandler(ValidationState validation) {
		this.validation = validation;
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
				validation.setShouldShutdown(false);
			}else if(LicFile.VFAIL.equals(actionResult.getActionResultMsg())){
				log.debug(LicFile.VFAIL+" calling shutdown shutdown");
				validation.setShouldShutdown(true);
				
				if(Config.getProp("use.direct.system.exit").equalsIgnoreCase("true")){

					//a -javaagent app is in the same jvm instance as the target so there is no need
					//to use a transformer in this case
					System.exit(0);
				}
			}
			break;
		}

	}

}
