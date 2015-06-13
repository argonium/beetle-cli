package test;

import io.miti.beetle.console.LineConsole;
import io.miti.beetle.model.ContentType;
import io.miti.beetle.model.Session;
import io.miti.beetle.util.Logger;

public class TestLineConsole
{
  public TestLineConsole() {
    super();
  }


  public static void main(final String[] args) {
    
    io.miti.beetle.prefs.PrefsDatabase.initializeDatabase();
    io.miti.beetle.cache.CacheManager.loadCache();
    Logger.updateLogLevel(1);
    Session s = new Session();
    s.setSourceTypeId(ContentType.SQL.getId());
    s.setSourceId(3);
    s.setSourceName("select * from test1");
    s.setTargetTypeId(ContentType.JSON.getId());
    s.setTargetName("out2.json");
    // new DataProcessor(s).run();
    LineConsole lc = new LineConsole();
    lc.setSession(s);
    lc.describeTable("test1");
    // lc.printTables();
  }
}
