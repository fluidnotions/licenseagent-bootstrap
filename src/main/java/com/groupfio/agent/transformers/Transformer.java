package com.groupfio.agent.transformers;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import com.groupfio.agent.ValidationState;

public abstract class Transformer implements ClassFileTransformer {

	ValidationState validation;
	
	public Transformer(ValidationState validation) {
		super();
		this.validation = validation;
	}

	public abstract byte[] transform(ClassLoader arg0, String arg1, Class<?> arg2,
			ProtectionDomain arg3, byte[] arg4)
			throws IllegalClassFormatException;

}
