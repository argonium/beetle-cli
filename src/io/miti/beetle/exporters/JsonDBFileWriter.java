package io.miti.beetle.exporters;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class JsonDBFileWriter extends DBFileWriter
{
  public JsonDBFileWriter(String sFilename, ResultSetMetaData pRSMD) {
    super(sFilename, pRSMD);
  }


  @Override
  public void writeHeader() {
    sb.append("[").append(EOL);
  }


  @Override
  public void writeFooter() {
    sb.append("]").append(EOL);
    writeString();
  }


  @Override
  public void writeObject(final ResultSet rs) {
    // TODO Write the object fields
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
    
    String outStr = str.replace("\"", "\\\"");
    
    if (outStr.contains("\n") || outStr.contains(",")) {
      outStr = "\"" + outStr + "\"";
    }
    
    return outStr;
  }
}
