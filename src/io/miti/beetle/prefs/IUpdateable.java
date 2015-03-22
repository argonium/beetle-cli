/**
 * @(#)IUpdateable.java
 * 
 * Created on Feb 02, 2010
 *
 * Copyright 2010 MobilVox, Inc. All rights reserved.
 * MOBILVOX PROPRIETARY/CONFIDENTIAL.
 */

package io.miti.beetle.prefs;

/**
 * Interface to support updating the fields in an SQL Update statement
 * by the calling class.
 * 
 * @author mwallace
 * @version 1.0
 */
public interface IUpdateable
{
  /**
   * Set the parameter values in the UPDATE statement.
   * 
   * @param ps the prepared statement
   * @throws java.sql.SQLException a database exception
   */
  void setUpdateFields(final java.sql.PreparedStatement ps)
    throws java.sql.SQLException;
}
