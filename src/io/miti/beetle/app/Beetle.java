package io.miti.beetle.app;

import io.miti.beetle.console.LineConsole;
import io.miti.beetle.prefs.DatabaseValidator;
import io.miti.beetle.prefs.PrefsDatabase;
import io.miti.beetle.util.Logger;
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
		
	    // Initialize the database
	    boolean result = PrefsDatabase.initializeDatabase();
	    
	    // Check the database connection
	    if (!result)
	    {
		  System.out.println("Unable to start preferences database.  " +
				  			 "Another copy may be running.  Exiting.");
		  return;
	    }
	    
	    // Check that the database version is up-to-date
	    if (!DatabaseValidator.checkDatabase())
	    {
	      return;
	    }
	    
		// TODO Start the cache manager
		
		// Start the console
	    new LineConsole().start();
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
		
		// Initialize our logger
		Logger.initialize(3, "stdout", false);
		
		// Parse any command line arguments
		if ((new ArgumentParser(args)).exit()) {
			return;
		}
		
		// Start the application
		new Beetle().start();
	}
}
