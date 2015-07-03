package io.miti.beetle.exporters;

import io.miti.beetle.util.FakeNode;
import io.miti.beetle.util.FakeSpecParser;
import io.miti.beetle.util.FakeType;
import io.miti.beetle.util.Logger;
import io.miti.beetle.util.NodeInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class DBFileWriter
{
  protected StringBuilder sb = null;
  protected String filename = null;
  protected String fileData = null;
  protected File file = null;
  
  private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
  
  // Save info about the result set - column names and types
  protected List<NodeInfo> nodes = null;
  
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
  public DBFileWriter(final String sFilename, final String sFileData, final ResultSetMetaData pRSMD) {
    
    // Initialize this class
    init(sFilename);
    fileData = sFileData;
    
    // Save just the info we need - column names and types - into an array
    initializeNodeList(pRSMD);
  }


  /**
   * Constructor taking the output filename and result set.
   * 
   * @param sFilename the output filename
   * @param specData the parsed fake-data spec
   */
  public DBFileWriter(final String sFilename, final String sFileData, final FakeSpecParser specData) {
    
    // Initialize this class
    init(sFilename);
    fileData = sFileData;
    
    // Save just the info we need - column names and types - into an array
    initializeNodeList(specData);
  }
  
  
  private void init(final String sFilename) {
    sb = new StringBuilder(100);
    file = new File(sFilename);
    filename = sFilename;
    
    // Check the output file - if it exists as a file, empty it
    if (file.exists() && file.isFile()) {
      try {
        file.delete();
      } catch (Exception ex) {
        Logger.error("Exception deleting file: " + ex.getMessage());
      }
    }
  }
  
  
  private void initializeNodeList(final FakeSpecParser specData) {
    // Save the column names and classes into NodeList
    final List<FakeNode> fakeNodes = specData.getNodes();
    nodes = new ArrayList<NodeInfo>();
    for (FakeNode fake : fakeNodes) {
      nodes.add(new NodeInfo(fake.getName(), fake.getClazz()));
    }
  }
  
  
  private void initializeNodeList(final ResultSetMetaData pRSMD) {
    // Check if the metadata has results
    if (pRSMD == null) {
      return;
    }
    
    // Get the number of columns
    int count = 0;
    try {
      count = pRSMD.getColumnCount();
    } catch (SQLException e) {
      Logger.error("Error getting metadata column count: " + e.getMessage());
    }
    
    if (count <= 0) {
      return;
    }
    
    nodes = new ArrayList<NodeInfo>(count);
    for (int i = 1; i <= count; ++i) {
      try {
        // Save the column name
        final String name = pRSMD.getColumnName(i);
        
        // Derive the correct class
        final int nClassType = pRSMD.getColumnType(i);
        final Class<?> clazz = getJavaClassFromSqlType(nClassType);
        
        // Save the node data
        final NodeInfo node = new NodeInfo(name, clazz);
        nodes.add(node);
        
        // Log the info
        Logger.debug("Node #" + i + ": " + node.toString());
      } catch (SQLException e) {
        Logger.error("Error getting metadata column info: " + e.getMessage());
      }
    }
  }
  
  
  public Object getValueFromSpec(final FakeNode fake) {
    return FakeType.getValue(fake);
  }
  
  
  public Object getValueFromRow(final ResultSet rs, Class<?> clazz, final int index) {
    // Check the output class
    if (clazz == null) {
      // Unsupported data type
      return "";
    }
    
    try {
      if (clazz.equals(Boolean.class)) {
        boolean value = rs.getBoolean(index);
        return (rs.wasNull() ? null : value);
      } else if (clazz.equals(String.class)) {
        return rs.getString(index);
      } else if (clazz.equals(Long.class)) {
        long value = rs.getLong(index);
        return (rs.wasNull() ? null : value);
      } else if (clazz.equals(Double.class)) {
        double value = rs.getDouble(index);
        return (rs.wasNull() ? null : value);
      } else if (clazz.equals(java.util.Date.class)) {
        final Timestamp ts = rs.getTimestamp(index);
        final java.util.Date date = (ts == null) ? null : new java.util.Date(ts.getTime());
        return date;
      } else {
        // Unknown data type
        return "";
      }
    } catch (SQLException se) {
      Logger.error(se);
      Logger.error("* Retrieving column #" + index);
    }
    
    return "";
  }
  
  
  private Class<?> getJavaClassFromSqlType(final int nClassType) {
    
    switch (nClassType) {
      case Types.BOOLEAN:
      case Types.BIT:
        return Boolean.class;
      
      case Types.TIME_WITH_TIMEZONE:
      case Types.TIMESTAMP_WITH_TIMEZONE:
      case Types.DATE:
      case Types.TIME:
      case Types.TIMESTAMP:
        return java.util.Date.class;
      
      case Types.TINYINT:
      case Types.SMALLINT:
      case Types.INTEGER:
      case Types.BIGINT:
        return Long.class;
      
      case Types.FLOAT:
      case Types.REAL:
      case Types.DOUBLE:
      case Types.NUMERIC:
      case Types.DECIMAL:
        return Double.class;
        
      case Types.CHAR:
      case Types.VARCHAR:
      case Types.LONGVARCHAR:
      case Types.BINARY:
      case Types.VARBINARY:
      case Types.LONGVARBINARY:
      case Types.BLOB:
      case Types.CLOB:
      case Types.NCHAR:
      case Types.NVARCHAR:
      case Types.LONGNVARCHAR:
      case Types.NCLOB:
        return String.class;
        
      default:
        return null;
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
  
  public abstract void writeObject(final FakeSpecParser str);
  
  
  /**
   * Write a string to the file.  Default to not forcing a write.
   */
  public final void writeString() {
    writeString(false);
  }
  
  
  public static final String toGMTDate(final java.util.Date date) {
    return sdf.format(date);
  }
  
  
  /**
   * If the string buffer is big enough, flush it to disk.
   */
  public final void writeString(final boolean forceWrite) {
    
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
