/*
 * Java class for the USER_DB database table.
 * Generated on 01 Apr 2015 11:59:30 by DB2Java.
 */

package io.miti.beetle.model;

import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import io.miti.beetle.prefs.*;
import io.miti.beetle.cache.DBTypeCache;
import io.miti.beetle.dbutil.*;

/**
 * Java class to encapsulate the USER_DB table.
 *
 * @version 1.0
 */
public final class UserDb implements FetchDatabaseRecords, IInsertable,
    IUpdateable, IIdentifiable, Comparable<UserDb>
{
  /**
   * The table column ID.
   */
  private int id;

  /**
   * The table column DB_NAME.
   */
  private String dbName;

  /**
   * The table column URL.
   */
  private String url;

  /**
   * The table column USER_ID.
   */
  private String userId;

  /**
   * The table column USER_PW.
   */
  private String userPw;

  /**
   * The table column DB_TYPE_ID.
   */
  private int dbTypeId;


  /**
   * Default constructor.
   */
  public UserDb() {
    super();
  }


  public UserDb(final String dbName, final String url, final String userId,
      final String userPw) {
    super();
    this.dbName = dbName;
    this.url = url;
    this.userId = userId;
    this.userPw = userPw;

    // Set the dbTypeId field based on the URL
    dbTypeId = DBTypeCache.get().getMatchingDbTypeId(url);

    // TODO Encrypt the password
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
        UserDb obj = new UserDb();

        // Save the data in our object
        obj.id = rs.getInt(1);
        obj.dbName = rs.getString(2);
        obj.url = rs.getString(3);
        obj.userId = rs.getString(4);
        obj.userPw = rs.getString(5);
        obj.dbTypeId = rs.getInt(6);

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
  public static List<UserDb> getList() {
    return getList(null);
  }


  /**
   * Get all objects from the database.
   * 
   * @param whereClause the where clause for the select statement
   * @return a list of all objects in the database
   */
  public static List<UserDb> getList(final String whereClause) {
    // This will hold the list that gets returned
    List<UserDb> listData = new ArrayList<UserDb>(100);

    // Build our query
    StringBuffer buf = new StringBuffer(100);
    buf.append("SELECT ID, DB_NAME, URL, USER_ID, ");
    buf.append("USER_PW, DB_TYPE_ID");
    buf.append(" from USER_DB");

    // Check if there's a where clause to append
    if (whereClause != null) {
      buf.append(" ").append(whereClause);
    }

    // Get all of the objects from the database
    boolean bResult = PrefsDatabase.executeSelect(buf.toString(), listData,
        new UserDb());
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
    sb.append("INSERT into USER_DB (");
    sb.append("DB_NAME, URL, USER_ID, ");
    sb.append("USER_PW, DB_TYPE_ID");
    sb.append(") values (");
    sb.append("?, ?, ?, ?, ?");
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
    ps.setString(1, dbName);
    ps.setString(2, url);
    ps.setString(3, userId);
    ps.setString(4, userPw);
    ps.setInt(5, dbTypeId);
  }


  /**
   * Update a record in the database.
   */
  public void update() {
    StringBuilder sb = new StringBuilder(200);
    sb.append("UPDATE USER_DB set ");
    sb.append("DB_NAME = ?, URL = ?, ");
    sb.append("USER_ID = ?, USER_PW = ?, ");
    sb.append("DB_TYPE_ID = ? ");
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
    ps.setString(1, dbName);
    ps.setString(2, url);
    ps.setString(3, userId);
    ps.setString(4, userPw);
    ps.setInt(5, dbTypeId);
    ps.setInt(6, id);
  }


  /**
   * Delete the record in the database.
   */
  public void delete() {
    StringBuilder sb = new StringBuilder(200);
    sb.append("DELETE from USER_DB where id = ");
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
   * Get the value for dbName.
   *
   * @return the dbName
   */
  public String getDbName() {
    return dbName;
  }


  /**
   * Update the value for dbName.
   *
   * @param pDbName the new value for dbName
   */
  public void setDbName(final String pDbName) {
    dbName = pDbName;
  }


  /**
   * Get the value for url.
   *
   * @return the url
   */
  public String getUrl() {
    return url;
  }


  /**
   * Update the value for url.
   *
   * @param pUrl the new value for url
   */
  public void setUrl(final String pUrl) {
    url = pUrl;
  }


  /**
   * Get the value for userId.
   *
   * @return the userId
   */
  public String getUserId() {
    return userId;
  }


  /**
   * Update the value for userId.
   *
   * @param pUserId the new value for userId
   */
  public void setUserId(final String pUserId) {
    userId = pUserId;
  }


  /**
   * Get the value for userPw.
   *
   * @return the userPw
   */
  public String getUserPw() {
    return userPw;
  }


  /**
   * Update the value for userPw.
   *
   * @param pUserPw the new value for userPw
   */
  public void setUserPw(final String pUserPw) {
    userPw = pUserPw;
  }


  /**
   * Get the value for dbTypeId.
   *
   * @return the dbTypeId
   */
  public int getDbTypeId() {
    return dbTypeId;
  }


  /**
   * Update the value for dbTypeId.
   *
   * @param pDbTypeId the new value for dbTypeId
   */
  public void setDbTypeId(final int pDbTypeId) {
    dbTypeId = pDbTypeId;
  }


  @Override
  public int compareTo(final UserDb o) {
    return Integer.compare(id, o.id);
  }


  @Override
  public String toString() {
    return "UserDb [id=" + id + ", dbName=" + dbName + ", url=" + url
        + ", userId=" + userId + ", dbTypeId=" + dbTypeId + "]";
  }
}
