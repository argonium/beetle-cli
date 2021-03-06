package io.miti.beetle.exporters;

import io.miti.beetle.util.FakeNode;
import io.miti.beetle.util.FakeSpecParser;
import io.miti.beetle.util.Logger;
import io.miti.beetle.util.NodeInfo;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.DateFormat;
import java.util.List;

public class SQLDBFileWriter extends DBFileWriter
{
  public SQLDBFileWriter(final String sFilename, final String sData, final ResultSetMetaData pRSMD) {
    super(sFilename, sData, pRSMD);
  }
  
  public SQLDBFileWriter(final String sFilename, final String sData, final FakeSpecParser spec) {
    super(sFilename, sData, spec);
  }
  
  public SQLDBFileWriter(final String sFilename) {
    super(sFilename);
  }


  @Override
  public void writeHeader() {
    
    // Write a comment with the date/time
    sb.append("-- SQL file generated by Beetle on ")
      .append(DateFormat.getDateTimeInstance().format(new java.util.Date()))
      .append(EOL);
    
    writeString();
  }


  @Override
  public void writeFooter() {
    // Nothing to do for a CSV footer
  }
  
  
  @Override
  public void writeObject(final FakeSpecParser spec) {
    
    // Write the start of the line (table name, columns names)
    writeStartOfLine();
    
    // Iterate over the data
    final int nodeCount = nodes.size();
    final List<FakeNode> fakes = spec.getNodes();
    for (int i = 0; i < nodeCount; ++i) {
      final NodeInfo node = nodes.get(i);
      
      // Write out the value
      final Object obj = getValueFromSpec(fakes.get(i));
      sb.append(outputValue(obj, node.getClazz()));
      
      // Add a comma if we have more data to write
      if (i < (nodeCount - 1)) {
        sb.append(", ");
      }
      
      writeString();
    }
    
    sb.append(");");
    sb.append(EOL);
  }


  @Override
  public void writeObject(final ResultSet rs) {
    
    // Write the start of the line (table name, columns names)
    writeStartOfLine();
    
    // Iterate over the data
    final int nodeCount = nodes.size();
    for (int i = 0; i < nodeCount; ++i) {
      final NodeInfo node = nodes.get(i);
      
      // Write out the value
      final Object obj = getValueFromRow(rs, node.getClazz(), i + 1);
      sb.append(outputValue(obj, node.getClazz()));
      
      // Add a comma if we have more data to write
      if (i < (nodeCount - 1)) {
        sb.append(", ");
      }
      
      writeString();
    }
    
    sb.append(");");
    sb.append(EOL);
  }
  
  
  private void writeStartOfLine() {
    
    // Start the line (fileData has the table name)
    sb.append("insert into ").append(fileData).append(" (");
    
    // Write the row of column names
    try {
      final int colCount = nodes.size();
      sb.append(nodes.get(0).getName());
      for (int i = 1; i < colCount; ++i) {
        sb.append(", ").append(nodes.get(i).getName());
      }
    } catch (Exception ex) {
      System.err.println("Exception writing CSV header: " + ex.getMessage());
    }
    
    sb.append(")").append(EOL).append("  values (");
  }
  
  
  private String outputValue(final Object obj, final Class<?> clazz) {
    // Handle the different values
    if (obj == null) {
      return "null";
    }
    
    if (obj instanceof Boolean) {
      return Boolean.toString(((Boolean) obj).booleanValue());
    } else if (obj instanceof Long) {
      return Long.toString(((Long) obj).longValue());
    } else if (obj instanceof Double) {
      return Double.toString(((Double) obj).doubleValue());
    } else if (obj instanceof String) {
      return quoteString(obj.toString());
    } else if (obj instanceof java.util.Date) {
      return quoteString(toGMTDate((java.util.Date) obj));
    }
    
    // Unknown type
    Logger.error("Unknown data type: " + obj.getClass().getName());
    return "";
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
      return "null";
    }
    
    final String outStr = str.replace("'", "\\'");
    final String outStr2 = "'" + outStr + "'";
    
    return outStr2;
  }
}
