package io.miti.beetle.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class CSVExporter {
  
  /** The field delimiter character. */
  private char delim = ',';
  
  /** The output directory name. */
  private String dir = ".";
  
  /** The output filename. */
  private String filename = null;
  
  /** The internal buffer. */
  private StringBuilder sb = null;
  
  /** The EOL string. */
  private String EOL = "\r\n";
  
  /** Formatter for doubles and floats. */
  private static final DecimalFormat df;
  
  /** The list of fields to ignore. */
  private Set<String> fieldsToIgnore = null;
  
  /** Whether to write null objects (in the list) to the output. */
  private boolean writeNullObjects = false;
  
  static {
    df = new DecimalFormat("#.################");
  }

  /**
   * Public default constructor.
   */
  public CSVExporter() {
  }

  /**
   * Set the field delimiter character
   * @param delim
   * @return
   */
  public CSVExporter setDelimiter(final char delim) {
    this.delim = delim;
    return this;
  }

  /**
   * Set the output directory.
   * 
   * @param dir the output directory
   * @return this
   */
  public CSVExporter setDirectory(final String dir) {
    this.dir = dir;
    return this;
  }
  
  
  /**
   * Set the EOL string.
   * 
   * @param eol the EOL string
   * @return this
   */
  public CSVExporter setEOL(final String eol) {
    EOL = eol;
    return this;
  }
  
  
  /**
   * Use the native EOL String for this platform.
   * 
   * @return this
   */
  public CSVExporter useNativeEOL() {
    EOL = System.lineSeparator();
    return this;
  }
  
  /**
   * When iterating over the list of objects passed to export(),
   * null objects are included in the ouput if this is set to true.
   * By default, this property is false.
   * 
   * @param bWrite whether to include null objects in the output
   * @return this
   */
  public CSVExporter writeNullObjects(final boolean bWrite) {
    writeNullObjects = bWrite;
    return this;
  }
  
  /**
   * If using reflection to access fields, we can add fields to ignore
   * via this method.
   * 
   * @param vals a field name to ignore
   * @return this
   */
  public CSVExporter addToIgnoredFields(final String... vals) {
    
    // Verify the array has some items
    if ((vals != null) && (vals.length > 0)) {
      // Iterate over the list of field names to ignore
      for (String val : vals) {
        // If we haven't initialized the set yet, do it now
        if (fieldsToIgnore == null) {
          fieldsToIgnore = new HashSet<String>(5);
        }
        
        // Add the current field to the set
        fieldsToIgnore.add(val);
      }
    }
    
    return this;
  }
  
  
  /**
   * If using reflection to access fields, we can add fields to ignore
   * via this method.
   * 
   * @param vals the list of field names
   * @return this
   */
  public CSVExporter addToIgnoredFields(final Set<String> vals) {
    
    if ((vals != null) && !vals.isEmpty()) {
      for (String val : vals) {
        addToIgnoredFields(val);
      }
    }
    
    return this;
  }

  /**
   * Initialize by saving the output filename, and delete any
   * existing file in the directory with the same name.
   * 
   * @param sFilename the output filename
   */
  private void init(final String sFilename) {
    sb = new StringBuilder(100);
    filename = sFilename;
    
    // Check the output file - if it exists as a file, empty it
    if (filename != null) {
      final File file = new File(dir, filename);
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
   * Export a list of data to CSV, using reflection for the field names.
   * 
   * @param fname the output filename; if null, output goes to stdout
   * @param data the list of objects
   * @param titles the Strings to write in the first line; if null, ignored
   * @param getTitlesFromData whether to get the titles via reflection
   * @return success or failure
   */
  public boolean export(final String fname, final List<?> data,
      final String[] titles, final boolean getTitlesFromData) {
    
    // Initialize
    init(fname);

    // The result of the file write
    boolean rc = true;

    // Write the header
    if (getTitlesFromData) {
      addLine(getTitlesFromData(data));
    } else {
      addLine(titles);
    }

    // Iterate over data, and write the fields
    for (Object row : data) {
      addRow(row);
    }

    // Flush the file
    writeString(true);

    // Return the result
    return rc;
  }

  /**
   * Get the names of the fields from the data.
   * 
   * @param data the list of objects
   * @return the field names as a CSV string
   */
  private String[] getTitlesFromData(List<?> data) {
    // Check that we have data
    if (data == null) {
      return null;
    }
    
    // Find the first non-null object, and get the title names
    final int size = data.size();
    boolean validRowFound = false;
    List<String> titles = null;
    for (int i = 0; (i < size) && !validRowFound; ++i) {
      // Check if the object is null
      final Object obj = data.get(i);
      if (obj != null) {
        // The row is OK, so get the titles via reflection
        validRowFound = true;
        titles = new ArrayList<String>(10);
        
        // Iterate over the fields and save their names
        for (Field field : obj.getClass().getDeclaredFields()) {
          field.setAccessible(true);
          
          // See if the field should be ignored
          if (ignoreField(field.getName())) {
            continue;
          }
          
          titles.add(field.getName());
        }
      }
    }
    
    // Convert the array to a list
    String[] array = null;
    if (titles != null) {
      array = new String[titles.size()];
      array = titles.toArray(array);
    }
    
    return array;
  }

  /**
   * Export a list of data to a CSV file.
   * 
   * @param fname the output filename; if null, output goes to sdout
   * @param data the list of objects
   * @param titles the Strings to write in the first line; if null, ignored
   * @param fields the names of the object's fields to write
   * @return success or failure
   */
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
  
  /**
   * Add the object to the output.
   * 
   * @param obj the object
   */
  private void addRow(final Object obj) {
    
    String val = null;
    if (obj != null) {
      val = getValueFromObject(obj);
      if (val == null) {
        val = getFieldsFromObject(obj);
      } else {
        val = quoteString(val);
      }
    } else {
      if (writeNullObjects) {
        val = "";
      } else {
        return;
      }
    }
    
    // The fields in the line need to be quoted before this call
    addLine(val);
  }
  
  /**
   * Get the value from an object.
   * 
   * @param obj the object
   * @return the value as a string
   */
  private String getValueFromObject(final Object obj) {
    String val = null;
    Class<?> clazz = obj.getClass();
    if (clazz.equals(Integer.class)) {
      val = Integer.toString(((Integer) obj).intValue());
    } else if (clazz.equals(Short.class)) {
      val = Short.toString(((Short) obj).shortValue());
    } else if (clazz.equals(Double.class)) {
      val = getDoubleAsString((Double) obj);
    } else if (clazz.equals(Long.class)) {
      val = Long.toString(((Long) obj).longValue());
    } else if (clazz.equals(Float.class)) {
      val = getFloatAsString((Float) obj);
    } else if (clazz.equals(Character.class)) {
      val = Character.toString(((Character) obj).charValue());
    } else if (clazz.equals(Byte.class)) {
      val = Byte.toString(((Byte) obj).byteValue());
    } else if (clazz.equals(Boolean.class)) {
      val = Boolean.toString(((Boolean) obj).booleanValue());
    } else if (clazz.equals(String.class)) {
      val = (String) obj;
    }
    
    return val;
  }

  /**
   * Get a floating point number as a formatted string.
   * 
   * @param val the value
   * @return the value as a string
   */
  private String getFloatAsString(final Float val) {
    return getDoubleAsString(val.doubleValue());
  }
  
  /**
   * Get a floating point number as a formatted string.
   * 
   * @param val the value
   * @return the value as a string
   */
  private String getDoubleAsString(final Double val) {
    return df.format(val.doubleValue());
  }

  /**
   * Get the fields from an object.
   * 
   * @param obj the object
   * @return the fields as a CSV-formatted string.
   */
  private String getFieldsFromObject(final Object obj) {
    StringBuilder line = new StringBuilder(50);
    for (Field field : obj.getClass().getDeclaredFields()) {
      field.setAccessible(true);
      
      // See if the field should be ignored
      if (ignoreField(field.getName())) {
        continue;
      }
      
      Object value = null;
      try {
        value = field.get(obj);
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
      
      if (value != null) {
        if (line.length() > 0) {
          line.append(delim);
        }
        
        line.append(quoteString(getValueFromObject(value)));
      }
    }
    
    return line.toString();
  }
  
  
  /**
   * Return whether the field should be ignored.
   * 
   * @param fieldName the name of the field
   * @return if the field should be ignored
   */
  private boolean ignoreField(final String fieldName) {
    return ((fieldsToIgnore != null) && fieldsToIgnore.contains(fieldName));
  }

  /**
   * Add the fields in the object to the output.
   * 
   * @param object the parent object
   * @param fields the fields in the object
   */
  private void addRow(final Object object, final String[] fields) {
    final int size = fields.length;
    List<String> line = new ArrayList<String>(5);
    for (int i = 0; i < size; ++i) {
      try {
        final Field f = object.getClass().getDeclaredField(fields[i]);
        
        // See if the field should be ignored
        if (ignoreField(f.getName())) {
          continue;
        }
        
        final String val = getValueFromField(f, object);
        line.add(quoteString(val));
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
    
    // See if the line array is empty
    if (line.isEmpty()) {
      return;
    }

    // Convert line to an array
    String[] row = new String[line.size()];
    row = line.toArray(row);
    
    // Add the row to the output list
    addLine(row);
  }

  
  /**
   * Get the value for the field in the object.
   * 
   * @param field the field in the object
   * @param object the parent object
   * @return the field's value as a string
   * @throws IllegalArgumentException reflection exception
   * @throws IllegalAccessException reflection exception
   */
  private String getValueFromField(final Field field, final Object object)
      throws IllegalArgumentException, IllegalAccessException {
    
    if (object == null) {
      return "";
    }
    
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
  
  
  /**
   * Add the line to the output.
   * 
   * @param line the line to write out
   */
  private void addLine(final String line) {
    if (line == null) {
      return;
    }
    
    sb.append(line).append(EOL);
    writeString();
  }

  
  /**
   * Add the array of values to the output.
   * 
   * @param fields the list of values
   */
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

  
  /**
   * Put the string in quotes, if necessary.
   * 
   * @param str the input string
   * @return the input string in quotes, if needed
   */
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
  
  
  /**
   * Write the current string builder, if needed.
   */
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

      if (filename == null) {
        System.out.print(sb.toString());
      } else {
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
      }

      sb.setLength(0);
    }
  }

  public static void main(String[] args) {
    List<java.awt.Point> plist = new ArrayList<java.awt.Point>(100);
    for (int i = 0; i < 20; ++i) {
      plist.add(new java.awt.Point(i + 10, i * 2));
    }
    
//    List<String> slist = new java.util.ArrayList<String>(5);
//    slist.add("Hello world");
//    slist.add("Sam I am");
//    slist.add("Hello, there");
//    
//    List<Double> dlist = new java.util.ArrayList<Double>(5);
//    dlist.add(Double.valueOf(3.5));
//    dlist.add(Double.valueOf(12351.1623));
//    dlist.add(Double.valueOf(-13));
//    dlist.add(null);
//    dlist.add(Double.valueOf(130958209835.1235));
    
    // TODO More testing
    CSVExporter ce = new CSVExporter();
    ce.export(null, plist, null, true);
    // ce.export(null, plist, new String[]{"x val", "y val"}, new String[]{"x", "y"});
    // ce.export(null, plist, new String[]{"x val", "y val"}, false);
    // ce.export(null, plist, null, true);
    //ce.export("data.csv", list, new String[]{"x pt", "y pt"}, new String[]{"x", "y"});
  }
}
