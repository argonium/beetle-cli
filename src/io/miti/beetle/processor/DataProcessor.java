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
    if ((cType != ContentType.JSON) && (cType != ContentType.CSV)) {
      Logger.error("Only export to JSON and CSV are supported");
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
      stmt = ConnManager.get().getConn().prepareStatement(session.getSourceName());
      ResultSet rs = stmt.getResultSet();
      // TODO Test this
      ResultSetMetaData rsmd = rs.getMetaData();
      final int colCount = rsmd.getColumnCount();
      // TODO Delete this
      System.out.println("Column count = " + colCount);
      
      // Configure the data target
      final DBFileWriter writer = (cType == ContentType.JSON) ? new JsonDBFileWriter(session.getTargetName(), rsmd) :
                                     new CsvDBFileWriter(session.getTargetName(), rsmd);
      
      // Write the header
      writer.writeHeader();
      
      // Iterate over the data for exporting
      Database.executeSelect(session.getSourceName(), writer);
      
      // Write the footer
      writer.writeFooter();
      
      // Close things
      rs.close();
      stmt.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    
    // Close the connection
    ConnManager.get().close();
  }
}
