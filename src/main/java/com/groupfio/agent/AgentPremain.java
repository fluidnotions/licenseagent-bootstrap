package com.groupfio.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.groupfio.agent.transformers.ShutdownTransformer;
import com.groupfio.agent.transformers.StartupTransformer;

public class AgentPremain {

	private static Logger log = Logger.getLogger(AgentPremain.class);

	private Controller controller;
	private String agentArguments;
	private Instrumentation instrumentation;

	public AgentPremain(String agentArguments, Instrumentation instrumentation) {
		this.controller = new Controller();
		this.agentArguments = agentArguments;
		this.instrumentation = instrumentation;

	}

	public static void premain(String agentArguments,
			Instrumentation instrumentation) {

		System.out.println("premain");
		AgentPremain agent = new AgentPremain(agentArguments, instrumentation);
		
		agent.setUpTransformers();
		
		agent.startValidationClient();

	}
	

	private void startValidationClient() {
		// start up the Validation client in it's own thread
		ValidationClient vc = new ValidationClient(controller);
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.execute(vc);
	}

	private void setUpTransformers() {
		ClassFileTransformer trans1 = new StartupTransformer(controller);
		instrumentation.addTransformer(trans1);
		//this method doesn't work and is unnecessary for the shutdown operation
		//ClassFileTransformer trans2 = new ShutdownTransformer(controller);
		//instrumentation.addTransformer(trans2);
	}

	// this is for test purposes within eclipse
	public static void main(String[] args) {
		AgentPremain agent = new AgentPremain(null, null);
		agent.startValidationClient();
	}

}
