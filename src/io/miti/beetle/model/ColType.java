/*
 * Java class for the COL_TYPE database table.
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
 * Java class to encapsulate the COL_TYPE table.
 *
 * @version 1.0
 */
public final class ColType
  implements FetchDatabaseRecords, IInsertable, IUpdateable, Comparable<ColType>
{
  /**
   * The table column ID.
   */
  private int id;
  
  /**
   * The table column IS_STR.
   */
  private boolean isStr;
  
  /**
   * The table column IS_NUM.
   */
  private boolean isNum;
  
  /**
   * The table column IS_BOOL.
   */
  private boolean isBool;
  
  /**
   * The table column IS_DATE.
   */
  private boolean isDate;
  
  /**
   * The table column TYPE_NAME.
   */
  private String typeName;
  
  
  /**
   * Default constructor.
   */
  public ColType()
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
        ColType obj = new ColType();
        
        // Save the data in our object
        obj.id = rs.getInt(1);
        obj.isStr = rs.getBoolean(2);
        obj.isNum = rs.getBoolean(3);
        obj.isBool = rs.getBoolean(4);
        obj.isDate = rs.getBoolean(5);
        obj.typeName = rs.getString(6);
        
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
  public static List<ColType> getList()
  {
    return getList(null);
  }
  
  
  /**
   * Get all objects from the database.
   * 
   * @param whereClause the where clause for the select statement
   * @return a list of all objects in the database
   */
  public static List<ColType> getList(final String whereClause)
  {
    // This will hold the list that gets returned
    List<ColType> listData = new ArrayList<ColType>(100);
    
    // Build our query
    StringBuffer buf = new StringBuffer(100);
    buf.append("SELECT ID, IS_STR, IS_NUM, IS_BOOL, ");
    buf.append("IS_DATE, TYPE_NAME");
    buf.append(" from COL_TYPE");
    
    // Check if there's a where clause to append
    if (whereClause != null)
    {
      buf.append(" ").append(whereClause);
    }
    
    // Get all of the objects from the database
    boolean bResult = PrefsDatabase.executeSelect(buf.toString(), listData, new ColType());
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
    sb.append("INSERT into COL_TYPE (");
    sb.append("ID, IS_STR, ");
    sb.append("IS_NUM, IS_BOOL, ");
    sb.append("IS_DATE, TYPE_NAME");
    sb.append(") values (");
    sb.append("?, ?, ?, ?, ?, ?");
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
    ps.setBoolean(2, isStr);
    ps.setBoolean(3, isNum);
    ps.setBoolean(4, isBool);
    ps.setBoolean(5, isDate);
    ps.setString(6, typeName);
  }
  
  
  /**
   * Update a record in the database.
   */
  public void update()
  {
    StringBuilder sb = new StringBuilder(200);
    sb.append("UPDATE COL_TYPE set ");
    sb.append("IS_STR = ?, IS_NUM = ?, ");
    sb.append("IS_BOOL = ?, IS_DATE = ?, ");
    sb.append("TYPE_NAME = ? ");
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
	  ps.setBoolean(1, isStr);
	  ps.setBoolean(2, isNum);
	  ps.setBoolean(3, isBool);
	  ps.setBoolean(4, isDate);
	  ps.setString(5, typeName);
	  ps.setInt(6, id);
  }
  
  
  /**
   * Delete the record in the database.
   */
  public void delete()
  {
    StringBuilder sb = new StringBuilder(200);
    sb.append("DELETE from COL_TYPE where id = ");
    sb.append(Integer.valueOf(id));
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
   * Get the value for isStr.
   *
   * @return the isStr
   */
  public boolean getIsStr()
  {
    return isStr;
  }
  
  
  /**
   * Update the value for isStr.
   *
   * @param pIsStr the new value for isStr
   */
  public void setIsStr(final boolean pIsStr)
  {
    isStr = pIsStr;
  }
  
  
  /**
   * Get the value for isNum.
   *
   * @return the isNum
   */
  public boolean getIsNum()
  {
    return isNum;
  }
  
  
  /**
   * Update the value for isNum.
   *
   * @param pIsNum the new value for isNum
   */
  public void setIsNum(final boolean pIsNum)
  {
    isNum = pIsNum;
  }
  
  
  /**
   * Get the value for isBool.
   *
   * @return the isBool
   */
  public boolean getIsBool()
  {
    return isBool;
  }
  
  
  /**
   * Update the value for isBool.
   *
   * @param pIsBool the new value for isBool
   */
  public void setIsBool(final boolean pIsBool)
  {
    isBool = pIsBool;
  }
  
  
  /**
   * Get the value for isDate.
   *
   * @return the isDate
   */
  public boolean getIsDate()
  {
    return isDate;
  }
  
  
  /**
   * Update the value for isDate.
   *
   * @param pIsDate the new value for isDate
   */
  public void setIsDate(final boolean pIsDate)
  {
    isDate = pIsDate;
  }
  
  
  /**
   * Get the value for typeName.
   *
   * @return the typeName
   */
  public String getTypeName()
  {
    return typeName;
  }
  
  
  /**
   * Update the value for typeName.
   *
   * @param pTypeName the new value for typeName
   */
  public void setTypeName(final String pTypeName)
  {
    typeName = pTypeName;
  }


	@Override
	public int compareTo(final ColType o) {
		return Integer.compare(id, o.id);
	}


	@Override
	public String toString() {
		return "ColType [id=" + id + ", isStr=" + isStr + ", isNum=" + isNum
				+ ", isBool=" + isBool + ", isDate=" + isDate + ", typeName="
				+ typeName + "]";
	}
}
