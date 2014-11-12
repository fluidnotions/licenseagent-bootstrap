package com.groupfio.agent.stomp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Transform a message received from network in an object manageable by java and
 * for get back some information in this message. Make also the reverse :
 * transform an object manageable by java in a message manageable by the
 * network.
 * 
 * @version 1.0.0
 * @author Emeric Perrin
 * 
 */
public class Frame {
	// private final static String CONTENT_LENGTH = "content-length";

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

		// int divider = data.indexOf(Byte.LF + Byte.LF);
		//
		// String[] headerLines = new String[0];
		// String command = "";
		// if(divider != -1){
		// String[] headerLines_ = data.substring(0, divider).split(Byte.LF);
		// command = headerLines_[0];
		// headerLines = new String[headerLines_.length - 1];
		// for(int i = 0; i < headerLines_.length - 1; i++){
		// headerLines[i] = headerLines_[i+1];
		// }
		// }
		//
		// String[] ref = new String[headerLines.length];
		// for(int i = headerLines.length-1, j = 0 ; i >= 0; i--, j++){
		// ref[j] = headerLines[i];
		// }
		// String line;
		// int idx;
		// Map<String, String> headers = new HashMap<String, String>();
		// for (int i = 0, len = ref.length; i < len; i++) {
		// line = ref[i];
		// idx = line.indexOf(':');
		// headers.put(line.substring(0, idx).trim(), line.substring(idx +
		// 1).trim());
		// }
		// String body = "";
		// int start = divider + 2;
		//
		// if(headers.containsKey(CONTENT_LENGTH)){
		// int len = Integer.parseInt(headers.get(CONTENT_LENGTH));
		// body = data.substring(start, start + len);
		// } else {
		// String chr = null;
		// for (int i = start ,j = start, ref1 = data.length();
		// start <= ref1 ? j < ref1 : j > ref1; i = start <= ref1 ? ++j : --j) {
		// chr = String.valueOf(data.charAt(i));
		// if (chr == Byte.NULL) {
		// break;
		// }
		// body += chr;
		// }
		// }
		return new Frame(command, headers, body);
	}

	// No need this method, a single frame will be always be send because body
	// of the message will never be excessive
	// /**
	// * Transform a message received from server in a Set of objects, named
	// frame, manageable by java
	// *
	// * @param datas
	// * message received from network
	// * @return
	// * a Set of Frame
	// */
	// public static Set<Frame> unmarshall(String datas){
	// String data;
	// String[] ref = datas.split(Byte.NULL + Byte.LF + "*");//NEED TO VERIFY
	// THIS PARAMETER
	// Set<Frame> results = new HashSet<Frame>();
	//
	// for (int i = 0, len = ref.length; i < len; i++) {
	// data = ref[i];
	//
	// if ((data != null ? data.length() : 0) > 0){
	// results.add(unmarshallSingle(data));//"unmarshallSingle" is the old name
	// method for "fromString"
	// }
	// }
	// return results;
	// }

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
		Frame frame = new Frame(command, headers, body);
		return frame.toStringg();
	}

	private class Byte {
		public static final String LF = "\n";
		public static final String NULL = "\0";
	}
}
