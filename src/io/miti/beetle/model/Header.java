/*
 * Java class for the HEADER database table.
 * Generated on 01 Apr 2015 11:59:30 by DB2Java.
 */

package io.miti.beetle.model;

import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import io.miti.beetle.prefs.*;
import io.miti.beetle.dbutil.*;

/**
 * Java class to encapsulate the HEADER table.
 *
 * @version 1.0
 */
public final class Header
  implements FetchDatabaseRecords, IInsertable, IUpdateable
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
  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
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
    return getList(null);
  }
  
  
  /**
   * Get all objects from the database.
   * 
   * @param whereClause the where clause for the select statement
   * @return a list of all objects in the database
   */
  public static List<Header> getList(final String whereClause)
  {
    // This will hold the list that gets returned
    List<Header> listData = new ArrayList<Header>(100);
    
    // Build our query
    StringBuffer buf = new StringBuffer(100);
    buf.append("SELECT ID, SESSION_ID, COL_NAME, COL_TYPE_ID, ");
    buf.append("DATE_FORMAT");
    buf.append(" from HEADER");
    
    // Check if there's a where clause to append
    if (whereClause != null)
    {
      buf.append(" ").append(whereClause);
    }
    
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
   * Insert a record into the database.
   */
  public void insert()
  {
    StringBuilder sb = new StringBuilder(200);
    sb.append("INSERT into HEADER (");
    sb.append("ID, SESSION_ID, ");
    sb.append("COL_NAME, COL_TYPE_ID, ");
    sb.append("DATE_FORMAT");
    sb.append(") values (");
    sb.append("?, ?, ?, ?, ?");
    sb.append(")");
  }
  
  
  /**
   * Set the parameter values in the INSERT statement.
   * 
   * @param ps the prepared statement
   * @throws java.sql.SQLException a database exception
   */
  @Override
  public void setInsertFields(final PreparedStatement ps)
    throws SQLException
  {
    ps.setInt(1, id);
    ps.setInt(2, sessionId);
    ps.setString(3, colName);
    ps.setInt(4, colTypeId);
    ps.setString(5, dateFormat);
  }
  
  
  /**
   * Update a record in the database.
   */
  public void update()
  {
    StringBuilder sb = new StringBuilder(200);
    sb.append("UPDATE HEADER set ");
    sb.append("SESSION_ID = ?, COL_NAME = ?, ");
    sb.append("COL_TYPE_ID = ?, DATE_FORMAT = ? ");
    sb.append("where null = ?");
    PrefsDatabase.update(sb.toString(), this);
  }
  
  
  /**
   * Set the parameter values in the INSERT statement.
   * 
   * @param ps the prepared statement
   * @throws java.sql.SQLException a database exception
   */
  @Override
  public void setUpdateFields(final PreparedStatement ps)
    throws SQLException
  {
	  // TODO
  }
  
  
  /**
   * Delete the record in the database.
   */
  public void delete()
  {
    StringBuilder sb = new StringBuilder(200);
    sb.append("DELETE from HEADER where id = ");
    sb.append(id);
    PrefsDatabase.delete(sb.toString());
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
}
