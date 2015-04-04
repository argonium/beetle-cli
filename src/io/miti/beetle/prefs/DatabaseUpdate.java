/*
 * @(#)DatabaseUpdate.java
 * 
 * Created on Apr 8, 2008
 *
 * Copyright 2008 MobilVox, Inc. All rights reserved.
 * MOBILVOX PROPRIETARY/CONFIDENTIAL.
 */

package io.miti.beetle.prefs;

import io.miti.beetle.util.Utility;

import java.util.StringTokenizer;

/**
 * Class to encapsulate database update objects (files).
 * 
 * @author mwallace
 * @version 1.0
 */
public final class DatabaseUpdate implements Comparable<DatabaseUpdate>
{
  /**
   * The source (starting) version number.
   */
  private int sourceVersion = 0;

  /**
   * The target (ending) version number.
   */
  private int targetVersion = 0;

  /**
   * The name of the update file.
   */
  private String filename = null;


  /**
   * Default constructor.
   */
  @SuppressWarnings("all")
  private DatabaseUpdate() {
    super();
  }


  /**
   * Constructor taking the required fields.
   * 
   * @param nSource the starting version number
   * @param nTarget the ending version number
   * @param sFilename the name of the update file
   */
  public DatabaseUpdate(final int nSource, final int nTarget,
      final String sFilename) {
    super();
    sourceVersion = nSource;
    targetVersion = nTarget;
    filename = sFilename;
  }


  /**
   * Copy constructor.
   * 
   * @param du the object to copy
   */
  public DatabaseUpdate(final DatabaseUpdate du) {
    // Verify it's not null
    if (du != null) {
      sourceVersion = du.sourceVersion;
      targetVersion = du.targetVersion;
      filename = du.filename;
    }
  }


  /**
   * Constructor using a file name.
   * 
   * @param name the name of the input file
   */
  public DatabaseUpdate(final String name) {
    // Save the filename
    filename = name;

    // Parse the file name to get the two database version numbers
    // (first is the starting version, second is the target version)
    sourceVersion = -1;
    targetVersion = -1;
    StringTokenizer st = new StringTokenizer(name, "u_.");
    if (st.hasMoreTokens()) {
      String start = st.nextToken();
      int nStart = Utility.getStringAsInteger(start, -1, -1);

      if ((nStart > 0) && (st.hasMoreTokens())) {
        String finish = st.nextToken();
        int nFinish = Utility.getStringAsInteger(finish, -1, -1);
        if (nFinish > 0) {
          sourceVersion = nStart;
          targetVersion = nFinish;
        }
      }
    }
  }


  /**
   * Return the source version.
   * 
   * @return the source version
   */
  public int getSourceVersion() {
    return sourceVersion;
  }


  /**
   * Return the target version.
   * 
   * @return the target version
   */
  public int getTargetVersion() {
    return targetVersion;
  }


  /**
   * Return the name of the file.
   * 
   * @return the filename
   */
  public String getFilename() {
    return filename;
  }


  /**
   * Return whether this object is in a valid state.
   * 
   * @return whether this object is in a valid state
   */
  public boolean isValid() {
    return ((filename != null) && (sourceVersion > 0) && (targetVersion > 0));
  }


  /**
   * Create a string representation of this object.
   * 
   * @return the string version of this
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(100);
    sb.append("Source: ").append(Integer.toString(sourceVersion))
        .append("  Target: ").append(Integer.toString(targetVersion))
        .append("  File: ").append(filename);
    return sb.toString();
  }


  /**
   * Return how this compares to another object.
   * 
   * @param obj the object to compare this to
   * @return how this compares to obj
   */
  @Override
  public int compareTo(final DatabaseUpdate obj) {
    // Check for null
    if (obj == null) {
      return 1;
    }

    // Compare the target versions
    return (targetVersion - obj.targetVersion);
  }
}
