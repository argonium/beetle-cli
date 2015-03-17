package io.miti.beetle.app;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

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
			} else if (arg.equals("-loadclass")) {
				loadClassNames = true;
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
	 * Get the build number from the jar manifest and print it.
	 */
	private String getBuiltDate() {
		
		// See if we're running inside a JAR file.  If we are,
		// there's no manifest.  Show an error and leave.
		Class<?> clazz = ArgumentParser.class;
		String className = clazz.getSimpleName() + ".class";
		String classPath = clazz.getResource(className).toString();
		if (!classPath.startsWith("jar")) {
		  System.err.println("Error: Class is not from a jar file");
		  return null;
		}
		
		// Build the manifest path
		String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + 
		    "/META-INF/MANIFEST.MF";
		Manifest manifest = null;
		String date = null;
		try {
			// Read the manifest
			manifest = new Manifest(new java.net.URL(manifestPath).openStream());
			
			// Get the attribute with the built date
			final Attributes attr = manifest.getMainAttributes();
			date = attr.getValue("Built-Date");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return date;
	}
	
	
	/**
	 * Print information on how to use the application.
	 */
	private static void printHelp() {
		
		final String lineSep = System.getProperty("line.separator");
		StringBuilder sb = new StringBuilder(100);
		sb.append(VER_ROOT_STR).append(lineSep);
		sb.append("Perform operations on databases").append(lineSep);
		sb.append("This normally requires adding a JDBC driver JAR ")
		  .append("to the classpath:").append(lineSep);
		sb.append("  java -cp dbconn.jar:pgjdbc.jar io.miti.dbconn.app.DBConn")
		  .append(lineSep);
		sb.append("Otherwise you can use 'java -jar dbconn.jar -loadclass'").append(lineSep);
		sb.append("This latter option will load the appropriate driver class").append(lineSep);
		sb.append("Options:").append(lineSep);
		sb.append("  -help: Print this help").append(lineSep);
		sb.append("  -listdbs: Print the databases supported for loading a class name").append(lineSep);
		sb.append("  -loadclass: When opening a JDBC URL, load the class (not needed for 4.0)").append(lineSep);
		
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
