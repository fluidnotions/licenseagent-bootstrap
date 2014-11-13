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
import com.groupfio.agent.ValidationState;
import com.groupfio.agent.config.Config;

public class ShutdownTransformer extends Transformer {

	private static Logger log = Logger.getLogger(ShutdownTransformer.class);

	public ShutdownTransformer(ValidationState validation) {
		super(validation);
	}

	@Override
	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {

		if (super.validation.isShouldShutdown()) {
			log.debug("validation.isShouldShutdown(): className: " + className);
			String targetpackage = Config
					.getProp("shutdown.target.package.starts.with");
			if (className.startsWith(targetpackage)) {
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
			} else {
				log.debug(className + " DOESN'T start with " + targetpackage
						+ "|");
			}
		}
		return classfileBuffer;
	}

}
