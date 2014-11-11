package com.groupfio.licenseagent.pojo;

public class LicFile {

	public static final String VPASS = "VERFICATION_PASSED";
	public static final String VFAIL = "VERFICATION_FAILED";

	private LicFileAction action;

	private String serialnumber;

	private int licfileByteSize;

	private String licfileCheckSum;

	public LicFileAction getAction() {
		return action;
	}

	public void setAction(LicFileAction action) {
		this.action = action;
	}

	public String getSerialnumber() {
		return serialnumber;
	}

	public void setSerialnumber(String serialnumber) {
		this.serialnumber = serialnumber;
	}

	public int getLicfileByteSize() {
		return licfileByteSize;
	}

	public void setLicfileByteSize(int licfileByteSize) {
		this.licfileByteSize = licfileByteSize;
	}

	public String getLicfileCheckSum() {
		return licfileCheckSum;
	}

	public void setLicfileCheckSum(String licfileCheckSum) {
		this.licfileCheckSum = licfileCheckSum;
	}

	@Override
	public String toString() {
		return "LicFile [action=" + action + ", serialnumber=" + serialnumber
				+ "]";
	}

	public enum LicFileAction {
		ChecksumAndFileSize;
	}
}
