package com.groupfio.agent.actions.checks;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Date;

import org.apache.log4j.Logger;

import com.groupfio.agent.actions.RemoteAction;
import com.groupfio.agent.config.Config;
import com.groupfio.agent.stomp.Websocket;
import com.groupfio.message.pojo.ActionMessageConstants;
import com.groupfio.message.pojo.LicFileMessage;
import com.groupfio.message.pojo.Message.Origin;

public class CheckLicFile extends RemoteAction {

	private static Logger log = Logger.getLogger(CheckLicFile.class);

	public CheckLicFile(Websocket websocket) {
		super(websocket);
	}

	public void run() {
		if (super.isWebSocketSessionOpen()) {
			log.debug("CheckLicFile running at " + (new Date().toString()));
			// send Test LicFileMessage Object Message
			LicFileMessage lf = new LicFileMessage();
			lf.setAction(ActionMessageConstants.LIC_FILE_ACTION_MSG);
			lf.setOrigin(Origin.CLIENT);
			/*lf.setLicfileByteSize(1000000);
			lf.setLicfileCheckSum("aaaaaa");*/
			doLicFileChecksum(lf);
			doLicFileSize(lf);
			
			send(lf, ActionMessageConstants.LIC_FILE_ACTION_DESTINATION);
		} else {
			log.error("Web Socket Session Closed - dropping operation run.");
		}
		
	}

	
	
	private void doLicFileSize(LicFileMessage lf){
		//TODO change db table data type to big int and map
		int size = (int)(new File(Config.getProp("licfile")).length());
		lf.setLicfileByteSize(size);
	}
	
	private void doLicFileChecksum(LicFileMessage lf){
		try {
			lf.setLicfileCheckSum(MD5Checksum.getMD5Checksum(Config.getProp("licfile")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

	
	static class MD5Checksum {

		   public static byte[] createChecksum(String filename) throws Exception {
		       InputStream fis =  new FileInputStream(filename);

		       byte[] buffer = new byte[1024];
		       MessageDigest complete = MessageDigest.getInstance("MD5");
		       int numRead;

		       do {
		           numRead = fis.read(buffer);
		           if (numRead > 0) {
		               complete.update(buffer, 0, numRead);
		           }
		       } while (numRead != -1);

		       fis.close();
		       return complete.digest();
		   }

		   // see this How-to for a faster way to convert
		   // a byte array to a HEX string
		   public static String getMD5Checksum(String filename) throws Exception {
		       byte[] b = createChecksum(filename);
		       String result = "";

		       for (int i=0; i < b.length; i++) {
		           result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		       }
		       return result;
		   }

		  
		}
	


}
