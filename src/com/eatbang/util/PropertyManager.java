package com.eatbang.util;

import java.io.IOException;
import java.util.Properties;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PropertyManager {
	private static PropertyManager instance = null;

	private final Properties properties;

	public static synchronized PropertyManager getInstance() {
		if (instance == null) {
			instance = new PropertyManager();
		}
		return instance;
	}

	public String getProperty(String str) {
		return properties.getProperty(str);
	}

	public int getIntProperty(String str, int defaultValue) {
		int value;
		try {
			value = Integer.valueOf(properties.getProperty(str, ""));
		} catch (NumberFormatException e) {
			value = defaultValue;
		}
		return value;
	}

	public Properties getAllProperties() {
		return new Properties(properties);
	}

	public String getDeviceID(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}

	private PropertyManager() {
		properties = new Properties();
		try {
			properties.load(getClass().getResourceAsStream(
					"parameters.properties"));
		} catch (IOException e) {
			Log.e(getClass().getSimpleName(),
					"Unable to load application properties!");
		}
	}
}
