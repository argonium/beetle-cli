package io.miti.beetle.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public final class FakeSpecParser
{
  private boolean isValid = false;
  private List<FakeNode> nodes = null;
  
  public FakeSpecParser() {
    super();
  }
  
  public boolean parse(final String spec) {
    // Check the input
    if ((spec == null) || spec.trim().isEmpty()) {
      return false;
    }
    
    // Parse the data
    nodes = new ArrayList<FakeNode>(10);
    final StringTokenizer st = new StringTokenizer(spec, ",");
    while (st.hasMoreTokens()) {
      // Split up the token; should be COLNAME@function[:value]
      final String token = st.nextToken();
      final int sep = token.indexOf('@');
      if ((sep < 1) || (sep == (token.length() - 1))) {
        continue;
      }
      
      // Save the column name
      final String colName = token.substring(0, sep);
      
      // Check for a colon
      final String substr = token.substring(sep + 1);
      final int colonIndex = substr.indexOf(':');
      if ((colonIndex == 0) || (colonIndex == (substr.length() - 1))) {
        continue;
      }
      final String func = (colonIndex < 0) ? substr : substr.substring(0, colonIndex);
      final String defValue = (colonIndex < 0) ? null : substr.substring(colonIndex + 1);
      
      // Get the functor enum
      final FakeType fakeFunc = FakeType.getFunc(func);
      if (fakeFunc == null) {
        Logger.error("Method " + func + " was not found.  Skipping.");
        continue;
      }
      
      // Determine the class type
      final Class<?> clazz = getClassFromMethod(func);
      if (clazz == null) {
        continue;
      }
      
      // Based on clazz, convert defValue into the appropriate type
      final Object constValue = getValueFromClass(clazz, defValue);
      
      // Save the object
      nodes.add(new FakeNode(colName, clazz, fakeFunc, constValue));
    }
    
    if (nodes.isEmpty()) {
      return false;
    }
    
    isValid = true;
    return isValid;
  }

  public static Object getValueFromClass(final Class<?> clazz, final String defValue) {
    
    if (defValue == null) {
      return null;
    }
    
    if (clazz.equals(Boolean.class)) {
      return Boolean.parseBoolean(defValue);
    } else if (clazz.equals(Long.class)) {
      final int val = Utility.getStringAsInteger(defValue, 0, 0);
      return Long.valueOf((long) val);
    } else if (clazz.equals(Double.class)) {
      final double val = Utility.getStringAsDouble(defValue, 0.0, 0.0);
      return Double.valueOf(val);
    }
    
    // Must be a string
    return defValue;
  }

  public static Class<?> getClassFromMethod(final String func) {
    
    if (func.equals("bool") || func.equals("bconst")) {
      return Boolean.class;
    } else if (func.equals("int") || func.equals("id") || func.equals("iconst")) {
      return Long.class;
    } else if (func.equals("double") || func.equals("dconst")) {
      return Double.class;
    }
    
    return String.class;
  }
  
  public List<FakeNode> getNodes() {
    return nodes;
  }
}
