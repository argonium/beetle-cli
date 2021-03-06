package io.miti.beetle.dbutil;

import io.miti.beetle.model.DbType;
import io.miti.beetle.util.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

public final class ConnManager
{
  /** Parameters of the method to add an URL to the System classes. */
  private static final Class<?>[] parameters = new Class[]{URL.class};

  private static final ConnManager mgr = new ConnManager();

  private String url = null;
  private String user = null;
  private String pw = null;
  
  private Set<String> loadedClasses = new HashSet<String>(10);

  private Connection conn = null;
  
  
  /**
   * Default constructor.
   */
  private ConnManager() {
    super();
  }
  
  
  /**
   * Get the single instance of this class.
   * 
   * @return the single instance of this class
   */
  public static ConnManager get() {
    return mgr;
  }
  
  
  /**
   * Initialize with a URL.
   * 
   * @param sUrl the JDBC URL
   */
  public void init(final String sUrl) {
    url = sUrl;
  }
  
  
  /**
   * Initialize with a JDBC URL, user name and password.
   * 
   * @param sUrl the JDBC URL
   * @param sUser the user name
   * @param sPass the password
   */
  public void init(final String sUrl, final String sUser, final String sPass) {
    url = sUrl;
    user = sUser;
    pw = sPass;
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
  
  
  /**
   * If the connection is null, attempt to connect now.
   * 
   * @return the connection
   */
  public Connection getConn() {
    if (conn == null) {
      initConnection();
    }

    return conn;
  }
  
  
  /**
   * Return whether the connection is not null.
   * 
   * @return if the connection is not null
   */
  public boolean isValid() {
    return (conn != null);
  }
  
  
  /**
   * Whether whether the connection is not null and valid for the timeout.
   * 
   * @param timeout the time (in seconds) to wait for the connection to be valid
   * @return whether the connection is valid for the timeout
   */
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
  
  
  /**
   * If the connection is not null, close it and null it out.
   */
  public void close() {
    if (conn != null) {
      try {
        conn.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }

      conn = null;
    }
  }


  /**
   * Close the connection (if open), and then connect.
   * 
   * @return whether the connection is not null
   */
  public boolean create() {
    close();
    initConnection();
    return (conn != null);
  }
  
  
  /**
   * If the connect is not null, connect now.
   */
  private void initConnection() {
    if (conn != null) {
      return;
    }

    try {
      final UrlInfo urlInfo = UrlInfo.createFromString(url);
      Logger.debug("Connecting to URL " + urlInfo.url);
      
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
  
  
  /**
   * Connect to a specific schema.
   * 
   * @param schema schema name
   * @param conn the connection
   * @return success flag
   */
  public static boolean connectToSchema(final String schema, final Connection conn) {
    if ((schema == null) || schema.trim().isEmpty()) {
      return true;
    }
    
    Logger.debug("Connecting to schema " + schema);
    Statement statement = null;
    boolean result = false;
    try {
      statement = conn.createStatement();
      statement.execute("set search_path to '" + schema + "'");
      result = true;
    } catch (SQLException ex) {
      Logger.error(ex);
    } finally {
      try {
        statement.close();
      } catch (SQLException e) {
        Logger.error(e);
      }
    }
    
    return result;
  }


  @Override
  public String toString() {
    return "URL: " + url + ", User: " + user + ", PW: " + pw;
  }


  /**
   * Add a JAR file to the class path and load the driver class.
   * 
   * @param dbType the type of database
   * @return the success flag
   */
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
      Logger.debug("Skipping the driver class since it's already loaded");
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
      // Print some debug information
      Logger.debug("Adding JAR file to classpath: " + jar.getAbsolutePath());
      
      // Add the JAR file to the classpath
      final URL fileUrl = jar.toURI().toURL();
      Logger.debug("Loading the JAR URL " + fileUrl.toString());
      try {
        addURL(fileUrl);
        result = true;
      } catch (IOException e) {
        result = false;
        Logger.error(e);
      }
      
      // Load the class
      if (result) {
        try {
          Logger.debug("Loading class " + dbType.getDriver());
          Class.forName(dbType.getDriver(), true, ClassLoader.getSystemClassLoader());
          
          // If we reach here, the result is true
          result = true;
          
          Logger.debug("JAR added and class loaded");
        } catch (ClassNotFoundException e) {
          result = false;
          Logger.error(e);
        }
      }
      
      // Store the loaded class as being loaded in the classpath
      if (result) {
        loadedClasses.add(dbType.getDriver());
      }
    } catch (MalformedURLException mue) {
      Logger.error("Malformed URL exception loading class " + dbType.getDriver() + ": " + mue.getMessage());
    }
    
    return result;
  }
  
  
  /**
   * Add the JAR file's URL to the classpath.
   * 
   * @param u the JAR URL to add
   * @throws IOException on error
   */
  public static void addURL(final URL u) throws IOException {
    final URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
    final Class<?> sysclass = URLClassLoader.class;
    try {
        Method method = sysclass.getDeclaredMethod("addURL", parameters);
        method.setAccessible(true);
        method.invoke(sysloader,new Object[]{ u }); 
    } catch (Exception e) {
        Logger.error(e);
        throw new IOException("Error, could not add URL to system classloader");
    }        
  }
}
