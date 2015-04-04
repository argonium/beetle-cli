package io.miti.beetle.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * Export an object to a JSON file.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class JSONExporter
{
  /**
   * The one instance of this class.
   */
  private static final JSONExporter exporter = new JSONExporter();

  /**
   * Used to export the data to a print stream.
   */
  private java.io.PrintStream printStream = null;

  /**
   * Used to export the data to a file.
   */
  private java.io.BufferedWriter bufferedWriter = null;

  /**
   * Used to export the data to a String.
   */
  private StringBuilder stringWriter = null;

  /**
   * The line terminator for this OS.
   */
  private String lineTerminator = null;

  /**
   * Whether to append a newline when exporting to a string.
   */
  private boolean appendNewlineToString = false;

  /**
   * The delta to increase the indentation by.
   */
  private static final int INDENT_DELTA = 2;


  /**
   * Default constructor.
   */
  private JSONExporter() {
    super();
  }


  /**
   * Get the one instance of this class.
   * 
   * @return the one instance of this class
   */
  public static JSONExporter getInstance() {
    return exporter;
  }


  /**
   * Return whether this is a valid file name.
   * 
   * @param name the file name to check
   * @return if the name is valid for a file
   */
  private static boolean checkFileName(final String name) {
    // Check the variable
    if ((name == null) || (name.length() < 1)) {
      return false;
    }

    // Create a File variable
    File file = new File(name);
    if (file.exists()) {
      // Something exists with this name; return whether this is a file
      return (file.isFile());
    }

    // The file doesn't exist, so we can create a file with its name
    return true;
  }


  /**
   * Write out the line to the output buffer.
   * 
   * @param line the line of text to print out
   */
  private void writeLine(final String line) {
    // Check how we should write out the data
    if (printStream != null) {
      printStream.println(line);
    } else if (bufferedWriter != null) {
      try {
        bufferedWriter.write(line);
        bufferedWriter.write(lineTerminator);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else if (stringWriter != null) {
      stringWriter.append(line);
      if (appendNewlineToString) {
        stringWriter.append(lineTerminator);
      } else {
        stringWriter.append(' ');
      }
    } else {
      System.out.println(line);
    }
  }


  /**
   * Write out the line to the output buffer with no newline at the end.
   * 
   * @param line the line of text to print out
   */
  private void write(final String line) {
    // Check how we should write out the data
    if (printStream != null) {
      printStream.print(line);
    } else if (bufferedWriter != null) {
      try {
        bufferedWriter.write(line);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else if (stringWriter != null) {
      stringWriter.append(line).append(' ');
    } else {
      System.out.print(line);
    }
  }


  /**
   * Return a string containing the specified number of spaces. Used to create
   * the indention space for a section.
   * 
   * @param indent the number of spaces
   * @return the string containing spaces
   */
  public static String getIndent(final int indent) {
    // Handle some common cases for indent
    switch (indent) {
    case 0:
      return "";
    case 1:
      return " ";
    case 2:
      return "  ";
    case 3:
      return "   ";
    case 4:
      return "    ";
    case 5:
      return "     ";
    case 6:
      return "      ";
    case 7:
      return "       ";
    case 8:
      return "        ";
    case 9:
      return "         ";
    default:
      break;
    }

    // None of the above cases were met, so build the string one
    // character at a time
    StringBuilder sb = new StringBuilder(indent);
    for (int i = 0; i < indent; ++i) {
      sb.append(' ');
    }

    // Return the string
    return sb.toString();
  }


  /**
   * Method to quote a string, if desired and the string is not null.
   * 
   * @param value the string
   * @param quoteValue whether the string should be quoted
   * @return the quoted or unmodified string
   */
  private static String quoteStr(final String value, final boolean quoteValue) {
    // Check for whether it's null, or should not be quoted
    if ((!quoteValue) || (value == null)) {
      return value;
    }

    StringBuilder sb = new StringBuilder(40);
    sb.append("\"").append(value).append("\"");
    return sb.toString();
  }


  /**
   * Build the line of data to write out.
   * 
   * @param varName the name of the variable
   * @param varValue the value of the variable
   * @param quoteValue whether to put quotes around the value
   * @param indent the number of spaces to indent the line
   * @param addComma whether to append a comma to the end of the line
   */
  private void writeData(final String varName, final String varValue,
      final boolean quoteValue, final int indent, final boolean addComma) {
    writeData(varName, varValue, quoteValue, indent, addComma, true);
  }


  /**
   * Build the line of data to write out.
   * 
   * @param varName the name of the variable
   * @param varValue the value of the variable
   * @param quoteValue whether to put quotes around the value
   * @param indent the number of spaces to indent the line
   * @param addComma whether to append a comma to the end of the line
   * @param newLine whether to append a newline
   */
  private void writeData(final String varName, final String varValue,
      final boolean quoteValue, final int indent, final boolean addComma,
      final boolean newLine) {
    StringBuilder sb = new StringBuilder(100);
    final String outValue = quoteStr(varValue, quoteValue);
    if (varName == null) {
      sb.append(getIndent(indent)).append(outValue);
      if (addComma) {
        sb.append(",");
      }
    } else if (varValue == null) {
      sb.append(getIndent(indent)).append("\"").append(varName)
          .append("\" : null");
      if (addComma) {
        sb.append(",");
      }
    } else {
      sb.append(getIndent(indent)).append("\"").append(varName).append("\" : ")
          .append(outValue);
      if (addComma) {
        sb.append(",");
      }
    }

    if (newLine) {
      writeLine(sb.toString());
    } else {
      write(sb.toString());
    }
  }


  /**
   * Return the index (in fields) of the last field of interest. Currently, this
   * means the last field that is not static or transient.
   * 
   * @param fields the array of Fields for the object
   * @return the index of the last field of interest
   */
  private int getLastFieldOfInterest(final Field[] fields) {
    // Check the input parameter
    if ((fields == null) || (fields.length < 1)) {
      return 0;
    }

    // Iterate over the list of fields, starting at the end, and
    // backtrack until we hit a field that satisfies the filter
    final int size = fields.length;
    int lastGoodIndex = -1;
    for (int index = size - 1; index >= 0; --index) {
      Field f = fields[index];
      if (useField(f)) {
        // We like this field, so return
        lastGoodIndex = index;
        break;
      }
    }

    return lastGoodIndex;
  }


  /**
   * Check whether this field should be exported.
   * 
   * @param f the field to consider
   * @return whether the field should be exported
   */
  private boolean useField(final Field f) {
    // Check for null
    if (f == null) {
      return false;
    }

    // Get the modifiers and check its state
    final int mods = f.getModifiers();

    // If it's a static final object, don't export it
    if ((Modifier.isStatic(mods)) && (Modifier.isFinal(mods))) {
      // We don't like this field, so return false
      return false;
    } else if (Modifier.isTransient(mods)) {
      // Let's not export transient fields
      return false;
    }

    // Export this field
    return true;
  }


  /**
   * Export the object to the output buffer, indenting the section the specified
   * number of spaces.
   * 
   * @param o the object to export
   * @param indent the number of spaces to indent each line in the section
   */
  private void export(final Object o, final int indent) {
    // Get the class for this object, and use that to get the fields
    Class<? extends Object> c = o.getClass();
    Field[] fields = c.getDeclaredFields();
    final int numFields = fields.length;
    if (numFields == 0) {
      // No fields in the object
      return;
    }

    // Get the index of the last field to export. We have to do this
    // in case the last field in fields may be static or have some
    // condition causing us not to want to export it.
    final int lastFieldOfInterest = getLastFieldOfInterest(fields);
    if (lastFieldOfInterest < 0) {
      // There are no fields to export, so return
      return;
    }

    // Iterate over the fields, exporting them one at a time
    for (int i = 0; i <= lastFieldOfInterest; ++i) {
      // Get the current field and make it accessible
      final Field f = fields[i];
      f.setAccessible(true);

      // If we're not using this field, skip it
      if (!useField(f)) {
        continue;
      }

      // Save whether there are additional fields of interest after this one
      final boolean addComma = (i < lastFieldOfInterest);

      // Save the name of the field
      final String name = f.getName();

      // Search for the field type
      try {
        final Object value = f.get(o);
        if (f.isEnumConstant()) {
          throw new RuntimeException("Enum constant found");
        } else if (!writeSimpleType(value, name, indent, addComma, true)) {
          if (value instanceof Map) {
            // Handle maps
            Map<?, ?> values = (Map<?, ?>) value;
            if (values.size() < 1) {
              // No elements
              writeData(name, "[]", false, indent, addComma);
            } else {
              // Iterate over the elements
              writeData(name, "", false, indent, false);
              writeLine(getIndent(indent) + "[");
              final int setSize = values.size();
              int currIndex = 0;
              for (java.util.Map.Entry<?, ?> entrySet : values.entrySet()) {
                Object key = entrySet.getKey();
                final boolean hasMore = (currIndex < (setSize - 1));
                ++currIndex;
                if (key != null) {
                  Object data = values.get(key);
                  if (data != null) {
                    // Build the output string of the key and then the data
                    // string. This
                    // needs to be done recursively, in case data is a map or
                    // similar.
                    // If key is a simple type, just spit it out followed by
                    // " : <data>".
                    // Else, call export() to write that out as " {...} : ".

                    // First export the key
                    if (writeSimpleType(key, null, indent + INDENT_DELTA,
                        false, false)) {
                      write(" : ");
                    } else {
                      // The key is a collection, map or compound object.
                      writeLine(getIndent(indent + INDENT_DELTA) + "{");
                      export(key, indent + (INDENT_DELTA * 2));
                      write(getIndent(indent + INDENT_DELTA) + "} : ");
                    }

                    // Now export the data
                    if (!writeSimpleType(data, null, 0, hasMore, true)) {
                      writeLine("");
                      writeLine(getIndent(indent + INDENT_DELTA + 2) + "{");
                      export(data, indent + (INDENT_DELTA * 3));
                      writeLine(getIndent(indent + INDENT_DELTA + 2) + "}"
                          + (hasMore ? "," : ""));
                    }
                  }
                }
              }
              writeData(null, "]", false, indent, addComma);
            }
          } else if (value instanceof Collection) {
            // Handle lists, sets, queues, etc.
            final Collection<?> values = (Collection<?>) value;
            Iterator<?> iter = values.iterator();
            if (!iter.hasNext()) {
              // No elements
              writeData(name, "[]", false, indent, addComma);
            } else {
              // Iterate over the elements
              writeData(name, "", false, indent, false);
              writeLine(getIndent(indent) + "[");
              while (iter.hasNext()) {
                Object obj = iter.next();
                final boolean hasMore = iter.hasNext();
                if (obj == null) {
                  writeData(null, "null", false, indent + INDENT_DELTA, hasMore);
                } else if (!writeSimpleType(obj, null, indent + INDENT_DELTA,
                    hasMore, true)) {
                  writeLine(getIndent(indent + INDENT_DELTA) + "{");
                  export(obj, indent + INDENT_DELTA + INDENT_DELTA);
                  writeLine(getIndent(indent + INDENT_DELTA) + "}"
                      + (hasMore ? "," : ""));
                }
              }
              writeData(null, "]", false, indent, addComma);
            }
          } else if (value.getClass().isArray()) {
            // See if the array has any elements
            int len = Array.getLength(value);
            if (len == 0) {
              // The array is empty
              writeData(name, "[]", false, indent, addComma);
            } else {
              writeData(name, "", false, indent, false);
              writeLine(getIndent(indent) + "[");
              for (int index = 0; index < len; ++index) {
                // Store whether there are additional elements
                final boolean hasMore = (index < (len - 1));

                // Get the current object
                Object obj = Array.get(value, index);

                if (obj == null) {
                  writeData(null, "null", false, indent + INDENT_DELTA, hasMore);
                } else if (!writeSimpleType(obj, null, indent + INDENT_DELTA,
                    hasMore, true)) {
                  writeLine(getIndent(indent + INDENT_DELTA) + "{");
                  export(obj, indent + INDENT_DELTA + INDENT_DELTA);
                  writeLine(getIndent(indent + INDENT_DELTA) + "}"
                      + (hasMore ? "," : ""));
                }
              }
              writeData(null, "]", false, indent, addComma);
            }
          } else {
            // Write out a simple object with subfields
            writeData(name, "", false, indent, false);
            writeLine(getIndent(indent) + "{");
            export(value, indent + INDENT_DELTA);
            writeLine(getIndent(indent) + "}" + (addComma ? "," : ""));
          }
        }
      } catch (IllegalArgumentException e) {
        System.err.println("IArgE for " + name + ": " + e.getMessage());
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        System.err.println("IAccessE for " + name + ": " + e.getMessage());
        e.printStackTrace();
      }
    }
  }


  /**
   * Check if this is a simple type (String, number, date, etc.), and if so,
   * write out the value.
   * 
   * @param value the object to check
   * @param name the variable name
   * @param indent the amount to indent
   * @param addComma whether to append a comma
   * @param newLine whether to append a newline
   * @return whether the object was written out
   */
  private boolean writeSimpleType(final Object value, final String name,
      final int indent, final boolean addComma, final boolean newLine) {
    if (value == null) {
      writeData(name, "null", false, indent, addComma, newLine);
    } else if (value instanceof String) {
      writeData(name, value.toString(), true, indent, addComma, newLine);
    } else if (value instanceof Number) {
      writeData(name, ((Number) value).toString(), false, indent, addComma,
          newLine);
    } else if (value instanceof Character) {
      writeData(name, ((Character) value).toString(), false, indent, addComma,
          newLine);
    } else if (value instanceof Boolean) {
      writeData(name, ((Boolean) value).toString(), false, indent, addComma,
          newLine);
    } else if (value instanceof Date) {
      writeData(name, Long.toString(((Date) value).getTime()), false, indent,
          addComma, newLine);
    } else {
      return false;
    }

    return true;
  }


  /**
   * Export the object.
   * 
   * @param obj the object to export
   */
  private void export(final Object obj) {
    // Check for null
    if (obj == null) {
      return;
    }

    // Export the object
    writeLine("{");
    export(obj, INDENT_DELTA);
    writeLine("}");
  }


  /**
   * Export the object to a String.
   * 
   * @param obj the object to export
   * @param appendNewline whether to append a newline after each JSON object
   * @return the object in JSON format, as a string
   */
  public String export(final Object obj, final boolean appendNewline) {
    lineTerminator = "\r\n"; // System.getProperty("line.separator");
    appendNewlineToString = appendNewline;

    // Allocate the string builder
    stringWriter = new StringBuilder(500);

    // Export
    export(obj);

    // Copy the string, and then clear out the buffer
    String output = stringWriter.toString();
    stringWriter.setLength(0);
    stringWriter = null;

    // Return the generated string
    return output;
  }


  /**
   * Export the specified object to the print stream.
   * 
   * @param obj the object to export
   * @param pPrintStream the print stream to export to
   */
  public void export(final Object obj, final PrintStream pPrintStream) {
    // Save the print stream
    printStream = pPrintStream;

    // Export
    export(obj);

    // Kill the print stream
    printStream = null;
  }


  /**
   * Export the specified object to the output file.
   * 
   * @param obj the object to export
   * @param fileName the name of the file to export the data to
   */
  public void export(final Object obj, final String fileName) {
    // Check the file name
    if (!checkFileName(fileName)) {
      return;
    }

    // Set up the line terminator
    lineTerminator = "\r\n"; // System.getProperty("line.separator");

    // Set up the output writer
    try {
      bufferedWriter = new BufferedWriter(new FileWriter(fileName));
    } catch (IOException e) {
      e.printStackTrace();
      bufferedWriter = null;
    }

    // Check the result
    if (bufferedWriter == null) {
      return;
    }

    // Export
    export(obj);

    // Close the writer
    if (bufferedWriter != null) {
      try {
        bufferedWriter.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    // Kill the buffer
    bufferedWriter = null;
  }
}
