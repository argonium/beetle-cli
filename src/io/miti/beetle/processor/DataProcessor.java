package io.miti.beetle.processor;

import io.miti.beetle.cache.DBTypeCache;
import io.miti.beetle.cache.UserDBCache;
import io.miti.beetle.dbutil.ConnManager;
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
      Logger.error("Error: Only SQL imports are supported for now");
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
    
    // TODO Get the metadata
    
    // TODO Configure the data target
    if (session.getTargetTypeId() == ContentType.CSV.getId()) {
      // TODO
    } else if (session.getTargetTypeId() == ContentType.JSON.getId()) {
      // TODO
    }
    
    // TODO Export to CSV or JSON
    
    // Close the connection
    ConnManager.get().close();
  }
}
