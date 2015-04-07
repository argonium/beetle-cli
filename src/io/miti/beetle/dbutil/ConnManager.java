package io.miti.beetle.dbutil;

import io.miti.beetle.model.DbType;
import io.miti.beetle.util.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class ConnManager
{
  private static final String FILENAME = "connhistory.txt";

  private static final ConnManager mgr = new ConnManager();

  private String url = null;
  private String user = null;
  private String pw = null;
  
  private Set<String> loadedClasses = new HashSet<String>(10);

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
      BufferedReader in = new BufferedReader(new InputStreamReader(
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
      UrlInfo urlInfo = UrlInfo.createFromString(url);
      conn = DriverManager.getConnection(urlInfo.url, user, pw);
      if (conn == null) {
        System.err.println("Error: The generated connection is null");
      } else {
        // Connect to a schema
        connectToSchema(urlInfo.schema, conn);
      }
    } catch (SQLException e) {
      System.err.println("Exception in connection: " + e.getMessage());
    }
  }


  private static void connectToSchema(String schema, final Connection conn) {
    if ((schema == null) || schema.trim().isEmpty()) {
      return;
    }

    Statement statement = null;
    try {
      statement = conn.createStatement();
      statement.execute("set search_path to '" + schema + "'");
    } catch (SQLException ex) {
      ex.printStackTrace();
    } finally {
      try {
        statement.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }


  @Override
  public String toString() {
    return "URL: " + url + ", User: " + user + ", PW: " + pw;
  }


  public boolean addDriverClass(final DbType dbType) {
    if (dbType == null) {
      Logger.error("No DB type found for the selected database");
      return false;
    } else if ((dbType.getJarName() == null) || dbType.getJarName().isEmpty()) {
      Logger.error("No JAR file specified for the database type");
      return false;
    } else if ((dbType.getDriver() == null) || dbType.getDriver().isEmpty()) {
      Logger.error("No driver class specified for the database type");
      return false;
    }
    
    // See if we've already loaded this class
    if (loadedClasses.contains(dbType.getDriver())) {
      return true;
    }
    
    // See if the JAR file exists
    final File jar = new File(dbType.getJarName());
    if (!jar.exists() || !jar.isFile()) {
      Logger.error("The JAR file was not found: " + dbType.getJarName());
      return false;
    }
    
    // Load the class
    boolean result = false;
    try {
      // Add the JAR file to the classpath
      URLClassLoader loader = URLClassLoader.newInstance(new URL[] {jar.toURI().toURL()});
      
      // Load the class
      Class.forName(dbType.getDriver(), true, loader);
      
      // If we reach here, the result is true
      result = true;
      
      // Store the loaded class as being loaded in the classpath
      loadedClasses.add(dbType.getDriver());
    } catch (ClassNotFoundException e) {
      Logger.error("Error loading class " + dbType.getDriver() + ": " + e.getMessage());
    } catch (MalformedURLException mue) {
      Logger.error("Malformed URL exception loading class " + dbType.getDriver() + ": " + mue.getMessage());
    }
    
    return result;
  }
}
