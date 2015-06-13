package io.miti.beetle.exporters;

import io.miti.beetle.util.FakeNode;
import io.miti.beetle.util.FakeSpecParser;
import io.miti.beetle.util.Logger;
import io.miti.beetle.util.NodeInfo;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;

public class XmlDBFileWriter extends DBFileWriter
{
  public XmlDBFileWriter(final String sFilename, final ResultSetMetaData pRSMD) {
    super(sFilename, pRSMD);
  }
  
  public XmlDBFileWriter(final String sFilename, final FakeSpecParser spec) {
    super(sFilename, spec);
  }


  @Override
  public void writeHeader() {
    sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(EOL);
    sb.append("<Results>").append(EOL);
  }


  @Override
  public void writeFooter() {
    sb.append("</Results>").append(EOL);
    writeString();
  }
  
  
  @Override
  public void writeObject(final FakeSpecParser spec) {
    
    // Start a new block
    sb.append("  <Row>").append(EOL);
    
    // Iterate over the data
    final int nodeCount = nodes.size();
    final List<FakeNode> fakes = spec.getNodes();
    for (int i = 0; i < nodeCount; ++i) {
      final NodeInfo node = nodes.get(i);
      
      // Write out the column name
      sb.append("    <").append(node.getName()).append(">");
      
      // Write out the value
      final Object obj = getValueFromSpec(fakes.get(i));
      sb.append(outputValue(obj, node.getClazz()));
      
      // Close the line
      sb.append("</").append(node.getName()).append(">");
      sb.append(EOL);
      writeString();
    }
    
    // End the block
    sb.append("  </Row>").append(EOL);
  }


  @Override
  public void writeObject(final ResultSet rs) {
    
    // Start a new block
    sb.append("  <Row>").append(EOL);
    
    // Iterate over the data
    final int nodeCount = nodes.size();
    for (int i = 0; i < nodeCount; ++i) {
      final NodeInfo node = nodes.get(i);
      
      // Write out the column name
      sb.append("    <").append(node.getName()).append(">");
      
      // Write out the value
      final Object obj = getValueFromRow(rs, node.getClazz(), i + 1);
      sb.append(outputValue(obj, node.getClazz()));
      
      // Close the line
      sb.append("</").append(node.getName()).append(">");
      sb.append(EOL);
      writeString();
    }
    
    // End the block
    sb.append("  </Row>").append(EOL);
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
      return toGMTDate((java.util.Date) obj);
    }
    
    // Unknown type
    Logger.error("Unknown data type: " + obj.getClass().getName());
    return "";
  }
  
  
  /**
   * Surround the string with quotes, and backquote any
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
    
    String outStr = str.replace("\"", "\\\"");
    
    return outStr;
  }
}
