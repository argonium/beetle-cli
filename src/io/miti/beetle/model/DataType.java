/*
 * Java class for the DATA_TYPE database table.
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
 * Java class to encapsulate the DATA_TYPE table.
 *
 * @version 1.0
 */
public final class DataType
  implements FetchDatabaseRecords, IInsertable, IUpdateable
{
  /**
   * The table column ID.
   */
  private int id;
  
  /**
   * The table column NAME.
   */
  private String name;
  
  
  /**
   * Default constructor.
   */
  public DataType()
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
        DataType obj = new DataType();
        
        // Save the data in our object
        obj.id = rs.getInt(1);
        obj.name = rs.getString(2);
        
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
  public static List<DataType> getList()
  {
    return getList(null);
  }
  
  
  /**
   * Get all objects from the database.
   * 
   * @param whereClause the where clause for the select statement
   * @return a list of all objects in the database
   */
  public static List<DataType> getList(final String whereClause)
  {
    // This will hold the list that gets returned
    List<DataType> listData = new ArrayList<DataType>(100);
    
    // Build our query
    StringBuffer buf = new StringBuffer(100);
    buf.append("SELECT ID, NAME");
    buf.append(" from DATA_TYPE");
    
    // Check if there's a where clause to append
    if (whereClause != null)
    {
      buf.append(" ").append(whereClause);
    }
    
    // Get all of the objects from the database
    boolean bResult = PrefsDatabase.executeSelect(buf.toString(), listData, new DataType());
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
    sb.append("INSERT into DATA_TYPE (");
    sb.append("ID, NAME");
    sb.append(") values (");
    sb.append("?, ?");
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
    ps.setString(2, name);
  }
  
  
  /**
   * Update a record in the database.
   */
  public void update()
  {
    StringBuilder sb = new StringBuilder(200);
    sb.append("UPDATE DATA_TYPE set ");
    sb.append("NAME = ? ");
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
    sb.append("DELETE from DATA_TYPE where id = ");
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
   * Get the value for name.
   *
   * @return the name
   */
  public String getName()
  {
    return name;
  }
  
  
  /**
   * Update the value for name.
   *
   * @param pName the new value for name
   */
  public void setName(final String pName)
  {
    name = pName;
  }
}
