package com.compuware.ruxit.synthetic.bootstrap;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class Bootstrap {

	private static final String APPLICATION_ENGINE = "compuware.application.engine";
	private static final String APPLICATION_DIR = "compuware.application.home";
	private Object engine;
	private ClassLoader applicationClassLoader;
	private String appHome = "..";
	
	public static void main(String[] args) {
		System.out.println("Starting the bootstrap sequence.");
		Bootstrap bootstrap = new Bootstrap();
		try {
			bootstrap.init();
		} catch (Throwable t) {
			t.printStackTrace();
			return;
		}
		
		try {
			String command = "start";
			if (args.length > 0) {
				command = args[args.length - 1];
			}
			if (command.equals("start")) {
				bootstrap.start();
			} else if (command.equals("stop")) {
				bootstrap.stop();
			} else {
				System.err.println("ERROR: Unrecognized command '" + command + "'.");
				return;
			}
			System.out.println("The bootstrap sequence completed successfully.");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}



	public Bootstrap () {
	}

	@SuppressWarnings("rawtypes")
	public void init() throws Exception {
		String baseDir = System.getProperty(APPLICATION_DIR);
		if (baseDir!=null) {
			appHome = baseDir;
		}
		System.out.printf("Using %s: %s%n", APPLICATION_DIR, appHome);
		String engineClassName = System.getProperty(APPLICATION_ENGINE);
		if (engineClassName == null) {
			System.err.println(String.format("ERROR: You must specify a value for the '%s' JVM property.", APPLICATION_ENGINE));
		}
		System.out.printf("Using %s: %s%n", APPLICATION_ENGINE, engineClassName);
		applicationClassLoader = createClassLoader();
		Thread.currentThread().setContextClassLoader(applicationClassLoader);
		Class engineClass = applicationClassLoader.loadClass(engineClassName);
		engine = engineClass.newInstance();
        Method method = engine.getClass().getMethod("init", (Class [] )null);
        method.invoke(engine, (Object [])null);
	}

    

	public void start() throws Exception {
        if( engine==null ) {
        	init();
        }
    
        Method method = engine.getClass().getMethod("start", (Class [] )null);
        method.invoke(engine, (Object [])null);
	}



	public void stop() throws Exception {
        if( engine==null ) {
        	init();
        }
    
        Method method = engine.getClass().getMethod("stop", (Class [] )null);
        method.invoke(engine, (Object [])null);
	}



	private ClassLoader createClassLoader() throws Exception {
		List<File> classpathFiles = new ArrayList<File>();
		addJarFiles(classpathFiles);
		addConfigFiles(classpathFiles);
		addClassesFiles(classpathFiles);
		
		URL [] urls = convertFilesToUrls(classpathFiles); 
		ClassLoader classLoader = new URLClassLoader(urls);
		return classLoader;
	}



	private void addClassesFiles(List<File> classpathFiles) {
		File classesDirectory = new File(appHome+"/classes");
		System.out.println("Searching for files within the '" + classesDirectory.getAbsolutePath() + "' directory...");
		if (classesDirectory.exists() && classesDirectory.isDirectory()) {
			classpathFiles.add(classesDirectory);
			System.out.println("The 'classes' directory was found.");
		} else {
		    System.out.println("The 'classes' directory was not found.");
		}
	}



	private void addConfigFiles(List<File> classpathFiles) {
		File configDirectory = new File(appHome+"/config");
		System.out.println("Searching for configuration files within the '" + configDirectory.getAbsolutePath() + "' directory...");
		if (configDirectory.exists() && configDirectory.isDirectory()) {
			classpathFiles.add(configDirectory);
			System.out.println("The 'config' directory was found.");
		} else {
		    System.out.println("The 'config' directory was not found.");
		}
	}



	private void addJarFiles(List<File> classpathFiles) {
		File libDirectory = new File(appHome+"/lib");
		System.out.println("Searching for JAR files within the '" + libDirectory.getAbsolutePath() + "' directory...");
		if (libDirectory.exists() && libDirectory.isDirectory()) {
			System.out.println("The 'lib' directory was found.");
			File [] jarFiles = libDirectory.listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File file, String name) {
					try {
					    String extension = name.substring(name.lastIndexOf(".") + 1);
					    if ("jar".equals(extension)) {
					    	return true;
					    }
					} catch (IndexOutOfBoundsException ex) {}    
					return false;
				}
			});
			for (File jarFile : jarFiles) {
				classpathFiles.add(jarFile);
			}
		} else {
		    System.out.println("The 'lib' directory was not found.");
		}
	}



	private URL[] convertFilesToUrls(List<File> classpathFiles) {
		URL [] urls = new URL [classpathFiles.size()];
		for (int i = 0; i < urls.length; i++) {
			urls[i] = convertFileToUrl(classpathFiles.get(i));
		}
		return urls;
	}



	private URL convertFileToUrl(File file) {
		URL url;
		try {
			url = file.toURI().toURL();
		} catch (MalformedURLException e) {
			// Rethrow this as an unchecked exception, since it should really never happen!
			throw new RuntimeException(e);
		}
		return url;
	}

}
