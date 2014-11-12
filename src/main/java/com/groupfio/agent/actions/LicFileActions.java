package com.groupfio.agent.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Date;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.groupfio.agent.config.Config;
import com.groupfio.agent.pojo.LicFile;
import com.groupfio.agent.pojo.LicFile.LicFileAction;
import com.groupfio.agent.stomp.Websocket;
import com.groupfio.pgp.PGPProcessor;

public class LicFileActions extends Action {

	private static Logger logger = Logger.getLogger(LicFileActions.class);

	public LicFileActions(Websocket websocket) {
		super(websocket);
	}

	public void run() {
		if (super.isWebSocketSessionOpen()) {
			logger.debug("LicFileActions running at " + (new Date().toString()));
			// send Test LicFile Object Message
			LicFile lf = new LicFile();
			lf.setAction(LicFileAction.ChecksumAndFileSize);
			/*lf.setLicfileByteSize(1000000);
			lf.setLicfileCheckSum("aaaaaa");*/
			doLicFileChecksum(lf);
			doLicFileSize(lf);
			
			String licfilejson = null;
			try {
				licfilejson = new ObjectMapper().writeValueAsString(lf);
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			super.getStompHandler().send(null, null, licfilejson, 15, true);
		} else {
			logger.error("Web Socket Session Closed - dropping operation run.");
		}
	}
	
	private void doLicFileSize(LicFile lf){
		//TODO change db table data type to big int and map
		int size = (int)(new File(Config.getProp("licfile")).length());
		lf.setLicfileByteSize(size);
	}
	
	private void doLicFileChecksum(LicFile lf){
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
