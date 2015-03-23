/*
 * Java class for the COL_TYPE database table.
 * Generated on 22 Mar 2015 19:44:10 by DB2Java.
 */

package io.miti.beetle.model;

import io.miti.beetle.dbutil.FetchDatabaseRecords;
import io.miti.beetle.prefs.PrefsDatabase;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Java class to encapsulate the COL_TYPE table.
 *
 * @version 1.0
 */
public final class ColType implements FetchDatabaseRecords
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
    // This will hold the list that gets returned
    List<ColType> listData = new ArrayList<ColType>(100);
    
    // Build our query
    StringBuffer buf = new StringBuffer(100);
    buf.append("select ID, IS_STR, IS_NUM, IS_BOOL, IS_DATE, TYPE_NAME ")
       .append("from COL_TYPE order by id");
    
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
	public String toString() {
		return "ColType [id=" + id + ", isStr=" + isStr + ", isNum=" + isNum
				+ ", isBool=" + isBool + ", isDate=" + isDate + ", typeName="
				+ typeName + "]";
	}
}
