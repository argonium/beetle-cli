package io.miti.beetle.cache;

import io.miti.beetle.model.DbType;

import java.util.List;

public final class DBTypeCache {
	
	/** The one instance of the cache object. */
	private static final DBTypeCache cache;
	
	/** The list of objects in the cache. */
	private List<DbType> list = null;
	
	/** Instantiate the cache. */
	static {
		cache = new DBTypeCache();
	}
	
	/**
	 * Private default constructor.
	 */
	private DBTypeCache() {
		super();
	}
	
	/**
	 * Get the cache.
	 * 
	 * @return the data cache
	 */
	public static DBTypeCache get() {
		return cache;
	}
	
	/**
	 * Load the cache.
	 */
	public void loadCache() {
		if (list != null) {
			return;
		}
		
		list = DbType.getList("order by ID");
	}
	
	public List<DbType> getList() {
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
		
		System.out.println("DbType Objects:");
		for (DbType obj : list) {
			System.out.println("  " + obj.toString());
		}
	}
	
	public int getMatchingDbTypeId(final String url) {
		
		int matchingID = -1;
		for (DbType dbt : list) {
			if (dbt.matchesUrlRef(url)) {
				matchingID = dbt.getId();
				break;
			}
		}
		
		return matchingID;
	}

	public boolean clearJar(int id) {
		
		boolean result = false;
		if (list != null) {
			// Find the object matching on ID
			for (DbType dbt : list) {
				if (dbt.getId() == id) {
					dbt.setJarName(null);
					boolean updateRow = dbt.update();
					if (updateRow) {
						result = true;
					}
					break;
				}
			}
		}
		
		return result;
	}
}
