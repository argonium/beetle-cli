package io.miti.beetle.model;

public enum ContentType {
  SQL(1), SQL_FILE(2), HADOOP(3), CSV(4), JSON(5), YAML(6), TEXT(7), TOML(8), XML(9), FAKE(10);
  
  private int id = -1;
  
  ContentType(final int nId) {
    id = nId;
  }
  
  public int getId() {
    return id;
  }
  
  public static ContentType getById(final int pId) {
    ContentType result = null;
    for (ContentType ct : values()) {
      if (ct.id == pId) {
        result = ct;
        break;
      }
    }
    
    return result;
  }
}
