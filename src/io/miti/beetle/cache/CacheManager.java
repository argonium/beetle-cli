package io.miti.beetle.cache;

public final class CacheManager {
  /**
   * Default constructor.
   */
  private CacheManager() {
    super();
  }
  
  
  /**
   * Load data into the various caches.
   */
  public static void loadCache() {

    DBTypeCache.get().loadCache();
    UserDBCache.get().loadCache();
    HadoopCache.get().loadCache();
    SessionCache.get().loadCache();
    DataTypeCache.get().loadCache();
  }
}
