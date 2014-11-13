package com.groupfio.agent.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.groupfio.agent.AgentPremain;

public class Config {
	
	private static Logger log = Logger.getLogger(Config.class);

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
			// try load from external first
			if (new File("config.properties").exists()) {
				in = new FileInputStream("config.properties");
				log.debug("loading config.properties from file: "+new File("config.properties").getPath());
			} else {
				in = getClass().getResourceAsStream("config.properties");
				log.debug("loading config.properties from resource");
			}
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
