package io.miti.beetle.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class FileEntry
{
  final static SimpleDateFormat sdf;
  
  static
  {
    sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
  }
  
  // Dir?  Name   Size (blank if directory)  Last Modified  Owner
  private boolean isDir = false;
  private String name = null;
  private String len = null;
  private String lastModified = null;
  
  
  public FileEntry()
  {
    super();
  }
  
  
  public FileEntry(final boolean isDir, final String name, final long len,
                    final long lastModified)
  {
    super();
    this.isDir = isDir;
    this.name = name;
    if (isDir)
    {
      this.name += "/";
    }
    
    this.len = (isDir && (len == 0)) ? "--" : formatSize(len);
    this.lastModified = sdf.format(new Date(lastModified));
  }
  
  
  private static String formatSize(final long size) {
	return Utility.formatLong(size);
  }
  
  
  public boolean isDir() {
    return isDir;
  }


  public void setDir(boolean isDir) {
    this.isDir = isDir;
  }


  public String getName() {
    return name;
  }


  public void setName(String name) {
    this.name = name;
  }


  public String getLen() {
    return len;
  }


  public void setLen(long len) {
    this.len = Long.toString(len);
  }


  public String getLastModified() {
    return lastModified;
  }


  public void setLastModified(String lastModified) {
    this.lastModified = lastModified;
  }
}
