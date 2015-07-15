package io.miti.beetle.prefs;

/**
 * Interface to support updating the fields in an SQL Update statement by the
 * calling class.
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
