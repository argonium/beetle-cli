package io.miti.beetle.prefs;

/**
 * Interface to support updating the fields in an SQL Insert statement by the
 * calling class.
 * 
 * @author mwallace
 * @version 1.0
 */
public interface IInsertable
{
  /**
   * Set the parameter values in the INSERT statement.
   * 
   * @param ps the prepared statement
   * @throws java.sql.SQLException a database exception
   */
  void setInsertFields(final java.sql.PreparedStatement ps)
      throws java.sql.SQLException;
}
