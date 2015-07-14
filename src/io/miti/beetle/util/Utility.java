package io.miti.beetle.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

    if (date == null) {
      return formatter.format(new Date());
    }

    // Return the date/time as a string
    return formatter.format(date);
  }


  /**
   * Format the date as a string, using a standard format.
   * 
   * @param date the date to format
   * @return the date as a string
   */
  public static String getTimeString(final Date date) {
    // Declare our formatter
    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

    if (date == null) {
      return formatter.format(new Date());
    }

    // Return the date/time as a string
    return formatter.format(date);
  }


  /**
   * Format the date as a string, using a standard format.
   * 
   * @param date the date to format
   * @return the date as a string
   */
  public static String getDateTimeString(final Date date) {
    // Declare our formatter
    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

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


  /**
   * Return the canonical name of the input file.
   * 
   * @param file the input file
   * @return the canonical name
   */
  private static String getFName(final File file) {
    String fname = null;
    try {
      fname = file.getCanonicalPath();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return fname;
  }


  /**
   * Search the specified jar file for the specified class name.
   * 
   * @param file the input file
   * @param className the class name
   */
  public static boolean searchFile(final File file, final String className) {
    if ((className == null) || className.isEmpty() || (file == null)
        || !file.exists() || !file.isFile()) {
      return false;
    }

    if (!getFName(file).toLowerCase().endsWith(".jar")) {
      return false;
    }

    FileInputStream inStream = null;
    boolean result = false;
    try {
      // Iterate over each entry in the input file
      inStream = new FileInputStream(file);
      ZipInputStream zis = new ZipInputStream(inStream);
      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        // Check if this is a class file
        String ename = entry.getName().trim();
        if (!ename.toLowerCase().endsWith(".class")) {
          continue;
        }

        // Convert the jar file entry to a package name (slashes
        // become periods, and remove the '.class' from the end)
        ename = ename.replace('/', '.').replace('\\', '.')
            .substring(0, ename.length() - 6);

        // If the class is in the default package, save the class
        // name as the modified entry name; else the class name
        // is just the text after the last period
        // final int lastPeriod = ename.lastIndexOf('.');
        // String cname = null;
        // if (lastPeriod < 0)
        // {
        // cname = ename;
        // }
        // else
        // {
        // cname = ename.substring(lastPeriod + 1);
        // }

        // Check if the current class name matches the target
        if (className.equalsIgnoreCase(ename)) {
          result = true;
          break;
        }
      }

      // Close the input streams
      zis.close();
      inStream.close();
      inStream = null;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return result;
  }
  
  public static String padString(final String str, final int maxLen, final char padder) {
    
    if (str.length() >= maxLen) {
      return str;
    }
    
    StringBuilder sb = new StringBuilder(maxLen);
    for (int toPrepend= maxLen - str.length(); toPrepend > 0; toPrepend--) {
      sb.append(padder);
    }
    
    sb.append(str);
    String result = sb.toString();
    return result;
  }
  
  
  /**
   * Split a string, and optionally put it in title case.
   * 
   * @param str the string to parse
   * @param split the character to split the string on
   * @param toTitleCase whether to put it in title case
   * @param restToLower if going to title case, whether to put
   *        characters after the first one in lowercase
   * @return the converted string
   */
  public static String toTitleCaseWithSplit(final String str,
                                            final char split,
                                            final boolean toTitleCase,
                                            final boolean restToLower)
  {
    // Split the input string
    StringTokenizer st = new StringTokenizer(str, Character.toString(split));
    
    // The string that gets returned
    StringBuilder sb = new StringBuilder(str.length());
    
    // Iterate over the strings
    while (st.hasMoreTokens())
    {
      // Check the processing
      if (toTitleCase)
      {
        // Append the current string in title case
        sb.append(toTitleCase(st.nextToken(), restToLower));
      }
      else
      {
        sb.append(st.nextToken());
      }
    }
    
    // Return the built string
    return (sb.toString());
  }
  
  
  /**
   * Convert a string to title case, with the first term
   * in lower case.
   * 
   * @param str the input string
   * @param putRestInLC whether to put the rest of the string in lowercase
   * @return the modified string
   */
  public static String toFirstLowerRestTitle(final String str,
                                             final boolean putRestInLC)
  {
    // First get the whole string in title case
    String title = toTitleCase(str, putRestInLC);
    
    // Save the length
    final int len = title.length();
    if (len < 1)
    {
      return title;
    }
    else if (len == 1)
    {
      // The string has a length of 1, so just return
      // it in lowercase
      return (title.toLowerCase());
    }
    else if (Character.isLowerCase(title.charAt(0)))
    {
      // The first character is already lower case
      return title;
    }
    
    // Return the converted string
    return (Character.toLowerCase(title.charAt(0)) + title.substring(1));
  }
  
  
  /**
   * Convert a string to title case.
   *
   * @param inStr string to put in title case
   * @param putRestInLC whether the rest of the string
   *                    should be made lowercase
   * @return the input string in title case
   */
  public static String toTitleCase(final String inStr,
                                   final boolean putRestInLC)
  {
    // Check for a null or empty string
    if ((inStr == null) || (inStr.length() < 1))
    {
      return "";
    }
    else
    {
      // Save the length
      final int nLen = inStr.length();
      
      // If one character, make it uppercase and return it
      if (nLen == 1)
      {
        return inStr.toUpperCase();
      }
      
      // Set this to true because we want to make the first character uppercase
      boolean blankFound = true;
      
      // Save the string to a stringbuffer
      StringBuffer buf = new StringBuffer(inStr);
      
      // Traverse the character array
      for (int nIndex = 0; nIndex < nLen; ++nIndex)
      {
        // Save the current character
        final char ch = buf.charAt(nIndex);
        
        // If we hit a space, set a flag so we make the next non-space char uppercase
        if (ch == ' ')
        {
          blankFound = true;
          continue;
        }
        else
        {
          // See if the previous character was a space
          if (blankFound)
          {
            // Check if this is lowercase
            if (Character.isLowerCase(ch))
            {
              // Make the character uppercase
              buf.setCharAt(nIndex, Character.toUpperCase(ch));
            }
          }
          else
          {
            // Check if the rest the string should be made lowercase
            if (putRestInLC)
            {
              // Check if this is uppercase
              if (Character.isUpperCase(ch))
              {
                // Make the character lowercase
                buf.setCharAt(nIndex, Character.toLowerCase(ch));
              }
            }
          }
          
          // Clear the flag
          blankFound = false;
        }
      }
      
      // Return it
      return (buf.toString());
    }
  }
  
  
  /**
   * Set the first character in the string to either
   * lowercase or uppercase.
   * 
   * @param str the string to convert
   * @param toUpper whether to make the first character uppercase or lowercase
   * @return the converted string
   */
  public static String setFirstCharacter(final String str,
                                         final boolean toUpper)
  {
    // Check the input
    if ((str == null) || (str.length() < 1))
    {
      return "";
    }
    
    // Build the string builder
    StringBuilder sb = new StringBuilder(str);
    
    // Check the operation to perform
    char ch = (toUpper ? Character.toUpperCase(str.charAt(0))
              : Character.toLowerCase(str.charAt(0)));
    
    // Update the character
    sb.setCharAt(0, ch);
    
    // Return the string
    return (sb.toString());
  }
  
  
  /**
   * Generate the class variable name from the column name.
   * 
   * @param columnName the column name
   * @return the corresponding class variable name
   */
  public static String generateFieldFromColumn(final String columnName)
  {
    // Check for upper-case runs in the column name
    final String colName = checkForUC(columnName);
    
    // Save the length
    final int colLen = colName.length();
    
    // This is the string that gets returned
    StringBuilder sb = new StringBuilder(colLen);
    
    // Iterate over the characters in the string
    boolean makeLower = true;
    for (int i = 0; i < colLen; ++i)
    {
      // Save the current character
      final char ch = colName.charAt(i);
      
      // If it's not a letter or a digit, skip it
      if (!Character.isLetterOrDigit(ch))
      {
        // Make the next character uppercase if
        // the string builder already has some
        // characters
        makeLower = (sb.length() == 0);
        
        // Go to the next character
        continue;
      }
      
      // Is this character uppercase?
      boolean isLower = Character.isLowerCase(ch);
      if (!isLower && (sb.length() > 0))
      {
        // It is, and the output string is not empty,
        // so make the character uppercase
        makeLower = false;
      }
      
      // Add the character to the string, after modifying the case
      if (makeLower)
      {
        sb.append(Character.toLowerCase(ch));
      }
      else
      {
        sb.append(Character.toUpperCase(ch));
      }
      
      // By default, the next character will be made lowercase
      makeLower = true;
    }
    
    // Return the string
    return sb.toString();
  }
  
  
  /**
   * Check for a run of uppercase characters.
   * 
   * @param inputName the string to check
   * @return modified input string
   */
  private static String checkForUC(final String inputName)
  {
    // See if the string has a non-uppercase letter
    boolean bHasNonUC = false;
    final int nLen = inputName.length();
    for (int i = 0; i < nLen; ++i)
    {
      // Check the character
      if (!Character.isUpperCase(inputName.charAt(i)))
      {
        // We found a character that's either not a letter,
        // or it's lowercase, so save the state and break
        bHasNonUC = true;
        break;
      }
    }
    
    // Check if a non-UC letter was found
    if (!bHasNonUC)
    {
      // None were found, so just return the string in all lower-case
      return (inputName.toLowerCase());
    }
    
    // Go through the string, looking for runs of uppercase letters
    StringBuilder sb = new StringBuilder(inputName);
    for (int i = 0; (i < nLen); ++i)
    {
      // Find the next uppercase character
      final char ch = sb.charAt(i);
      if (Character.isUpperCase(ch))
      {
        // Find the end of the run
        int j = i + i;
        boolean bContinue = true;
        while (bContinue && (j < nLen))
        {
          if (!Character.isUpperCase(sb.charAt(j)))
          {
            bContinue = false;
          }
          else
          {
            ++j;
          }
        }
        
        if (!bContinue)
        {
          if (Character.isLowerCase(sb.charAt(j)))
          {
            putInLowerCase(sb, i + 1, j - 1);
          }
          else
          {
            putInLowerCase(sb, i + 1, j);
          }
        }
        else
        {
          // Hit the end of the line
          putInLowerCase(sb, i + 1, j);
        }
        
        i = j;
      }
    }
    
    // Return the string
    return sb.toString();
  }
  
  
  /**
   * Put the input string in lowercase.
   * 
   * @param str the input string
   * @param nStart the start of the run
   * @param nEnd the end (exclusive) of the run
   */
  private static void putInLowerCase(final StringBuilder str,
                                     final int nStart,
                                     final int nEnd)
  {
    final int nLen = str.length();
    final int end = Math.min(nEnd, nLen);
    for (int i = nStart; i < end; ++i)
    {
      str.setCharAt(i, Character.toLowerCase(str.charAt(i)));
    }
  }
}
