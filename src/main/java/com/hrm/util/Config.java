package com.hrm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class Config {

	public static final String CONFIG_PRP = "config.properties";
	public static final String CONFIG_LOCAL_PRP = "config.local.properties";
	private static final Properties prop = new Properties();

	static {
		// 1. Load base configuration file
		loadPropertiesFile(CONFIG_PRP);

		// 2. Load optional local override configuration file if present
		File localConfigFile = new File(CONFIG_LOCAL_PRP);
		if (localConfigFile.exists()) {
			loadPropertiesFile(CONFIG_LOCAL_PRP);
		}
	}

	private static void loadPropertiesFile(String fileName) {
		InputStream is = null;
		try {
			File f = new File(fileName);
			if (f.exists()) {
				is = new FileInputStream(f);
			} else {
				is = Config.class.getClassLoader().getResourceAsStream(fileName);
			}
			if (is != null) {
				prop.load(is);
			}
		} catch (Exception e) {
			System.err.println("Failed to load property file: " + fileName + " (" + e.getMessage() + ")");
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception ignored) {
				}
			}
		}
	}

	public static String getProperty(String key) {
		// 1. Check JVM System property (-Dkey=value)
		String sysProp = System.getProperty(key);
		if (sysProp != null && !sysProp.trim().isEmpty()) {
			return sysProp.trim();
		}

		// 2. Check loaded properties (config.local.properties > config.properties)
		String value = prop.getProperty(key);
		if (value != null && !value.trim().isEmpty()) {
			return value.trim();
		}

		// 3. Check Environment variable (e.g. APP_URL_LOCAL for app.url.local)
		String envKey = key.toUpperCase().replace('.', '_');
		String envVal = System.getenv(envKey);
		if (envVal != null && !envVal.trim().isEmpty()) {
			return envVal.trim();
		}

		System.out.println(key + " is not present in configuration");
		return null;
	}

}

