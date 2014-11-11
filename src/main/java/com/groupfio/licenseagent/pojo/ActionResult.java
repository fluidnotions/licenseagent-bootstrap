package com.groupfio.licenseagent.pojo;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties
public class ActionResult {

	private String serialNumber;
	private long timestamp;

	private String action;
	private String actionResultMsg;

	public ActionResult() {
	}

	public ActionResult(String serialNumber, long timestamp, String action,
			String actionResultMsg) {
		super();
		this.serialNumber = serialNumber;
		this.timestamp = timestamp;
		this.action = action;
		this.actionResultMsg = actionResultMsg;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getActionResultMsg() {
		return actionResultMsg;
	}

	public void setActionResultMsg(String actionResultMsg) {
		this.actionResultMsg = actionResultMsg;
	};

}