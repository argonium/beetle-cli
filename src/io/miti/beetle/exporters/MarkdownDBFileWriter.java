package io.miti.beetle.exporters;

import io.miti.beetle.util.FakeNode;
import io.miti.beetle.util.FakeSpecParser;
import io.miti.beetle.util.Logger;
import io.miti.beetle.util.NodeInfo;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;

public class MarkdownDBFileWriter extends DBFileWriter
{
  public MarkdownDBFileWriter(final String sFilename, final String sData, final ResultSetMetaData pRSMD) {
    super(sFilename, sData, pRSMD);
  }
  
  public MarkdownDBFileWriter(final String sFilename, final String sData, final FakeSpecParser spec) {
    super(sFilename, sData, spec);
  }
  
  public MarkdownDBFileWriter(final String sFilename) {
    super(sFilename);
  }


  @Override
  public void writeHeader() {
    // Write the row of column names
    if (nodes != null) {
      final int colCount = nodes.size();
      
      // Write the header row
      sb.append("| ");
      sb.append(nodes.get(0).getName());
      sb.append(" |");
      for (int i = 1; i < colCount; ++i) {
        sb.append(' ').append(nodes.get(i).getName()).append(" |");
      }
      sb.append(EOL);
      
      // Write the underscores
      sb.append('|');
      for (int i = 0; i < colCount; ++i) {
        sb.append(" --- |");
      }
      sb.append(EOL);
    }
    
    writeString();
  }


  @Override
  public void writeFooter() {
    // Nothing to do for a CSV footer
  }
  
  
  @Override
  public void writeObject(final FakeSpecParser spec) {
    
    // Iterate over the data
    final int nodeCount = nodes.size();
    sb.append("|");
    final List<FakeNode> fakes = spec.getNodes();
    for (int i = 0; i < nodeCount; ++i) {
      final NodeInfo node = nodes.get(i);
      
      // Write out the value
      sb.append(" ");
      final Object obj = getValueFromSpec(fakes.get(i));
      sb.append(outputValue(obj, node.getClazz()));
      sb.append(" |");
      
      writeString();
    }
    
    sb.append(EOL);
  }


  @Override
  public void writeObject(final ResultSet rs) {
    
    // Iterate over the data
    final int nodeCount = nodes.size();
    sb.append("|");
    for (int i = 0; i < nodeCount; ++i) {
      final NodeInfo node = nodes.get(i);
      
      // Write out the value
      sb.append(" ");
      final Object obj = getValueFromRow(rs, node.getClazz(), i + 1);
      sb.append(outputValue(obj, node.getClazz()));
      sb.append(" |");
      
      writeString();
    }
    
    sb.append(EOL);
  }
  
  
  /**
   * Simple method to write out a list of strings.
   * 
   * @param items the list of values
   */
  public void writeObject(final List<String> items) {
    
    // Iterate over the data
    final int count = items.size();
    sb.append("|");
    for (int i = 0; i < count; ++i) {
      final String item = items.get(i);
      
      // Write out the value
      sb.append(" ");
      sb.append(outputValue(item, String.class));
      sb.append(" |");
      
      writeString();
    }
    
    sb.append(EOL);
  }
  
  
  public void writeString(final String str) {
    sb.append(str);
    writeString();
  }
  
  
  private String outputValue(final Object obj, final Class<?> clazz) {
    // Handle the different values
    if (obj == null) {
      return "";
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
      return toGMTDate((java.util.Date) obj);
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
  public static String quoteString(final String str)
  {
    // Check the input
    if (str == null)
    {
      // It's null, so just return that
      return "";
    }
    
    String outStr = str.replace("\"", "\\\"");
    
    if (outStr.contains("\n") || outStr.contains("|")) {
      outStr = "\"" + outStr + "\"";
    }
    
    return outStr;
  }
}
