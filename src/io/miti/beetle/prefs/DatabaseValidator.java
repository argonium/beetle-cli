/**
 * @(#)DatabaseValidator.java
 * 
 * Created on Oct 30, 2007
 *
 * Copyright 2007 MobilVox, Inc. All rights reserved.
 * MOBILVOX PROPRIETARY/CONFIDENTIAL.
 */

package io.miti.beetle.prefs;

import io.miti.beetle.dbutil.Config;
import io.miti.beetle.util.Content;
import io.miti.beetle.util.Logger;
import io.miti.beetle.util.Utility;

/**
 * Verify that the database is the correct version.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class DatabaseValidator
{
  /**
   * Default constructor.
   */
  private DatabaseValidator() {
    super();
  }


  /**
   * Check the required and current database versions.
   * 
   * @return the result of the comparison
   */
  public static boolean checkDatabase() {
    // Get the two versions
    final int currDB = Config.getDBVersion();
    final int reqdDB = Utility.getStringAsInteger(
        Content.getContentAsString(Content.REQD_DB_FILE), 0, 0);

    // Compare them
    if (currDB == reqdDB) {
      // We're OK
      return true;
    } else if (currDB < reqdDB) {
      // We need to update the database
      return updateDatabase(currDB, reqdDB);
    } else {
      // The installed database version is later than the version of
      // the software. We don't degrade databases, so show a message
      // and then exit
      StringBuilder sb = new StringBuilder(100);
      sb.append("This software installation is running a later version")
          .append("\nof the database (").append(Integer.toString(currDB))
          .append(") than it requires (").append(Integer.toString(reqdDB))
          .append(").  Exiting.");
      System.out.println(sb.toString());
    }

    return false;
  }


  /**
   * Update the database to the latest available version.
   * 
   * @param startingVer the starting (current) version of the database
   * @param requiredVer the required version of the database for this software
   * @return whether to continue running the software (DB update was successful)
   */
  private static boolean updateDatabase(final int startingVer,
      final int requiredVer) {
    // This is used to show messages to the user
    StringBuilder sb = new StringBuilder(200);

    // Check if any updates are available
    boolean updatesAvailable = DatabaseUpdateData.updatesAvailable(startingVer,
        requiredVer);
    if (!updatesAvailable) {
      System.out
          .println("This installation requires a later version of the database than is available.  Exiting.");
      // // Build the error message
      // sb.setLength(0);
      // sb.append("This software installation requires a later version")
      // .append(" of the\ndatabase (").append(Integer.toString(requiredVer))
      // .append(") than is currently installed (").append(Integer.toString(startingVer))
      // .append(").  Do you\nwant to delete your current database?  The ")
      // .append("software\nwill close after it is finished.");
      //
      // // Get the user's input
      // int result = JOptionPane.showConfirmDialog(frame, sb.toString(),
      // "Error", JOptionPane.YES_NO_OPTION);
      // if (result == JOptionPane.YES_OPTION)
      // {
      // PrefsDatabase.dropDatabase();
      // }

      // We want to exit the app now, so return false
      return false;
    }

    // See if the user wants to update the database
    System.out.println("Updating the database from version " + startingVer
        + " to " + requiredVer);
    // sb.setLength(0);
    // sb.append("This software installation requires a later version")
    // .append(" of the\ndatabase (").append(Integer.toString(requiredVer))
    // .append(") than is currently installed (").append(Integer.toString(startingVer))
    // .append(").  Do you\nwant to update your database?");
    // int result = JOptionPane.showConfirmDialog(frame, sb.toString(),
    // "Update Required", JOptionPane.YES_NO_OPTION);
    // if (result == JOptionPane.NO_OPTION)
    // {
    // // The user does not want to update the database. Exit.
    // sb.setLength(0);
    // sb.append("The database update will not be performed.  Exiting.");
    // System.out.println(sb.toString());
    // return false;
    // }

    // Get the highest update for this version
    int currVer = startingVer;
    DatabaseUpdate update = DatabaseUpdateData.getUpdate(currVer, requiredVer);
    while ((updatesAvailable) && (currVer < requiredVer)) {
      // Check for a null update
      if (update == null) {
        // This should never happen since we always call updatesAvailable()
        // first; if that returns true, then getUpdate() should not return
        // null.
        sb.setLength(0);
        sb.append("The update is null for version ")
            .append(Integer.toString(currVer)).append(".  Exiting.");
        System.out.println(sb.toString());
        return false;
      }

      // Execute the update file
      Logger.debug("Updating the database from " + currVer + " to "
          + update.getTargetVersion());
      boolean rc = PrefsDatabase.executeScript(update.getFilename());
      if (!rc) {
        // An error occurred
        Logger.debug("The update failed");
        sb.setLength(0);
        sb.append("An error occurred updating the database from version ")
            .append(Integer.toString(currVer)).append(" to version ")
            .append(update.getTargetVersion()).append(".  Exiting.");
        System.out.println(sb.toString());
        return false;
      } else {
        Logger.debug("The update was successful");
      }

      // Make sure the database has the right version
      currVer = update.getTargetVersion();
      Config.update(currVer);

      // See if we still need to update the database (true when the update
      // requires multiple steps)
      if (currVer < requiredVer) {
        // We have not yet reached the required version of the database.
        // See if updates are available.
        updatesAvailable = DatabaseUpdateData.updatesAvailable(currVer,
            requiredVer);
        if (updatesAvailable) {
          // Updates are available, so get the latest one
          update = DatabaseUpdateData.getUpdate(currVer, requiredVer);
        }
      }
    }

    // Show result message if the updates did not reach the required version
    if (currVer < requiredVer) {
      sb.setLength(0);
      sb.append("The database has been updated to version ")
          .append(Integer.toString(currVer))
          .append(".  Additional updates are\nrequired to reach version ")
          .append(Integer.toString(requiredVer))
          .append(" but no more updates were found.  Exiting.");
      System.out.println(sb.toString());
      return false;
    }

    // Show a success message
    sb.setLength(0);
    sb.append("The database has been updated to version ")
        .append(Integer.toString(currVer)).append(".");
    System.out.println(sb.toString());

    // If we reached this point, the update was successful
    return true;
  }
}
