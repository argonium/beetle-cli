/*
 * Java class for the CONFIG database table.
 * Generated on 22 Mar 2015 19:44:10 by DB2Java.
 */

package io.miti.beetle.model;

import io.miti.beetle.dbutil.FetchDatabaseRecords;
import io.miti.beetle.prefs.PrefsDatabase;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Java class to encapsulate the CONFIG table.
 *
 * @version 1.0
 */
public final class Config implements FetchDatabaseRecords
{
  /**
   * The table column DB_VERSION.
   */
  private int dbVersion;
  
  
  /**
   * Default constructor.
   */
  public Config()
  {
    super();
  }
  
  
  /**
   * Implement the FetchDatabaseRecords interface.
   * 
   * @param rs the result set to get the data from
   * @param listRecords the list of data to add to
   * @return the success of the operation
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public boolean getFields(final ResultSet rs,
                           final List listRecords)
  {
    // Default return value
    boolean bResult = false;
    
    // Set up our try/catch block
    try
    {
      // Iterate over the result set
      while (rs.next())
      {
        // Instantiate a new object
        Config obj = new Config();
        
        // Save the data in our object
        obj.dbVersion = rs.getInt(1);
        
        // Add to our list
        listRecords.add(obj);
      }
      
      // There was no error
      bResult = true;
    }
    catch (java.sql.SQLException sqle)
    {
      // Add the exception to the master list and save the
      // result as the error code
      System.err.println(sqle.getMessage());
    }
    
    // Return the result of the operation
    return bResult;
  }
  
  
  /**
   * Get all objects from the database.
   * 
   * @return a list of all objects in the database
   */
  public static List<Config> getList()
  {
    // This will hold the list that gets returned
    List<Config> listData = new ArrayList<Config>(100);
    
    // Build our query
    StringBuffer buf = new StringBuffer(100);
    buf.append("select DB_VERSION ")
       .append("from CONFIG");
    
    // Get all of the objects from the database
    boolean bResult = PrefsDatabase.executeSelect(buf.toString(), listData, new Config());
    if (!bResult)
    {
      // An error occurred
      listData.clear();
      listData = null;
    }
    
    // Return the list
    return listData;
  }
  
  
  /**
   * Get the value for dbVersion.
   *
   * @return the dbVersion
   */
  public int getDbVersion()
  {
    return dbVersion;
  }
  
  
  /**
   * Update the value for dbVersion.
   *
   * @param pDbVersion the new value for dbVersion
   */
  public void setDbVersion(final int pDbVersion)
  {
    dbVersion = pDbVersion;
  }


	@Override
	public String toString() {
		return "Config [dbVersion=" + dbVersion + "]";
	}
}
