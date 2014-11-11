package com.groupfio.pgp;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

public class PGPEncrypt {

	private String passphrase;
	private String publicKeyFileName;
	private String secretKeyFileName;
	//private String inputFileName;
	//private String outputFileName;
	
	private InputStream publicKeyIn;
	private InputStream secretKeyIn;
	
	private boolean asciiArmored = false;
	private boolean integrityCheck = true;
	
	private static final String pubKey = "-----BEGIN PGP PUBLIC KEY BLOCK-----"
			+ "\r\n"
			+ "Version: BCPG C# v1.6.1.0"
			+ "\r\n"
			+ "\r\n"
			+ "mQENBFRg3tsBCAChz6VMHmEpz7n0o9H88TjRgVDNxKbE9V184mfOA7dHPxF2brtR"
			+ "\r\n"
			+ "86Tn0Oyxgi2y3OHG48DtxYOqcE3MJz9zsPXbPf6mo/kLA+WhtBOjNMHlfNWwlno9"
			+ "\r\n"
			+ "78SfjJ5saz5HQ6dyf5Xc1q4U3DDYeBHlzMwnyOGr9XfA3UhUekYnKOpomBndbb9z"
			+ "\r\n"
			+ "ugdwcdTvdCTpORVctyKbZC7NEBBfk/WtPIYR4BFPjSVqQZip8LjLrfHHRfXRV1mn"
			+ "\r\n"
			+ "IgXJDVUITdRXEI4GS0pFUc0Q/Q+0BCEvW4YF1s9zE2JMHVFfkVgLMpW5CNiImshc"
			+ "\r\n"
			+ "XwNRlLwqI602/plDKWiRxmzb5HBM8r0+IUlZABEBAAG0EHRlc3RAZXhhbXBsZS5j"
			+ "\r\n"
			+ "b22JARwEEAECAAYFAlRg3tsACgkQtVqMp4RnuBh7Qwf+LKarKPgkfLAnh+u8XSgu"
			+ "\r\n"
			+ "lnZ9uPFzZezTW3pMU7HQF6pTNdcd8UYu2Ha8TCqpqofaq+QP+lxQlGyCTi8V2qei"
			+ "\r\n"
			+ "f1Nlj88ZvKh+IebSpis4p9NeySB4jsjsqq5969NXPuy5OOWYR4kgn8tEtiEODTnr"
			+ "\r\n"
			+ "48jycS2Ncqodcknt1cdaOTngiM2B4tqAoG3OpA0npOYT+IUgrQ3tmqd6Br0TmgiU"
			+ "\r\n"
			+ "A7plnh27pk3vK1kSy4lIHHBJ4uQWEhugGY7hwYFTt8fNrWNB2rOzKc8d4il7Shaq"
			+ "\r\n"
			+ "gpa5mgRvTLQN6t5CZTKwTNequwbB9ILFf6kuLD6jOnhzQZNl4mT456ypnoKz2TvY"
			+ "\r\n" + "bQ==" + "\r\n" + "=bHoF" + "\r\n"
			+ "-----END PGP PUBLIC KEY BLOCK-----";
	
	

	public PGPEncrypt() {
		super();
		InputStream publicKeyIn = new ByteArrayInputStream(pubKey.getBytes());
		setPublicKeyIn(publicKeyIn);
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
