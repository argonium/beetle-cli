package io.miti.beetle.processor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.miti.beetle.cache.DBTypeCache;
import io.miti.beetle.cache.UserDBCache;
import io.miti.beetle.dbutil.ConnManager;
import io.miti.beetle.dbutil.Database;
import io.miti.beetle.exporters.CsvDBFileWriter;
import io.miti.beetle.exporters.DBFileWriter;
import io.miti.beetle.exporters.JsonDBFileWriter;
import io.miti.beetle.exporters.MarkdownDBFileWriter;
import io.miti.beetle.exporters.SQLDBFileWriter;
import io.miti.beetle.exporters.TabDBFileWriter;
import io.miti.beetle.exporters.TomlDBFileWriter;
import io.miti.beetle.exporters.XmlDBFileWriter;
import io.miti.beetle.exporters.YamlDBFileWriter;
import io.miti.beetle.model.ContentType;
import io.miti.beetle.model.DbType;
import io.miti.beetle.model.Session;
import io.miti.beetle.model.UserDb;
import io.miti.beetle.util.FakeNode;
import io.miti.beetle.util.FakeSpecParser;
import io.miti.beetle.util.Logger;
import io.miti.beetle.util.Utility;

public final class DataProcessor
{
  private Session session = null;
  private int runCount = 1;
  
  public DataProcessor() {
    super();
  }
  
  
  public DataProcessor(final Session pSession) {
    session = pSession;
  }
  
  public void setRunCount(final int nRunCount) {
    runCount = nRunCount;
  }
  
  
  public void run() {
    // Check the session
    if (session == null) {
      Logger.error("Error: The session is null or invalid");
      return;
    }
    
    // Check for SQL imports
    if (session.getSourceTypeId() == ContentType.SQL.getId()) {
      importSQL();
    } else if (session.getSourceTypeId() == ContentType.FAKE.getId()) {
      saveFakeData();
    } else {
      Logger.error("Error: Only SQL imports are supported for now; type = " + session.getSourceTypeId());
    }
  }
  
  
  public void saveFakeData() {
    
    final ContentType cType = ContentType.getById(session.getTargetTypeId());
    if ((cType != ContentType.JSON) && (cType != ContentType.CSV) &&
        (cType != ContentType.YAML) && (cType != ContentType.TOML) &&
        (cType != ContentType.XML) && (cType != ContentType.SQL_FILE) &&
        (cType != ContentType.MARKDOWN) && (cType != ContentType.TSV) &&
        (cType != ContentType.JAVA)) {
      Logger.error("Only supported export formats: CSV, JSON, YAML, TOML, XML, SQL, TSV, Markdown, Java");
      return;
    }
    
    // Parse the specification in the source name.
    // It should store the list of column names, their class type, and a pointer to
    // the function call to generate the fake data for that column
    final FakeSpecParser spec = new FakeSpecParser();
    if (!spec.parse(session.getSourceName())) {
      Logger.error("Invalid specification for fake data");
      return;
    }
    
    if (cType == ContentType.JAVA) {
      writeJavaClass(spec);
    } else {
      // Configure the data target
      final DBFileWriter writer = getFileWriter(cType,
          session.getTargetName(), session.getTargetData(), spec);
      
      // Write the header
      writer.writeHeader();
      
      // Iterate over the data for exporting
      for (int i = 0; i < runCount; ++i) {
        // Write out the data
        writer.writeObject(spec);
      }
      
      // Write the footer
      writer.writeFooter();
      
      // Force out any pending data
      writer.writeString(true);
    }
  }
  
  
  private void writeJavaClass(final FakeSpecParser spec) {
    generateJavaClass(new File("."), session.getTargetData(), session.getTargetName(), spec.getNodes());
  }
  
  
  private void writeJavaClassFromSQL() {
    // TODO
  }


  public void importSQL() {
    
    final ContentType cType = ContentType.getById(session.getTargetTypeId());
    if ((cType != ContentType.JSON) && (cType != ContentType.CSV) &&
        (cType != ContentType.YAML) && (cType != ContentType.TOML) &&
        (cType != ContentType.XML) && (cType != ContentType.SQL_FILE) &&
        (cType != ContentType.MARKDOWN) && (cType != ContentType.TSV) &&
        (cType != ContentType.JAVA)) {
      Logger.error("Only supported export formats: CSV, JSON, YAML, TOML, XML, SQL, TSV, Markdown, Java");
      return;
    }
    
    // Find the user DB with the specified ID
    final UserDb userDb = UserDBCache.get().find(session.getSourceId());
    if (userDb == null) {
      Logger.error("Error: Invalid database ID in the session");
      return;
    }
    
    // Make sure the JDBC DB's driver class is loaded
    final DbType dbType = DBTypeCache.get().find(userDb.getDbTypeId());
    ConnManager.get().addDriverClass(dbType);
    
    // Open a connection to the database
    Logger.debug("Initializing the database " + userDb.getUrl());
    ConnManager.get().init(userDb.getUrl(), userDb.getUserId(), userDb.getUserPw());
    if (!ConnManager.get().create()) {
      Logger.error("Unable to connect to database " + userDb.getUrl());
      return;
    }
    
    if (cType == ContentType.JAVA) {
      writeJavaClassFromSQL();
    } else {
      // Get the metadata
      PreparedStatement stmt = null;
      try {
        // Prepare the statement
        stmt = ConnManager.get().getConn().prepareStatement(session.getSourceName());
        
        // Verify it's not null
        if (stmt != null) {
          // Execute the statement and check the result
          final boolean result = stmt.execute();
          if (!result) {
            Logger.error("The statement did not execute correctly");
          } else {
            // Get the result set of executing the query
            ResultSet rs = stmt.getResultSet();
            
            // Verify the result set is not null
            if (rs != null) {
              // Get the metadata
              ResultSetMetaData rsmd = rs.getMetaData();
              
              // Configure the data target
              final DBFileWriter writer = getFileWriter(cType,
                  session.getTargetName(), session.getTargetData(), rsmd);
              
              // Write the header
              writer.writeHeader();
              
              // Iterate over the data for exporting
              Database.executeSelect(rs, writer);
              
              // Write the footer
              writer.writeFooter();
              
              // Force out any pending data
              writer.writeString(true);
              
              // Close the result set
              rs.close();
              rs = null;
            } else {
              Logger.error("The database result set is null");
            }
          }
          
          // Close the statement
          stmt.close();
          stmt = null;
        } else {
          Logger.error("The database statement is null");
        }
      } catch (SQLException e) {
        Logger.error("SQL Exception: " + e.getMessage());
        e.printStackTrace();
      }
    }
    
    // Close the connection
    ConnManager.get().close();
  }
  
  
  private static DBFileWriter getFileWriter(final ContentType cType,
                                            final String outName,
                                            final String outData,
                                            final ResultSetMetaData rsmd) {
    // Create the appropriate file writer object
    if (cType == ContentType.JSON) {
      return new JsonDBFileWriter(outName, outData, rsmd);
    } else if (cType == ContentType.CSV) {
      return new CsvDBFileWriter(outName, outData, rsmd);
    } else if (cType == ContentType.TSV) {
      return new TabDBFileWriter(outName, outData, rsmd);
    } else if (cType == ContentType.YAML) {
      return new YamlDBFileWriter(outName, outData, rsmd);
    } else if (cType == ContentType.TOML) {
      return new TomlDBFileWriter(outName, outData, rsmd);
    } else if (cType == ContentType.SQL_FILE) {
      return new SQLDBFileWriter(outName, outData, rsmd);
    } else if (cType == ContentType.MARKDOWN) {
      return new MarkdownDBFileWriter(outName, outData, rsmd);
    } else if (cType == ContentType.XML) {
      return new XmlDBFileWriter(outName, outData, rsmd);
    } else {
      return null;
    }
  }
  
  
  private static DBFileWriter getFileWriter(final ContentType cType,
                                            final String outName,
                                            final String outData,
                                            final FakeSpecParser spec) {
    // Create the appropriate file writer object
    if (cType == ContentType.JSON) {
      return new JsonDBFileWriter(outName, outData, spec);
    } else if (cType == ContentType.CSV) {
      return new CsvDBFileWriter(outName, outData, spec);
    } else if (cType == ContentType.TSV) {
      return new TabDBFileWriter(outName, outData, spec);
    } else if (cType == ContentType.YAML) {
      return new YamlDBFileWriter(outName, outData, spec);
    } else if (cType == ContentType.TOML) {
      return new TomlDBFileWriter(outName, outData, spec);
    } else if (cType == ContentType.SQL_FILE) {
      return new SQLDBFileWriter(outName, outData, spec);
    } else if (cType == ContentType.MARKDOWN) {
      return new MarkdownDBFileWriter(outName, outData, spec);
    } else if (cType == ContentType.XML) {
      return new XmlDBFileWriter(outName, outData, spec);
    } else {
      return null;
    }
  }
  
  
  /**
   * Generate the Java class for the table.
   * 
   * @param outputDir the output directory
   * @param packageName the package name of the class
   * @param tableName the table name
   * @param nodes the list of columns and types
   */
  private void generateJavaClass(final File outputDir,
                                 final String packageName,
                                 final String tableName,
                                 final List<FakeNode> nodes)
  {
    final String lineSep = DBFileWriter.EOL;
    
    // Generate the class name from the table name
    final String className = tableName;
    
    // The output file
    File file = new File(outputDir, className + ".java");
    
    // Write to the file
    BufferedWriter out = null;
    try
    {
      // Open the output writer
      out = new BufferedWriter(new FileWriter(file));
      
      // Build the date
      SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
      String dateStr = sdf.format(new Date());
      
      // Write the header comment
      out.write("/*" + lineSep);
      out.write(" * Java class for the " + tableName + " object." + lineSep);
      out.write(" * Generated on " + dateStr + " by Beetle." + lineSep);
      out.write(" */" + lineSep + lineSep);
      
      // Write the package name (if any)
      if ((packageName != null) && (packageName.length() > 0))
      {
        out.write("package " + packageName + ";" + lineSep + lineSep);
      }
      
      // Write the class comment
      out.write("/**" + lineSep);
      out.write(" * Java class to encapsulate the " + tableName + " object." + lineSep);
      out.write(" *" + lineSep);
      out.write(" * @version 1.0" + lineSep);
      out.write(" */" + lineSep);
      
      // Write the class declaration
      out.write("public final class " + className);
      out.write(lineSep + "{" + lineSep);
      
      // Write the field declarations
      for (FakeNode col : nodes)
      {
        out.write("  /**" + lineSep);
        out.write("   * The field " + col.getName() + "." + lineSep);
        out.write("   */" + lineSep);
        out.write("  private " + col.getTypeAsJavaClass() + " " +
                  col.getName() + " = " + col.getDefaultValue() + ";" +
                  lineSep + "  " + lineSep);
      }
      
      // Write the default constructor
      out.write("  " + lineSep);
      out.write("  /**" + lineSep);
      out.write("   * Default constructor." + lineSep);
      out.write("   */" + lineSep);
      out.write("  public " + className + "()" + lineSep);
      out.write("  {" + lineSep);
      out.write("    super();" + lineSep);
      out.write("  }" + lineSep);
      
      // Write the getters/setters
      for (FakeNode col : nodes)
      {
        // The field name with the first character in uppercase
        final String fieldInUC = Utility.setFirstCharacter(col.getName(), true);
        
        // Write the getter
        out.write("  " + lineSep);
        out.write("  " + lineSep);
        out.write("  /**" + lineSep);
        out.write("   * Get the value for " + col.getName() + "." + lineSep);
        out.write("   *" + lineSep);
        out.write("   * @return the " + col.getName() + lineSep);
        out.write("   */" + lineSep);
        out.write("  public " + col.getTypeAsJavaClass() + " get" +
                  fieldInUC + "()" + lineSep);
        out.write("  {" + lineSep);
        out.write("    return " + col.getName() + ";" + lineSep);
        out.write("  }" + lineSep);
        
        // Write the setter
        out.write("  " + lineSep);
        out.write("  " + lineSep);
        out.write("  /**" + lineSep);
        out.write("   * Update the value for " + col.getName() + "." + lineSep);
        out.write("   *" + lineSep);
        out.write("   * @param p" + fieldInUC + " the new value for " +
                  col.getName() + lineSep);
        out.write("   */" + lineSep);
        out.write("  public void set" + fieldInUC + "(final " +
                  col.getTypeAsJavaClass() + " p" + fieldInUC + ")" + lineSep);
        out.write("  {" + lineSep);
        out.write("    " + col.getName() + " = p" + fieldInUC + ";" + lineSep);
        out.write("  }" + lineSep);
      }
      
      // Write the class declaration
      out.write("}" + lineSep);
      
      // Close the writer
      out.close();
      out = null;
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    finally
    {
      if (out != null)
      {
        try
        {
          out.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
        
        out = null;
      }
    }
  }
}
