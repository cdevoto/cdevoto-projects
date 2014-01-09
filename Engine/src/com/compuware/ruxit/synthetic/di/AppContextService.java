package com.compuware.ruxit.synthetic.di;

public interface AppContextService {

	public Object getBean (String name);
	public <T> T getBean (String name, Class<T> c);
	public <T> T getBean (Class<T> c);
	
}
