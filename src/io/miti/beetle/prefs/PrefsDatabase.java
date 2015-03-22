/**
 * @(#)Database.java
 * 
 * Created on Apr 30, 2007
 *
 * Copyright 2007 MobilVox, Inc. All rights reserved.
 * MOBILVOX PROPRIETARY/CONFIDENTIAL.
 */

package io.miti.beetle.prefs;

import io.miti.beetle.dbutil.FetchDatabaseRecords;
import io.miti.beetle.util.Content;
import io.miti.beetle.util.Logger;
import io.miti.beetle.util.Utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.h2.tools.RunScript;

/**
 * Encapsulate the database functionality.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class PrefsDatabase
{
  /**
   * The database connection.
   */
  private static Connection conn = null;
  
  /**
   * Whether the database driver has been loaded into memory.
   */
  private static boolean driverLoaded = false;
  
  /**
   * Declare the URL for our database.
   */
  private static final String DB_URL = "jdbc:h2:./btl";
  
  
  /**
   * Default constructor.
   */
  private PrefsDatabase()
  {
    super();
  }
  
  
  /**
   * Generate a script of the database.
   */
  public static void generateScript()
  {
    // Get the connection
    getConnection();
    
    // Generate the script
    DBScript.processDatabase("dbbackup.sql", conn);
    
    // Close the connection
    closeConnection();
  }
  
  
  /**
   * Execute an Insert against a database table.
   * 
   * @param sql the SQL command to run
   * @param inserter the object used to set the data fields in the SQL command
   * @return the result of the operation
   */
  public static boolean insert(final String sql, final IInsertable inserter)
  {
    // The return value
    boolean bResult = false;
    
    // Check the DB connection
    if (conn == null)
    {
      return bResult;
    }
    else if ((sql == null) || (sql.length() < 1))
    {
      return bResult;
    }
    
    // Log this event, if debugging
    parseSqlInsert(sql);
    
    PreparedStatement ps = null;
    try
    {
      // Execute the statement
      ps = conn.prepareStatement(sql);
      
      // If the IUpdateable object is not null, call its method
      if (inserter != null)
      {
        inserter.setInsertFields(ps);
      }
      
      // Execute the update statement
      ps.executeUpdate();
      
      // We reached this point, so it must have succeeded
      bResult = true;
      
      // Close the statement
      ps.close();
      ps = null;
    }
    catch (SQLException e)
    {
      Logger.error(e);
    }
    finally
    {
      // Close the Statement if it's not null
      try
      {
        if (ps != null)
        {
          ps.close();
          ps = null;
        }
      }
      catch (SQLException sqle)
      {
        Logger.error(sqle);
      }
    }
    
    return bResult;
  }
  
  
  /**
   * Execute an Insert against a database table.
   * 
   * @param sql the SQL command to run
   * @param inserter the object used to set the data fields in the SQL command
   * @param ider the identifiable object
   * @return the result of the operation
   */
  public static boolean insert(final String sql,
                                 final IInsertable inserter,
                                 final IIdentifiable ider)
  {
    // The return value
    boolean bResult = false;
    
    // Check the DB connection
    if (conn == null)
    {
      return bResult;
    }
    else if ((sql == null) || (sql.length() < 1))
    {
      return bResult;
    }
    
    // Log this event, if debugging
    parseSqlInsert(sql);
    
    PreparedStatement ps = null;
    ResultSet rs = null;
    try
    {
      // Execute the statement
      ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      
      // If the IUpdateable object is not null, call its method
      if (inserter != null)
      {
        inserter.setInsertFields(ps);
      }
      
      // Execute the update statement
      ps.executeUpdate();
      
      // Get the result set with the generated key
      rs = ps.getGeneratedKeys();
      
      // Check the returned result set
      if (rs != null)
      {
        if (rs.next())
        {
          ider.setId(rs.getInt(1));
        }
        
        // Close the result set
        rs.close();
        rs = null;
      }
      
      // We reached this point, so it must have succeeded
      bResult = true;
      
      // Close the statement
      ps.close();
      ps = null;
    }
    catch (SQLException e)
    {
      Logger.error(e);
    }
    finally
    {
      // Close the Statement if it's not null
      try
      {
        if (ps != null)
        {
          ps.close();
          ps = null;
        }
      }
      catch (SQLException sqle)
      {
        Logger.error(sqle);
      }
      
      // Close the Result Set if it's not null
      try
      {
        if (rs != null)
        {
          rs.close();
          rs = null;
        }
      }
      catch (SQLException sqle)
      {
        Logger.error(sqle);
      }
    }
    
    return bResult;
  }
  
  
  /**
   * Execute an Update against a database table.
   * 
   * @param sql the SQL command to run
   * @param updater the object used to set the data fields in the SQL command
   * @return the result of the operation
   */
  public static boolean update(final String sql, final IUpdateable updater)
  {
    // The return value
    boolean bResult = false;
    
    // Check the DB connection
    if (conn == null)
    {
      return bResult;
    }
    else if ((sql == null) || (sql.length() < 1))
    {
      return bResult;
    }
    
    // Log this event, if debugging
    parseSqlUpdate(sql);
    
    PreparedStatement ps = null;
    try
    {
      // Execute the statement
      ps = conn.prepareStatement(sql);
      
      // If the IUpdateable object is not null, call its method
      if (updater != null)
      {
        updater.setUpdateFields(ps);
      }
      
      // Execute the update statement
      ps.executeUpdate();
      
      // We reached this point, so it must have succeeded
      bResult = true;
      
      // Close the statement
      ps.close();
      ps = null;
    }
    catch (SQLException e)
    {
      Logger.error(e);
    }
    finally
    {
      // Close the Statement if it's not null
      try
      {
        if (ps != null)
        {
          ps.close();
          ps = null;
        }
      }
      catch (SQLException sqle)
      {
        Logger.error(sqle);
      }
    }
    
    return bResult;
  }
  
  
  /**
   * Executes a database SELECT.
   * 
   * @param sqlCmd the database ststement to execute
   * @param listData will hold the retrieved data
   * @param fetcher used to retrieve the selected database columns
   * @return the result of the operation
   */
  public static boolean executeSelect(final String sqlCmd,
                                      final List<?> listData,
                                      final FetchDatabaseRecords fetcher)
  {
    // The return value
    boolean bResult = false;
    
    // Check the SQL command
    if ((sqlCmd == null) || (sqlCmd.length() < 1))
    {
      return bResult;
    }
    else if (conn == null)
    {
      return bResult;
    }
    
    // Check the fetcher
    if (fetcher == null)
    {
      return bResult;
    }
    
    // Log this event, if debugging
    parseSqlSelect(sqlCmd);
    
    // Execute the statement and get the returned ID
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try
    {
      // Create the Statement object from the connection
      stmt = conn.prepareStatement(sqlCmd);
      if (null != stmt)
      {
        // Now execute the query and save the result set
        rs = stmt.executeQuery();
        
        // If we reached this point, no error was found
        bResult = true;
        
        // Check for a result
        if (rs != null)
        {
          // Get the data from the result set
          fetcher.getFields(rs, listData);
          
          // Close the result set
          rs.close();
          rs = null;
        }
        
        // Close the statement
        stmt.close();
        stmt = null;
      }
    }
    catch (SQLException sqlex)
    {
      Logger.error(sqlex);
    }
    catch (Exception ex)
    {
      Logger.error(ex, -1);
    }
    finally
    {
      // Close the ResultSet if it's not null
      try
      {
        if (rs != null)
        {
          rs.close();
          rs = null;
        }
      }
      catch (SQLException sqle)
      {
        Logger.error(sqle);
      }
      
      // Close the Statement if it's not null
      try
      {
        if (stmt != null)
        {
          stmt.close();
          stmt = null;
        }
      }
      catch (SQLException sqle)
      {
        Logger.error(sqle);
      }
    }
    
    // Return the result of the operation
    return bResult;
  }
  
  
  /**
   * Record a SQL event.
   * 
   * @param table the table name
   * @param event the database event
   */
  @SuppressWarnings("all")
  public static synchronized void recordSqlEvent(final String table,
                                                    final DatabaseEvent event)
  {
    SQLLogger.getInstance().recordEvent(table, event);
  }
  
  
  /**
   * Parse out the table name from a SELECT statement, and log it.
   * 
   * @param sqlCmd the SQL command to parse
   */
  @SuppressWarnings("all")
  private static void parseSqlSelect(final String sqlCmd)
  {
    // Check if we are logging this event
    if (!SQLLogger.getInstance().willRecordEvent(DatabaseEvent.SELECT))
    {
      return;
    }
    
    // Parse out the table name from the SQL command
    final int index = sqlCmd.toLowerCase().indexOf(" from dm_");
    if (index < 0)
    {
      recordSqlEvent("UNKNOWN", DatabaseEvent.SELECT);
    }
    else
    {
      String ending = sqlCmd.substring(index + 6);
      int space = ending.indexOf(' ');
      String table = ((space < 0) ? ending : ending.substring(0, space));
      recordSqlEvent(table, DatabaseEvent.SELECT);
    }
  }
  
  
  /**
   * Parse out the table name from a DELETE statement, and log it.
   * 
   * @param sqlCmd the SQL command to parse
   */
  @SuppressWarnings("all")
  private static void parseSqlDelete(final String sqlCmd)
  {
    // Check if we are logging this event
    if (!SQLLogger.getInstance().willRecordEvent(DatabaseEvent.DELETE))
    {
      return;
    }
    
    // Parse out the table name from the SQL command
    final int index = sqlCmd.toLowerCase().indexOf(" from dm_");
    if (index < 0)
    {
      recordSqlEvent("UNKNOWN", DatabaseEvent.DELETE);
    }
    else
    {
      String ending = sqlCmd.substring(index + 6);
      int space = ending.indexOf(' ');
      String table = ((space < 0) ? ending : ending.substring(0, space));
      recordSqlEvent(table, DatabaseEvent.DELETE);
    }
  }
  
  
  /**
   * Parse out the table name from an UPDATE statement, and log it.
   * 
   * @param sqlCmd the SQL command to parse
   */
  @SuppressWarnings("all")
  private static void parseSqlUpdate(final String sqlCmd)
  {
    // Check if we are logging this event
    if (!SQLLogger.getInstance().willRecordEvent(DatabaseEvent.UPDATE))
    {
      return;
    }
    
    // Parse out the table name from the SQL command
    final int index = sqlCmd.toLowerCase().indexOf("update dm_");
    if (index < 0)
    {
      recordSqlEvent("UNKNOWN", DatabaseEvent.UPDATE);
    }
    else
    {
      String ending = sqlCmd.substring(index + 7);
      int space = ending.indexOf(' ');
      String table = ((space < 0) ? ending : ending.substring(0, space));
      recordSqlEvent(table, DatabaseEvent.UPDATE);
    }
  }
  
  
  /**
   * Parse out the table name from an INSERT statement, and log it.
   * 
   * @param sqlCmd the SQL command to parse
   */
  @SuppressWarnings("all")
  private static void parseSqlInsert(final String sqlCmd)
  {
    // Check if we are logging this event
    if (!SQLLogger.getInstance().willRecordEvent(DatabaseEvent.INSERT))
    {
      return;
    }
    
    // Parse out the table name from the SQL command
    final int index = sqlCmd.toLowerCase().indexOf("insert into dm_");
    if (index < 0)
    {
      recordSqlEvent("UNKNOWN", DatabaseEvent.INSERT);
    }
    else
    {
      String ending = sqlCmd.substring(index + 12);
      int space = ending.indexOf(' ');
      String table = ((space < 0) ? ending : ending.substring(0, space));
      recordSqlEvent(table, DatabaseEvent.INSERT);
    }
  }
  
  
  /**
   * Executes a database SELECT that returns a single integer, such
   * as 'select count(x) from y'.
   * 
   * @param sqlCmd the database ststement to execute
   * @return the integer in the select statement
   */
  public static int executeSelectToReturnInt(final String sqlCmd)
  {
    // The return value
    int result = -1;
    
    // Check the SQL command
    if ((sqlCmd == null) || (sqlCmd.length() < 1))
    {
      return result;
    }
    else if (conn == null)
    {
      return result;
    }
    
    // Log this event, if debugging
    parseSqlSelect(sqlCmd);
    
    // Execute the statement and get the returned ID
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try
    {
      // Create the Statement object from the connection
      stmt = conn.prepareStatement(sqlCmd);
      if (null != stmt)
      {
        // Now execute the query and save the result set
        rs = stmt.executeQuery();
        
        // Check for a result
        if (rs != null)
        {
          // Check for a result
          if (rs.next())
          {
            // Save the value
            result = rs.getInt(1);
          }
          
          // Close the result set
          rs.close();
          rs = null;
        }
        
        // Close the statement
        stmt.close();
        stmt = null;
      }
    }
    catch (SQLException sqlex)
    {
      Logger.error(sqlex);
    }
    catch (Exception ex)
    {
      Logger.error(ex, -1);
    }
    finally
    {
      // Close the ResultSet if it's not null
      try
      {
        if (rs != null)
        {
          rs.close();
          rs = null;
        }
      }
      catch (SQLException sqle)
      {
        Logger.error(sqle);
      }
      
      // Close the Statement if it's not null
      try
      {
        if (stmt != null)
        {
          stmt.close();
          stmt = null;
        }
      }
      catch (SQLException sqle)
      {
        Logger.error(sqle);
      }
    }
    
    // Return the result of the operation
    return result;
  }
  
  
  /**
   * Delete a record from the database.
   * 
   * @param sql the SQL command to execute
   */
  public static void delete(final String sql)
  {
    if (conn == null)
    {
      return;
    }
    else if ((sql == null) || (sql.length() < 1))
    {
      return;
    }
    
    // Log this event, if debugging
    parseSqlDelete(sql);
    
    PreparedStatement ps = null;
    try
    {
      // Execute the statement
      ps = conn.prepareStatement(sql);
      ps.executeUpdate();
      
      // Close the statement
      ps.close();
      ps = null;
    }
    catch (SQLException e)
    {
      Logger.error(e);
    }
    finally
    {
      // Close the Statement if it's not null
      try
      {
        if (ps != null)
        {
          ps.close();
          ps = null;
        }
      }
      catch (SQLException sqle)
      {
        Logger.error(sqle);
      }
    }
  }
  
  
  /**
   * Initialize the database.
   * 
   * @return the success of the operation
   */
  public static boolean initializeDatabase()
  {
    // Create the connection
    if (null == getConnection())
    {
      return false;
    }
    
    // See if the tables exist
    List<String> tables = getTableNames();
    if ((tables != null) && (tables.size() > 0))
    {
      // The tables exist, so return
      return true;
    }
    
    // Build the database by running the boot-strap script
    boolean result = executeScript("initdb.sql");
    return result;
  }
  
  
  /**
   * Execute the specified SQL file against the database.
   * 
   * @param scriptFile the name of the file
   * @return the result status
   */
  public static boolean executeSqlFile(final String scriptFile)
  {
    // Verify the file exists
    File file = new File(scriptFile);
    if (!file.exists())
    {
      // The file doesn't exist
      Logger.error("The file " + scriptFile + " was not found");
      return false;
    }
    else if (!file.isFile())
    {
      // It's not a file
      Logger.error("The directory " + scriptFile + " must be a file");
      return false;
    }
    
    // Run the file
    boolean result = false;
    Reader reader = null;
    try
    {
      // Execute the script
      reader = new FileReader(scriptFile);
      
      // Execute the script
      RunScript.execute(conn, reader);
      
      // If we reach this point, the operation was successful
      result = true;
      
      // Close the reader
      reader.close();
      reader = null;
      
      // Commit the changes
      commitChanges();
    }
    catch (SQLException e)
    {
      Logger.error(e);
    }
    catch (FileNotFoundException e)
    {
      Logger.error(e);
    }
    catch (IOException e)
    {
      Logger.error(e);
    }
    finally
    {
      if (reader != null)
      {
        try
        {
          reader.close();
        }
        catch (IOException e)
        {
          Logger.error(e, 10);
        }
        
        reader = null;
      }
    }
    
    // Return the result of reading the file
    return result;
  }
  
  
  /**
   * Execute the specified script against the database.
   * 
   * @param scriptFile the name of the database script
   * @return the result status
   */
  public static boolean executeScript(final String scriptFile)
  {
    boolean result = false;
    try
    {
      // Execute the script
      String filename = Content.getContentPath(scriptFile);
      Reader reader = null;
      if (Utility.readFilesAsStream())
      {
        InputStream is = PrefsDatabase.class.getResourceAsStream(filename);
        reader = new BufferedReader(new InputStreamReader(is));
      }
      else
      {
        reader = new FileReader(filename);
      }
      
      // Execute the script
      RunScript.execute(conn, reader);
      
      // The operation was successful
      result = true;
      
      // Close the reader
      reader.close();
      reader = null;
    }
    catch (SQLException e)
    {
      Logger.error(e);
    }
    catch (FileNotFoundException e)
    {
      Logger.error(e);
    }
    catch (IOException e)
    {
      Logger.error(e);
    }
    
    return result;
  }
  
  
  /**
   * Return the list of table names.
   * 
   * @return the list of table names
   */
  public static List<String> getTableNames()
  {
    // This will hold the list of table names
    List<String> tableNames = new ArrayList<String>(20);
    
    // Ensure the connection is created
    getConnection();
    
    // Get the list of table names
    try
    {
      // Gets the database metadata
      DatabaseMetaData dbmd = conn.getMetaData();
      
      // Specify the type of object; in this case we want tables
      String[] types = {"TABLE"};
      ResultSet resultSet = dbmd.getTables(null, null, "%", types);
      
      // Get the table names
      while (resultSet.next())
      {
        // Get the table name
        String tableName = resultSet.getString(3);
        
        // Save the table name
        tableNames.add(tableName);
      }
      
      // Close the result set
      resultSet.close();
      resultSet = null;
    }
    catch (SQLException e)
    {
      Logger.error(e);
    }
    
    return tableNames;
  }
  
  
  /**
   * Load the database driver.
   */
  private static void loadDriver()
  {
    // See if the class needs to be loaded
    if (!driverLoaded)
    {
      // Mark the driver as loaded, whether it's loaded or not
      driverLoaded = true;
      
      try
      {
        Class.forName("org.h2.Driver");
      }
      catch (ClassNotFoundException cnfe)
      {
        Logger.error(cnfe);
      }
    }
  }
  
  
  /**
   * Get a connection to the database.
   * 
   * @return a connection to the database
   */
  public static Connection getConnection()
  {
    // Load the database driver
    loadDriver();
    
    try
    {
      // Check for a null or invalid connection
      if (conn == null)
      {
        conn = DriverManager.getConnection(DB_URL, "sa", "");
      }
    }
    catch (SQLException sqle)
    {
      String errMsg = sqle.getMessage();
      if (errMsg.contains("may be already "))
      {
        // An instance of the app must be already running, and
        // has the DB locked, so just write the exception message
        Logger.error("SQLException: " + errMsg);
      }
      else
      {
        // There's another problem, so log the exception
        Logger.error(sqle);
      }
    }
    
    
    // Return the connection
    return conn;
  }
  
  
  /**
   * Close the database connection.
   */
  public static void closeConnection()
  {
    // Check if the database connection is null
    if (conn == null)
    {
      return;
    }
    
    try
    {
      // Check if it's valid
      if (conn != null)
      {
        // It's valid, so let's commit any changes
        conn.prepareStatement("checkpoint sync").execute();
        conn.commit();
      }
      
      // Close the connection
      conn.close();
      conn = null;
    }
    catch (SQLException sqle)
    {
      Logger.error(sqle);
    }
    finally
    {
      // Check if the connection is null
      if (conn != null)
      {
        // It's not null, so it hasn't been closed
        try
        {
          // Close it now
          conn.close();
        }
        catch (SQLException e)
        {
          Logger.error(e);
        }
        
        // Set the variable to null
        conn = null;
      }
    }
  }
  
  
  /**
   * Commit any database changes.
   */
  public static void commitChanges()
  {
    // Check if the database connection is null
    if (conn == null)
    {
      return;
    }
    
    try
    {
      // Check if it's valid
      if (conn != null)
      {
        // It's valid, so let's commit any changes
        conn.prepareStatement("checkpoint sync").execute();
        conn.commit();
      }
    }
    catch (SQLException sqle)
    {
      Logger.error(sqle);
    }
  }
  
  
  /**
   * Generate the script for the database and return as a string.
   * 
   * @return the script for the database as a string
   */
  public static String getScriptAsString()
  {
    getConnection();
    
    StringBuilder sb = new StringBuilder(500);
    Statement stmt = null;
    ResultSet rs = null;
    try
    {
      // Create a database statement and execute the SQL command
      stmt = conn.createStatement();
      rs = stmt.executeQuery("SCRIPT DROP");
      
      // Check for a null result set
      if (rs != null)
      {
        // Check the number of columns in the result
        ResultSetMetaData metaData = rs.getMetaData();
        if (metaData.getColumnCount() == 1)
        {
          // Iterate over the result set and save the data
          while (rs.next())
          {
            String str = rs.getString(1);
            sb.append(str).append("\n");
          }
        }
        
        rs.close();
        rs = null;
      }
      
      stmt.close();
      stmt = null;
    }
    catch (SQLException e)
    {
      Logger.error(e);
    }
    finally
    {
      // Close the ResultSet if it's not null
      try
      {
        if (rs != null)
        {
          rs.close();
        }
      }
      catch (SQLException sqle)
      {
        Logger.error(sqle);
      }
      rs = null;
      
      // Close the Statement if it's not null
      try
      {
        if (stmt != null)
        {
          stmt.close();
        }
      }
      catch (SQLException sqle)
      {
        Logger.error(sqle);
      }
      stmt = null;
    }
    
    // Return the database script
    return sb.toString();
  }
  
  
  /**
   * Delete the database files.
   */
  public static void dropDatabase()
  {
    // First close the existing database connection
    closeConnection();
    
    // Get the list of files in the database directory, with a filter
    // so we only grab the DB files
    File dir = new File("db");
    String[] files = dir.list(new FilenameFilter()
    {
      /**
       * Initialize our filter to only accept the right filenames.
       * 
       * @param dir the parent directory
       * @name the file name
       */
      public boolean accept(final File dir, final String name)
      {
        if ((name.startsWith("uddc.")) && (name.endsWith(".db")))
        {
          // This is a DB file, so return true
          return true;
        }
        
        // Not a DB file, so skip it
        return false;
      }
    });
    
    // Check if any files matched the filter
    if ((files == null) || (files.length == 0))
    {
      // Nothing to do
      return;
    }
    
    // Delete the files
    for (String name : files)
    {
      File file = new File("db", name);
      // System.out.println("Deleting " + file.getAbsolutePath());
      file.delete();
    }
  }
}
