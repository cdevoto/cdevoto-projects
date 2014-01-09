package com.compuware.ruxit.synthetic.termination;

import com.compuware.ruxit.synthetic.engine.Lifecycle;

public interface TerminationSensor extends Lifecycle {
	public int getPort();
	public int[] getTerminationCode ();
	public boolean isTerminated();
	public void addTerminationListener (TerminationListener listener);
	public void removeTerminationListener(TerminationListener listener);

}
