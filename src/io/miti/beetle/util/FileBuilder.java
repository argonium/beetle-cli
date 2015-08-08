package io.miti.beetle.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public final class FileBuilder implements Closeable
{
  /** The internal buffer. */
  private StringBuilder sb = null;
  
  /** The output filename. */
  private String filename = null;
  
  /** The end-of-line string. */
  private static String EOL = "\r\n";
  
  /** The max size of the buffer before we write it to file. */
  private static int MAX_BUFFER_SIZE = 10000;
  
  
  /**
   * Private default constructor.
   */
  @SuppressWarnings("unused")
  private FileBuilder() {
    super();
  }
  
  
  /**
   * Constructor taking the output filename.  The file will be overwritten.
   * 
   * @param sFilename the output filename
   */
  public FileBuilder(final String sFilename) {
    
    // Initialize this class
    init(sFilename, false);
  }
  
  
  /**
   * Constructor taking the output filename.
   * 
   * @param sFilename the output filename
   * @param appendToFile whether to append to the output file
   */
  public FileBuilder(final String sFilename, final boolean appendToFile) {
    
    // Initialize this class
    init(sFilename, appendToFile);
  }
  
  
  /**
   * Initialize with the filename.
   * 
   * @param sFilename the output filename
   * @param appendToFile whether to append to the output file
   */
  private void init(final String sFilename, final boolean appendToFile) {
    
    // Initialize the member variables
    sb = new StringBuilder(100);
    filename = sFilename;
    
    // If we're not appending to the file, delete if it already exists
    if (!appendToFile) {
      // Check the output file - if it exists as a file, delete it
      final File file = new File(sFilename);
      if (file.exists() && file.isFile()) {
        try {
          file.delete();
        } catch (Exception ex) {
          System.err.println("Exception deleting file: " + ex.getMessage());
        }
      }
    }
  }
  
  
  /**
   * Set the maximum size of the buffer.  Must be at least zero.
   * 
   * @param maxSize the new maximum size of the buffer
   * @return this
   */
  public FileBuilder setMaxBufferSize(final int maxSize) {
    if (maxSize >= 0) {
      MAX_BUFFER_SIZE = maxSize;
    }
    
    return this;
  }
  
  
  /**
   * Set the EOL string.
   * 
   * @param eol the EOL string
   * @return this
   */
  public FileBuilder setEOL(final String eol) {
    EOL = eol;
    return this;
  }
  
  
  /**
   * Use the native EOL string for this platform.
   * 
   * @return this
   */
  public FileBuilder useNativeEOL() {
    EOL = System.lineSeparator();
    return this;
  }
  
  
  /**
   * Append a string to the buffer.
   * 
   * @param str the value to append
   * @return this
   */
  public FileBuilder append(final String str) {
    sb.append(str);
    writeToFile(false);
    return this;
  }
  
  
  /**
   * Append a character to the buffer.
   * 
   * @param ch the value to append
   * @return this
   */
  public FileBuilder append(final char ch) {
    sb.append(ch);
    writeToFile(false);
    return this;
  }
  
  
  /**
   * Append a character array to the buffer.
   * 
   * @param val the value to append
   * @return this
   */
  public FileBuilder append(final char[] val) {
    sb.append(val);
    writeToFile(false);
    return this;
  }
  
  
  /**
   * Append a character array to the buffer.
   * 
   * @param val the value to append
   * @return this
   */
  public FileBuilder append(final char[] val, final int offset, final int len) {
    sb.append(val, offset, len);
    writeToFile(false);
    return this;
  }
  
  
  /**
   * Append a character sequence to the buffer.
   * 
   * @param val the value to append
   * @return this
   */
  public FileBuilder append(final CharSequence val) {
    sb.append(val);
    writeToFile(false);
    return this;
  }
  
  
  /**
   * Append a character sequence to the buffer.
   * 
   * @param val the value to append
   * @return this
   */
  public FileBuilder append(final CharSequence val, final int start, final int end) {
    sb.append(val, start, end);
    writeToFile(false);
    return this;
  }
  
  
  /**
   * Append a string buffer to the buffer.
   * 
   * @param val the value to append
   * @return this
   */
  public FileBuilder append(final StringBuffer val) {
    sb.append(val);
    writeToFile(false);
    return this;
  }
  
  
  /**
   * Append a boolean to the buffer.
   * 
   * @param val the value to append
   * @return this
   */
  public FileBuilder append(final boolean val) {
    sb.append(val);
    writeToFile(false);
    return this;
  }
  
  
  /**
   * Append an object to the buffer.
   * 
   * @param val the value to append
   * @return this
   */
  public FileBuilder append(final Object val) {
    sb.append(val);
    writeToFile(false);
    return this;
  }
  
  
  /**
   * Append an int to the buffer.
   * 
   * @param val the value to append
   * @return this
   */
  public FileBuilder append(final int val) {
    sb.append(val);
    writeToFile(false);
    return this;
  }
  
  
  /**
   * Append a double to the buffer.
   * 
   * @param val the value to append
   * @return this
   */
  public FileBuilder append(final double val) {
    sb.append(val);
    writeToFile(false);
    return this;
  }
  
  
  /**
   * Append a float to the buffer.
   * 
   * @param val the value to append
   * @return this
   */
  public FileBuilder append(final float val) {
    sb.append(val);
    writeToFile(false);
    return this;
  }
  
  
  /**
   * Append a long to the buffer.
   * 
   * @param val the long to append
   * @return this
   */
  public FileBuilder append(final long val) {
    sb.append(val);
    writeToFile(false);
    return this;
  }
  
  
  /**
   * Append the EOL string to the buffer.
   * 
   * @return this
   */
  public FileBuilder appendEOL() {
    return append(EOL);
  }
  
  
  /**
   * Write out the remainder of the buffer to the output file.
   */
  @Override
  public void close() {
    
    // Verify the string builder has not been closed already
    if (sb == null) {
      return;
    }
    
    // Force the rest of the string builder to the output file
    writeToFile(true);
    
    // Null out the member variables
    sb.setLength(0);
    sb = null;
    filename = null;
  }
  
  
  /**
   * For any string in the buffer to get written to the file.
   * This method should not normally be called.
   */
  public void forceWriteToDisk() {
    writeToFile(true);
  }
  
  
  /**
   * If the string buffer is big enough, flush it to disk.
   */
  private void writeToFile(final boolean forceWrite) {
    
    // If nothing to write, nothing to do
    if (sb.length() == 0) {
      return;
    }
    
    // Check the length of the string
    if (forceWrite || sb.length() > MAX_BUFFER_SIZE) {

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
