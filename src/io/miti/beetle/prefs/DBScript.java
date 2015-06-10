/**
 * @(#)DBScript.java
 * 
 * Created on May 10, 2007
 *
 * Copyright 2007 MobilVox, Inc. All rights reserved.
 * MOBILVOX PROPRIETARY/CONFIDENTIAL.
 */

package io.miti.beetle.prefs;

import io.miti.beetle.util.Logger;
import io.miti.beetle.util.Utility;

import java.io.BufferedWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Encapsulate generating a script from a database.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class DBScript
{
  /**
   * The line separator for this OS.
   */
  private String lineSep;


  /**
   * Default constructor.
   */
  private DBScript() {
    super();
    lineSep = Utility.getLineSeparator();
  }


  /**
   * Generate a DB script for the specified connection.
   * 
   * @param filename output file name
   * @param conn database connection
   */
  public static void processDatabase(final String filename,
      final Connection conn) {
    // Get the output stream
    BufferedWriter writer = getOutputStream(filename);

    DBScript script = new DBScript();
    script.process(writer, conn);

    // Close the output stream
    closeOutputStream(writer);
    writer = null;
  }


  /**
   * Create the output data for the database.
   * 
   * @param writer the output writer
   * @param conn the database connection
   */
  private void process(final BufferedWriter writer, final Connection conn) {
    // Write out the header
    writeHeader(writer);

    // Get the table names
    List<String> tables = PrefsDatabase.getTableNames();
    if ((tables == null) || (tables.size() < 1)) {
      return;
    }

    // Build the Index data map
    Map<String, Map<IndexData, List<IndexColData>>> indexData = null;
    indexData = buildIndexData(conn, tables);

    // Drop the tables
    writeTableDrops(tables, writer);

    // Create the tables
    writeTableCreates(tables, writer, conn, indexData);

    // Create the INSERT statements
    writeTableInserts(tables, writer, conn);

    // Commit the changes
    writeOut(writer, lineSep + "commit;");
    writeOut(writer, lineSep + "checkpoint sync;" + lineSep);
  }


  /**
   * Build a map of the index data for all tables.
   * 
   * @param conn the database connection
   * @param tables the list of tables in the database
   * @return the map of index data
   */
  private Map<String, Map<IndexData, List<IndexColData>>> buildIndexData(
      final Connection conn, final List<String> tables) {
    // Build the full map of table index data
    ResultSet rs = null;
    Map<String, Map<IndexData, List<IndexColData>>> map = new HashMap<String, Map<IndexData, List<IndexColData>>>(
        10);
    try {
      DatabaseMetaData meta = conn.getMetaData();
      if (meta == null) {
        return map;
      }

      // Iterate over the tables to get any indexes for each one
      for (String table : tables) {
        // Get the set of indexes for the table
        rs = meta.getIndexInfo(null, null, table, false, false);
        if (rs == null) {
          continue;
        }

        // Declare a map to hold the data for each index
        Map<IndexData, List<IndexColData>> childMap = new HashMap<IndexData, List<IndexColData>>(
            10);
        while (rs.next()) {
          boolean bNonUnique = rs.getBoolean(4);
          String sIName = rs.getString(6);
          short sType = rs.getShort(7);
          short sOrdPos = rs.getShort(8);
          String colName = rs.getString(9);
          String ascDesc = rs.getString(10);

          // Skip indexes created for primary keys and constraints
          if ((sIName == null) || (sIName.startsWith("PRIMARY_KEY_"))
              || (sIName.startsWith("CONSTRAINT_INDEX_"))) {
            continue;
          }

          // Create the data for the index column
          boolean bAsc = ((ascDesc != null) && (ascDesc.equals("A")));
          IndexColData colData = new IndexColData(sOrdPos, colName, bAsc);

          // Create the index object and append the column info object to its
          // list
          IndexData indData = new IndexData(sIName, !bNonUnique, sType);
          if (childMap.containsKey(indData)) {
            childMap.get(indData).add(colData);
          } else {
            List<IndexColData> list = new ArrayList<IndexColData>(5);
            list.add(colData);
            childMap.put(indData, list);
          }
        }

        // If the child map isn't empty, save its data in the parent map
        if (!childMap.isEmpty()) {
          // Sort the lists for each map
          for (Entry<IndexData, List<IndexColData>> set : childMap.entrySet()) {
            Collections.sort(set.getValue());
          }

          // Add the child map to the parent map
          map.put(table, childMap);
        }
      }
    } catch (SQLException sqle) {
      sqle.printStackTrace();
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }

    // Print out the map
    // printIndexData(map);

    return map;
  }


  /**
   * Log the index data, for debugging purposes.
   * 
   * @param map the map of index data
   */
  @SuppressWarnings("unused")
  private void printIndexData(
      final Map<String, Map<IndexData, List<IndexColData>>> map) {
    if (map == null) {
      Logger.debug("The index map is null");
      return;
    }

    // Iterate over the data
    for (Entry<String, Map<IndexData, List<IndexColData>>> entrySet : map
        .entrySet()) {
      String tableName = entrySet.getKey();
      Logger.debug("Table Name: " + tableName);
      Map<IndexData, List<IndexColData>> value = entrySet.getValue();
      for (Entry<IndexData, List<IndexColData>> childSet : value.entrySet()) {
        IndexData key = childSet.getKey();
        Logger.debug("  " + key.toString());
        List<IndexColData> list = childSet.getValue();
        for (IndexColData icd : list) {
          Logger.debug("    " + icd.toString());
        }
      }
    }
  }


  /**
   * Write the 'insert' statements.
   * 
   * @param tables the list of table names
   * @param writer the output writer
   * @param conn the database connection
   */
  private void writeTableInserts(final List<String> tables,
      final BufferedWriter writer, final Connection conn) {
    // Write out the section header
    writeOut(writer, lineSep + "// Insert data into the tables");

    // Iterate over the table names
    for (String table : tables) {
      try {
        // Create a result set
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM " + table);

        // Get result set meta data
        ResultSetMetaData rsmd = rs.getMetaData();
        int numColumns = rsmd.getColumnCount();

        StringBuilder sbCols = new StringBuilder(200);
        StringBuilder sbValues = new StringBuilder(400);

        // Get the column names; column indices start from 1
        for (int i = 1; i <= numColumns; i++) {
          // Save the column name
          sbCols.append(rsmd.getColumnName(i));

          // Write out the comma if we're not on the last column
          if (i != numColumns) {
            sbCols.append(", ");
          }
        }

        boolean blankLineWritten = false;

        // Iterate over all the rows
        while (rs.next()) {
          // Write a blank line before this data, if we haven't
          // done so already
          if (!blankLineWritten) {
            blankLineWritten = true;
            writeOut(writer, lineSep);
          }

          // Clear the string builder
          sbValues.setLength(0);

          // Iterate over all the columns
          for (int col = 1; col <= numColumns; ++col) {
            // Check the column type
            switch (rsmd.getColumnType(col)) {
            case java.sql.Types.INTEGER:
            case java.sql.Types.NUMERIC:
              sbValues.append(Integer.toString(rs.getInt(col)));
              break;

            case java.sql.Types.BOOLEAN:
              sbValues.append(Boolean.toString(rs.getBoolean(col)));
              break;

            case java.sql.Types.TINYINT:
              sbValues.append(Byte.toString(rs.getByte(col)));
              break;

            case java.sql.Types.BIGINT:
              sbValues.append(Long.toString(rs.getLong(col)));
              break;

            case java.sql.Types.SMALLINT:
              sbValues.append(Long.toString(rs.getShort(col)));
              break;

            case java.sql.Types.TIME:
              sbValues.append(Long.toString(rs.getTime(col).getTime()));
              break;

            case java.sql.Types.DATE:
              sbValues.append(Long.toString(rs.getDate(col).getTime()));
              break;

            case java.sql.Types.TIMESTAMP:
              sbValues.append(Long.toString(rs.getTimestamp(col).getTime()));
              break;

            case java.sql.Types.DOUBLE:
              sbValues.append(Double.toString(rs.getDouble(col)));
              break;

            case java.sql.Types.REAL:
            case java.sql.Types.FLOAT:
              sbValues.append(Float.toString(rs.getFloat(col)));
              break;

            case java.sql.Types.BLOB:
              sbValues.append(rs.getBlob(col));
              break;

            case java.sql.Types.CLOB:
              sbValues.append(quoteString(rs.getClob(col).toString()));
              break;

            case java.sql.Types.VARCHAR:
            default:
              // Need to handle quotes/control characters in the string
              sbValues.append(quoteString(rs.getString(col)));
            }

            // Add a comma if there's another column
            if (col < numColumns) {
              sbValues.append(", ");
            }
          }

          // The output string builder
          StringBuilder sb = new StringBuilder(100);

          sb.append("INSERT INTO ").append(table).append(" (");

          // Add the column names and values
          sb.append(sbCols).append(") VALUES (").append(sbValues).append(");")
              .append(lineSep);

          // Close out the row insert
          writeOut(writer, sb.toString());
        }
      } catch (SQLException e) {
        Logger.error(e);
      }
    }
  }


  /**
   * Surround the string with single quotes, and backquote any single quotes in
   * the string.
   * 
   * @param str the input string
   * @return the quoted string
   */
  private String quoteString(final String str) {
    // Check the input
    if (str == null) {
      // It's null, so just return that
      return "null";
    }

    // Check for the quote character
    if (str.indexOf('\'') < 0) {
      // Quotes not found, so just return the string surrounded
      // with quotes
      return ("'" + str + "'");
    }

    // Replace the single quotes with a backquote
    String outStr = str.replace("'", "\\'");

    // Surround the output string with quotes and return it
    return ("'" + outStr + "'");
  }


  /**
   * Write the 'create table' statements.
   * 
   * @param tables the list of table names
   * @param writer the output writer
   * @param conn the database connection
   * @param indexData the map of index data
   */
  private void writeTableCreates(final List<String> tables,
      final BufferedWriter writer, final Connection conn,
      final Map<String, Map<IndexData, List<IndexColData>>> indexData) {
    // Write out the section header
    writeOut(writer, lineSep + "// Create the tables");

    // Iterate over the table names
    for (String table : tables) {
      writeOut(writer, lineSep);

      try {
        // Get the primary key column name
        String primaryKeyColumnName = getPrimaryKeyName(conn, table);

        // Create a result set
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM " + table);

        // Get result set meta data
        ResultSetMetaData rsmd = rs.getMetaData();
        int numColumns = rsmd.getColumnCount();

        writeOut(writer, "create table " + table + lineSep + "(" + lineSep);

        // Get the column names; column indices start from 1
        for (int i = 1; i <= numColumns; i++) {
          // Get the column name
          String colName = rsmd.getColumnName(i);

          // Get the column descriptor
          String colTypeStr = getColumnType(rsmd, i);

          // The output string builder
          StringBuilder sb = new StringBuilder(100);

          // Write out the column info
          sb.append("  ").append(colName).append(" ").append(colTypeStr);

          // Check if the column is null/not-null
          boolean nullable = (rsmd.isNullable(i) == java.sql.ResultSetMetaData.columnNullable);
          if (!nullable) {
            sb.append(" not");
          }
          sb.append(" null");

          // Check if this is auto-increment
          if (rsmd.isAutoIncrement(i)) {
            sb.append(" auto_increment");
          }

          // Check if the column is a primary key
          if (colName.equals(primaryKeyColumnName)) {
            sb.append(" primary key");
          }

          // Write out the comma if we're not on the last column
          if (i != numColumns) {
            sb.append(",");
          }
          sb.append(lineSep);

          writeOut(writer, sb.toString());
        }

        // Close out the table info
        writeOut(writer, ");" + lineSep);

        // Write out any indexes for this table
        writeIndexData(table, writer, indexData);
      } catch (SQLException e) {
        Logger.error(e);
      }
    }
  }


  /**
   * Write out data on the table indexes.
   * 
   * @param table the table to inspect for indexes
   * @param writer the output writer
   * @param indexData the index data set
   */
  private void writeIndexData(final String table, final BufferedWriter writer,
      final Map<String, Map<IndexData, List<IndexColData>>> indexData) {
    // Check for no indexes for this table
    if ((indexData == null) || (!indexData.containsKey(table))) {
      return;
    }

    Map<IndexData, List<IndexColData>> map = indexData.get(table);
    if ((map == null) || (map.isEmpty())) {
      return;
    }

    writeOut(writer, lineSep);
    writeOut(writer, "// Create the indexes for " + table + lineSep);

    for (Entry<IndexData, List<IndexColData>> set : map.entrySet()) {
      IndexData index = set.getKey();
      List<IndexColData> list = set.getValue();

      StringBuilder sb = new StringBuilder(100);
      sb.append("create ");
      if (index.isUnique()) {
        sb.append("unique ");
      }
      short itype = index.getType();
      if (itype == 1) {
        sb.append("clustered ");
      } else if (itype == 2) {
        sb.append("hash ");
      }
      sb.append("index if not exists ").append(index.getName()).append(" on ")
          .append(table).append(" (");
      final int size = list.size();
      for (int i = 0; i < size; ++i) {
        if (i > 0) {
          sb.append(", ");
        }

        IndexColData icd = list.get(i);
        sb.append(icd.getName()).append(" ")
            .append(icd.isAscending() ? "asc" : "desc");
      }
      sb.append(");").append(lineSep);

      // Write this out to writer
      writeOut(writer, sb.toString());
    }
  }


  /**
   * Get the primary column key name.
   * 
   * @param conn the connection
   * @param table the table name
   * @return the name of the primary key name
   */
  private String getPrimaryKeyName(final Connection conn, final String table) {
    // This will hold the primary key column name
    String primaryKeyColumnName = "";

    // Get the key
    {
      ResultSet primaryKeysRS = null;
      try {
        primaryKeysRS = conn.getMetaData().getPrimaryKeys(null, null, table);

        if (primaryKeysRS.next()) {
          primaryKeyColumnName = primaryKeysRS.getString(4);
        }
      } catch (SQLException e) {
        Logger.error(e);
        primaryKeysRS = null;
      }
    }

    // Return the primary key column name (empty if none)
    return primaryKeyColumnName;
  }


  /**
   * Get the column type string.
   * 
   * @param rsmd the resultset metadata
   * @param colNum the column number
   * @return the column type string for the output
   */
  private String getColumnType(final ResultSetMetaData rsmd, final int colNum) {
    try {
      // Get info on the column
      final int colSize = rsmd.getColumnDisplaySize(colNum);
      final String colType = rsmd.getColumnTypeName(colNum);
      final int colTypeIndex = rsmd.getColumnType(colNum);
      final int colPrecision = rsmd.getPrecision(colNum);

      // The string builder
      StringBuilder sb = new StringBuilder(50);

      // Build the string
      sb.append(colType);

      // Determine whether to include the field size
      boolean bIncludeSize = false;
      switch (colTypeIndex) {
      case java.sql.Types.ARRAY:
      case java.sql.Types.LONGVARBINARY:
      case java.sql.Types.LONGVARCHAR:
      case java.sql.Types.VARBINARY:
      case java.sql.Types.VARCHAR:
        bIncludeSize = true;
        break;

      default:
        bIncludeSize = false;
      }

      // Determine whether to include the mantissa size
      boolean bIncludeDecimals = false;
      switch (colTypeIndex) {
      case java.sql.Types.REAL:
      case java.sql.Types.DOUBLE:
      case java.sql.Types.FLOAT:
      case java.sql.Types.DECIMAL:
      case java.sql.Types.NUMERIC:
        bIncludeDecimals = true;
        break;

      default:
        bIncludeDecimals = false;
      }

      // Check if we're including the size
      if (bIncludeSize) {
        // Check if we're including the precision
        if (bIncludeDecimals) {
          sb.append("(").append(Integer.toString(colSize)).append(", ")
              .append(Integer.toString(colPrecision)).append(")");
        } else {
          sb.append("(").append(Integer.toString(colPrecision)).append(")");
        }
      }

      // Return the string
      return sb.toString();
    } catch (SQLException sqle) {
      Logger.error(sqle);
    }

    return "";
  }


  /**
   * Write the 'drop table' statements.
   * 
   * @param tables the list of table names
   * @param writer the output writer
   */
  private void writeTableDrops(final List<String> tables,
      final BufferedWriter writer) {
    writeOut(writer, lineSep + "// Drop the tables" + lineSep);

    // Iterate over the table names
    for (String table : tables) {
      writeOut(writer, "drop table if exists " + table + ";" + lineSep);
    }
  }


  /**
   * Write out the file header.
   * 
   * @param writer the output writer
   */
  private void writeHeader(final BufferedWriter writer) {
    // First line
    writeOut(writer, "//" + lineSep);

    // Get the date
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss, MM/dd/yy");
    String date = sdf.format(new Date());

    // Write the date and the next line
    writeOut(writer, "// H2 data file: " + date + lineSep);
    writeOut(writer, "//" + lineSep);
  }


  /**
   * Write a string to the output writer.
   * 
   * @param writer the output writer
   * @param msg the string to write
   */
  private void writeOut(final BufferedWriter writer, final String msg) {
    // Check if the writer is null
    if (writer == null) {
      // It is, so send the message to the logger
      Logger.info(msg);

      // Exit this method
      return;
    }

    // Write out the message to the file
    try {
      writer.write(msg);
    } catch (java.io.IOException e) {
      Logger.error(e);
    }
  }


  /**
   * Get the output stream.
   * 
   * @param outName the name of the output file
   * @return the output stream
   */
  private static BufferedWriter getOutputStream(final String outName) {
    // Check the value
    if ((outName == null) || (outName.length() < 1)) {
      return null;
    }

    // Open the file
    BufferedWriter out = null;
    try {
      out = new BufferedWriter(new java.io.FileWriter(outName));
    } catch (java.io.IOException e) {
      out = null;
    }

    // Return writer
    return out;
  }


  /**
   * Close the output stream.
   *
   * @param out the writer to close
   */
  private static void closeOutputStream(final BufferedWriter out) {
    // Check the argument
    if (out == null) {
      return;
    }

    // Close it
    try {
      out.close();
    } catch (java.io.IOException e) {
      Logger.error(e);
    }
  }
}

/**
 * The class encapsulating a database table index.
 * 
 * @author mwallace
 * @version 1.0
 */
class IndexData
{
  /**
   * The index name.
   */
  private String name;

  /**
   * Whether this is a unique index.
   */
  private boolean uniq;

  /**
   * The index type.
   */
  private short indexType;


  /**
   * Default constructor.
   */
  public IndexData() {
    name = null;
    uniq = false;
    indexType = -1;
  }


  /**
   * Constructor taking all data.
   * 
   * @param sName index name
   * @param bUniq whether this is unique
   * @param sType the index type
   */
  public IndexData(final String sName, final boolean bUniq, final short sType) {
    name = sName;
    uniq = bUniq;
    indexType = sType;
  }


  /**
   * Return the index name.
   * 
   * @return the index name
   */
  public String getName() {
    return name;
  }


  /**
   * Return whether this is a unique index.
   * 
   * @return whether this is a unique index
   */
  public boolean isUnique() {
    return uniq;
  }


  /**
   * Return the index type.
   * 
   * @return the index type
   */
  public short getType() {
    return indexType;
  }


  /**
   * Check for equality between this and another object.
   * 
   * @param obj the object to check
   * @return whether this and the object are equal
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj == null) {
      return false;
    } else if (!(obj instanceof IndexData)) {
      return false;
    }

    IndexData id = (IndexData) obj;
    return name.equals(id.name);
  }


  /**
   * Return the hash code for this object.
   * 
   * @return the hash code
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return ((name == null) ? 0 : name.hashCode());
  }


  /**
   * Generate a string representation of this object.
   * 
   * @return a string representation of this object
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(50);
    sb.append(name).append(" (").append(uniq ? "unique" : "not unique")
        .append(", type=").append(Short.valueOf(indexType)).append(")");
    return sb.toString();
  }
}

/**
 * A class encapsulating the data for an index's column.
 * 
 * @author mwallace
 * @version 1.0
 */
class IndexColData implements Comparable<IndexColData>
{
  /**
   * The column position.
   */
  private int pos;

  /**
   * The column name.
   */
  private String name;

  /**
   * Whether the column sorting is ascending.
   */
  private boolean ascending;


  /**
   * Default constructor.
   */
  public IndexColData() {
    pos = -1;
    name = null;
    ascending = false;
  }


  /**
   * Constructor taking all of the data.
   * 
   * @param nPos column's ordinal position in the index
   * @param sName column name
   * @param bAscending whether the sorting is ascending for this column
   */
  public IndexColData(final int nPos, final String sName,
      final boolean bAscending) {
    pos = nPos;
    name = sName;
    ascending = bAscending;
  }


  /**
   * Return the column's ordinal positioning.
   * 
   * @return the column's ordinal positioning
   */
  public int getPos() {
    return pos;
  }


  /**
   * Return the column's name.
   * 
   * @return the column's name
   */
  public String getName() {
    return name;
  }


  /**
   * Return whether the column sorting is ascending.
   * 
   * @return whether the column sorting is ascending
   */
  public boolean isAscending() {
    return ascending;
  }


  /**
   * Generate a string representation of this object.
   * 
   * @return a string representation of this object
   */
  @Override
  public String toString() {
    String s = MessageFormat.format("#{0}: {1}, ascending={2}",
        Integer.valueOf(pos), name, ascending);
    return s;
  }


  /**
   * Compare this object to another.
   * 
   * @param obj the object to compare this to
   * @return how this compares to object
   */
  @Override
  public int compareTo(final IndexColData obj) {
    if (obj == null) {
      return -1;
    }

    return (pos - obj.pos);
  }
}
