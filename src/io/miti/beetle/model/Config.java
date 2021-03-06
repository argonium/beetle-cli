/*
 * Java class for the CONFIG database table.
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
 * Java class to encapsulate the CONFIG table.
 *
 * @version 1.0
 */
public final class Config implements FetchDatabaseRecords, IInsertable,
    IUpdateable
{
  /**
   * The table column DB_VERSION.
   */
  private int dbVersion;


  /**
   * Default constructor.
   */
  public Config() {
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
        Config obj = new Config();

        // Save the data in our object
        obj.dbVersion = rs.getInt(1);

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
  public static List<Config> getList() {
    return getList(null);
  }


  /**
   * Get all objects from the database.
   * 
   * @param whereClause the where clause for the select statement
   * @return a list of all objects in the database
   */
  public static List<Config> getList(final String whereClause) {
    // This will hold the list that gets returned
    List<Config> listData = new ArrayList<Config>(100);

    // Build our query
    StringBuffer buf = new StringBuffer(100);
    buf.append("SELECT DB_VERSION");
    buf.append(" from CONFIG");

    // Check if there's a where clause to append
    if (whereClause != null) {
      buf.append(" ").append(whereClause);
    }

    // Get all of the objects from the database
    boolean bResult = PrefsDatabase.executeSelect(buf.toString(), listData,
        new Config());
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
    sb.append("INSERT into CONFIG (");
    sb.append("DB_VERSION");
    sb.append(") values (");
    sb.append("?");
    sb.append(")");

    // There should only be one row in this table, so delete before inserting
    PrefsDatabase.delete("delete from CONFIG");
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
    ps.setInt(1, dbVersion);
  }


  /**
   * Update a record in the database.
   */
  public void update() {
    StringBuilder sb = new StringBuilder(200);
    sb.append("UPDATE CONFIG set ");
    sb.append("DB_VERSION = ?");
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
    ps.setInt(1, dbVersion);
  }


  /**
   * Get the value for dbVersion.
   *
   * @return the dbVersion
   */
  public int getDbVersion() {
    return dbVersion;
  }


  /**
   * Update the value for dbVersion.
   *
   * @param pDbVersion the new value for dbVersion
   */
  public void setDbVersion(final int pDbVersion) {
    dbVersion = pDbVersion;
  }


  @Override
  public String toString() {
    return "Config [dbVersion=" + dbVersion + "]";
  }
}
