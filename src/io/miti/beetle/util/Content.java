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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.swing.ImageIcon;

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
   * The header for a line from the input file meaning that the
   * line should be indented by some number of spaces when
   * placed in a JButton.
   */
  private static final String subMarker = "^";
  
  /** The replacement string for the subMarker string. */
  private static final String subLeader = "   ";
  
  /** Holder for no task data to return. */
  private static final HashMap<String, List<String>> NO_TASK_CONTENT =
    new HashMap<String, List<String>>(0);
  
  /** SQL file. */
  public static final int INITDB_SQL = 0;
  
  /** The JSON file of commands. */
  public static final int CMDS_JSON = 1;
  
  /** The map of filenames for IDs. */
  private static HashMap<Integer, String> mapFilenames = null;
  
  /**
   * Static block of code to execute at startup.
   */
  static
  {
    // Instantiate the filename hashmap.
    mapFilenames = new HashMap<Integer, String>(20);
    
    // Populate it with the filenames for different types of data.
    mapFilenames.put(Integer.valueOf(INITDB_SQL), "initdb.sql");
    mapFilenames.put(Integer.valueOf(CMDS_JSON), "cmds.json");
  }
  
  
  /**
   * Default constructor.
   */
  private Content()
  {
    super();
  }
  
  
  /**
   * Return the filename for the specified ID.
   * 
   * @param id the requested type of file
   * @return the corresponding file name
   */
  public static String getFilename(final int id)
  {
    return mapFilenames.get(Integer.valueOf(id));
  }
  
  
  /**
   * Return the contents of a file as a single string.
   * 
   * @param id the ID of the file to read
   * @return the contents of the file as a string
   */
  public static String getContentAsString(final int id)
  {
    // Get the data and check if it's null or empty
    final List<String> data = getContent(id);
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
  public static List<String> getContent(final int id)
  {
    // Check the parameter
    final String name = mapFilenames.get(Integer.valueOf(id));
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
   * Load the category and tasks.
   * 
   * @param id the mission ID
   * @return the categories and tasks for the mission
   */
  public static HashMap<String, List<String>> loadTask(final int id)
  {
    // Check the parameter
    final String name = mapFilenames.get(Integer.valueOf(id));
    if ((name == null) || (name.length() < 1))
    {
      // The ID was not found
      return NO_TASK_CONTENT;
    }
    
    // Get the file contents
    List<String> contents = readTextFromFile(name);
    if (contents == null)
    {
      return NO_TASK_CONTENT;
    }
    
    // Create the linked hash map of content
    HashMap<String, List<String>> map = new LinkedHashMap<String, List<String>>(10);
    
    // The subtask indentation for this task
    int subtaskIndent = -1;
    
    // Iterate over the data
    final int size = contents.size();
    String currSection = null;
    Vector<String> sectionData = null;
    for (int i = 0; i < size; ++i)
    {
      // Get the row
      final String row = contents.get(i);
      
      // Get the number of leading spaces
      final int numLeadingSpaces = getNumberOfLeadingSpaces(row);
      
      if (numLeadingSpaces == 0)
      {
        // Check if there's any data to save
        if ((currSection != null) && (sectionData != null) && (sectionData.size() > 0))
        {
          map.put(currSection, sectionData);
        }
        
        // Save the current section name
        currSection = row;
        
        // Reinitialize the list
        sectionData = new Vector<String>(20);
        
        // Reset the number of leading spaces for subtasks
        subtaskIndent = -1;
      }
      else if ((subtaskIndent < 0) || (numLeadingSpaces == subtaskIndent))
      {
        // If this is the first subtask for the current task, save the
        // number of leading spaces, for subsequent subtasks
        if (subtaskIndent < 0)
        {
          subtaskIndent = numLeadingSpaces;
        }
        
        // Read the row and turn a leading marker (^) into leading spaces
        final String nodeText = row.trim().replace(subMarker, subLeader);
        if (shouldAddNode(nodeText))
        {
          sectionData.add(nodeText);
        }
      }
    }
    
    // Add the last section
    if ((currSection != null) && (sectionData != null) && (sectionData.size() > 0))
    {
      map.put(currSection, sectionData);
    }
    
    // Return the content
    return map;
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
   * Return an ordered hashmap of limpet data from the file.
   * 
   * @param id the mission type
   * @return hashmap of tasks
   */
  public static HashMap<String, List<String>> getTaskContent(final int id)
  {
    // Check the parameter
    final String name = mapFilenames.get(Integer.valueOf(id));
    if ((name == null) || (name.length() < 1))
    {
      // The ID was not found
      return NO_TASK_CONTENT;
    }
    
    // Get the file contents
    List<String> contents = readTextFromFile(name);
    if (contents == null)
    {
      return NO_TASK_CONTENT;
    }
    
    // Create the linked hash map of content
    HashMap<String, List<String>> map = new LinkedHashMap<String, List<String>>(10);
    
    // The subtask indentation for this task
    int subtaskIndent = -1;
    
    // Iterate over the data
    final int size = contents.size();
    String currSection = null;
    List<String> sectionData = null;
    for (int i = 0; i < size; ++i)
    {
      // Get the row
      final String row = contents.get(i);
      
      // Get the number of leading spaces
      final int numLeadingSpaces = getNumberOfLeadingSpaces(row);
      
      if (numLeadingSpaces == 0)
      {
        // Check if there's any data to save
        if ((currSection != null) && (sectionData != null) && (sectionData.size() > 0))
        {
          map.put(currSection, sectionData);
        }
        
        // Save the current section name
        currSection = row;
        
        // Reinitialize the list
        sectionData = new ArrayList<String>(20);
        
        // Reset the number of leading spaces for subtasks
        subtaskIndent = -1;
      }
      else if ((subtaskIndent < 0) || (numLeadingSpaces == subtaskIndent))
      {
        // If this is the first subtask for the current task, save the
        // number of leading spaces, for subsequent subtasks
        if (subtaskIndent < 0)
        {
          subtaskIndent = numLeadingSpaces;
        }
        
        final String nodeText = row.trim();
        if (shouldAddNode(nodeText))
        {
          sectionData.add(nodeText);
        }
      }
    }
    
    // Add the last section
    if ((currSection != null) && (sectionData != null) && (sectionData.size() > 0))
    {
      map.put(currSection, sectionData);
    }
    
    // Return the content
    return map;
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
  
  
  /**
   * Let the caller specify a handler when reading each line.
   * 
   * @param handler the file content handler
   * @param id the input filename ID
   * @return whether a line was processed successfully
   */
  public static boolean processFileLines(final IContentHandler handler,
                                         final int id)
  {
    boolean result = false;
    if (Utility.readFilesAsStream())
    {
      result = processFileContentsStream(handler, id);
    }
    else
    {
      result = processFileContents(handler, id);
    }
    
    return result;
  }
  
  
  /**
   * Read the file as a regular file.
   * 
   * @param handler the file content handler
   * @param id the input filename ID
   * @return whether a line was processed successfully
   */
  private static boolean processFileContentsStream(final IContentHandler handler,
                                                   final int id)
  {
    // Declare the reader
    BufferedReader in = null;
    boolean lineProcessed = false;
    
    // Check the parameter
    final String fileName = getFilename(id);
    if ((fileName == null) || (fileName.length() < 1))
    {
      // The ID was not found
      return lineProcessed;
    }
    
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
          // Process the line and check whether to continue processing
          if (submitCmd(str) && !handler.processLine(str))
          {
            // Stop processing
            lineProcessed = true;
            break;
          }
        }
        
        // Close the input reader
        in.close();
        in = null;
      }
    }
    catch (IOException e)
    {
      Logger.error("Exception: " + e.getMessage());
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
    
    return lineProcessed;
  }
  
  
  /**
   * Read the file as a regular file.
   * 
   * @param handler the file content handler
   * @param id the input filename ID
   * @return whether a line was processed successfully
   */
  private static boolean processFileContents(final IContentHandler handler,
                                             final int id)
  {
    // Initialize the reader
    BufferedReader in = null;
    boolean lineProcessed = false;
    
    // Check the parameter
    final String fileName = getFilename(id);
    if ((fileName == null) || (fileName.length() < 1))
    {
      // The ID was not found
      return lineProcessed;
    }
    
    // Process the file
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
          // Process the line and check whether to continue processing
          if (submitCmd(str) && !handler.processLine(str))
          {
            // Stop processing
            lineProcessed = true;
            break;
          }
        }
        
        // Close the reader
        in.close();
        in = null;
      }
    }
    catch (IOException e)
    {
      Logger.error("Exception: " + e.getMessage());
      e.printStackTrace();
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
    
    return lineProcessed;
  }
  
  
  /**
   * Do some basic error-checking for the commands.
   * 
   * @param str the line from the commands file
   * @return whether to submit for processing
   */
  private static boolean submitCmd(String str)
  {
    // String is null or empty
    if ((str == null) || (str.trim().length() < 1))
    {
      return false;
    }
    
    // The line is a comment
    if (str.startsWith("#"))
    {
      return false;
    }
    
    // Process it
    return true;
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
   * Let the caller specify a handler when reading each line.
   * 
   * @param handler the file content handler
   * @param id the input filename ID
   * @return whether a line was processed successfully
   */
  public static List<String> getFileLines(final int id)
  {
    if (Utility.readFilesAsStream())
    {
      return getFileContentsStream(id);
    }
    else
    {
      return getFileContents(id);
    }
  }
  
  
  /**
   * Read the file as a regular file.
   * 
   * @param handler the file content handler
   * @param id the input filename ID
   * @return whether a line was processed successfully
   */
  private static List<String> getFileContentsStream(final int id)
  {
    // Declare the reader
    BufferedReader in = null;
    List<String> lines = new ArrayList<String>(20);
    
    // Check the parameter
    final String fileName = getFilename(id);
    if ((fileName == null) || (fileName.length() < 1))
    {
      // The ID was not found
      return lines;
    }
    
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
          final String cmd = parseCmdFromLine(str);
          if (cmd != null)
          {
            lines.add(cmd);
          }
        }
        
        // Close the input reader
        in.close();
        in = null;
      }
    }
    catch (IOException e)
    {
      Logger.error("Exception: " + e.getMessage());
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
    
    return lines;
  }
  
  
  /**
   * Read the file as a regular file.
   * 
   * @param handler the file content handler
   * @param id the input filename ID
   * @return whether a line was processed successfully
   */
  private static List<String> getFileContents(final int id)
  {
    // Initialize the reader
    BufferedReader in = null;
    List<String> lines = new ArrayList<String>(20);
    
    // Check the parameter
    final String fileName = getFilename(id);
    if ((fileName == null) || (fileName.length() < 1))
    {
      // The ID was not found
      return lines;
    }
    
    // Process the file
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
          final String cmd = parseCmdFromLine(str);
          if (cmd != null)
          {
            lines.add(cmd);
          }
        }
        
        // Close the reader
        in.close();
        in = null;
      }
    }
    catch (IOException e)
    {
      Logger.error("Exception: " + e.getMessage());
      e.printStackTrace();
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
    
    return lines;
  }
  
  
  private static String parseCmdFromLine(String str)
  {
    // Check the input
    if ((str == null) || (str.trim().length() < 1))
    {
      return null;
    }
    
    // Check for a comment
    if (str.startsWith("#"))
    {
      return null;
    }
    
    // Find the colon, and return anything after it
    final int colonIndex = str.indexOf(":");
    if ((colonIndex < 0) || (colonIndex == (str.length() - 1)))
    {
      return null;
    }
    
    return str.substring(colonIndex + 1).trim();
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
   * Determine whether the node should be added to the tree.
   *  
   * @param nodeText the text of the node
   * @return whether to add the node to the tree
   */
  private static boolean shouldAddNode(final String nodeText)
  {
    // Check the input
    if ((nodeText == null) || (nodeText.length() < 1) || (nodeText.startsWith("#")))
    {
      // The node should not be added
      return false;
    }
    
    // By default, add the node to the tree
    return true;
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
          in = new BufferedReader(new FileReader(file));
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
      // Get the name of this jar file
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
   * Get the specified icon.
   * 
   * @param filename the name of the image file
   * @return the generated icon
   */
  public static ImageIcon getIcon(final String filename)
  {
    // Check how to read the input files
    if (Utility.readFilesAsStream())
    {
      return new ImageIcon(Content.class.getResource("/" + filename));
    }
    
    return new ImageIcon("data/" + filename);
  }
}
