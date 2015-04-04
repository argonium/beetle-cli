package io.miti.beetle.util;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility methods.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class Utility
{

  /**
   * Whether to read input files as a stream.
   */
  private static boolean readAsStream = false;

  /**
   * The line separator for this OS.
   */
  private static String lineSep = null;


  /**
   * Default constructor.
   */
  private Utility() {
    super();
  }


  /**
   * Return the line separator for this OS.
   * 
   * @return the line separator for this OS
   */
  public static String getLineSeparator() {
    // See if it's been initialized
    if (lineSep == null) {
      lineSep = System.getProperty("line.separator");
    }

    return lineSep;
  }


  /**
   * Whether to read content files as a stream. This is used when running the
   * program as a standalone jar file.
   * 
   * @param useStream whether to read files via a stream
   */
  public static void readFilesAsStream(final boolean useStream) {
    readAsStream = useStream;
  }


  /**
   * Whether to read content files as a stream.
   * 
   * @return whether to read content files as a stream
   */
  public static boolean readFilesAsStream() {
    return readAsStream;
  }


  /**
   * Sleep for the specified number of milliseconds.
   * 
   * @param time the number of milliseconds to sleep
   */
  public static void sleep(final long time) {
    try {
      Thread.sleep(time);
    } catch (InterruptedException e) {
      Logger.error(e);
    }
  }


  /**
   * Convert a string into an integer.
   * 
   * @param sInput the input string
   * @param defaultValue the default value
   * @param emptyValue the value to return for an empty string
   * @return the value as an integer
   */
  public static int getStringAsInteger(final String sInput,
      final int defaultValue, final int emptyValue) {
    // This is the variable that gets returned
    int value = defaultValue;

    // Check the input
    if (sInput == null) {
      return emptyValue;
    }

    // Trim the string
    final String inStr = sInput.trim();
    if (inStr.length() < 1) {
      // The string is empty
      return emptyValue;
    }

    // Convert the number
    try {
      value = Integer.parseInt(inStr);
    } catch (NumberFormatException nfe) {
      value = defaultValue;
    }

    // Return the value
    return value;
  }


  /**
   * Compare two strings, handling nulls.
   * 
   * @param str1 the first string to compare
   * @param str2 the second string to compare
   * @return the numeric value of comparing str1 to str2
   */
  public static int compareTwoStrings(final String str1, final String str2) {
    if ((str1 == null) && (str2 == null)) {
      return 0;
    } else if (str1 == null) {
      return -1;
    } else if (str2 == null) {
      return 1;
    }

    return str1.compareTo(str2);
  }


  /**
   * Convert a string into a floating point number.
   * 
   * @param sInput the input string
   * @param defaultValue the default value
   * @param emptyValue the value to return for an empty string
   * @return the value as a float
   */
  public static float getStringAsFloat(final String sInput,
      final float defaultValue, final float emptyValue) {
    // This is the variable that gets returned
    float fValue = defaultValue;

    // Check the input
    if (sInput == null) {
      return emptyValue;
    }

    // Trim the string
    final String inStr = sInput.trim();
    if (inStr.length() < 1) {
      // The string is empty
      return emptyValue;
    }

    // Convert the number
    try {
      fValue = Float.parseFloat(inStr);
    } catch (NumberFormatException nfe) {
      fValue = defaultValue;
    }

    // Return the value
    return fValue;
  }


  /**
   * Convert a string into a double.
   * 
   * @param sInput the input string
   * @param defaultValue the default value
   * @param emptyValue the value to return for an empty string
   * @return the value as a double
   */
  public static double getStringAsDouble(final String sInput,
      final double defaultValue, final double emptyValue) {
    // This is the variable that gets returned
    double value = defaultValue;

    // Check the input
    if (sInput == null) {
      return emptyValue;
    }

    // Trim the string
    final String inStr = sInput.trim();
    if (inStr.length() < 1) {
      // The string is empty
      return emptyValue;
    }

    // Convert the number
    try {
      value = Double.parseDouble(inStr);
    } catch (NumberFormatException nfe) {
      value = defaultValue;
    }

    // Return the value
    return value;
  }


  /**
   * Return whether the string is null or has no length.
   * 
   * @param msg the input string
   * @return whether the string is null or has no length
   */
  public static boolean isStringEmpty(final String msg) {
    return ((msg == null) || (msg.length() == 0));
  }


  /**
   * Make the application compatible with Apple Macs.
   */
  public static void makeMacCompatible() {
    // Set the system properties that a Mac uses
    System.setProperty("apple.awt.brushMetalLook", "true");
    System.setProperty("apple.laf.useScreenMenuBar", "true");
    System.setProperty("apple.awt.showGrowBox", "true");
    System.setProperty("com.apple.mrj.application.apple.menu.about.name",
        "FGServer");
  }


  /**
   * Get the specified date as a string.
   * 
   * @param time the date and time
   * @return the date as a string
   */
  public static String getDateTimeString(final long time) {
    // Check the input
    if (time <= 0) {
      return "Invalid time (" + Long.toString(time) + ")";
    }

    // Convert the time into a Date object
    Date date = new Date(time);

    // Declare our formatter
    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    // Return the date/time as a string
    return formatter.format(date);
  }


  /**
   * Format the date as a string, using a standard format.
   * 
   * @param date the date to format
   * @return the date as a string
   */
  public static String getDateString(final Date date) {
    // Declare our formatter
    SimpleDateFormat formatter = new SimpleDateFormat("MMMM d, yyyy");

    if (date == null) {
      return formatter.format(new Date());
    }

    // Return the date/time as a string
    return formatter.format(date);
  }


  /**
   * Format the date and time as a string, using a standard format.
   * 
   * @return the date as a string
   */
  public static String getDateTimeString() {
    // Declare our formatter
    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    // Return the date/time as a string
    return formatter.format(new Date());
  }


  /**
   * Initialize the application's Look And Feel with the default for this OS.
   */
  public static void initLookAndFeel() {
    // Use the default look and feel
    try {
      javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager
          .getSystemLookAndFeelClassName());
    } catch (Exception e) {
      Logger.error("Exception: " + e.getMessage());
    }
  }


  public static Boolean stringToBoolean(final String val) {

    if ((val == null) || val.trim().isEmpty()) {
      return null;
    }

    final Set<String> trues = new HashSet<String>(10);
    trues.add("1");
    trues.add("TRUE");
    trues.add("true");
    trues.add("yes");
    trues.add("t");
    trues.add("y");

    return (trues.contains(val));
  }


  public static Integer getAscii(final String val) {

    if ((val == null) || (val.length() != 1)) {
      return null;
    }

    char ch = val.charAt(0);
    return Integer.valueOf(((int) ch));
  }


  public static String formatLong(final long val) {
    return NumberFormat.getInstance().format(val);
  }


  /**
   * Surround the string with single quotes, and backquote any single quotes in
   * the string.
   * 
   * @param str the input string
   * @return the quoted string
   */
  public static String quoteString(final String str) {
    // Check the input
    if (str == null) {
      // It's null, so just return that
      return "null";
    }

    String outStr = str.replace("\"", "\\\"");

    if (outStr.contains("\n") || outStr.contains(",")) {
      outStr = "\"" + outStr + "\"";
    }

    return outStr;
  }
}
