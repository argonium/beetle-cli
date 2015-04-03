package io.miti.beetle.dbutil;

public final class UrlInfo {
	
	public String url = null;
	public String schema = null;

	public UrlInfo() {
	}
	
	public UrlInfo(final String sUrl, final String sSchema) {
		url = sUrl;
		schema = sSchema;
	}

	public static UrlInfo createFromString(String fullUrl) {
		
		UrlInfo rc = new UrlInfo();
		if (fullUrl == null) {
			return rc;
		}
		
		final int index = fullUrl.indexOf('?');
		if (index < 0) {
			rc.url = fullUrl;
			return rc;
		}
		
		rc.url = fullUrl.substring(0, index);
		rc.schema = fullUrl.substring(index + 1);
		return rc;
	}
	
	@Override
	public String toString() {
		String msg = String.format("URL: %s  Schema: %s", url, schema);
		return msg;
	}
}
