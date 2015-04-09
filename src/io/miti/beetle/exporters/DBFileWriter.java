package io.miti.beetle.exporters;

import java.io.File;
import java.sql.ResultSetMetaData;

public abstract class DBFileWriter
{
  protected StringBuilder sb = null;
  protected String filename = null;
  protected File file = null;
  protected ResultSetMetaData rsmd = null;
  
  @SuppressWarnings("unused")
  private DBFileWriter() {
    super();
  }
  
  public DBFileWriter(final String sFilename, final ResultSetMetaData pRSMD) {
    sb = new StringBuilder(100);
    file = new File(sFilename);
    filename = sFilename;
    rsmd = pRSMD;
  }
  
  public abstract void writeHeader();
  
  public abstract void writeFooter();
  
  public abstract void writeObject(final Object obj);
  
  public final void writeString() {
    // TODO
  }
}
