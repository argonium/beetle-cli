package io.miti.beetle.exporters;

import java.sql.ResultSetMetaData;

public class CsvDBFileWriter extends DBFileWriter
{
  public CsvDBFileWriter(String sFilename, ResultSetMetaData pRSMD) {
    super(sFilename, pRSMD);
  }


  @Override
  public void writeHeader() {
    // Write the row of column names
    try {
      final int colCount = rsmd.getColumnCount();
      sb.append(rsmd.getColumnName(1));
      for (int i = 2; i <= colCount; ++i) {
        sb.append(',').append(rsmd.getColumnName(i));
      }
    } catch (Exception ex) {
      // TODO
    }
    
    writeString();
  }


  @Override
  public void writeFooter() {
    // Nothing to do for a CSV footer
  }


  @Override
  public void writeObject(final Object obj) {
    // TODO Write the object
  }
}
