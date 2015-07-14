package io.miti.beetle.util;

public final class FakeNode
{
  private String name = null;
  private Class<?> clazz = null;
  private FakeType func = null;
  private Object data = null;
  
  public FakeNode() {
    super();
  }
  
  public FakeNode(final String sName, final Class<?> pClazz) {
    name = sName;
    clazz = pClazz;
  }
  
  public FakeNode(final String sName, final Class<?> pClazz,
                  final FakeType pFunc, final Object pData) {
    name = sName;
    clazz = pClazz;
    func = pFunc;
    data = pData;
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
  
  public FakeType getFunc() {
    return func;
  }
  
  public void setFunc(final FakeType pFunc) {
    func = pFunc;
  }
  
  public Object getData() {
    return data;
  }
  
  public void setData(final Object pData) {
    data = pData;
  }

  public String getTypeAsJavaClass() {
    if (clazz.equals(Boolean.class)) {
      return "boolean";
    } else if (clazz.equals(Double.class)) {
      return "double";
    } else if (clazz.equals(Long.class)) {
      return "long";
    }
    
    return "String";
  }
}
