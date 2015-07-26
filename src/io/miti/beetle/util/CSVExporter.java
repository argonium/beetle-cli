package io.miti.beetle.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

public final class CSVExporter {

  private char delim = ',';
  private String dir = ".";
  private String filename = null;
  private StringBuilder sb = null;
  private String EOL = "\r\n";

  public CSVExporter() {
  }

  public CSVExporter setDelimiter(final char delim) {
    this.delim = delim;
    return this;
  }

  public CSVExporter setDirectory(final String dir) {
    this.dir = dir;
    return this;
  }
  
  public CSVExporter setEOL(final String eol) {
    EOL = eol;
    return this;
  }
  
  public CSVExporter useNativeEOL() {
    EOL = System.lineSeparator();
    return this;
  }

  private void init(final String sFilename) {
    sb = new StringBuilder(100);
    filename = sFilename;
    
    // Check the output file - if it exists as a file, empty it
    final File file = new File(dir, filename);
    if (file.exists() && file.isFile()) {
      try {
        file.delete();
      } catch (Exception ex) {
        System.err.println("Exception deleting file: " + ex.getMessage());
      }
    }
  }

  public boolean export(final String fname, final List<?> data,
      final String[] titles, final String[] fields) {

    // Initialize
    init(fname);

    // The result of the file write
    boolean rc = true;

    // Write the header
    addLine(titles);

    // Iterate over data, and write the fields
    for (Object row : data) {
      addRow(row, fields);
    }

    // Flush the file
    writeString(true);

    // Return the result
    return rc;
  }

  private void addRow(final Object object, final String[] fields) {
    final int size = fields.length;
    String[] row = new String[size];
    for (int i = 0; i < size; ++i) {
      try {
        Field f = object.getClass().getDeclaredField(fields[i]);
        final String val = getValueFromField(f, object);
        row[i] = quoteString(val);
      } catch (SecurityException e) {
        e.printStackTrace();
      } catch (NoSuchFieldException e) {
        e.printStackTrace();
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }

    // Add the row to the output list
    addLine(row);
  }

  private String getValueFromField(final Field field, final Object object)
      throws IllegalArgumentException, IllegalAccessException {
    field.setAccessible(true);
    String value = null;
    Class<?> type = field.getType();
    String typeName = type.getName();
    if (typeName.equals("int")) {
      value = Integer.toString(field.getInt(object));
    } else if (typeName.equals("float")) {
      value = Float.toString(field.getFloat(object));
    } else if (typeName.equals("double")) {
      value = Double.toString(field.getDouble(object));
    } else if (typeName.equals("long")) {
      value = Long.toString(field.getLong(object));
    } else if (typeName.equals("boolean")) {
      value = Boolean.toString(field.getBoolean(object));
    } else if (typeName.equals("byte")) {
      value = Byte.toString(field.getByte(object));
    } else if (typeName.equals("char")) {
      value = Character.toString(field.getChar(object));
    } else if (typeName.equals("short")) {
      value = Short.toString(field.getShort(object));
    } else if (typeName.equals("java.lang.String")) {
      value = (String) field.get(object);
    } else if (typeName.equals("java.math.BigDecimal")) {
      BigDecimal bd = (BigDecimal) field.get(object);
      value = bd.toPlainString();
    } else if (typeName.equals("java.util.Date")) {
      Date date = (Date) field.get(object);
      value = Utility.getDateTimeString(date.getTime());
    } else {
      System.err.println("Unhandled type of " + typeName);
    }
    
    if (value == null) {
      return "";
    }

    return value;
  }

  private void addLine(final String[] fields) {
    if ((fields == null) || (fields.length == 0)) {
      return;
    }

    sb.append(quoteString(fields[0]));
    final int size = fields.length;
    for (int i = 1; i < size; ++i) {
      sb.append(delim).append(quoteString(fields[i]));
    }
    sb.append(EOL);

    writeString();
  }

  private String quoteString(final String str) {
    // Check the input
    if (str == null) {
      // It's null, so just return that
      return "";
    }

    String outStr = str.replace("\"", "\\\"");

    if (outStr.contains("\n")
        || outStr.contains(Character.valueOf(delim).toString())) {
      outStr = "\"" + outStr + "\"";
    }

    return outStr;
  }

  private final void writeString() {
    writeString(false);
  }

  /**
   * If the string buffer is big enough, flush it to disk.
   */
  private final void writeString(final boolean forceWrite) {

    // If nothing to write, nothing to do
    if (sb.length() == 0) {
      return;
    }

    // Check the length of the string
    if (forceWrite || sb.length() > 10000) {

      // The input buffer is long enough that we need to
      // write it to file and then clear out the buffer
      PrintWriter filePw = null;
      try {
        // Open the file for appending
        File file = new File(dir, filename);
        filePw = new PrintWriter(new OutputStreamWriter(
            new FileOutputStream(file, true), StandardCharsets.UTF_8));

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

//  public static void main(String[] args) {
//    List<java.awt.Point> list = new java.util.ArrayList<java.awt.Point>(100);
//    for (int i = 0; i < 20; ++i) {
//      list.add(new java.awt.Point(i + 10, i * 2));
//    }
//    
//    CSVExporter ce = new CSVExporter();
//    ce.export("data.csv", list, new String[]{"x pt", "y pt"}, new String[]{"x", "y"});
//  }
}
