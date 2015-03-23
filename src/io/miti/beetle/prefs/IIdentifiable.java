/**
 * @(#)IIdentifiable.java
 * 
 * Created on Feb 04, 2010
 *
 * Copyright 2010 MobilVox, Inc. All rights reserved.
 * MOBILVOX PROPRIETARY/CONFIDENTIAL.
 */

package io.miti.beetle.prefs;

/**
 * Interface to support setting the ID field. Used to save the key
 * generated by a database INSERT statement.
 * 
 * @author mwallace
 * @version 1.0
 */
public interface IIdentifiable
{
  /**
   * Set the ID.
   * 
   * @param nID the generated ID value
   */
  void setId(final int nID);
}