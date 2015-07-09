package io.miti.beetle.processor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Date;

import io.miti.beetle.dbutil.Database;
import io.miti.beetle.exporters.SQLDBFileWriter;
import io.miti.beetle.prefs.PrefsDatabase;
import io.miti.beetle.util.Logger;

public final class BackupPrefs
{
  private static final String[] tables = new String[]{"db_type", "app_property",
    "user_db", "config", "hadoop", "session", "data_type", "header", "col_type"};
  
  public BackupPrefs() {
    super();
  }
  
  public boolean backupDatabase(final String fname) {
    // Open the writer
    SQLDBFileWriter writer = new SQLDBFileWriter(fname);
    
    // Add a header
    writer.addString("-- Back up of database on ");
    writer.addString(DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(new Date()));
    writer.addEOL();
    writer.addEOL();
    
    // Write the statements to drop the data
    addDeleteStatements(writer);
    writer.writeString();
    
    // Write the contents of each file
    addInsertStatements(writer);
    writer.writeString();
    
    // Write the file
    writer.writeString(true);
    
    return true;
  }

  private void addInsertStatements(final SQLDBFileWriter writer) {
    // Get a connection to the preferences database
    Connection conn = PrefsDatabase.getConnection();
    
    // Iterate over the tables
    for (String table : tables) {
      writer.addString("-- Insert data into table " + table.toUpperCase());
      writer.addEOL();
      writer.setFileData(table);
      final String select = "select * from " + table;
      
      writeInsertStatements(conn, select, writer);
    }
  }

  private void writeInsertStatements(final Connection conn,
      final String select, final SQLDBFileWriter writer) {
    
    // Get the metadata
    PreparedStatement stmt = null;
    try {
      // Prepare the statement
      stmt = conn.prepareStatement(select);
      
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
            writer.initializeNodeList(rsmd);
            
            // Iterate over the data for exporting
            Database.executeSelect(rs, writer);
            
            // Add an EOL
            writer.addEOL();
            writer.writeString();
            
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

  private void addDeleteStatements(final SQLDBFileWriter writer) {
    // Iterate over the tables
    writer.addString("-- Drop data from the existing tables");
    for (String table : tables) {
      writer.addString("delete from " + table + ";");
      writer.addEOL();
    }
    
    writer.addEOL();
  }
}
