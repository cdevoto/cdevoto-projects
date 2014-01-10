package com.compuware.ruxit.synthetic.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.compuware.ruxit.synthetic.di.AppContextService;
import com.compuware.ruxit.synthetic.di.AppContextServices;
import com.compuware.ruxit.synthetic.termination.TerminationListener;
import com.compuware.ruxit.synthetic.termination.TerminationSensor;
import com.compuware.ruxit.synthetic.termination.Terminator;

public abstract class Engine implements Lifecycle {
	private static final Logger log = LoggerFactory.getLogger(Engine.class);
	
	
	public Engine () {}
	

	@Override
	public void init() throws Exception {
		onInit();
	}

	@Override
	public final void start() throws Exception {
		log.info(String.format("Attempting to start the %s Engine...", getApplicationName()));
		AppContextService appContext = AppContextServices.getInstance();
		TerminationSensor terminationSensor = appContext.getBean(TerminationSensor.class);
		KeepAliveDaemon keepAliveDaemon = new KeepAliveDaemon();
		terminationSensor.addTerminationListener(keepAliveDaemon);
		Thread keepAliveThread = new Thread(keepAliveDaemon, "Keep-Alive-Thread");
		keepAliveThread.start();
		terminationSensor.start();
		onStart();
		log.info(String.format("The %s Engine has been started", getApplicationName()));
	}

	@Override
	public final void stop() throws Exception {
		log.info(String.format("Attempting to stop the %s Engine...", getApplicationName()));
		onStop();
		AppContextService appContext = AppContextServices.getInstance();
        Terminator terminator = appContext.getBean(Terminator.class);
        terminator.terminate();
	}
	
	public abstract String getApplicationName ();
	protected abstract void onInit () throws Exception;
	protected abstract void onStart () throws Exception;
	protected abstract void onStop () throws Exception;

	
	private class KeepAliveDaemon implements TerminationListener, Runnable {
		private volatile boolean terminated;
		private volatile Thread contextThread;

		@Override
		public void run() {
			this.contextThread = Thread.currentThread();
			while (!terminated) {
				try {
					Thread.sleep(60000);
				} catch (InterruptedException ex) {}
			}
			log.info(String.format("Stopping the %s Engine...", getApplicationName()));
			// Grant a 1 second period before exiting so that other threads can terminate themselves gracefully.
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {}
			log.info(String.format("The %s Engine has been stopped.", getApplicationName()));
		}

		@Override
		public void stop() {
			terminated = true;
			if (contextThread != null) {
				contextThread.interrupt();
			}
			
		}
		
	}
	
}
