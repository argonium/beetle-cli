package io.miti.beetle.util;

public final class NodeInfo
{
  private String name = null;
  private Class<?> clazz = null;
  
  public NodeInfo() {
    super();
  }
  
  public NodeInfo(final String sName, final Class<?> pClazz) {
    name = sName;
    clazz = pClazz;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Class<?> getClazz() {
    return clazz;
  }

  public void setClazz(Class<?> clazz) {
    this.clazz = clazz;
  }

  @Override
  public String toString() {
    return "Name=" + name + ", clazz=" + clazz;
  }
}
