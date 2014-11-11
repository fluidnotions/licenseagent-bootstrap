package com.groupfio.agent.transformers;

import java.io.IOException;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.NotFoundException;

import com.groupfio.agent.ValidationState;
import com.groupfio.licenseagent.config.Config;

public class ShutdownTransformer extends Transformer {
	
	public ShutdownTransformer(ValidationState validation) {
		super(validation);
	}

	@Override
	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer)
			throws IllegalClassFormatException {

		if (super.validation.isShouldShutdown()) {
			try {
				String normalizedClassName = className.replaceAll("/", ".");
				CtClass clazz = ClassPool.getDefault().get(normalizedClassName);
				CtConstructor constructor = clazz.getConstructors()[0];
				constructor.insertBefore(Config.getProp("shutdown.srccode.statement"));
				classfileBuffer = clazz.toBytecode();
			} catch (NotFoundException | CannotCompileException | IOException e) {
				e.printStackTrace();
			}
		}
		return classfileBuffer;
	}

}
