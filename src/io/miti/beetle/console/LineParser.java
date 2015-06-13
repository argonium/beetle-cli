package io.miti.beetle.console;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Parse a line into a collection of phrases. Supports quoted strings; assumes a
 * separator of a space. Commas are preserved.
 * 
 * @author mwallace
 * @version 1.0
 */
public final class LineParser
{
  /**
   * Default constructor.
   */
  public LineParser() {
    super();
  }


  /**
   * Parse the string into a list of phrases. A phrase is either a standalone
   * word from the input string, or a quoted string.
   * 
   * @param sInputTerm the string to parse
   * @return the list of phrases in the word
   */
  @SuppressWarnings("unchecked")
  public List<String> parseIntoPhrases(final String sInputTerm) {
    // Check the input
    if ((sInputTerm == null) || (sInputTerm.length() < 1)) {
      // Return an empty list
      return Collections.EMPTY_LIST;
    }

    // Allocate an array to hold the items
    List<String> list = new ArrayList<String>(20);

    // Trim the string
    final String sInput = sInputTerm.trim();
    if (sInput.length() < 1) {
      // Return an empty list
      return Collections.EMPTY_LIST;
    }

    // Build the list
    final int nLen = sInput.length();
    boolean inQuote = false;
    int i = 0;
    StringBuilder sb = new StringBuilder(200);
    while (i < nLen) {
      // Save the character
      char ch = sInput.charAt(i);

      // Check for a leading quote
      if (ch == '"') {
        // See if we're already inside a quote
        if (inQuote) {
          list.add(sb.toString());
        }

        inQuote = !inQuote;
        sb.setLength(0);
        ++i;
      } else if (ch == '\\') {
        // Go to the next character
        ++i;

        // See if we're at the end
        if (i >= nLen) {
          break;
        } else {
          ch = sInput.charAt(i);
          switch (ch) {
          case 'n':
            sb.append('\n');
            break;
          case 't':
            sb.append('\t');
            break;
          case 'r':
            sb.append('\r');
            break;
          default:
            sb.append(ch);
            break;
          }
        }

        ++i;
      } else if (ch == ' ') {
        // See if we're in quotes
        if (inQuote) {
          // Save the character
          sb.append(ch);
        } else {
          // Break on spaces, so save the current word, if
          // there is one
          if (sb.length() > 0) {
            list.add(sb.toString());
            sb.setLength(0);
          }
        }

        ++i;
      } else {
        // Just add the character
        sb.append(ch);
        ++i;
      }
    }

    // Check if sb has any leftover strings
    if (sb.length() > 0) {
      list.add(sb.toString());
    }

    // Return the list
    return list;
  }
}
