package com.compuware.ruxit.synthetic.scheduler.recur.di;

import com.compuware.ruxit.synthetic.di.AppContextProvider;
import com.compuware.ruxit.synthetic.di.AppContextService;
import com.compuware.ruxit.synthetic.di.AppContextServices;

public class SpringAppContextProvider implements AppContextProvider {

	static {
		AppContextProvider provider = new SpringAppContextProvider();
		AppContextServices.registerDefaultProvider(provider);
	}

	@Override
	public AppContextService getAppContextService() {
		return SpringAppContextService.getInstance();
	}
}
