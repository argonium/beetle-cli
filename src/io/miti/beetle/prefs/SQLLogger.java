/**
 * @(#)SQLLogger.java
 * 
 * Created on Jan 25, 2010
 *
 * Copyright 2010 MobilVox, Inc. All rights reserved.
 * MOBILVOX PROPRIETARY/CONFIDENTIAL.
 */

package io.miti.beetle.prefs;

import io.miti.beetle.util.Logger;

/**
 * Handle logging SQL events.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class SQLLogger
{
  /**
   * The one instance of this class.
   */
  private static SQLLogger logger = null;

  /**
   * Whether to log SQL events.
   */
  private boolean sqlDebug = false;

  /**
   * Whether to clear SQL logging on close.
   */
  private boolean sqlClearOnOpen = false;

  /**
   * SQL debug level. 1=Select, 2=Delete, 4=Update, 8=Insert.
   */
  private int sqlLevel = 0;


  /**
   * Default constructor.
   */
  private SQLLogger() {
    super();
  }


  /**
   * Return the one instance of this class.
   * 
   * @return the one instance of this class
   */
  public static SQLLogger getInstance() {
    // Check if the instance has been created yet
    if (logger == null) {
      logger = new SQLLogger();
    }

    return logger;
  }


  /**
   * Initialize the variables.
   * 
   * @param bDebug whether to log events or not
   * @param nLevel the event recording level
   * @param sqlDebugClear whether to clear events
   */
  public void initialize(final boolean bDebug, final int nLevel,
      final boolean sqlDebugClear) {
    logger.sqlDebug = bDebug;
    logger.sqlLevel = nLevel;
    logger.sqlClearOnOpen = sqlDebugClear;
  }


  /**
   * Return the SQL debug mode.
   * 
   * @return whether we're recording SQL events
   */
  public boolean getSqlDebug() {
    return sqlDebug;
  }


  /**
   * Turn logging on or off.
   * 
   * @param bLogEvents whether logging should be on or off
   */
  public void setSqlDebug(final boolean bLogEvents) {
    sqlDebug = bLogEvents;
    // TODO Code deleted to set this elsewhere
  }


  /**
   * Return the SQL log level.
   * 
   * @return the SQL log level
   */
  public int getSqlLevel() {
    return sqlLevel;
  }


  /**
   * Return whether logging is enabled and this type of event will be saved.
   * 
   * @param event the database event type
   * @return whether logging is enabled and this type of event will be saved
   */
  public boolean willRecordEvent(final DatabaseEvent event) {
    return (sqlDebug && ((sqlLevel & event.getEventBit()) != 0));
  }


  /**
   * Return whether to log the specified event type (ignoring if logging is on
   * or off).
   * 
   * @param event the database event type
   * @return whether to log the specified event type
   */
  public boolean isEventEnabled(final DatabaseEvent event) {
    return ((sqlLevel & event.getEventBit()) != 0);
  }


  /**
   * Set whether to log the specified event type.
   * 
   * @param logEvent whether to log the specified event type
   * @param event the database event type
   */
  public void enableEvent(final boolean logEvent, final DatabaseEvent event) {
    // Check whether to set or clear the bit for this event, and
    // then update the level variable
    final int v = ((logEvent) ? event.getEventBit() : ~event.getEventBit());
    sqlLevel |= v;
  }


  /**
   * Record the specified event.
   * 
   * @param table the table name
   * @param event the database event
   */
  public void recordEvent(final String table, final DatabaseEvent event) {
    // Check if the event should be recorded
    if (!willRecordEvent(event)) {
      return;
    }

    // Log the event
    Logger.info("Recorded " + event.toString() + " on table "
        + table.toUpperCase());
  }


  /**
   * Returns the sqlClearOnOpen.
   * 
   * @return the sqlClearOnOpen
   */
  public boolean isSqlClearOnOpen() {
    return sqlClearOnOpen;
  }


  /**
   * Set the sqlClearOnOpen.
   * 
   * @param pSqlClearOnOpen the sqlClearOnOpen to set
   */
  public void setSqlClearOnClose(final boolean pSqlClearOnOpen) {
    this.sqlClearOnOpen = pSqlClearOnOpen;
  }
}
