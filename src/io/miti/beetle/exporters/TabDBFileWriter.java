package io.miti.beetle.exporters;

import io.miti.beetle.util.FakeSpecParser;

import java.sql.ResultSetMetaData;

public final class TabDBFileWriter extends DelimiterDBFileWriter
{
  public TabDBFileWriter(String sFilename, String sData, ResultSetMetaData pRSMD) {
    super(sFilename, sData, pRSMD);
  }

  public TabDBFileWriter(String sFilename, String sData, FakeSpecParser spec) {
    super(sFilename, sData, spec);
  }

  public TabDBFileWriter(String sFilename) {
    super(sFilename);
  }

  @Override
  public Character getDelimiter() {
    return '\t';
  }
}
