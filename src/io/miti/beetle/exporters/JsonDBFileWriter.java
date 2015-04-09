package io.miti.beetle.exporters;

import java.sql.ResultSetMetaData;

public class JsonDBFileWriter extends DBFileWriter
{
  public JsonDBFileWriter(String sFilename, ResultSetMetaData pRSMD) {
    super(sFilename, pRSMD);
  }


  @Override
  public void writeHeader() {
    sb.append("[\r\n  ");
  }


  @Override
  public void writeFooter() {
    sb.append("]\r\n");
    writeString();
  }


  @Override
  public void writeObject(Object obj) {
    // TODO the object fields
  }
}
