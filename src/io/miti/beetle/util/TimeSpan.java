package io.miti.beetle.util;

import java.text.DecimalFormat;

public final class TimeSpan
{
  private static final long MSECS_PER_MIN  = 60000L;
  private static final long MSECS_PER_HOUR = MSECS_PER_MIN * 60L;
  private static final long MSECS_PER_DAY  = MSECS_PER_HOUR * 24L;
  private static final long MSECS_PER_WEEK  = MSECS_PER_DAY * 7L;
  
  public static String millisToTimeSpan(long lMillis)
  {
    // Declare our string buffer
    StringBuffer buf = new StringBuffer(100);
    
    // Check for zero and negative values
    if (lMillis <= 0L)
    {
      // The value is either illegal, or zero, so return
      buf.append("0 seconds");
      return buf.toString();
    }
    
    // Get the number of weeks
    final long lWeeks = (long) (lMillis / MSECS_PER_WEEK);
    
    // Update lMillis with the remainder
    lMillis = lMillis % MSECS_PER_WEEK;
    
    // Get the number of days
    final long lDays = (long) (lMillis / MSECS_PER_DAY);
    
    // Update lMillis with the remainder
    lMillis = lMillis % MSECS_PER_DAY;
    
    // Get the number of hours
    final long lHours = (long) (lMillis / MSECS_PER_HOUR);
    
    // Update lMillis with the remainder
    lMillis = lMillis % MSECS_PER_HOUR;
    
    // Get the number of minutes
    final long lMinutes = (long) (lMillis / MSECS_PER_MIN);
    
    // Update lMillis with the remainder
    lMillis = lMillis % MSECS_PER_MIN;
    
    // Get the number of seconds
    final float fSeconds = (float) (((float)lMillis) / 1000.0F);
    
    // Now generate the string. First check if there are any weeks.
    if (lWeeks > 0L)
    {
      // Add the number and unit
      buf.append(Long.toString(lWeeks)).append(" week");
      
      // Make the unit plural, if necessary
      if (lWeeks > 1L) { buf.append('s'); }
    }
    
    // Check if there are any days.
    if (lDays > 0L)
    {
      // Append a leading comma, if necessary, and then add the number and unit
      if (buf.length() > 0) { buf.append(", "); }
      buf.append(Long.toString(lDays)).append(" day");
      
      // Make the unit plural, if necessary
      if (lDays > 1L) { buf.append('s'); }
    }
    
    // Check if there are any hours.
    if (lHours > 0L)
    {
      // Append a leading comma, if necessary, and then add the number and unit
      if (buf.length() > 0) { buf.append(", "); }
      buf.append(Long.toString(lHours)).append(" hr");
      
      // Make the unit plural, if necessary
      if (lHours > 1L) { buf.append('s'); }
    }
    
    // Check if there are any minutes.
    if (lMinutes > 0L)
    {
      // Append a leading comma, if necessary, and then add the number and unit
      if (buf.length() > 0) { buf.append(", "); }
      buf.append(Long.toString(lMinutes)).append(" min");
      
      // Make the unit plural, if necessary
      if (lMinutes > 1L) { buf.append('s'); }
    }
    
    // Check if there are any seconds.
    if (Float.compare(fSeconds, 0.0F) > 0)
    {
      // Append a leading comma, if necessary
      if (buf.length() > 0) { buf.append(", "); }
      
      // Format it because it's a floating point number
      DecimalFormat df = new DecimalFormat();
      df.setDecimalSeparatorAlwaysShown(false);
      df.setMaximumFractionDigits(3);
      buf.append(df.format((double) fSeconds)).append(" sec");
      
      // Make the unit plural, if necessary (if the number is anything but 1.0)
      if (Float.compare(fSeconds, 1.0F) != 0) { buf.append('s'); }
    }
    
    // Return the string
    return buf.toString();
  }
  
  
  public static void main(String[] args)
  {
    //System.out.println(millisToTimeSpan(MSECS_PER_DAY * 2 + 120000L));
    //System.out.println(millisToTimeSpan(MSECS_PER_DAY));
    System.out.println(millisToTimeSpan(2411239847L));
    System.out.println(millisToTimeSpan(411239847L));
    //System.out.println(millisToTimeSpan(411240000L));
    //System.out.println(millisToTimeSpan(MSECS_PER_HOUR));
    //System.out.println(millisToTimeSpan(MSECS_PER_MIN));
    //System.out.println(millisToTimeSpan(0L));
    //System.out.println(millisToTimeSpan(1000L));
    //System.out.println(millisToTimeSpan(100L));
    //System.out.println(millisToTimeSpan(10L));
    //System.out.println(millisToTimeSpan(1L));
    //System.out.println(millisToTimeSpan(1001L));
  }
}
