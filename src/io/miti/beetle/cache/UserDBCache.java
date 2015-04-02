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
	
	/**
	 * Load the cache.
	 */
	public void loadCache() {
		if (list != null) {
			return;
		}
		
		list = UserDb.getList("order by ID");
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
}
