package io.miti.beetle.cache;

import io.miti.beetle.model.DataType;

import java.util.List;

public final class DataTypeCache {
	
	/** The one instance of the cache object. */
	private static final DataTypeCache cache;
	
	/** The list of objects in the cache. */
	private List<DataType> list = null;
	
	/** Instantiate the cache. */
	static {
		cache = new DataTypeCache();
	}
	
	/**
	 * Private default constructor.
	 */
	private DataTypeCache() {
		super();
	}
	
	/**
	 * Get the cache.
	 * 
	 * @return the data cache
	 */
	public static DataTypeCache get() {
		return cache;
	}
	
	public List<DataType> getList() {
		return list;
	}
	
	/**
	 * Load the cache.
	 */
	public void loadCache() {
		if (list != null) {
			return;
		}
		
		list = DataType.getList("order by ID");
	}
	
	/**
	 * Print the list of objects in the cache.
	 */
	public void printList() {
		if (list == null) {
			System.out.println("(List is null)");
			return;
		}
		
		System.out.println("DataType Objects:");
		for (DataType obj : list) {
			System.out.println("  " + obj.toString());
		}
	}
}
