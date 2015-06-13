package io.miti.beetle.exporters;

import io.miti.beetle.util.FakeNode;
import io.miti.beetle.util.FakeSpecParser;
import io.miti.beetle.util.Logger;
import io.miti.beetle.util.NodeInfo;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;

public class JsonDBFileWriter extends DBFileWriter
{
  private boolean isFirstRow = true;
  
  public JsonDBFileWriter(final String sFilename, final ResultSetMetaData pRSMD) {
    super(sFilename, pRSMD);
  }
  
  public JsonDBFileWriter(final String sFilename, final FakeSpecParser spec) {
    super(sFilename, spec);
  }


  @Override
  public void writeHeader() {
    sb.append("[").append(EOL);
  }


  @Override
  public void writeFooter() {
    
    // Only write this out if we've written an object
    if (!isFirstRow) {
      sb.append("  }").append(EOL);
    }
    
    sb.append("]").append(EOL);
    writeString();
  }
  
//  Sample:
//  [
//   {
//     "abc": 5,
//     "def": "hello"
//   },
//   {
//     "name": 12,
//     "omen": "sam"
//   }
// ]
  
  
  @Override
  public void writeObject(final FakeSpecParser spec) {
    
    startBlock();
    
    // Iterate over the data
    final int nodeCount = nodes.size();
    final List<FakeNode> fakes = spec.getNodes();
    for (int i = 0; i < nodeCount; ++i) {
      final NodeInfo node = nodes.get(i);
      
      // Write out the column name
      sb.append("    \"").append(node.getName()).append("\": ");
      
      // Write out the value
      final Object obj = getValueFromSpec(fakes.get(i));
      sb.append(outputValue(obj, node.getClazz()));
      
      // Add a comma if we have more data to write
      if (i < (nodeCount - 1)) {
        sb.append(",");
      }
      
      sb.append(EOL);
      writeString();
    }
    
    endBlock();
  }


  @Override
  public void writeObject(final ResultSet rs) {
    
    startBlock();
    
    // Iterate over the data
    final int nodeCount = nodes.size();
    for (int i = 0; i < nodeCount; ++i) {
      final NodeInfo node = nodes.get(i);
      
      // Write out the column name
      sb.append("    \"").append(node.getName()).append("\": ");
      
      // Write out the value
      Object obj = getValueFromRow(rs, node.getClazz(), i + 1);
      sb.append(outputValue(obj, node.getClazz()));
      
      // Add a comma if we have more data to write
      if (i < (nodeCount - 1)) {
        sb.append(",");
      }
      
      sb.append(EOL);
      writeString();
    }
    
    endBlock();
  }
  
  
  private void endBlock() {
    // We're not in the first JSON row anymore
    isFirstRow = false;
  }
  
  
  private void startBlock() {
    
    // If we started a block previously, end it now
    if (!isFirstRow) {
      sb.append("  },").append(EOL);
    }
    
    // Start a new block
    sb.append("  {").append(EOL);
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
      return "\"" + toGMTDate((java.util.Date) obj) + "\"";
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
    outStr = "\"" + outStr + "\"";
    
    return outStr;
  }
}
