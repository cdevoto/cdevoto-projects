package com.compuware.ruxit.synthetic.scheduler.recur.engine;

import com.compuware.ruxit.synthetic.di.AppContextProvider;
import com.compuware.ruxit.synthetic.engine.Engine;

public class RecurrentTestSchedulingEngine extends Engine {

	private static final String APP_NAME = "Recurrent Test Scheduler";

	@Override
	public String getApplicationName() {
		return APP_NAME;
	}

	@Override
	protected void onInit() throws Exception {
		System.setProperty(AppContextProvider.PROPERTY_NAME, "com.compuware.ruxit.synthetic.scheduler.recur.di.SpringAppContextProvider");
	}

	@Override
	protected void onStart() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onStop() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
