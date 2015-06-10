package io.miti.beetle.dbutil;

import io.miti.beetle.exporters.DBFileWriter;
import io.miti.beetle.prefs.IUpdateable;
import io.miti.beetle.util.Logger;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Database
{
  /**
   * Default constructor. Private.
   */
  private Database() {
    super();
  }


  /**
   * Executes a database SELECT.
   * 
   * @param sqlCmd the database ststement to execute
   * @param listData will hold the retrieved data
   * @param fetcher used to retrieve the selected database columns
   * @return the result of the operation
   */
  public static List<List<String>> executeSelect(final String sqlCmd,
      final int numColumns) {
    // Check the SQL command
    if ((sqlCmd == null) || (sqlCmd.length() < 1)) {
      return null;
    }

    Logger.info("DB Query: " + sqlCmd);

    List<List<String>> results = new ArrayList<List<String>>(20);

    // Execute the statement and get the returned ID
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      // Create the Statement object from the connection
      Connection conn = ConnManager.get().getConn();
      stmt = conn.prepareStatement(sqlCmd);
      if (null != stmt) {
        // Now execute the query and save the result set
        rs = stmt.executeQuery();

        // Check for a result
        if (rs != null) {
          while (rs.next()) {

            final List<String> line = new ArrayList<String>(numColumns);
            for (int i = 0; i < numColumns; ++i) {
              final String val = rs.getString(i + 1);
              line.add(val);
            }

            results.add(line);
          }

          // Close the result set
          rs.close();
          rs = null;
        }

        // Close the statement
        stmt.close();
        stmt = null;
      }
    } catch (SQLException sqlex) {
      Logger.error(sqlex);
    } catch (Exception ex) {
      Logger.error(ex, -1);
    } finally {
      // Close the ResultSet if it's not null
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
      } catch (SQLException sqle) {
        Logger.error(sqle);
      }

      // Close the Statement if it's not null
      try {
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
      } catch (SQLException sqle) {
        Logger.error(sqle);
      }
    }

    // Return the result of the operation
    return results;
  }


  /**
   * Executes a database SELECT.
   * 
   * @param sqlCmd the database statement to execute
   * @param writer the file to write the results to
   */
  public static void executeSelect(final String sqlCmd,
      final DBFileWriter writer) {
    // Check the SQL command
    if ((sqlCmd == null) || (sqlCmd.length() < 1)) {
      return;
    }

    Logger.info("DB Query: " + sqlCmd);

    // Execute the statement and get the returned ID
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      // Create the Statement object from the connection
      Connection conn = ConnManager.get().getConn();
      stmt = conn.prepareStatement(sqlCmd);
      if (null != stmt) {
        // Now execute the query and save the result set
        rs = stmt.executeQuery();

        // Check for a result
        if (rs != null) {
          while (rs.next()) {
            writer.writeObject(rs);
          }

          // Close the result set
          rs.close();
          rs = null;
        }

        // Close the statement
        stmt.close();
        stmt = null;
      }
    } catch (SQLException sqlex) {
      Logger.error(sqlex);
    } catch (Exception ex) {
      Logger.error(ex, -1);
    } finally {
      // Close the ResultSet if it's not null
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
      } catch (SQLException sqle) {
        Logger.error(sqle);
      }

      // Close the Statement if it's not null
      try {
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
      } catch (SQLException sqle) {
        Logger.error(sqle);
      }
    }
  }


  /**
   * Executes a database SELECT.
   * 
   * @param rs the result set of data to iterate over
   * @param writer the file to write results to
   */
  public static void executeSelect(final ResultSet rs, final DBFileWriter writer) {
    try {
      while (rs.next()) {
        writer.writeObject(rs);
      }

      // Close the result set
      rs.close();
    } catch (SQLException sqlex) {
      Logger.error(sqlex);
    } catch (Exception ex) {
      Logger.error(ex, -1);
    } finally {
      // Close the ResultSet if it's not null
      try {
        if (rs != null) {
          rs.close();
        }
      } catch (SQLException sqle) {
        Logger.error(sqle);
      }
    }
  }


  public static List<String> getTableNames(final String tablePattern) {
    // This will hold the list of table names
    List<String> tableNames = new ArrayList<String>(20);

    // The connection must already exist before this method is called!

    // Get the list of table names
    try {
      // Gets the database metadata
      Connection conn = ConnManager.get().getConn();
      DatabaseMetaData dbmd = conn.getMetaData();

      final String tPattern = (tablePattern == null) ? "%" : (tablePattern);

      // Specify the type of object; in this case we want tables
      String[] types = { "TABLE" };
      ResultSet resultSet = dbmd.getTables(conn.getCatalog(),
          tPattern, tPattern, types);

      // Get the table names
      while (resultSet.next()) {
        // Get the table name
        String tableName = resultSet.getString(3);

        // Save the table name
        tableNames.add(tableName);
      }

      // Close the result set
      resultSet.close();
      resultSet = null;
    } catch (SQLException e) {
      Logger.error(e);
    }

    return tableNames;
  }


  /**
   * Execute an Update against a database table.
   * 
   * @param sql the SQL command to run
   * @param updater the object used to set the data fields in the SQL command
   * @return the result of the operation
   */
  public static boolean update(final String sql, final IUpdateable updater) {
    // The return value
    boolean bResult = false;

    // Check the query
    if ((sql == null) || (sql.length() < 1)) {
      return bResult;
    }

    // Log this event, if debugging
    Logger.info("DB Update: " + sql);

    PreparedStatement ps = null;
    try {
      // Execute the statement
      Connection conn = ConnManager.get().getConn();
      ps = conn.prepareStatement(sql);

      // If the IUpdateable object is not null, call its method
      if (updater != null) {
        updater.setUpdateFields(ps);
      }

      // Execute the update statement
      ps.executeUpdate();

      // We reached this point, so it must have succeeded
      bResult = true;

      // Close the statement
      ps.close();
      ps = null;
    } catch (SQLException e) {
      Logger.error(e);
    } finally {
      // Close the Statement if it's not null
      try {
        if (ps != null) {
          ps.close();
          ps = null;
        }
      } catch (SQLException sqle) {
        Logger.error(sqle);
      }
    }

    return bResult;
  }


  /**
   * Returns information about the columns in the table.
   * 
   * @param table the table name
   * @param notePKs include whether the column is part of the primary key
   * @return the list of table info
   */
  public static List<List<String>> getColumns(final String table,
      final boolean notePKs) {
    // This is the object that gets returned
    List<List<String>> listColumns = new ArrayList<List<String>>(10);

    // Get the list of columns in the primary key
    Set<String> pkColumns = notePKs ? getPrimaryKeyColumns(table) : null;

    // Get the info for all columns in this table
    try {
      // Get the database metadata
      final Connection conn = ConnManager.get().getConn();
      DatabaseMetaData dbmd = conn.getMetaData();

      // Get the table info
      ResultSet rs = dbmd.getColumns(conn.getCatalog(), null,
          table.toUpperCase(), null);

      // Iterate over all column info for the table
      while (rs.next()) {
        // Save the column info
        final String colName = rs.getString("COLUMN_NAME");
        final int dataType = rs.getInt("DATA_TYPE");
        final String colType = rs.getString("TYPE_NAME");
        final int nullableCol = rs.getInt("NULLABLE");
        final int colSize = rs.getInt("COLUMN_SIZE"); // can be null?
        final int colOrder = rs.getInt("ORDINAL_POSITION");

        // Use this value for relevant data types
        final int decDigits = rs.getInt("DECIMAL_DIGITS");

        final boolean isNotNullable = (nullableCol == ResultSetMetaData.columnNoNulls);

        final int colTypeEnum = TableColumn.getJavaTypeForDBType(dataType,
            colType);
        String varType = colType;
        if (colTypeEnum != TableColumn.COL_DATE) {
          if ((colTypeEnum == TableColumn.COL_DECIMAL)
              || (colTypeEnum == TableColumn.COL_DOUBLE)
              || (colTypeEnum == TableColumn.COL_FLOAT)) {
            if (decDigits == 0) {
              varType += "(" + colSize + ")";
            } else {
              varType += "(" + colSize + ", " + decDigits + ")";
            }
          } else {
            varType += "(" + colSize + ")";
          }
        }

        // final int colType =
        // TableColumn.getJavaTypeForDBType(rs.getInt("DATA_TYPE"),
        // rs.getString("TYPE_NAME"));

        // Save the info about the column
        List<String> data = new ArrayList<String>(5);
        data.add(Integer.toString(colOrder));
        data.add(colName);
        data.add(varType);
        data.add(isNotNullable ? "NOT NULL" : "");
        if (notePKs) {
          // Include whether this column is a primary key
          data.add(pkColumns.contains(colName) ? "PK" : "-");
        }

        // Add it to our list
        listColumns.add(data);
      }

      // Close the result set
      rs.close();
      rs = null;
    } catch (SQLException e) {
      Logger.error(e);
    }

    // Return the column info
    return listColumns;
  }


  /**
   * Returns information about the columns in the table.
   * 
   * @param table the table name
   * @return the list of table info
   */
  public static Set<String> getPrimaryKeyColumns(final String table) {
    // This is the object that gets returned
    Set<String> listColumns = new HashSet<String>(5);

    // Get the info for all columns in this table
    try {
      // Get the database metadata
      Connection conn = ConnManager.get().getConn();
      DatabaseMetaData dbmd = conn.getMetaData();

      // Get the primary key column names
      ResultSet rs = dbmd.getPrimaryKeys(conn.getCatalog(), null,
          table);

      // Iterate over all column info for the table
      while (rs.next()) {
        // Save the column info
        final String colName = rs.getString("COLUMN_NAME");
        // final String pkName = rs.getString("PK_NAME");

        // Add it to our list
        listColumns.add(colName);
      }

      // Close the result set
      rs.close();
      rs = null;
    } catch (SQLException e) {
      Logger.error(e);
    }

    // Return the column info
    return listColumns;
  }


  /**
   * Executes a database SELECT that returns a single integer, such as 'select
   * count(x) from y'.
   * 
   * @param sqlCmd the database ststement to execute
   * @return the integer in the select statement
   */
  public static int executeSelectToReturnInt(final String sqlCmd) {
    // The return value
    int result = -1;

    // Check the SQL command
    if ((sqlCmd == null) || (sqlCmd.length() < 1)) {
      return result;
    }

    Logger.info("DB Query: " + sqlCmd);

    // Execute the statement and get the returned ID
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
      // Create the Statement object from the connection
      final Connection conn = ConnManager.get().getConn();
      stmt = conn.prepareStatement(sqlCmd);
      if (null != stmt) {
        // Now execute the query and save the result set
        rs = stmt.executeQuery();

        // Check for a result
        if (rs != null) {
          // Check for a result
          if (rs.next()) {
            // Save the value
            result = rs.getInt(1);
          }

          // Close the result set
          rs.close();
          rs = null;
        }

        // Close the statement
        stmt.close();
        stmt = null;
      }
    } catch (SQLException sqlex) {
      Logger.error(sqlex);
    } catch (Exception ex) {
      Logger.error(ex, -1);
    } finally {
      // Close the ResultSet if it's not null
      try {
        if (rs != null) {
          rs.close();
          rs = null;
        }
      } catch (SQLException sqle) {
        Logger.error(sqle);
      }

      // Close the Statement if it's not null
      try {
        if (stmt != null) {
          stmt.close();
          stmt = null;
        }
      } catch (SQLException sqle) {
        Logger.error(sqle);
      }
    }

    // Return the result of the operation
    return result;
  }


  public static List<String> getSchemas() {
    // This will hold the list of table names
    List<String> schemas = new ArrayList<String>(20);

    // The connection must already exist before this method is called!

    // Get the list of table names
    try {
      // Gets the database metadata
      Connection conn = ConnManager.get().getConn();
      DatabaseMetaData dbmd = conn.getMetaData();
      ResultSet resultSet = dbmd.getSchemas();

      // Get the table names
      while (resultSet.next()) {
        // Get the schema name
        String schemaName = resultSet.getString(1);

        // Save the table name
        schemas.add(schemaName);
      }

      // Close the result set
      resultSet.close();
      resultSet = null;
    } catch (SQLException e) {
      Logger.error(e);
    }

    return schemas;
  }
}
