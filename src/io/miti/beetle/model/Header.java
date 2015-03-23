/*
 * Java class for the HEADER database table.
 * Generated on 22 Mar 2015 19:44:10 by DB2Java.
 */

package io.miti.beetle.model;

import io.miti.beetle.dbutil.FetchDatabaseRecords;
import io.miti.beetle.prefs.PrefsDatabase;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Java class to encapsulate the HEADER table.
 *
 * @version 1.0
 */
public final class Header implements FetchDatabaseRecords
{
  /**
   * The table column ID.
   */
  private int id;
  
  /**
   * The table column SESSION_ID.
   */
  private int sessionId;
  
  /**
   * The table column COL_NAME.
   */
  private String colName;
  
  /**
   * The table column COL_TYPE_ID.
   */
  private int colTypeId;
  
  /**
   * The table column DATE_FORMAT.
   */
  private String dateFormat;
  
  
  /**
   * Default constructor.
   */
  public Header()
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
        Header obj = new Header();
        
        // Save the data in our object
        obj.id = rs.getInt(1);
        obj.sessionId = rs.getInt(2);
        obj.colName = rs.getString(3);
        obj.colTypeId = rs.getInt(4);
        obj.dateFormat = rs.getString(5);
        
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
  public static List<Header> getList()
  {
    // This will hold the list that gets returned
    List<Header> listData = new ArrayList<Header>(100);
    
    // Build our query
    StringBuffer buf = new StringBuffer(100);
    buf.append("select ID, SESSION_ID, COL_NAME, COL_TYPE_ID, DATE_FORMAT ")
       .append("from HEADER order by ID");
    
    // Get all of the objects from the database
    boolean bResult = PrefsDatabase.executeSelect(buf.toString(), listData, new Header());
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
   * Get the value for id.
   *
   * @return the id
   */
  public int getId()
  {
    return id;
  }
  
  
  /**
   * Update the value for id.
   *
   * @param pId the new value for id
   */
  public void setId(final int pId)
  {
    id = pId;
  }
  
  
  /**
   * Get the value for sessionId.
   *
   * @return the sessionId
   */
  public int getSessionId()
  {
    return sessionId;
  }
  
  
  /**
   * Update the value for sessionId.
   *
   * @param pSessionId the new value for sessionId
   */
  public void setSessionId(final int pSessionId)
  {
    sessionId = pSessionId;
  }
  
  
  /**
   * Get the value for colName.
   *
   * @return the colName
   */
  public String getColName()
  {
    return colName;
  }
  
  
  /**
   * Update the value for colName.
   *
   * @param pColName the new value for colName
   */
  public void setColName(final String pColName)
  {
    colName = pColName;
  }
  
  
  /**
   * Get the value for colTypeId.
   *
   * @return the colTypeId
   */
  public int getColTypeId()
  {
    return colTypeId;
  }
  
  
  /**
   * Update the value for colTypeId.
   *
   * @param pColTypeId the new value for colTypeId
   */
  public void setColTypeId(final int pColTypeId)
  {
    colTypeId = pColTypeId;
  }
  
  
  /**
   * Get the value for dateFormat.
   *
   * @return the dateFormat
   */
  public String getDateFormat()
  {
    return dateFormat;
  }
  
  
  /**
   * Update the value for dateFormat.
   *
   * @param pDateFormat the new value for dateFormat
   */
  public void setDateFormat(final String pDateFormat)
  {
    dateFormat = pDateFormat;
  }


	@Override
	public String toString() {
		return "Header [id=" + id + ", sessionId=" + sessionId + ", colName="
				+ colName + ", colTypeId=" + colTypeId + ", dateFormat="
				+ dateFormat + "]";
	}
}
