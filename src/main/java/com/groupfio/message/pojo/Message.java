package com.groupfio.message.pojo;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties
public class Message {

	private String serialNumber;
	private long timestamp;

	private String action;
	private String actionDescription;
	private String actionMsg;

	private Origin origin;
	private String destinationUrl;

	public Message() {
	}

	public Message(String serialNumber, long timestamp, String action,
			String actionMsg) {
		super();
		this.serialNumber = serialNumber;
		this.timestamp = timestamp;
		this.action = action;
		this.actionMsg = actionMsg;
	
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

	public String getActionDescription() {
		return actionDescription;
	}

	public void setActionDescription(String actionDescription) {
		this.actionDescription = actionDescription;
	}

	public String getActionMsg() {
		return actionMsg;
	}

	public void setActionMsg(String actionMsg) {
		this.actionMsg = actionMsg;
	};

	public Origin getOrigin() {
		return origin;
	}

	public void setOrigin(Origin origin) {
		this.origin = origin;
	}

	public String getDestinationUrl() {
		return destinationUrl;
	}

	public void setDestinationUrl(String destinationUrl) {
		this.destinationUrl = destinationUrl;
	}

	
	
	@Override
	public String toString() {
		return "Message [serialNumber=" + serialNumber + ", timestamp="
				+ timestamp + ", action=" + action + ", actionDescription="
				+ actionDescription + ", actionMsg=" + actionMsg + ", origin="
				+ origin + ", destinationUrl=" + destinationUrl + "]";
	}



	public enum Origin{
		CLIENT,
		SERVER
	}
}