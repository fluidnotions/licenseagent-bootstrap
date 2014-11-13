package com.groupfio.agent.transformers;

import java.io.IOException;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.apache.log4j.Logger;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.NotFoundException;

import com.groupfio.agent.AgentPremain;
import com.groupfio.agent.Controller;
import com.groupfio.agent.config.Config;

public class ShutdownTransformer extends Transformer {

	private static Logger log = Logger.getLogger(ShutdownTransformer.class);
	private String targetpackage = null;

	public ShutdownTransformer(Controller controller) {
		super(controller);
		targetpackage = Config
				.getProp("shutdown.target.package.starts.with");
	}

	@Override
	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {

		
		if (className.startsWith(targetpackage)) {
			log.debug("controller.isShouldShutdown(): className: " + className);
			if (super.controller.isShouldShutdown()) {
				try {
					String normalizedClassName = className.replaceAll("/", ".");
					CtClass clazz = ClassPool.getDefault().get(
							normalizedClassName);
					CtConstructor constructor = clazz.getConstructors()[0];
					log.debug("clazz.getConstructors()[0] LongName:"
							+ constructor.getLongName());
					constructor.insertBefore(Config
							.getProp("shutdown.srccode.statement"));
					classfileBuffer = clazz.toBytecode();
				} catch (NotFoundException | CannotCompileException
						| IOException e) {
					e.printStackTrace();
				}
			}
		}
		return classfileBuffer;
	}

}
