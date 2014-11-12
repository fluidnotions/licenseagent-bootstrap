package com.groupfio.agent.stomp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.groupfio.pgp.PGPProcessor;

public class Frame {


	private String command;
	private Map<String, String> headers;
	private String body;

	private static Logger logger = Logger.getLogger(Frame.class);

	/**
	 * Constructor of a Frame object. All parameters of a frame can be
	 * instantiate
	 * 
	 * @param command
	 * @param headers
	 * @param body
	 */
	public Frame(String command, Map<String, String> headers, String body) {
		this.command = command;
		this.headers = headers != null ? headers
				: new HashMap<String, String>();
		this.body = body != null ? body : "";
	}

	public String getCommand() {
		return command;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public String getBody() {
		return body;
	}

	/**
	 * Transform a frame object into a String. This method is copied on the
	 * objective C one, in the MMPReactiveStompClient library
	 * 
	 * @return a frame object convert in a String
	 */
	private String toStringg() {
		String strLines = this.command;
		strLines += Byte.LF;
		for (String key : this.headers.keySet()) {
			strLines += key + ":" + this.headers.get(key);
			strLines += Byte.LF;
		}
		strLines += Byte.LF;
		strLines += this.body;
		strLines += Byte.NULL;

		return strLines;
	}

	/**
	 * Create a frame from a received message. This method is copied on the
	 * objective C one, in the MMPReactiveStompClient library
	 * 
	 * @param data
	 *            a part of the message received from network, which represented
	 *            a frame
	 * @return An object frame
	 */
	public static Frame fromString(String data) {
		List<String> contents = new ArrayList<String>(Arrays.asList(data
				.split(Byte.LF)));

		while (contents.size() > 0 && contents.get(0).equals("")) {
			contents.remove(0);
		}

		String command = contents.get(0);
		Map<String, String> headers = new HashMap<String, String>();
		String body = "";

		contents.remove(0);
		boolean hasHeaders = false;
		for (String line : contents) {
			if (hasHeaders) {
				for (int i = 0; i < line.length(); i++) {
					Character c = line.charAt(i);
					if (!c.equals('\0'))
						body += c;
				}
			} else {
				if (line.equals("")) {
					hasHeaders = true;
				} else {
					String[] header = line.split(":");
					headers.put(header[0], header[1]);
				}
			}
		}
		return new Frame(command, headers, body);
	}

	/**
	 * Create a frame with based fame component and convert them into a string
	 * 
	 * @param command
	 * @param headers
	 * @param body
	 * @return a frame object convert in a String, thanks to
	 *         <code>toStringg()</code> method
	 */
	public static String marshall(String command, Map<String, String> headers,
			String body) {
		return marshall( command, headers,
				 body, false);
	}
	
	public static String marshall(String command, Map<String, String> headers,
			String body, boolean encrypt){
		Frame frame = new Frame(command, headers, body);
		String frameString = frame.toStringg();
		if(encrypt){
			frameString = new String(PGPProcessor.encryptByteArray(frameString.getBytes()));
		}
		return frameString;
	}

	private class Byte {
		public static final String LF = "\n";
		public static final String NULL = "\0";
	}
}
