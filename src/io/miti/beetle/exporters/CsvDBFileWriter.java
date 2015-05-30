package io.miti.beetle.exporters;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class CsvDBFileWriter extends DBFileWriter
{
  public CsvDBFileWriter(String sFilename, ResultSetMetaData pRSMD) {
    super(sFilename, pRSMD);
  }


  @Override
  public void writeHeader() {
    // Write the row of column names
    try {
      final int colCount = nodes.size();
      sb.append(nodes.get(0).getName());
      for (int i = 1; i < colCount; ++i) {
        sb.append(',').append(nodes.get(i).getName());
      }
      sb.append(EOL);
    } catch (Exception ex) {
      System.err.println("Exception writing CSV header: " + ex.getMessage());
    }
    
    writeString();
  }


  @Override
  public void writeFooter() {
    // Nothing to do for a CSV footer
  }


  @Override
  public void writeObject(final ResultSet rs) {
    try {
      // TODO Use the correct class (via nodes)
      final int colCount = nodes.size();
      sb.append(quoteString(rs.getString(1)));
      for (int i = 2; i <= colCount; ++i) {
        sb.append(',').append(quoteString(rs.getString(i)));
      }
      sb.append(EOL);
    } catch (SQLException se) {
      System.err.println("SQLException in CsvDBFileWriter.writeObject: " + se.getMessage());
    }
  }
  
  
  /**
   * Surround the string with single quotes, and backquote any
   * single quotes in the string.
   * 
   * @param str the input string
   * @return the quoted string
   */
  private static String quoteString(final String str)
  {
    // Check the input
    if (str == null)
    {
      // It's null, so just return that
      return "";
    }
    
    String outStr = str.replace("\"", "\\\"");
    
    if (outStr.contains("\n") || outStr.contains(",")) {
      outStr = "\"" + outStr + "\"";
    }
    
    return outStr;
  }
}
