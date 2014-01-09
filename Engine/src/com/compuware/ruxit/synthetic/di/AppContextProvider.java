package com.compuware.ruxit.synthetic.di;

public interface AppContextProvider {
	public static final String PROPERTY_NAME = "compuware.application.di.providers";
	
	public AppContextService getAppContextService ();

}
