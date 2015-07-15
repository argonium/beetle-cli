package io.miti.beetle.prefs;

import io.miti.beetle.util.Content;
import io.miti.beetle.util.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class to retrieve the available database update files.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class DatabaseUpdateData
{
  /**
   * The available updates.
   */
  private static List<DatabaseUpdate> updates = null;


  /**
   * Default constructor.
   */
  @SuppressWarnings("all")
  private DatabaseUpdateData() {
    super();
  }


  /**
   * Delete the cache.
   */
  public static void clearCache() {
    if (updates != null) {
      updates.clear();
      updates = null;
    }
  }


  /**
   * Return the list of available database update files.
   */
  private static void loadUpdates() {
    // Check if the data has already been loaded
    if (updates != null) {
      return;
    }

    // Get the list of filenames
    List<String> filenames = Content.getFileList("u", ".sql");

    // Declare the local list
    updates = new ArrayList<DatabaseUpdate>(filenames.size());

    // Save the list
    for (String filename : filenames) {
      // Create an update object from the filename
      final DatabaseUpdate update = new DatabaseUpdate(filename);

      // If it's valid, save it
      if (update.isValid()) {
        updates.add(update);
      } else {
        Logger.info("Invalid DBUpdate: File=" + filename + "  DBU=" + update);
      }
    }
  }


  /**
   * Return whether any updates are available for the source version and not
   * greater than the target bound.
   * 
   * @param sourceRev the source version of the database
   * @param targetRevBound the bound on the target version
   * @return whether updates are available
   */
  public static boolean updatesAvailable(final int sourceRev,
      final int targetRevBound) {
    // Verify the data is loaded
    loadUpdates();

    // Look for a match
    boolean match = false;
    for (int i = 0; ((i < updates.size()) && (!match)); ++i) {
      DatabaseUpdate update = updates.get(i);
      match = (sourceRev == update.getSourceVersion())
          && (targetRevBound >= update.getTargetVersion());
    }

    return match;
  }


  /**
   * Return the best update for the specified source version of the database,
   * where the target is not higher than the target bound. If no such match
   * found, it returns null.
   * 
   * @param sourceRev the source version to match on
   * @param targetRevBound the upper bound on the target version
   * @return the best available update, or null
   */
  public static DatabaseUpdate getUpdate(final int sourceRev,
      final int targetRevBound) {
    // Verify the data is loaded
    loadUpdates();

    // Find the update for the specified source, where the target
    // is not higher than the specified target bound
    int currMatch = -1;
    int currTarget = -1;
    for (int i = 0; i < updates.size(); ++i) {
      // Get the current update
      DatabaseUpdate update = updates.get(i);

      // See if it fits our criteria
      if ((update.getSourceVersion() == sourceRev)
          && (update.getTargetVersion() <= targetRevBound)) {
        // Check the current state of our matches
        if (currMatch < 0) {
          // No match found yet, so save this one
          currMatch = i;
          currTarget = update.getTargetVersion();
        } else if (update.getTargetVersion() > currTarget) {
          currMatch = i;
          currTarget = update.getTargetVersion();
        }
      }
    }

    // Check for no match
    if (currMatch < 0) {
      return null;
    }

    // Return our match
    return (new DatabaseUpdate(updates.get(currMatch)));
  }


  /**
   * Return the available updates for the specified source version of the
   * database.
   * 
   * @param sourceRev the source version to match on
   * @param targetRevBound the upper bound on the target version
   * @return the available update target versions
   */
  public static List<DatabaseUpdate> getUpdates(final int sourceRev,
      final int targetRevBound) {
    // Verify the data is loaded
    loadUpdates();

    // Declare our list to return
    List<DatabaseUpdate> data = new ArrayList<DatabaseUpdate>(10);

    // Copy the updates matching our criteria
    for (DatabaseUpdate update : updates) {
      if ((update.getSourceVersion() == sourceRev)
          && (update.getTargetVersion() <= targetRevBound)) {
        data.add(new DatabaseUpdate(update));
        Logger.debug("  Update: " + update);
      }
    }

    // Sort the list
    Collections.sort(data);

    // Return the data
    return data;
  }
}
