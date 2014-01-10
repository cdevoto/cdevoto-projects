package com.compuware.ruxit.synthetic.termination;

import com.compuware.ruxit.synthetic.engine.Lifecycle;

public abstract class LifecycleTerminationListener implements Lifecycle,
		TerminationListener {

	@Override
	public void onTermination() throws Exception {
		stop();
	}


}
