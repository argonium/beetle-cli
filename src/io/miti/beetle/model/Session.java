/*
 * Java class for the SESSION database table.
 * Generated on 22 Mar 2015 19:44:10 by DB2Java.
 */

package io.miti.beetle.model;

import io.miti.beetle.dbutil.FetchDatabaseRecords;
import io.miti.beetle.prefs.PrefsDatabase;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Java class to encapsulate the SESSION table.
 *
 * @version 1.0
 */
public final class Session implements FetchDatabaseRecords
{
  /**
   * The table column ID.
   */
  private int id;
  
  /**
   * The table column SOURCE_TYPE_ID.
   */
  private int sourceTypeId;
  
  /**
   * The table column TARGET_TYPE_ID.
   */
  private int targetTypeId;
  
  /**
   * The table column SOURCE_DELIM.
   */
  private String sourceDelim;
  
  /**
   * The table column TARGET_DELIM.
   */
  private String targetDelim;
  
  
  /**
   * Default constructor.
   */
  public Session()
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
        Session obj = new Session();
        
        // Save the data in our object
        obj.id = rs.getInt(1);
        obj.sourceTypeId = rs.getInt(2);
        obj.targetTypeId = rs.getInt(3);
        obj.sourceDelim = rs.getString(4);
        obj.targetDelim = rs.getString(5);
        
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
  public static List<Session> getList()
  {
    // This will hold the list that gets returned
    List<Session> listData = new ArrayList<Session>(100);
    
    // Build our query
    StringBuffer buf = new StringBuffer(100);
    buf.append("select ID, SOURCE_TYPE_ID, TARGET_TYPE_ID, SOURCE_DELIM, TARGET_DELIM ")
       .append("from SESSION order by ID");
    
    // Get all of the objects from the database
    boolean bResult = PrefsDatabase.executeSelect(buf.toString(), listData, new Session());
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
   * Get the value for sourceTypeId.
   *
   * @return the sourceTypeId
   */
  public int getSourceTypeId()
  {
    return sourceTypeId;
  }
  
  
  /**
   * Update the value for sourceTypeId.
   *
   * @param pSourceTypeId the new value for sourceTypeId
   */
  public void setSourceTypeId(final int pSourceTypeId)
  {
    sourceTypeId = pSourceTypeId;
  }
  
  
  /**
   * Get the value for targetTypeId.
   *
   * @return the targetTypeId
   */
  public int getTargetTypeId()
  {
    return targetTypeId;
  }
  
  
  /**
   * Update the value for targetTypeId.
   *
   * @param pTargetTypeId the new value for targetTypeId
   */
  public void setTargetTypeId(final int pTargetTypeId)
  {
    targetTypeId = pTargetTypeId;
  }
  
  
  /**
   * Get the value for sourceDelim.
   *
   * @return the sourceDelim
   */
  public String getSourceDelim()
  {
    return sourceDelim;
  }
  
  
  /**
   * Update the value for sourceDelim.
   *
   * @param pSourceDelim the new value for sourceDelim
   */
  public void setSourceDelim(final String pSourceDelim)
  {
    sourceDelim = pSourceDelim;
  }
  
  
  /**
   * Get the value for targetDelim.
   *
   * @return the targetDelim
   */
  public String getTargetDelim()
  {
    return targetDelim;
  }
  
  
  /**
   * Update the value for targetDelim.
   *
   * @param pTargetDelim the new value for targetDelim
   */
  public void setTargetDelim(final String pTargetDelim)
  {
    targetDelim = pTargetDelim;
  }


	@Override
	public String toString() {
		return "Session [id=" + id + ", sourceTypeId=" + sourceTypeId
				+ ", targetTypeId=" + targetTypeId + ", sourceDelim=" + sourceDelim
				+ ", targetDelim=" + targetDelim + "]";
	}
}
