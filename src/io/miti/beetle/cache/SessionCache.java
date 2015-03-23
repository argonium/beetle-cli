package io.miti.beetle.cache;

import io.miti.beetle.model.Session;

import java.util.List;

public final class SessionCache {
	
	/** The one instance of the cache object. */
	private static final SessionCache cache;
	
	/** The list of objects in the cache. */
	private List<Session> list = null;
	
	/** Instantiate the cache. */
	static {
		cache = new SessionCache();
	}
	
	/**
	 * Private default constructor.
	 */
	private SessionCache() {
		super();
	}
	
	/**
	 * Get the cache.
	 * 
	 * @return the data cache
	 */
	public static SessionCache get() {
		return cache;
	}
	
	/**
	 * Load the cache.
	 */
	public void loadCache() {
		if (list != null) {
			return;
		}
		
		list = Session.getList();
	}
	
	/**
	 * Print the list of objects in the cache.
	 */
	public void printList() {
		if (list == null) {
			System.out.println("(List is null)");
			return;
		}
		
		System.out.println("Session Objects:");
		for (Session obj : list) {
			System.out.println("  " + obj.toString());
		}
	}
}
