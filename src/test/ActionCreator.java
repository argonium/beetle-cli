package test;

import java.util.GregorianCalendar;

import io.miti.beetle.util.Faker;
import io.miti.beetle.util.FileBuilder;

public class ActionCreator
{
  public static long getDate(final int min, final int max) {

    GregorianCalendar gc = new GregorianCalendar();
    int year = randBetween(min, max);
    gc.set(gc.YEAR, year);
    int dayOfYear = randBetween(1, gc.getActualMaximum(gc.DAY_OF_YEAR));
    gc.set(gc.DAY_OF_YEAR, dayOfYear);
    // System.out.println(gc.get(gc.YEAR) + "-" + gc.get(gc.MONTH) + "-" + gc.get(gc.DAY_OF_MONTH));
    return gc.getTime().getTime();
  }

  public static int randBetween(int start, int end) {
      return start + (int)Math.round(Math.random() * (end - start));
  }

  public static void main(String[] args) {
    
    FileBuilder fb = new FileBuilder("actions.csv");
    fb.append("id,name,assignee,createdDate,dueDate")
      .appendEOL();
    
    for (int i = 0; i < 100; ++i) {
      long created = getDate(2005, 2008);
      long due = getDate(2009, 2013);
      fb.append(i + 1).append(',').append(Faker.getWord(5, 7, true))
        .append(' ').append(Faker.getWord(4,6, false)).append(',')
        .append(Faker.getFullName()).append(',').append(created)
        .append(',').append(due).appendEOL();
    }
    
    fb.close();
  }
}
