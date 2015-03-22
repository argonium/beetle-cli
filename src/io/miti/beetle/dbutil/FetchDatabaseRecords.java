/**
 * @(#)FetchDatabaseRecords.java
 * 
 * Created on May 3, 2007
 *
 * Copyright 2007 MobilVox, Inc. All rights reserved.
 * MOBILVOX PROPRIETARY/CONFIDENTIAL.
 */

package io.miti.beetle.dbutil;

/**
 * This interface is used to retrieve data from a database table.
 * 
 * @author mwallace
 * @version 1.0
 */
public interface FetchDatabaseRecords
{
  /**
   * Defines how to retrieve data from a SELECT statement issued
   * against a table.
   * 
   * @param rs the ResultSet to read data from
   * @param listRecords the list to store data in
   * @return the result of the operation
   */
  boolean getFields(final java.sql.ResultSet rs,
                    final java.util.List<?> listRecords);
}
