package com.groupfio.licenseagent.stomp;

public class Subscription {

	private String id;

	private String destination;

	public Subscription(String destination) {
		this.destination = destination;

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDestination() {
		return destination;
	}

}
