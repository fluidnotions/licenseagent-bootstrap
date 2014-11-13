package com.groupfio.agent;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.groupfio.agent.config.Config;

public class Controller {
	
	private static Logger log = Logger.getLogger(Controller.class);
	
	private boolean hasConnectionToServer = false;
	private boolean shouldShutdown = false;
	
	public Controller() {
		hasConnectionToServerShutdownTimer();
	}
	
	private void hasConnectionToServerShutdownTimer(){
		final String timeoutseconds = Config.getProp("startup.has.server.connection.shutdown.timeout");
		  // creating timer task, timer 
	      TimerTask task = new TimerTask(){

			@Override
			public void run() {
				if(!hasConnectionToServer){
					log.debug("hasConnectionToServer is still false after "+timeoutseconds+" seconds since startup/disconnect");
					shutdown();
				}else{
					log.debug(timeoutseconds+" seconds since startup/disconnect ... hasConnectionToServer is now: "+hasConnectionToServer);
				}
				
			}
	    	  
	      };
	      Timer timer = new Timer();
	      
	      long milli = ((new Long(timeoutseconds)*1000));
	      
	      // scheduling the task - sec to millisec convert
	      timer.schedule(task, milli);
	}

	

	public void shutdown() {
		setShouldShutdown(true);
		log.debug("Shutdown called the syetem will now exit...");
		System.exit(0);
		
	}

	public boolean isHasConnectionToServer() {
		return hasConnectionToServer;
	}

	public void setHasConnectionToServer(boolean hasConnectionToServer) {
		//if it was true and is being set to false on this method call restart the shutdown timer
		if(this.hasConnectionToServer && !hasConnectionToServer){
			hasConnectionToServerShutdownTimer();
		}
		this.hasConnectionToServer = hasConnectionToServer;
	}

	public boolean isShouldShutdown() {
		return shouldShutdown;
	}

	public void setShouldShutdown(boolean shouldShutdown) {
		this.shouldShutdown = shouldShutdown;
	}
	
	
	
	

}
