package io.miti.beetle.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * Class to manage providing content from flat text-files.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class Content
{
  /**
   * Default constructor.
   */
  private Content()
  {
    super();
  }
  
  
  /**
   * Write out the strings in the list.
   * 
   * @param list the strings to print
   */
  public static void printList(final HashMap<String, List<String>> list)
  {
    // Check the input
    if (list == null)
    {
      Logger.error("Content::printList(): The list is null");
      return;
    }
    
    // Iterate over the list of keys
    for (Entry<String, List<String>> e : list.entrySet())
    {
      // Write out the key
      Logger.info(e.getKey());
      
      // Iterate over the list of strings for this key
      for (String value : e.getValue())
      {
        Logger.info("  " + value);
      }
    }
  }
  
  
  /**
   * Return the path to use for accessing this file.
   * 
   * @param fileName the input file name
   * @return the full path for the file name
   */
  public static String getContentPath(final String fileName)
  {
    // Check how to read the input files
    if (Utility.readFilesAsStream())
    {
      return "/" + fileName;
    }
    
    return "data/" + fileName;
  }
  
  
  public static InputStream getFileStream(final String filename)
  {
	  if (Utility.readFilesAsStream())
	  {
		  return getStreamFromStream(filename);
	  }
	  else
	  {
		  return getStreamFromFile(filename);
	  }
  }
  
  
  public static InputStream getStreamFromStream(final String filename)
  {
      // Get the name of the input file
      final String fullPath = getContentPath(filename);
      
      // Get the input stream
      InputStream is = Content.class.getResourceAsStream(fullPath);
	  return is;
  }
  
  
  public static InputStream getStreamFromFile(final String filename)
  {
      // Get the name of the input file
      final String fullPath = getContentPath(filename);
      InputStream is = null;
      try
      {
		is = new FileInputStream(fullPath);
      }
      catch (FileNotFoundException e)
      {
		e.printStackTrace();
	  }
      
      return is;
  }


  /**
   * Return the number of leading spaces.
   * 
   * @param input the input string
   * @return the number of leading spaces
   */
  public static int getNumberOfLeadingSpaces(final String input)
  {
    // Check the input
    if ((input == null) || (input.length() < 1))
    {
      return 0;
    }
    
    // Save the string length
    final int len = input.length();
    
    // The current index into the string
    int index = 0;
    
    // Iterate over the string until we hit either the end of the
    // string or a non-space character
    while ((index < len) && (input.charAt(index) == ' '))
    {
      ++index;
    }
    
    // Return the number of leading spaces
    return index;
  }
  
  
  /**
   * Return the contents of a file as a string.
   * 
   * @param file the input file
   * @return the contents of the file
   */
  public static String getFileAsText(final File file)
  {
      // Check the input parameter
      if((file == null) || (!file.exists()) || (file.isDirectory()))
      {
          return "";
      }

      // Get the text of the file
      StringBuilder sb = new StringBuilder(1000);

      // Read the file
      BufferedReader in = null;
      try
      {
    	  in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
          String str;
          while((str = in.readLine()) != null)
          {
              sb.append(str).append('\n');
          }

          in.close();
          in = null;
      }
      catch(IOException e)
      {
          e.printStackTrace();
      }
      finally
      {
          if(in != null)
          {
              try
              {
                  in.close();
              }
              catch(IOException e)
              {
                  e.printStackTrace();
              }

              in = null;
          }
      }

      // Return the builder
      return sb.toString();
  }
  
  
  /**
   * Return the contents of a file as a string.
   * 
   * @param file the input file
   * @return the contents of the file
   */
  public static String getFileAsText(final File file, final int maxRowCount)
  {
      // Check the input parameter
      if((file == null) || (!file.exists()) || (file.isDirectory()) || (maxRowCount == 0))
      {
          return "";
      }

      // Get the text of the file
      StringBuilder sb = new StringBuilder(1000);

      // Read the file
      BufferedReader in = null;
      try
      {
          in = new BufferedReader(new FileReader(file));
          String str;
          int currLine = 1;
          while((str = in.readLine()) != null)
          {
        	  ++currLine;
              sb.append(str).append('\n');
              
              if ((maxRowCount >= 0) && (currLine > maxRowCount)) {
            	  break;
              }
          }

          in.close();
          in = null;
      }
      catch(IOException e)
      {
          e.printStackTrace();
      }
      finally
      {
          if(in != null)
          {
              try
              {
                  in.close();
              }
              catch(IOException e)
              {
                  e.printStackTrace();
              }

              in = null;
          }
      }

      // Return the builder
      return sb.toString();
  }
  
  
  /**
   * Return the contents of a file as a string.
   * 
   * @param file the input file
   * @return the contents of the file
   */
  public static List<String> getFileAsTextArray(final File file, final int maxRowCount)
  {
      // Check the input parameter
      if((file == null) || (!file.exists()) || (file.isDirectory()) || (maxRowCount == 0))
      {
          return Collections.<String>emptyList();
      }

      // Get the text of the file
      List<String> lines = new ArrayList<String>(10);
      
      // Read the file
      BufferedReader in = null;
      try
      {
    	  in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
          String str;
          int currLine = 1;
          while((str = in.readLine()) != null)
          {
        	  ++currLine;
        	  lines.add(str);
              
              if ((maxRowCount >= 0) && (currLine > maxRowCount)) {
            	  break;
              }
          }

          in.close();
          in = null;
      }
      catch(IOException e)
      {
          e.printStackTrace();
      }
      finally
      {
          if(in != null)
          {
              try
              {
                  in.close();
              }
              catch(IOException e)
              {
                  e.printStackTrace();
              }

              in = null;
          }
      }

      // Return the builder
      return lines;
  }
}
