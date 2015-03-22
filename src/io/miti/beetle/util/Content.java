package io.miti.beetle.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * Class to manage providing content from flat text-files.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class Content
{
  /**
   * Holder for no data to return.
   */
  private static final List<String> NO_CONTENT = new ArrayList<String>(0);
  
  /**
   * The file containing the required database version.
   */
  public static final String REQD_DB_FILE = "reqd_db.txt";
  
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
  
  
  /**
   * Return the list of files in the content directory.
   * 
   * @param prefix the filename prefix to match on (or null)
   * @param suffix the filename suffix to match on (or null)
   * @return the list of matching file names
   */
  public static List<String> getFileList(final String prefix,
                                         final String suffix)
  {
    // Return the list of files based on how we read files
    return ((Utility.readFilesAsStream()) ? getFileListFromJar(prefix, suffix)
                                          : getFileListFromDir(prefix, suffix));
  }
  
  
  /**
   * Return the list of files in the jar file that match on
   * prefix and suffix.
   * 
   * @param prefix the filename prefix to match on (or null)
   * @param suffix the filename suffix to match on (or null)
   * @return the list of matching file names
   */
  private static List<String> getFileListFromJar(final String prefix,
                                                 final String suffix)
  {
    // Declare the list that gets returned at the end
    List<String> names = new ArrayList<String>(10);
    
    try
    {
      // Get the name of this jar file (usually 'diver.jar')
      String jarPath = Content.class.getProtectionDomain().getCodeSource()
                              .getLocation().toURI().getPath();
      
      // Get a reference to the jar file
      JarFile jar = new JarFile(jarPath);
      Logger.info("The jar file name is " + jar.getName());
      
      // Iterate over the jar entries
      Enumeration<JarEntry> e = jar.entries();
      while (e.hasMoreElements())
      {
        // Get the element and save the name
        ZipEntry ze = (ZipEntry) e.nextElement();
        String name = ze.getName();
        
        // Test whether this file name is a match
        if (shouldAcceptFile(name, prefix, suffix))
        {
          // Save this name
          Logger.debug("  Found the match " + name);
          names.add(name);
        }
      }
      
      jar.close();
    }
    catch (URISyntaxException e)
    {
      Logger.error("URISyntaxException while getting the list of files in the jar");
      Logger.error(e);
    }
    catch (IOException e)
    {
      Logger.error("IOException while getting the list of files in the jar");
      Logger.error(e);
    }
    
    // Return the list of matching file names
    return names;
  }
  
  
  /**
   * Return the list of files in the content directory matching on
   * prefix and suffix.
   * 
   * @param prefix the filename prefix to match on (or null)
   * @param suffix the filename suffix to match on (or null)
   * @return the list of matching file names
   */
  private static List<String> getFileListFromDir(final String prefix,
                                                 final String suffix)
  {
    // Declare the list that gets returned at the end
    List<String> names = new ArrayList<String>(5);
    
    // Declare our filename filter, later passed to File::listFiles
    FilenameFilter filter = new FilenameFilter()
    {
      /**
       * Return whether to accept the file, based on matching the
       * specified prefix and suffix.
       * 
       * @param dir the directory path
       * @param name the file name
       * @return whether to accept the file
       */
      @Override
      public boolean accept(final File dir, final String name)
      {
        return shouldAcceptFile(name, prefix, suffix);
      }
    };
    
    // Get files matching on prefix and suffix
    File dir = new File(getContentPath("."));
    File[] files = dir.listFiles(filter);
    
    // Check for a null list
    if (files == null)
    {
      return names;
    }
    
    // Debugging info.  Check if we're logging anything so we don't
    // traverse the array without needing to.
    if (Logger.getLogLevel() > 0)
    {
      Logger.debug("Found " + Integer.toString(files.length) + " files in " +
          "content with prefix " + prefix + " and suffix " + suffix);
      for (File f : files)
      {
        Logger.debug("  File: " + f.getName());
      }
    }
    
    // Copy the names of the files
    for (File f : files)
    {
      names.add(f.getName());
    }
    
    // Return the list of filenames
    return names;
  }
  
  
  /**
   * Determine whether to accept the filename as a match.
   * 
   * @param name the filename
   * @param prefix the prefix to match (if not null)
   * @param suffix the suffix to match (if not null)
   * @return whether the file is a match
   */
  private static boolean shouldAcceptFile(final String name,
                                          final String prefix,
                                          final String suffix)
  {
    // Check the filename
    if (name == null)
    {
      return false;
    }
    
    // Check the name against the prefix (if not null)
    if ((prefix != null) && (!name.startsWith(prefix)))
    {
      return false;
    }
    
    // Check the name against the suffix (if not null)
    if ((suffix != null) && (!name.endsWith(suffix)))
    {
      return false;
    }
    
    // The file must match, so return true
    return true;
  }
  
  
  /**
   * Return the contents of a file as a single string.
   * 
   * @param id the ID of the file to read
   * @return the contents of the file as a string
   */
  public static String getContentAsString(final String fname)
  {
    // Get the data and check if it's null or empty
    final List<String> data = getContent(fname);
    if ((data == null) || (data.size() < 1))
    {
      // No strings to join
      return "";
    }
    
    // Build the string, adding a space after each line
    StringBuilder sb = new StringBuilder(500);
    for (String row : data)
    {
      sb.append(row).append(' ');
    }
    
    // Empty the list
    data.clear();
    
    // Return the string
    return sb.toString();
  }
  
  
  /**
   * Return the contents of the file associated with the specified ID.
   * 
   * @param id a supported data ID
   * @return the contents of the file associated with the ID
   */
  public static List<String> getContent(final String name)
  {
    if ((name == null) || (name.length() < 1))
    {
      // The ID was not found
      return NO_CONTENT;
    }
    
    // Get the file contents
    List<String> contents = readTextFromFile(name);
    
    // Return the contents of the file
    return contents;
  }
  
  
  /**
   * Read the contents of a file and return as a list of strings.
   * 
   * @param fileName the input filename
   * @return list of strings from the file
   */
  private static List<String> readTextFromFile(final String fileName)
  {
    // Declare the list of strings that will get populated
    // with the file contents
    List<String> contents = new ArrayList<String>(50);
    
    // Check how to read the input file
    if (Utility.readFilesAsStream())
    {
      readFileContentsAsStream(contents, fileName);
    }
    else
    {
      readFileContents(contents, fileName);
    }
    
    // Return the file contents
    return contents;
  }
  
  
  /**
   * Read the file as a stream.
   * 
   * @param contents the list to populate with the file contents
   * @param fileName the input filename
   */
  private static void readFileContentsAsStream(final List<String> contents,
                                               final String fileName)
  {
    // Declare the reader
    BufferedReader in = null;
    
    try
    {
      // Get the name of the input file
      final String fullPath = getContentPath(fileName);
      
      // Get the input stream
      InputStream is = Content.class.getResourceAsStream(fullPath);
      if (is == null)
      {
        Logger.error("Exception: Stream is null for " + fullPath);
      }
      else
      {
        // Open the reader
        in = new BufferedReader(new InputStreamReader(is));
        
        // Read the stream and add each row to contents
        String str;
        while ((str = in.readLine()) != null)
        {
          contents.add(str);
        }
        
        // Close the input reader
        in.close();
        in = null;
      }
    }
    catch (IOException e)
    {
      Logger.error("Exception: " + e.getMessage());
      contents.clear();
    }
    finally
    {
      // Check if the reader was closed
      if (in != null)
      {
        try
        {
          in.close();
        }
        catch (IOException e)
        {
          Logger.error(e);
        }
        
        in = null;
      }
    }
  }
  
  
  /**
   * Read the file as a regular file.
   * 
   * @param contents the list to populate with the file contents
   * @param fileName the input filename
   */
  private static void readFileContents(final List<String> contents,
                                       final String fileName)
  {
    // Initialize the reader
    BufferedReader in = null;
    
    try
    {
      // Get the full path/filename
      final String fullPath = getContentPath(fileName);
      if (fullPath == null)
      {
        Logger.error("Exception: Null path for " + fileName);
      }
      else
      {
        // Open the reader
        FileReader reader = new FileReader(fullPath);
        
        // Open the reader
        in = new BufferedReader(reader);
        String str;
        while ((str = in.readLine()) != null)
        {
          contents.add(str);
        }
        
        // Close the reader
        in.close();
        in = null;
      }
    }
    catch (IOException e)
    {
      Logger.error("Exception: " + e.getMessage());
      contents.clear();
    }
    finally
    {
      // Check if the reader was closed
      if (in != null)
      {
        try
        {
          in.close();
        }
        catch (IOException e)
        {
          Logger.error(e);
        }
        
        in = null;
      }
    }
  }
}
