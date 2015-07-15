package io.miti.beetle.dbutil;

import io.miti.beetle.prefs.PrefsDatabase;
import io.miti.beetle.util.Logger;

import java.sql.ResultSet;
import java.util.List;

/**
 * Encapsulate the configuration information, such as the database version.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class Config implements FetchDatabaseRecords
{
  /**
   * The version number of this database.
   */
  private static int dbVersion = 0;

  /**
   * Whether the data has been loaded.
   */
  private static boolean dataLoaded = false;


  /**
   * Default constructor.
   */
  private Config() {
    super();
  }


  /**
   * Return the current DB version number.
   * 
   * @return the current DB version number
   */
  public static int getDBVersion() {
    // By default, don't force reloading the version number from the database
    return getDBVersion(false);
  }


  /**
   * Return the current DB version number, with an option for forcing the
   * database to retrieve it even if already cached.
   * 
   * @param forceRetrieval overwrite the cached version number
   * @return the current DB version number
   */
  public static int getDBVersion(final boolean forceRetrieval) {
    // See if we should mark the cache as dirty
    if (forceRetrieval) {
      dataLoaded = false;
    }

    // Ensure the data is loaded
    loadData();

    // Return the value
    return dbVersion;
  }


  /**
   * Load the configuration information from the database.
   */
  private static void loadData() {
    // See if the data has already been loaded
    if (dataLoaded) {
      return;
    }

    // Mark the data as loaded
    dataLoaded = true;

    // Build our query
    StringBuilder buf = new StringBuilder(100);
    buf.append("select DB_VERSION from config");

    // Get the data from the database
    PrefsDatabase.executeSelect(buf.toString(), null, new Config());
  }


  /**
   * Implement the FetchDatabaseRecords interface.
   * 
   * @param rs the result set to get the data from
   * @param listRecords the list of data to add to
   * @return the success of the operation
   */
  @SuppressWarnings("rawtypes")
  public boolean getFields(final ResultSet rs, final List listRecords) {
    // Default return value
    boolean bResult = false;

    // Set up our try/catch block
    try {
      // Just get the first row
      if (rs.next()) {
        dbVersion = rs.getInt(1);
      }

      // There was no error
      bResult = true;
    } catch (java.sql.SQLException sqle) {
      // Add the exception to the master list and save the
      // result as the error code
      Logger.error(sqle.getMessage());
    }

    // Return the result of the operation
    return bResult;
  }


  /**
   * Update the CONFIG table with the new information.
   * 
   * @param nDBVersion the new DB version
   */
  public static void update(final int nDBVersion) {
    // Save the new DB version
    dbVersion = nDBVersion;

    // Build the UPDATE string
    StringBuilder sb = new StringBuilder(100);
    sb.append("update config set DB_VERSION = ").append(
        Integer.toString(dbVersion));

    // Update the table
    PrefsDatabase.update(sb.toString(), null);
  }
}
