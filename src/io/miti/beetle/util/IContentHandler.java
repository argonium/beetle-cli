package io.miti.beetle.util;

/**
 * Interface for a class to handle a line read from a content file.
 * 
 * @author mwallace
 * @version 1.0
 */
public interface IContentHandler
{
  /**
   * Process a line read from a content file.
   * 
   * @param line the line that was read
   * @return whether to continue reading the file
   */
  boolean processLine(final String line);
}
