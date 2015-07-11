package io.miti.beetle.processor;

import io.miti.beetle.exporters.CsvDBFileWriter;
import io.miti.beetle.util.CSVParser;
import io.miti.beetle.util.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CSVJoiner
{
  /**
   * Default constructor.
   */
  public CSVJoiner() {
    super();
  }
  
  
  /**
   * Join the data in the input file and write to the output file.
   * 
   * @param inputFile the input file
   * @param outputFile the output file
   * @param keys the array of key field indexes
   */
  public void join(final File inputFile, final File outputFile, final int keys[]) {
    
    // Get the indexes of the key field as a set
    final Set<Integer> keyIds = getKeysAsSet(keys);
    
    // Set up the output file
    final CsvDBFileWriter writer = new CsvDBFileWriter(outputFile.getAbsolutePath());
    
    // Write the header
    writer.writeHeader();
    
    // Iterate over the inputFile and save the map of keys to file offsets
    final CSVParser parser = new CSVParser(",");
    BufferedReader br = null;
    try {
      br  = new BufferedReader(new InputStreamReader(
              new FileInputStream(inputFile), StandardCharsets.UTF_8));
      
      String line = br.readLine();
      String prevKey = null;
      int rowNum = 1;
      while (line != null) {
        // Parse the line
        Iterator<String> iter = parser.parse(line);
        
        // Get the key and value strings
        StringBuilder sbKey = new StringBuilder(50);
        StringBuilder sbVal = new StringBuilder(50);
        final boolean result = getKeyAndValues(iter, sbKey, sbVal, keyIds, rowNum);
        if (!result) {
          break;
        }
        
        final String key = sbKey.toString();
        final String val = sbVal.toString();
        
        // Add the data to the output buffer
        final StringBuilder sb = new StringBuilder(100);
        
        // We already have output, and we found a different key
        if ((prevKey != null) && !key.equals(prevKey)) {
          // Start a new line
          sb.append("\r\n");
        }
        
        // If the key is different, write it out
        if ((prevKey == null) || !key.equals(prevKey)) {
          sb.append(key);
          prevKey = key;
        }
        
        // Add the values
        sb.append(",").append(val);
        writer.writeString(sb.toString());
        
        // Read the next line and increment the row counter
        line = br.readLine();
        ++rowNum;
      }
    } catch (IOException e) {
      Logger.error(e);
    } finally {
      try {
        br.close();
      } catch (IOException e) {
        Logger.error(e);
      }
    }
    
    writer.addEOL();
    
    // Write the footer
    writer.writeFooter();
    
    // Force out any pending data
    writer.writeString(true);
  }
  
  
  /**
   * Get the key string and value string from the current row.
   * 
   * @param iter the iterator of strings in the input file
   * @param key the key (output)
   * @param val the value (output)
   * @param keyIds the set of key field indexes
   * @param rowNum the current line number from the input file
   * @return success flag
   */
  private boolean getKeyAndValues(final Iterator<String> iter, final StringBuilder key,
      final StringBuilder val, final Set<Integer> keyIds, final int rowNum) {
    
    // Iterate over the fields and store in a list
    final List<String> list = new ArrayList<String>(10);
    while (iter.hasNext()) {
      list.add(iter.next());
    }
    
    // Get the key fields
    boolean result = true;
    final Iterator<Integer> keyIter = keyIds.iterator();
    while (keyIter.hasNext()) {
      // Get the current index of the key field
      final int index = keyIter.next().intValue();
      if ((index < 0) || (index >= list.size())) {
        Logger.error("Key index of " + index + " is invalid for row " + rowNum);
        result = false;
        break;
      }
      
      // Add the key field
      final String keyField = list.get(index);
      if (key.length() > 0) {
        key.append(",");
      }
      
      // Add the quoted string
      key.append(CsvDBFileWriter.quoteString(keyField, ','));
    }
    
    // Check the result
    if (!result) {
      return result;
    }
    
    // Get the rest of the line and store as the value
    final int numFields = list.size();
    for (int i = 0; i < numFields; ++i) {
      // Get the current value if it's not part of the key
      if (!keyIds.contains(Integer.valueOf(i))) {
        if (val.length() > 0) {
          val.append(",");
        }
        
        // Add the quoted string
        val.append(CsvDBFileWriter.quoteString(list.get(i), ','));
      }
    }
    
    // Verify some data was found
    if (val.length() == 0) {
      Logger.error("No non-key data found for row " + rowNum);
      result = false;
    }
    
    return result;
  }
  
  
  /**
   * Get the array of keys as a linked set.
   * 
   * @param keys the list of key field indexes
   * @return the keys as a set
   */
  private Set<Integer> getKeysAsSet(final int[] keys) {
    final int num = keys.length;
    Set<Integer> set = new LinkedHashSet<Integer>(num);
    for (int i = 0; i < num; ++i) {
      set.add(Integer.valueOf(keys[i]));
    }
    
    return set;
  }
}
