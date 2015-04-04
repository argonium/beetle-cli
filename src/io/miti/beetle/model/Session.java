/*
 * Java class for the SESSION database table.
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
 * Java class to encapsulate the SESSION table.
 *
 * @version 1.0
 */
public final class Session implements FetchDatabaseRecords, IInsertable,
    IUpdateable, IIdentifiable, Comparable<Session>
{
  /**
   * The table column ID.
   */
  private int id;

  /**
   * The table column SOURCE_TYPE_ID.
   */
  private int sourceTypeId = -1;

  private int sourceId = -1;

  /**
   * The table column TARGET_TYPE_ID.
   */
  private int targetTypeId = -1;

  private int targetId = -1;

  /**
   * The table column SOURCE_DELIM.
   */
  private String sourceDelim = null;

  /**
   * The table column TARGET_DELIM.
   */
  private String targetDelim = null;


  /**
   * Default constructor.
   */
  public Session() {
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
  public static List<Session> getList() {
    return getList(null);
  }


  /**
   * Get all objects from the database.
   * 
   * @param whereClause the where clause for the select statement
   * @return a list of all objects in the database
   */
  public static List<Session> getList(final String whereClause) {
    // This will hold the list that gets returned
    List<Session> listData = new ArrayList<Session>(100);

    // Build our query
    StringBuffer buf = new StringBuffer(100);
    buf.append("SELECT ID, SOURCE_TYPE_ID, TARGET_TYPE_ID, SOURCE_DELIM, ");
    buf.append("TARGET_DELIM");
    buf.append(" from SESSION");

    // Check if there's a where clause to append
    if (whereClause != null) {
      buf.append(" ").append(whereClause);
    }

    // Get all of the objects from the database
    boolean bResult = PrefsDatabase.executeSelect(buf.toString(), listData,
        new Session());
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
    sb.append("INSERT into SESSION (");
    sb.append("SOURCE_TYPE_ID, TARGET_TYPE_ID, SOURCE_DELIM, ");
    sb.append("TARGET_DELIM");
    sb.append(") values (");
    sb.append("?, ?, ?, ?");
    sb.append(")");

    PrefsDatabase.insert(sb.toString(), this, this);
  }


  /**
   * Set the parameter values in the INSERT statement.
   * 
   * @param ps the prepared statement
   * @throws java.sql.SQLException a database exception
   */
  @Override
  public void setInsertFields(final PreparedStatement ps) throws SQLException {
    ps.setInt(1, sourceTypeId);
    ps.setInt(2, targetTypeId);
    ps.setString(3, sourceDelim);
    ps.setString(4, targetDelim);
  }


  /**
   * Update a record in the database.
   */
  public void update() {
    StringBuilder sb = new StringBuilder(200);
    sb.append("UPDATE SESSION set ");
    sb.append("SOURCE_TYPE_ID = ?, TARGET_TYPE_ID = ?, ");
    sb.append("SOURCE_DELIM = ?, TARGET_DELIM = ? ");
    sb.append("where id = ?");
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
    ps.setInt(1, sourceTypeId);
    ps.setInt(2, targetTypeId);
    ps.setString(3, sourceDelim);
    ps.setString(4, targetDelim);
    ps.setInt(5, id);
  }


  /**
   * Delete the record in the database.
   */
  public void delete() {
    StringBuilder sb = new StringBuilder(200);
    sb.append("DELETE from SESSION where id = ");
    sb.append(id);
    PrefsDatabase.delete(sb.toString());
  }


  /**
   * Get the value for id.
   *
   * @return the id
   */
  public int getId() {
    return id;
  }


  /**
   * Update the value for id.
   *
   * @param pId the new value for id
   */
  @Override
  public void setId(final int pId) {
    id = pId;
  }


  /**
   * Get the value for sourceTypeId.
   *
   * @return the sourceTypeId
   */
  public int getSourceTypeId() {
    return sourceTypeId;
  }


  /**
   * Update the value for sourceTypeId.
   *
   * @param pSourceTypeId the new value for sourceTypeId
   */
  public void setSourceTypeId(final int pSourceTypeId) {
    sourceTypeId = pSourceTypeId;
  }


  /**
   * Get the value for sourceId.
   *
   * @return the sourceId
   */
  public int getSourceId() {
    return sourceId;
  }


  /**
   * Update the value for sourceId.
   *
   * @param pSourceId the new value for sourceId
   */
  public void setSourceId(final int pSourceId) {
    sourceId = pSourceId;
  }


  /**
   * Get the value for targetId.
   *
   * @return the targetId
   */
  public int getTargetId() {
    return targetId;
  }


  /**
   * Update the value for targetId.
   *
   * @param pTargetId the new value for targetId
   */
  public void setTargetId(final int pTargetId) {
    targetId = pTargetId;
  }


  /**
   * Get the value for targetTypeId.
   *
   * @return the targetTypeId
   */
  public int getTargetTypeId() {
    return targetTypeId;
  }


  /**
   * Update the value for targetTypeId.
   *
   * @param pTargetTypeId the new value for targetTypeId
   */
  public void setTargetTypeId(final int pTargetTypeId) {
    targetTypeId = pTargetTypeId;
  }


  /**
   * Get the value for sourceDelim.
   *
   * @return the sourceDelim
   */
  public String getSourceDelim() {
    return sourceDelim;
  }


  /**
   * Update the value for sourceDelim.
   *
   * @param pSourceDelim the new value for sourceDelim
   */
  public void setSourceDelim(final String pSourceDelim) {
    sourceDelim = pSourceDelim;
  }


  /**
   * Get the value for targetDelim.
   *
   * @return the targetDelim
   */
  public String getTargetDelim() {
    return targetDelim;
  }


  /**
   * Update the value for targetDelim.
   *
   * @param pTargetDelim the new value for targetDelim
   */
  public void setTargetDelim(final String pTargetDelim) {
    targetDelim = pTargetDelim;
  }


  @Override
  public int compareTo(final Session o) {
    return Integer.compare(id, o.id);
  }


  @Override
  public String toString() {
    return "Session [id=" + id + ", sourceTypeId=" + sourceTypeId
        + ", sourceId=" + sourceId + ", targetTypeId=" + targetTypeId
        + ", targetId=" + targetId + ", sourceDelim=" + sourceDelim
        + ", targetDelim=" + targetDelim + "]";
  }
}
