package io.miti.beetle.exporters;

import io.miti.beetle.util.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public abstract class DBFileWriter
{
  protected StringBuilder sb = null;
  protected String filename = null;
  protected File file = null;
  
  // TODO Replace list with a list
  protected ResultSetMetaData rsmd = null;
  
  protected static final String EOL = "\r\n";


  @SuppressWarnings("unused")
  private DBFileWriter() {
    super();
  }


  /**
   * Constructor taking the output filename and result set.
   * 
   * @param sFilename the output filename
   * @param pRSMD the database result set metadata (column names, etc.)
   */
  public DBFileWriter(final String sFilename, final ResultSetMetaData pRSMD) {
    sb = new StringBuilder(100);
    file = new File(sFilename);
    filename = sFilename;
    
    // TODO Save just the info we need - column names and types - into an array
    rsmd = pRSMD;
    
    // Check the output file - if it exists as a file, empty it
    if (file.exists() && file.isFile()) {
      try {
        file.delete();
      } catch (Exception ex) {
        Logger.error("Exception deleting file: " + ex.getMessage());
      }
    }
  }


  /**
   * Abstract method for writing the file's header.
   */
  public abstract void writeHeader();


  /**
   * Abstract method for writing the file's footer.
   */
  public abstract void writeFooter();


  /**
   * Abstract method for writing a database result set row.
   * 
   * @param rsj the result set
   */
  public abstract void writeObject(final ResultSet rsj);
  
  
  /**
   * Write a string to the file.  Default to not forcing a write.
   */
  public final void writeString() {
    writeString(false);
  }
  
  
  /**
   * If the string buffer is big enough, flush it to disk.
   */
  public final void writeString(final boolean forceWrite) {
    // Check the length of the string
    if (forceWrite || sb.length() > 10000) {

      // The input buffer is long enough that we need to
      // write it to file and then clear out the buffer
      PrintWriter filePw = null;
      try {
        // Open the file for appending
        filePw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
            filename, true), StandardCharsets.UTF_8));

        filePw.print(sb.toString());

        filePw.close();
        filePw = null;
      } catch (FileNotFoundException e) {
      } finally {
        if (filePw != null) {
          // Close the writer
          filePw.close();
        }
      }

      sb.setLength(0);
    }
  }
}
