package com.groupfio.agent.transformers;

import java.io.IOException;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.apache.log4j.Logger;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import com.groupfio.agent.Controller;
import com.groupfio.agent.config.Config;

public class StartupTransformer extends Transformer {
	
	private static Logger log = Logger.getLogger(StartupTransformer.class);
	
	public StartupTransformer(Controller controller) {
		super(controller);
	}

	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {

		if (Config.getProp("startup.target.classname").equals(className)) {
			try {
				String normalizedClassName = className.replaceAll("/", ".");
				CtClass clazz = ClassPool.getDefault().get(normalizedClassName);
				CtMethod m = clazz.getDeclaredMethod(Config.getProp("startup.insertafter.method.target"));
				m.insertAfter(Config.getProp("startup.srccode.statement"));
				classfileBuffer = clazz.toBytecode();
			} catch (NotFoundException | CannotCompileException | IOException e) {
				e.printStackTrace();
			}
		}
		return classfileBuffer;
	}
}
