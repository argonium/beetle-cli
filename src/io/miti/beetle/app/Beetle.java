package io.miti.beetle.app;

import io.miti.beetle.util.Utility;

/**
 * The main class for the application.
 * 
 * @author mike
 */
public final class Beetle {

	/**
	 * Default constructor.
	 */
	public Beetle() {
		super();
	}
	
	
	private void start() {
		// Store whether we're in a jar or not
		checkInputFileSource();
		
		// TODO Start the H2 database (build the DB if it doesn't exist)
		
		// TODO Start the cache manager
		
		// TODO Start the console
	}
  
  
	/**
	 * Check how the application is run.
	 */
	private void checkInputFileSource() {
		final java.net.URL url = getClass().getResource("/initdb.sql");
		Utility.readFilesAsStream(url != null);
	}
	
	
	/**
	 * Entry point for the application
	 * 
	 * @param args arguments to the application
	 */
	public static void main(final String[] args) {
		
		// Parse any command line arguments
		if ((new ArgumentParser(args)).exit()) {
			return;
		}
		
		// Start the application
		new Beetle().start();
	}
}
