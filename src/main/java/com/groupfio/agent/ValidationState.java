package com.groupfio.agent;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.groupfio.agent.config.Config;

public class ValidationState {
	
	private static Logger log = Logger.getLogger(ValidationState.class);
	
	private boolean shouldShutdown = false;
	private boolean hasConnectionToServer = false;
	
	public ValidationState() {
		if(Config.getProp("validation.state.shouldShutdown").equalsIgnoreCase("true")){
			shouldShutdown = true;
		}
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
					setShouldShutdown(true);
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

	public synchronized boolean isShouldShutdown() {
		return shouldShutdown;
	}

	public void setShouldShutdown(boolean shouldShutdown) {
		log.debug("setShouldShutdown: "+shouldShutdown);
		this.shouldShutdown = shouldShutdown;
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
	
	
	
	

}
