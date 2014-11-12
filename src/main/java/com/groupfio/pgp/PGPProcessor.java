package com.groupfio.pgp;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

public class PGPProcessor {

	private String passphrase;
	private String publicKeyFileName;
	private String secretKeyFileName;
	//private String inputFileName;
	//private String outputFileName;
	
	private InputStream publicKeyIn;
	private InputStream secretKeyIn;
	
	private boolean asciiArmored = false;
	private boolean integrityCheck = true;
	
	public PGPProcessor() {
		super();
		InputStream publicKeyIn = new ByteArrayInputStream(PGPTestKeys.pubKey.getBytes());
		setPublicKeyIn(publicKeyIn);
		InputStream secretKeyIn = new ByteArrayInputStream(PGPTestKeys.secKey.getBytes());
		setSecretKeyIn(secretKeyIn);
		setPassphrase(PGPTestKeys.pass);
	}
	
	public static byte[] encryptByteArray(byte[] string){
		byte[] encrypted = null;
		try {
			encrypted = new PGPProcessor().encrypt(string);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encrypted;
	}

	public byte[] encrypt(byte[] string) throws Exception {
		
		InputStream keyIn;
		if(publicKeyFileName!=null){
			keyIn = new FileInputStream(publicKeyFileName);
		}else{
			keyIn = this.publicKeyIn;
		}
	
		byte[] encrpted = PGPUtils.encryptPayload(string, PGPUtils.readPublicKey(keyIn),
				asciiArmored, integrityCheck);
		
		keyIn.close();
		return encrpted;
	}
	
	public static byte[] decryptByteArray(byte[] string){
		byte[] decrypted = null;
		try {
			decrypted = new PGPProcessor().decrypt(string);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return decrypted;
	}
	
	public byte[] decrypt(byte[] string) throws Exception {
		InputStream keyIn;
		if(secretKeyFileName!=null){
			keyIn = new FileInputStream(secretKeyFileName);
		}else{
			keyIn = this.secretKeyIn;
		}
	
		byte[] decrypted = PGPUtils.decryptPayload(string, keyIn, passphrase.toCharArray());
		keyIn.close();
		return decrypted;
	}
	
	

	public boolean isAsciiArmored() {
		return asciiArmored;
	}

	public void setAsciiArmored(boolean asciiArmored) {
		this.asciiArmored = asciiArmored;
	}

	public boolean isIntegrityCheck() {
		return integrityCheck;
	}

	public void setIntegrityCheck(boolean integrityCheck) {
		this.integrityCheck = integrityCheck;
	}

	public String getPassphrase() {
		return passphrase;
	}

	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}

	public String getPublicKeyFileName() {
		return publicKeyFileName;
	}

	public void setPublicKeyFileName(String publicKeyFileName) {
		this.publicKeyFileName = publicKeyFileName;
	}

	public String getSecretKeyFileName() {
		return secretKeyFileName;
	}

	public void setSecretKeyFileName(String secretKeyFileName) {
		this.secretKeyFileName = secretKeyFileName;
	}

	public InputStream getPublicKeyIn() {
		return publicKeyIn;
	}

	public void setPublicKeyIn(InputStream publicKeyIn) {
		this.publicKeyIn = publicKeyIn;
	}

	public InputStream getSecretKeyIn() {
		return secretKeyIn;
	}

	public void setSecretKeyIn(InputStream secretKeyIn) {
		this.secretKeyIn = secretKeyIn;
	}

	
	/*public boolean signEncrypt() throws Exception {
		FileOutputStream out = new FileOutputStream(outputFileName);
		FileInputStream publicKeyIn = new FileInputStream(publicKeyFileName);
		FileInputStream secretKeyIn = new FileInputStream(secretKeyFileName);

		PGPPublicKey publicKey = PGPUtils.readPublicKey(publicKeyIn);
		PGPSecretKey secretKey = PGPUtils.readSecretKey(secretKeyIn);

		PGPUtils.signEncryptFile(out, this.getInputFileName(), publicKey,
				secretKey, this.getPassphrase(), this.isAsciiArmored(),
				this.isIntegrityCheck());

		out.close();
		publicKeyIn.close();
		secretKeyIn.close();

		return true;
	}*/

}
