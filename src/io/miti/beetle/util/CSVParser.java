package io.miti.beetle.util;

import java.util.ArrayList;
import java.util.Iterator;

/** Parse comma-separated values (CSV), a common Windows file format.
 * Sample input: "LU",86.25,"ab,cd","11/4/1998","2:19PM",+4.0625
 * <p>
 * Inner logic adapted from a C++ original that was
 * Copyright (C) 1999 Lucent Technologies
 * Excerpted from 'The Practice of Programming'
 * by Brian W. Kernighan and Rob Pike.
 */
public class CSVParser
{ 
  private static final boolean debugMode = false;
  public static final String SEP = ",";

  /** Construct a CSV parser, with the default separator (`,'). */
  public CSVParser() {
    this(SEP);
  }

  /** Construct a CSV parser with a given separator. Must be
   * exactly the string that is the separator, not a list of
   * separator characters!
   */
  public CSVParser(String sep) {
    fieldsep = sep;
  }

  /** The fields in the current String */
  protected ArrayList<String> list = new ArrayList<String>();

  /** the separator string for this parser */
  protected String fieldsep;

  /** parse: break the input String into fields
   * @return java.util.Iterator containing each field 
   * from the original as a String, in order.
   */
  public Iterator<String> parse(String line)
  {
    StringBuffer sb = new StringBuffer();
    list.clear();     // discard previous, if any
    int i = 0;

    if (line.length() == 0) {
      list.add(line);
      return list.iterator();
    }

    do {
      sb.setLength(0);
      if (i < line.length() && line.charAt(i) == '"')
        i = advquoted(line, sb, ++i); // skip quote
      else
        i = advplain(line, sb, i);
      list.add(sb.toString());
      i++;
    } while (i < line.length());

    return list.iterator();
  }

  /** advquoted: quoted field; return index of next separator */
  protected int advquoted(String s, StringBuffer sb, int i)
  {
    int j;

    // Loop through input s, handling escaped quotes
    // and looking for the ending " or , or end of line.

    for (j = i; j < s.length(); j++) {
      // found end of field if find unescaped quote.
      if (s.charAt(j) == '"' && s.charAt(j-1) != '\\') {
        int k = s.indexOf(fieldsep, j);
        debug("j = " + j + ", k = " + k);
        if (k == -1) {  // no separator found after this field
          k += s.length();
          for (k -= j; k-- > 0; ) {
            sb.append(s.charAt(j++));
          }
        } else {
          --k;  // omit quote from copy
          for (k -= j; k-- > 0; ) {
            sb.append(s.charAt(j++));
          }
          ++j;  // skip over quote
        }
        break;
      }
      sb.append(s.charAt(j)); // regular character.
    }
    return j;
  }

  /** advplain: unquoted field; return index of next separator */
  protected int advplain(String s, StringBuffer sb, int i)
  {
    int j;

    j = s.indexOf(fieldsep, i); // look for separator
    debug("i = " + i + ", j = " + j);
    if (j == -1) {                // none found
      sb.append(s.substring(i));
      return s.length();
    } else {
      sb.append(s.substring(i, j));
      return j;
    }
  }
  
  /* debug - Write debugging statements if we're in debug mode */
  protected static void debug(final String str)
  {
    if (debugMode)
    {
      System.out.println(str);
    }
  }
  
  /* main - Used for testing */
  public static void main(String[] args)
  {
    CSVParser parser = new CSVParser();
    Iterator<String> it = parser.parse(
      "\"LU\",86.25,\"ab,cd\",\"11/4/1998\",\"2:19PM\",+4.0625");
    while (it.hasNext())
    {
      System.out.println(it.next());
    }
  }
}
