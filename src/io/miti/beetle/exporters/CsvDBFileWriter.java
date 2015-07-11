package io.miti.beetle.exporters;

import io.miti.beetle.util.FakeSpecParser;

import java.sql.ResultSetMetaData;

public final class CsvDBFileWriter extends DelimiterDBFileWriter
{
  public CsvDBFileWriter(final String sFilename, final String sData, final ResultSetMetaData pRSMD) {
    super(sFilename, sData, pRSMD);
  }
  
  public CsvDBFileWriter(final String sFilename, final String sData, final FakeSpecParser spec) {
    super(sFilename, sData, spec);
  }
  
  public CsvDBFileWriter(final String sFilename) {
    super(sFilename);
  }

  @Override
  public Character getDelimiter() {
    return ',';
  }
}
