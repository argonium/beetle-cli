/*
 * Java class for the HADOOP database table.
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
 * Java class to encapsulate the HADOOP table.
 *
 * @version 1.0
 */
public final class Hadoop
  implements FetchDatabaseRecords, IInsertable, IUpdateable, Comparable<Hadoop>
{
  /**
   * The table column ID.
   */
  private int id;
  
  /**
   * The table column LABEL.
   */
  private String label;
  
  /**
   * The table column NAME.
   */
  private String name;
  
  /**
   * The table column URL.
   */
  private String url;
  
  
  /**
   * Default constructor.
   */
  public Hadoop()
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
        Hadoop obj = new Hadoop();
        
        // Save the data in our object
        obj.id = rs.getInt(1);
        obj.label = rs.getString(2);
        obj.name = rs.getString(3);
        obj.url = rs.getString(4);
        
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
  public static List<Hadoop> getList()
  {
    return getList(null);
  }
  
  
  /**
   * Get all objects from the database.
   * 
   * @param whereClause the where clause for the select statement
   * @return a list of all objects in the database
   */
  public static List<Hadoop> getList(final String whereClause)
  {
    // This will hold the list that gets returned
    List<Hadoop> listData = new ArrayList<Hadoop>(100);
    
    // Build our query
    StringBuffer buf = new StringBuffer(100);
    buf.append("SELECT ID, LABEL, NAME, URL");
    buf.append(" from HADOOP");
    
    // Check if there's a where clause to append
    if (whereClause != null)
    {
      buf.append(" ").append(whereClause);
    }
    
    // Get all of the objects from the database
    boolean bResult = PrefsDatabase.executeSelect(buf.toString(), listData, new Hadoop());
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
    sb.append("INSERT into HADOOP (");
    sb.append("ID, LABEL, ");
    sb.append("NAME, URL");
    sb.append(") values (");
    sb.append("?, ?, ?, ?");
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
    ps.setString(2, label);
    ps.setString(3, name);
    ps.setString(4, url);
  }
  
  
  /**
   * Update a record in the database.
   */
  public void update()
  {
    StringBuilder sb = new StringBuilder(200);
    sb.append("UPDATE HADOOP set ");
    sb.append("LABEL = ?, NAME = ?, ");
    sb.append("URL = ? ");
    sb.append("where ID = ?");
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
	  ps.setString(1, label);
	  ps.setString(2, name);
	  ps.setString(3, url);
	  ps.setInt(4, id);
  }
  
  
  /**
   * Delete the record in the database.
   */
  public void delete()
  {
    StringBuilder sb = new StringBuilder(200);
    sb.append("DELETE from HADOOP where id = ");
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
   * Get the value for label.
   *
   * @return the label
   */
  public String getLabel()
  {
    return label;
  }
  
  
  /**
   * Update the value for label.
   *
   * @param pLabel the new value for label
   */
  public void setLabel(final String pLabel)
  {
    label = pLabel;
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
  
  
  /**
   * Get the value for url.
   *
   * @return the url
   */
  public String getUrl()
  {
    return url;
  }
  
  
  /**
   * Update the value for url.
   *
   * @param pUrl the new value for url
   */
  public void setUrl(final String pUrl)
  {
    url = pUrl;
  }


	@Override
	public int compareTo(final Hadoop o) {
		return Integer.compare(id, o.id);
	}


	@Override
	public String toString() {
		return "Hadoop [id=" + id + ", label=" + label + ", name=" + name
				+ ", url=" + url + "]";
	}
}
