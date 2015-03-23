package io.miti.beetle.cache;

public final class CacheManager {

	private CacheManager() {
		super();
	}
	
	public static void loadCache() {
		
		// TODO
		DBTypeCache.get().loadCache();
	}
}
