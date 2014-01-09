package com.compuware.ruxit.synthetic.scheduler.recur.di;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.compuware.ruxit.synthetic.di.AppContextService;

class SpringAppContextService implements AppContextService {
	private ApplicationContext context;
	
	
	private static class InstanceHolder {
		private static SpringAppContextService INSTANCE = new SpringAppContextService();
	}
	
	public static SpringAppContextService getInstance () {
		return InstanceHolder.INSTANCE;
	}
	
	private SpringAppContextService () {
		this.context = new ClassPathXmlApplicationContext("app-context.xml");
	}
	
	public Object getBean (String name) {
		return this.context.getBean(name);
	}

	public <T> T getBean (String name, Class<T> c) {
		return this.context.getBean(name, c);
	}

	@Override
	public <T> T getBean(Class<T> c) {
		return this.context.getBean(c);
	}
	
}
