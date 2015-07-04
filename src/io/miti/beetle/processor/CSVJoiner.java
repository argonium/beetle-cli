package io.miti.beetle.processor;

import io.miti.beetle.exporters.CsvDBFileWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVJoiner
{
  public CSVJoiner() {
    super();
  }
  
  
  public void join(final File inputFile, final File outputFile, final int keys[]) {
    
    // TODO Iterate over the inputFile and save the map of keys to file offsets
    Map<String, List<Long>> map = new HashMap<String, List<Long>>(10);
    
    // TODO Group the data
    int keyCount = 0;
    
    // TODO Write out the output file
    final CsvDBFileWriter writer = new CsvDBFileWriter(inputFile.getAbsolutePath());
    
    // Write the header
    writer.writeHeader();
    
    // Iterate over the data for exporting
    for (int i = 0; i < keyCount; ++i) {
      // Write out the data
      List<String> row = new ArrayList<String>(5); // TODO
      writer.writeObject(row);
    }
    
    // Write the footer
    writer.writeFooter();
    
    // Force out any pending data
    writer.writeString(true);
  }
}
