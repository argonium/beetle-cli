package io.miti.beetle.app;

public final class ArgumentParser {
	
	// Set the default values for the system parameters
	private boolean shouldExit = false;
	public boolean loadClassNames = false;
	
	// Strings holding the version and build date
	public static final String VER_ROOT_STR = "Beetle - v. 0.0.1";
	
	/**
	 * Default constructor.
	 */
	@SuppressWarnings("unused")
	private ArgumentParser() {
		super();
	}
	
	
	/**
	 * Constructor taking the argument array to parse.
	 * 
	 * @param args the input arguments to the application
	 */
	public ArgumentParser(final String[] args) {
		
		// Whether to print the help at the end of processing
		boolean printHelp = false;
		
		// Iterate over the arguments
		for (String arg : args) {
			
			if (arg.equals("-help")) {
				printHelp = true;
			} else {
				// Unknown argument
				printHelp = true;
				break;
			}
		}
		
		// If the user only wanted help, show that now
		if (printHelp) {
			printHelp();
			shouldExit = true;
		}
	}
	
	
	/**
	 * Print information on how to use the application.
	 */
	private static void printHelp() {
		
		final String lineSep = System.getProperty("line.separator");
		StringBuilder sb = new StringBuilder(100);
		sb.append(VER_ROOT_STR).append(lineSep);
		sb.append("ETL from the command line, because why not?").append(lineSep);
		sb.append("Options:").append(lineSep);
		sb.append("  -help: Print this help").append(lineSep);
		
		System.out.print(sb.toString());
	}
	
	
	/**
	 * Return whether the application should exit.
	 * 
	 * @return whether the application should stop running
	 */
	public boolean exit() {
		return shouldExit;
	}
}
