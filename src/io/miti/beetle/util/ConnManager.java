package io.miti.beetle.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class ConnManager {
	
	private static final String FILENAME = "connhistory.txt";
	
	private static final ConnManager mgr = new ConnManager();
	
	private String url = null;
	private String user = null;
	private String pw = null;
	
	private Set<String> history = new java.util.LinkedHashSet<String>(5);
	
	private Connection conn = null;
	
	private ConnManager() {
		loadHistory();
	}
	
	public static ConnManager get() {
		return mgr;
	}
	
	public void init(final String sUrl) {
		url = sUrl;
	}
	
	public void init(final String sUrl, final String sUser, final String sPass) {
		url = sUrl;
		user = sUser;
		pw = sPass;
		
		if (url != null) {
			addToHistory();
		}
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(final String sUser) {
		user = sUser;
	}
	
	public void setPassword(final String sPass) {
		pw = sPass;
	}
	
	public Connection getConn() {
		if (conn == null) {
			initConnection();
		}
		
		return conn;
	}
	
	public boolean isValid() {
		return (conn != null);
	}
	
	public boolean isValid(final int timeout) {
		if (conn == null) {
			return false;
		}
		
		boolean timed = false;
		try {
			timed = conn.isValid(timeout);
		} catch (SQLException e) {
			timed = false;
		}
		
		return timed;
	}
	
	public void close() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			conn = null;
			url = null;
			user = null;
			pw = null;
		}
	}
	
	public boolean isNull() {
		return (conn == null);
	}
	
	public boolean create() {
		close();
		initConnection();
		return (conn != null);
	}
	
	private void addToHistory() {
		
		// Save the new URL
		history.add(url);
		
		// Update the file
		writeHistoryFile();
	}
	
	private void writeHistoryFile() {
		
		if ((history == null) || history.isEmpty()) {
			return;
		}
		
		final File file = new File(FILENAME);
		if (file.exists() && file.isDirectory()) {
			return;
		}
		
		if (file.exists()) {
			file.delete();
		}
		
		List<String> names = new ArrayList<String>(history.size());
		for (String item : history) {
			names.add(item);
		}
		Collections.sort(names);
		
		try {
			PrintWriter writer = new PrintWriter(file, "UTF-8");
			for (String name : names) {
				writer.println(name);
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public Iterator<String> getHistory() {
		return history.iterator();
	}
	
	public boolean hasHistory() {
		return (!history.isEmpty());
	}
	
	private void loadHistory() {
		File file = new File(FILENAME);
		if (!file.exists() || file.isDirectory()) {
			return;
		}
		
		try {
			BufferedReader in = new BufferedReader(
					   new InputStreamReader(
			                      new FileInputStream(file), "UTF8"));
			String line = null;
			while ((line = in.readLine()) != null) {
				if (!line.startsWith("#")) {
					history.add(line);
				}
			}
			
			in.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void initConnection() {
		if (conn != null) {
			return;
		}
		
		try {
			conn = DriverManager.getConnection(url, user, pw);
			if (conn == null) {
				System.err.println("Error: The generated connection is null");
			}
		} catch (SQLException e) {
			System.err.println("Exception in connection: " + e.getMessage());
		}
	}
	
	@Override
	public String toString() {
		return "URL: " + url + ", User: " + user + ", PW: " + pw;
	}
}
