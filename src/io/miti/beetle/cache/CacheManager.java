package io.miti.beetle.cache;

public final class CacheManager
{
  private CacheManager() {
    super();
  }


  public static void loadCache() {

    DBTypeCache.get().loadCache();
    UserDBCache.get().loadCache();
    HadoopCache.get().loadCache();
    SessionCache.get().loadCache();
    DataTypeCache.get().loadCache();
  }
}
