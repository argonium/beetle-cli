package io.miti.beetle.cache;

import io.miti.beetle.model.UserDb;

import java.util.List;

public final class UserDBCache {
  /** The one instance of the cache object. */
  private static final UserDBCache cache;

  /** The list of objects in the cache. */
  private List<UserDb> list = null;

  /** Instantiate the cache. */
  static {
    cache = new UserDBCache();
  }


  /**
   * Private default constructor.
   */
  private UserDBCache() {
    super();
  }


  /**
   * Get the cache.
   * 
   * @return the data cache
   */
  public static UserDBCache get() {
    return cache;
  }


  public List<UserDb> getList() {
    return list;
  }


  /**
   * Load the cache.
   */
  public void loadCache() {
    if (list != null) {
      return;
    }

    list = UserDb.getList("order by ID");
  }


  public void add(final UserDb userDb) {
    list.add(userDb);
  }
  
  
  /**
   * Find a matching object by ID.
   * 
   * @param id the ID to search for a match on
   * @return the object (in the list) matching on ID)
   */
  public UserDb find(final int id) {
    if ((list == null) || list.isEmpty()) {
      return null;
    }
    
    UserDb match = null;
    for (UserDb obj : list) {
      if (obj.getId() == id) {
        match = obj;
        break;
      }
    }
    
    return match;
  }
  
  
  /**
   * Print the list of objects in the cache.
   */
  public void printList() {
    if (list == null) {
      System.out.println("(List is null)");
      return;
    }

    System.out.println("UserDb Objects:");
    for (UserDb obj : list) {
      System.out.println("  " + obj.toString());
    }
  }


  public boolean remove(int id) {
    if (list == null) {
      return false;
    }
    
    // Find the object matching on ID
    boolean found = false;
    final int size = list.size();
    for (int i = 0; i < size; ++i) {
      final UserDb udb = list.get(i);
      if (udb.getId() == id) {
        // Remove the object from the cache and the database
        list.remove(i);
        udb.delete();
        found = true;
        break;
      }
    }
    
    return found;
  }
}
