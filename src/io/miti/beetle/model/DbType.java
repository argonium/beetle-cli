/*
 * Java class for the DB_TYPE database table.
 * Generated on 22 Mar 2015 19:44:10 by DB2Java.
 */

package io.miti.beetle.model;

import io.miti.beetle.dbutil.FetchDatabaseRecords;
import io.miti.beetle.prefs.IIdentifiable;
import io.miti.beetle.prefs.IInsertable;
import io.miti.beetle.prefs.IUpdateable;
import io.miti.beetle.prefs.PrefsDatabase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Java class to encapsulate the DB_TYPE table.
 *
 * @version 1.0
 */
public final class DbType
	implements FetchDatabaseRecords, IUpdateable, IInsertable, IIdentifiable, Comparable<DbType>
{
  /**
   * The table column ID.
   */
  private int id;
  
  /**
   * The table column NAME.
   */
  private String name;
  
  /**
   * The table column REF.
   */
  private String ref;
  
  /**
   * The table column DRIVER.
   */
  private String driver;
  
  /**
   * The table column JAR_NAME.
   */
  private String jarName;
  
  
  /**
   * Default constructor.
   */
  public DbType()
  {
    super();
  }
  
  
  /**
   * Implement the FetchDatabaseRecords interface.
   * 
   * @param rs the result set to get the data from
   * @param listRecords the list of data to add to
   * @return the success of the operation
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public boolean getFields(final ResultSet rs,
                           final List listRecords)
  {
    // Default return value
    boolean bResult = false;
    
    // Set up our try/catch block
    try
    {
      // Iterate over the result set
      while (rs.next())
      {
        // Instantiate a new object
        DbType obj = new DbType();
        
        // Save the data in our object
        obj.id = rs.getInt(1);
        obj.name = rs.getString(2);
        obj.ref = rs.getString(3);
        obj.driver = rs.getString(4);
        obj.jarName = rs.getString(5);
        
        // Add to our list
        listRecords.add(obj);
      }
      
      // There was no error
      bResult = true;
    }
    catch (java.sql.SQLException sqle)
    {
      // Add the exception to the master list and save the
      // result as the error code
      System.err.println(sqle.getMessage());
    }
    
    // Return the result of the operation
    return bResult;
  }
  
  
  /**
   * Get all objects from the database.
   * 
   * @return a list of all objects in the database
   */
  public static List<DbType> getList()
  {
    return getList(null);
  }
  
  
  /**
   * Get all objects from the database.
   * 
   * @param whereClause the where clause for the select statement
   * @return a list of all objects in the database
   */
  public static List<DbType> getList(final String whereClause)
  {
    // This will hold the list that gets returned
    List<DbType> listData = new ArrayList<DbType>(100);
    
    // Build our query
    StringBuffer buf = new StringBuffer(100);
    buf.append("SELECT ID, NAME, REF, DRIVER, ");
    buf.append("JAR_NAME");
    buf.append(" from DB_TYPE");
    
    // Check if there's a where clause to append
    if (whereClause != null)
    {
      buf.append(" ").append(whereClause);
    }
    
    // Get all of the objects from the database
    boolean bResult = PrefsDatabase.executeSelect(buf.toString(), listData, new DbType());
    if (!bResult)
    {
      // An error occurred
      listData.clear();
      listData = null;
    }
    
    // Return the list
    return listData;
  }
  
  
  /**
   * Insert a record into the database.
   */
  public void insert()
  {
    StringBuilder sb = new StringBuilder(200);
    sb.append("INSERT into DB_TYPE (");
    sb.append("NAME, REF, DRIVER, JAR_NAME");
    sb.append(") values (?, ?, ?, ?)");
    
    PrefsDatabase.insert(sb.toString(), this, this);
  }
  
  
  /**
   * Set the parameter values in the INSERT statement.
   * 
   * @param ps the prepared statement
   * @throws java.sql.SQLException a database exception
   */
  @Override
  public void setInsertFields(final PreparedStatement ps)
    throws SQLException
  {
    ps.setString(1, name);
    ps.setString(2, ref);
    ps.setString(3, driver);
    ps.setString(4, jarName);
  }
  
  
  /**
   * Update a record in the database.
   */
  public boolean update()
  {
    StringBuilder sb = new StringBuilder(200);
    sb.append("UPDATE DB_TYPE set ");
    sb.append("NAME = ?, REF = ?, ");
    sb.append("DRIVER = ?, JAR_NAME = ? ");
    sb.append("where id = ?");
    boolean rc = PrefsDatabase.update(sb.toString(), this);
    return rc;
  }
  
  
  /**
   * Set the parameter values in the INSERT statement.
   * 
   * @param ps the prepared statement
   * @throws java.sql.SQLException a database exception
   */
  @Override
  public void setUpdateFields(final PreparedStatement ps)
    throws SQLException
  {
	ps.setString(1, name);
	ps.setString(2, ref);
	ps.setString(3, driver);
	ps.setString(4, jarName);
	ps.setInt(5, id);
  }
  
  
  /**
   * Delete the record in the database.
   */
  public void delete()
  {
    StringBuilder sb = new StringBuilder(200);
    sb.append("DELETE from DB_TYPE where id = ");
    sb.append(id);
    PrefsDatabase.delete(sb.toString());
  }
  
  
  /**
   * Get the value for id.
   *
   * @return the id
   */
  public int getId()
  {
    return id;
  }
  
  
  /**
   * Update the value for id.
   *
   * @param pId the new value for id
   */
  @Override
  public void setId(final int pId)
  {
    id = pId;
  }
  
  
  /**
   * Get the value for name.
   *
   * @return the name
   */
  public String getName()
  {
    return name;
  }
  
  
  /**
   * Update the value for name.
   *
   * @param pName the new value for name
   */
  public void setName(final String pName)
  {
    name = pName;
  }
  
  
  /**
   * Get the value for ref.
   *
   * @return the ref
   */
  public String getRef()
  {
    return ref;
  }
  
  
  /**
   * Update the value for ref.
   *
   * @param pRef the new value for ref
   */
  public void setRef(final String pRef)
  {
    ref = pRef;
  }
  
  
  /**
   * Get the value for driver.
   *
   * @return the driver
   */
  public String getDriver()
  {
    return driver;
  }
  
  
  /**
   * Update the value for driver.
   *
   * @param pDriver the new value for driver
   */
  public void setDriver(final String pDriver)
  {
    driver = pDriver;
  }
  
  
  /**
   * Get the value for jarName.
   *
   * @return the jarName
   */
  public String getJarName()
  {
    return jarName;
  }
  
  
  /**
   * Update the value for jarName.
   *
   * @param pJarName the new value for jarName
   */
  public void setJarName(final String pJarName)
  {
    jarName = pJarName;
  }


	@Override
	public String toString() {
		return "DbType [id=" + id + ", name=" + name + ", ref=" + ref + ", driver="
				+ driver + ", jarName=" + jarName + "]";
	}


	@Override
	public int compareTo(final DbType o) {
		return Integer.compare(id, o.id);
	}
}
