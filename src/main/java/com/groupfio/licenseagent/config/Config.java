package com.groupfio.licenseagent.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

	private Properties prop;
	private static Config config;

	static {
		config = new Config();
	}

	public static String getProp(String key) {
		return config.getProperty(key);
	}

	private String getProperty(String key) {
		return prop.getProperty(key);
	}

	public Config() {
		prop = new Properties();
		InputStream in = null;
		try {
			in = getClass().getResourceAsStream("config.properties");
			prop.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
