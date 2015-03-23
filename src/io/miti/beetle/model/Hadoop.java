/*
 * Java class for the HADOOP database table.
 * Generated on 22 Mar 2015 19:44:10 by DB2Java.
 */

package io.miti.beetle.model;

import io.miti.beetle.dbutil.FetchDatabaseRecords;
import io.miti.beetle.prefs.PrefsDatabase;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Java class to encapsulate the HADOOP table.
 *
 * @version 1.0
 */
public final class Hadoop implements FetchDatabaseRecords
{
  /**
   * The table column ID.
   */
  private int id;
  
  /**
   * The table column LABEL.
   */
  private String label;
  
  /**
   * The table column NAME.
   */
  private String name;
  
  /**
   * The table column URL.
   */
  private String url;
  
  
  /**
   * Default constructor.
   */
  public Hadoop()
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
        Hadoop obj = new Hadoop();
        
        // Save the data in our object
        obj.id = rs.getInt(1);
        obj.label = rs.getString(2);
        obj.name = rs.getString(3);
        obj.url = rs.getString(4);
        
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
  public static List<Hadoop> getList()
  {
    // This will hold the list that gets returned
    List<Hadoop> listData = new ArrayList<Hadoop>(100);
    
    // Build our query
    StringBuffer buf = new StringBuffer(100);
    buf.append("select ID, LABEL, NAME, URL ")
       .append("from HADOOP order by ID");
    
    // Get all of the objects from the database
    boolean bResult = PrefsDatabase.executeSelect(buf.toString(), listData, new Hadoop());
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
  public void setId(final int pId)
  {
    id = pId;
  }
  
  
  /**
   * Get the value for label.
   *
   * @return the label
   */
  public String getLabel()
  {
    return label;
  }
  
  
  /**
   * Update the value for label.
   *
   * @param pLabel the new value for label
   */
  public void setLabel(final String pLabel)
  {
    label = pLabel;
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
   * Get the value for url.
   *
   * @return the url
   */
  public String getUrl()
  {
    return url;
  }
  
  
  /**
   * Update the value for url.
   *
   * @param pUrl the new value for url
   */
  public void setUrl(final String pUrl)
  {
    url = pUrl;
  }


	@Override
	public String toString() {
		return "Hadoop [id=" + id + ", label=" + label + ", name=" + name
				+ ", url=" + url + "]";
	}
}
