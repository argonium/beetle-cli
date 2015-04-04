package io.miti.beetle.cache;

import io.miti.beetle.model.Hadoop;

import java.util.List;

public final class HadoopCache {
  /** The one instance of the cache object. */
  private static final HadoopCache cache;

  /** The list of objects in the cache. */
  private List<Hadoop> list = null;

  /** Instantiate the cache. */
  static {
    cache = new HadoopCache();
  }


  /**
   * Private default constructor.
   */
  private HadoopCache() {
    super();
  }


  /**
   * Get the cache.
   * 
   * @return the data cache
   */
  public static HadoopCache get() {
    return cache;
  }


  /**
   * Load the cache.
   */
  public void loadCache() {
    if (list != null) {
      return;
    }

    list = Hadoop.getList("order by ID");
  }


  public List<Hadoop> getList() {
    return list;
  }


  /**
   * Print the list of objects in the cache.
   */
  public void printList() {
    if (list == null) {
      System.out.println("(List is null)");
      return;
    }

    System.out.println("Hadoop Objects:");
    for (Hadoop obj : list) {
      System.out.println("  " + obj.toString());
    }
  }
}
