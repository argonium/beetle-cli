package io.miti.beetle.cache;

import io.miti.beetle.model.DbType;

import java.util.List;

public final class DBTypeCache {
	
	/** The one instance of the cache object. */
	private static final DBTypeCache cache;
	
	/** The list of objects in the cache. */
	private List<DbType> list = null;
	
	static {
		cache = new DBTypeCache();
	}
	
	private DBTypeCache() {
		super();
	}
	
	public static DBTypeCache get() {
		return cache;
	}
	
	public void loadCache() {
		if (list != null) {
			return;
		}
		
		list = DbType.getList();
	}
	
	public void printList() {
		if (list == null) {
			System.out.println("(List is null)");
			return;
		}
		
		System.out.println("DbType Objects:");
		for (DbType obj : list) {
			System.out.println("  " + obj.toString());
		}
	}
}
