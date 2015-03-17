package io.miti.beetle.app;

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
		
		// TODO Start the H2 database
		
		// TODO Start the cache manager
		
		// TODO Start the console
	}
	
	
	/**
	 * Entry point for the application
	 * 
	 * @param args arguments to the application
	 */
	public static void main(final String[] args) {
		
		// TODO Parse any command line arguments
		
		// Start the application
		new Beetle().start();
	}
}
