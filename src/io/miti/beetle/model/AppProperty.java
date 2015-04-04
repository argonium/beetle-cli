/*
 * Java class for the APP_PROPERTY database table.
 * Generated on 01 Apr 2015 11:59:30 by DB2Java.
 */

package io.miti.beetle.model;

import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import io.miti.beetle.prefs.*;
import io.miti.beetle.util.Utility;
import io.miti.beetle.dbutil.*;

/**
 * Java class to encapsulate the APP_PROPERTY table.
 *
 * @version 1.0
 */
public final class AppProperty implements FetchDatabaseRecords, IInsertable,
    IUpdateable, Comparable<AppProperty>
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
  public AppProperty() {
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
  public boolean getFields(final ResultSet rs, final List listRecords) {
    // Default return value
    boolean bResult = false;

    // Set up our try/catch block
    try {
      // Iterate over the result set
      while (rs.next()) {
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
    } catch (java.sql.SQLException sqle) {
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
  public static List<AppProperty> getList() {
    return getList(null);
  }


  /**
   * Get all objects from the database.
   * 
   * @param whereClause the where clause for the select statement
   * @return a list of all objects in the database
   */
  public static List<AppProperty> getList(final String whereClause) {
    // This will hold the list that gets returned
    List<AppProperty> listData = new ArrayList<AppProperty>(100);

    // Build our query
    StringBuffer buf = new StringBuffer(100);
    buf.append("SELECT KEY, VALUE");
    buf.append(" from APP_PROPERTY");

    // Check if there's a where clause to append
    if (whereClause != null) {
      buf.append(" ").append(whereClause);
    }

    // Get all of the objects from the database
    boolean bResult = PrefsDatabase.executeSelect(buf.toString(), listData,
        new AppProperty());
    if (!bResult) {
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
  public void insert() {
    StringBuilder sb = new StringBuilder(200);
    sb.append("INSERT into APP_PROPERTY (");
    sb.append("KEY, VALUE");
    sb.append(") values (");
    sb.append("?, ?");
    sb.append(")");

    PrefsDatabase.insert(sb.toString(), this);
  }


  /**
   * Set the parameter values in the INSERT statement.
   * 
   * @param ps the prepared statement
   * @throws java.sql.SQLException a database exception
   */
  @Override
  public void setInsertFields(final PreparedStatement ps) throws SQLException {
    ps.setString(1, key);
    ps.setString(2, value);
  }


  /**
   * Update a record in the database.
   */
  public void update() {
    StringBuilder sb = new StringBuilder(200);
    sb.append("UPDATE APP_PROPERTY set ");
    sb.append("VALUE = ? ");
    sb.append("where key = ?");
    PrefsDatabase.update(sb.toString(), this);
  }


  /**
   * Set the parameter values in the INSERT statement.
   * 
   * @param ps the prepared statement
   * @throws java.sql.SQLException a database exception
   */
  @Override
  public void setUpdateFields(final PreparedStatement ps) throws SQLException {
    ps.setString(1, value);
    ps.setString(2, key);
  }


  /**
   * Delete the record in the database.
   */
  public void delete() {
    StringBuilder sb = new StringBuilder(200);
    sb.append("DELETE from APP_PROPERTY where key = '");
    sb.append(key).append("'");
    PrefsDatabase.delete(sb.toString());
  }


  /**
   * Get the value for key.
   *
   * @return the key
   */
  public String getKey() {
    return key;
  }


  /**
   * Update the value for key.
   *
   * @param pKey the new value for key
   */
  public void setKey(final String pKey) {
    key = pKey;
  }


  /**
   * Get the value for value.
   *
   * @return the value
   */
  public String getValue() {
    return value;
  }


  /**
   * Update the value for value.
   *
   * @param pValue the new value for value
   */
  public void setValue(final String pValue) {
    value = pValue;
  }


  @Override
  public int compareTo(final AppProperty o) {
    return Utility.compareTwoStrings(key, o.key);
  }


  @Override
  public String toString() {
    return "AppProperty [key=" + key + ", value=" + value + "]";
  }
}
