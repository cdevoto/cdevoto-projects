package com.compuware.ruxit.synthetic.engine;

public interface Lifecycle {
	
	public void init () throws Exception;
	public void start () throws Exception;
	public void stop () throws Exception;

}
