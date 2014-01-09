package com.compuware.ruxit.synthetic.termination;

import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.compuware.ruxit.synthetic.di.AppContextService;
import com.compuware.ruxit.synthetic.di.AppContextServices;

public class SocketSignalTerminator implements Terminator {
	private static final Log log = LogFactory.getLog(SocketSignalTerminator.class);
	
	public SocketSignalTerminator () {
	}

	@Override
	public void terminate() throws Exception {
		log.info("Sending termination signal to stop the application...");
		AppContextService appContext = AppContextServices.getInstance();
		TerminationSensor monitor = appContext.getBean(TerminationSensor.class);
		int port = monitor.getPort();
		int [] terminationCode = monitor.getTerminationCode();
		Socket socket = null;
		try {
			InetAddress server = InetAddress.getLocalHost();
			socket = new Socket(server, port);
			OutputStream out = socket.getOutputStream();
			for (int i = 0; i < terminationCode.length; i++) {
			    out.write(terminationCode[i]);
			}
			log.info("Termination signal successfully sent.");
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
	}
	

}
