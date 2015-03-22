/**
 * @(#)DatabaseEvent.java
 *
 * Created on Jan 18, 2010
 *
 * Copyright 2010 MobilVox, Inc.  All rights reserved.
 * MOBILVOX PROPRIETARY/CONFIDENTIAL.
 */

package io.miti.beetle.prefs;

/**
 * An enum describing the various database events.
 * 
 * @author mwallace
 * @version 1.0
 */
public enum DatabaseEvent
{
  /** SQL Select. */
  SELECT("SELECT", 1),
  
  /** SQL Delete. */
  DELETE("DELETE", 2),
  
  /** SQL Update. */
  UPDATE("UPDATE", 4),
  
  /** SQL Insert. */
  INSERT("INSERT", 8);
  
  /** The event description. */
  private final String event;
  
  /** The bit for this event. */
  private final int eventBit;
  
  
  /**
   * Default constructor.
   */
  private DatabaseEvent()
  {
    event = null;
    eventBit = 0;
  }
  
  
  /**
   * Constructor taking the event description.
   *  
   * @param sEvent the event
   * @param nEventBit the logging bit for this event
   */
  private DatabaseEvent(final String sEvent, final int nEventBit)
  {
    event = sEvent;
    eventBit = nEventBit;
  }
  
  
  /**
   * Return the bit for this event.
   * 
   * @return the bit for this event
   */
  public int getEventBit()
  {
    return eventBit;
  }
  
  
  /**
   * Return the description of the event.
   * 
   * @return the description of the event
   */
  @Override
  public String toString()
  {
    return event;
  }
}
