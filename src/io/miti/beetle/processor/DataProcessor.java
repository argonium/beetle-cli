package io.miti.beetle.processor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import io.miti.beetle.cache.DBTypeCache;
import io.miti.beetle.cache.UserDBCache;
import io.miti.beetle.dbutil.ConnManager;
import io.miti.beetle.dbutil.Database;
import io.miti.beetle.exporters.CsvDBFileWriter;
import io.miti.beetle.exporters.DBFileWriter;
import io.miti.beetle.exporters.JsonDBFileWriter;
import io.miti.beetle.exporters.TomlDBFileWriter;
import io.miti.beetle.exporters.XmlDBFileWriter;
import io.miti.beetle.exporters.YamlDBFileWriter;
import io.miti.beetle.model.ContentType;
import io.miti.beetle.model.DbType;
import io.miti.beetle.model.Session;
import io.miti.beetle.model.UserDb;
import io.miti.beetle.util.Logger;

public final class DataProcessor
{
  private Session session = null;
  
  public DataProcessor() {
    super();
  }
  
  
  public DataProcessor(final Session pSession) {
    session = pSession;
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
    } else {
      Logger.error("Error: Only SQL imports are supported for now; type = " + session.getSourceTypeId());
    }
  }
  
  
  public void importSQL() {
    
    ContentType cType = ContentType.getById(session.getTargetTypeId());
    if ((cType != ContentType.JSON) && (cType != ContentType.CSV) &&
        (cType != ContentType.YAML) && (cType != ContentType.TOML) &&
        (cType != ContentType.XML)) {
      Logger.error("Only supported export formats: CSV, JSON, YAML, TOML, XML");
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
    ConnManager.get().init(userDb.getUrl(), userDb.getUserId(), userDb.getUserPw());
    if (!ConnManager.get().create()) {
      Logger.error("Unable to connect to database " + userDb.getUrl());
      return;
    }
    
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
            final DBFileWriter writer = getFileWriter(cType, session.getTargetName(), rsmd);
            
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
    
    // Close the connection
    ConnManager.get().close();
  }
  
  
  private static DBFileWriter getFileWriter(final ContentType cType,
                                            final String outType,
                                            final ResultSetMetaData rsmd) {
    // Create the appropriate file writer object
    if (cType == ContentType.JSON) {
      return new JsonDBFileWriter(outType, rsmd);
    } else if (cType == ContentType.CSV) {
      return new CsvDBFileWriter(outType, rsmd);
    } else if (cType == ContentType.YAML) {
      return new YamlDBFileWriter(outType, rsmd);
    } else if (cType == ContentType.TOML) {
      return new TomlDBFileWriter(outType, rsmd);
    } else {
      return new XmlDBFileWriter(outType, rsmd);
    }
  }
}
