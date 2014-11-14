package com.groupfio.message.pojo;

public interface ActionMessageConstants {
	
	//public static final String N = "";
	//Action ids
	public static final  String LIC_FILE_ACTION_MSG = "ChecksumAndFileSize";
	public static final  String IS_ENABLED_ACTION_MSG = "IsEnabled";
	public static final  String TERM_ACTION_MSG = "Terminate";
	
	
	//end point locations
	public static final  String LIC_FILE_ACTION_DESTINATION = "/app/licfileverify";
	public static final  String IS_ENABLED_ACTION_DESTINATION = "/app/isenabled";
	
	//status messages
	public static final String LIC_FILE_VPASS = "VERFICATION_PASSED";
	public static final String LIC_FILE_VFAIL = "VERFICATION_FAILED";

}
