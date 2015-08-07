package io.miti.beetle.util;

import java.util.ArrayList;
import java.util.Collections;
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
      final int index = defValue.indexOf(':');
      if (index < 0) {
        final int val = Utility.getStringAsInteger(defValue, 0, 0);
        return Long.valueOf((long) val);
      } else if ((index > 0) && (index < (defValue.length() - 1))) {
        final String word1 = defValue.substring(0, index);
        final String word2 = defValue.substring(index + 1);
        final long val1 = Utility.getStringAsInteger(word1, 0, 0);
        final long val2 = Utility.getStringAsInteger(word2, 100, 100);
        return new LongPoint(val1, val2);
      } else {
        return new LongPoint(0, 100);
      }
    } else if (clazz.equals(Double.class)) {
      final int index = defValue.indexOf(':');
      if (index < 0) {
        final double val = Utility.getStringAsDouble(defValue, 0.0, 0.0);
        return Double.valueOf(val);
      } else if ((index > 0) && (index < (defValue.length() - 1))) {
        final String word1 = defValue.substring(0, index);
        final String word2 = defValue.substring(index + 1);
        final double val1 = Utility.getStringAsDouble(word1, 0.0, 0.0);
        final double val2 = Utility.getStringAsDouble(word2, 100.0, 100.0);
        return new DoublePoint(val1, val2);
      } else {
        return new DoublePoint(0.0, 100.0);
      }
    }
    
    // Must be a string
    return defValue;
  }

  public static Class<?> getClassFromMethod(final String func) {
    
    if (func.equals("bool") || func.equals("bconst")) {
      return Boolean.class;
    } else if (func.equals("int") || func.equals("id") || func.equals("iconst") || func.equals("irange")) {
      return Long.class;
    } else if (func.equals("double") || func.equals("dconst") || func.equals("drange")) {
      return Double.class;
    }
    
    return String.class;
  }
  
  public List<FakeNode> getNodes() {
    return nodes;
  }

  public List<List<String>> getNodesAsListList() {
    if (nodes == null) {
      return null;
    } else if (nodes.isEmpty()) {
      return Collections.emptyList();
    }
    
    // Declare the list we return
    List<List<String>> list = new ArrayList<List<String>>(nodes.size());
    
    // Add the header row
    List<String> header = new ArrayList<String>(4);
    header.add("Field");
    header.add("Class");
    header.add("Method");
    header.add("Data");
    list.add(header);
    
    for (FakeNode node : nodes) {
      List<String> row = new ArrayList<String>(4);
      row.add(node.getName());
      
      final Class<?> clazz = node.getClazz();
      row.add(clazz == null ? "" : clazz.getName());
      
      final FakeType func = node.getFunc();
      row.add(func == null ? "" : func.toString());
      
      final Object obj = node.getData();
      if ((obj != null) && (obj instanceof LongPoint)) {
        final LongPoint lp = (LongPoint) obj;
        row.add(String.format("[%d, %d]", lp.getX(), lp.getY()));
      } else if ((obj != null) && (obj instanceof DoublePoint)) {
        final DoublePoint dp = (DoublePoint) obj;
        row.add(String.format("[%f, %f]", dp.getX(), dp.getY()));
      } else {
        row.add(obj == null ? "" : obj.toString());
      }
      
      list.add(row);
    }
    
    return list;
  }
}
