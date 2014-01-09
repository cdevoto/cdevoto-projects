package com.compuware.ruxit.synthetic.di;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AppContextServices {
	public static final String DEFAULT_PROVIDER_NAME = "<def>";
	
	// Maps provider names to providers
	private static final Map<String, AppContextProvider> providers = new ConcurrentHashMap<String, AppContextProvider>();
	
	static {
		String providerList = System.getProperty(AppContextProvider.PROPERTY_NAME);
		if (providerList != null) {
			String [] providers = providerList.split(":");
			for (String provider : providers) {
				try {
					Class.forName(provider.trim());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

	// Provider Registration APIs
	public static void registerDefaultProvider(AppContextProvider p) {
		registerProvider(DEFAULT_PROVIDER_NAME, p);
	}
	
	public static void registerProvider(String name, AppContextProvider p) {
		providers.put(name, p);
	}
	
	// Service Access API
	public static AppContextService getInstance () {
		return getInstance(DEFAULT_PROVIDER_NAME);
	}

	public static AppContextService getInstance(String name) {
		AppContextProvider p = providers.get(name);
		if (p == null) {
			throw new IllegalArgumentException("No provider registered with name: " + name);
		}
		return p.getAppContextService();
	}

	private AppContextServices () {}

}
