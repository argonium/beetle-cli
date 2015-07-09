package io.miti.beetle.processor;

public final class BackupPrefs
{
  private static final String[] tables = new String[]{"db_type", "app_property",
    "user_db", "config", "hadoop", "session", "data_type", "header", "col_type"};
  
  public BackupPrefs() {
    super();
  }
  
  public boolean backupDatabase(final String fname) {
    // TODO
    return true;
  }
}
