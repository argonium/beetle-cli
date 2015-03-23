/*
 * Java class for the APP_PROPERTY database table.
 * Generated on 22 Mar 2015 19:44:10 by DB2Java.
 */

package io.miti.beetle.model;

import io.miti.beetle.dbutil.FetchDatabaseRecords;
import io.miti.beetle.prefs.PrefsDatabase;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Java class to encapsulate the APP_PROPERTY table.
 *
 * @version 1.0
 */
public final class AppProperty implements FetchDatabaseRecords
{
  /**
   * The table column KEY.
   */
  private String key;
  
  /**
   * The table column VALUE.
   */
  private String value;
  
  
  /**
   * Default constructor.
   */
  public AppProperty()
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
        AppProperty obj = new AppProperty();
        
        // Save the data in our object
        obj.key = rs.getString(1);
        obj.value = rs.getString(2);
        
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
  public static List<AppProperty> getList()
  {
    // This will hold the list that gets returned
    List<AppProperty> listData = new ArrayList<AppProperty>(100);
    
    // Build our query
    StringBuffer buf = new StringBuffer(100);
    buf.append("select KEY, VALUE ")
       .append("from APP_PROPERTY order by key");
    
    // Get all of the objects from the database
    boolean bResult = PrefsDatabase.executeSelect(buf.toString(), listData, new AppProperty());
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
   * Get the value for key.
   *
   * @return the key
   */
  public String getKey()
  {
    return key;
  }
  
  
  /**
   * Update the value for key.
   *
   * @param pKey the new value for key
   */
  public void setKey(final String pKey)
  {
    key = pKey;
  }
  
  
  /**
   * Get the value for value.
   *
   * @return the value
   */
  public String getValue()
  {
    return value;
  }
  
  
  /**
   * Update the value for value.
   *
   * @param pValue the new value for value
   */
  public void setValue(final String pValue)
  {
    value = pValue;
  }


	@Override
	public String toString() {
		return "AppProperty [key=" + key + ", value=" + value + "]";
	}
}
