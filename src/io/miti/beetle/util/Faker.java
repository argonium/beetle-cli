package io.miti.beetle.util;

import java.util.Date;
import java.util.Random;

public class Faker
{
  private static int id = 1;
  private static int idIncr = 1;
  
  private static final Random rn;
  
  private static final String[] emails = {"aol.com", "fastmail.com", "gmail.com",
    "gmx.net", "gmx.com", "hushmail.com", "icloud.com", "lycos.com", "mail.com",
    "outlook.com", "protonmail.ch", "yahoo.com", "hotmail.com"};

  static {
    rn = new Random();
  }


  private Faker() {
    super();
  }
  
  public static int getId() {
    final int val = id;
    id += idIncr;
    return val;
  }
  
  public static void setId(final int nId) {
    id = nId;
  }
  
  public static void setId(final int nId, final int nIdIncr) {
    id = nId;
    idIncr = nIdIncr;
  }
  
  public static String getEmail() {
    final StringBuilder sb = new StringBuilder(25);
    sb.append(getWord(5, 10, false)).append("@").append(getMailDomain());
    return sb.toString();
  }
  
  public static String getMailDomain() {
    final int value = rn.nextInt(emails.length);
    return emails[value];
  }
  
  public static String getPhoneNumber() {
    StringBuilder sb = new StringBuilder(12);
    sb.append(getPhoneAreaCode()).append("-").append(getPhoneExchange()).append("-");
    
    final String str = Integer.toString(getRandom(0, 9999));
    sb.append(Utility.padString(str, 4, '0'));
    return sb.toString();
  }
  
  public static String getPhoneAreaCode() {
    StringBuilder sb = new StringBuilder(3);
    sb.append(Integer.toString(getRandom(2, 9))).append(Integer.toString(getRandom(10, 89)));
    return sb.toString();
  }
  
  public static String getPhoneExchange() {
    StringBuilder sb = new StringBuilder(3);
    sb.append(Integer.toString(getRandom(2, 9))).append(Integer.toString(getRandom(20, 99)));
    return sb.toString();
  }


  public static String getGender() {
    final int value = rn.nextInt(2);
    return ((value < 1) ? "Male" : "Female");
  }


  public static boolean getBoolean() {
    final int value = rn.nextInt(2);
    return (value < 1);
  }
  
  
  public static String getZipCode() {
    final int num = getRandom(1, 99999);
    final String value = Utility.padString(Integer.toString(num), 5, '0');
    return value;
  }


  public static String getStreetSuffix() {
    final int value = rn.nextInt(8);
    switch (value) {
    case 0:
      return "St.";
    case 1:
      return "Ave.";
    case 2:
      return "Blvd.";
    case 3:
      return "Way";
    case 4:
      return "Ct.";
    case 5:
      return "Dr.";
    case 6:
      return "Ln.";
    default:
    case 7:
      return "Rd.";
    }
  }


  public static String getIPAddress() {
    final int val1 = rn.nextInt(256);
    final int val2 = rn.nextInt(256);
    final int val3 = rn.nextInt(256);
    final int val4 = rn.nextInt(256);
    final String result = val1 + "." + val2 + "." + val3 + "." + val4;
    return result;
  }
  
  
  public static String getColor() {
    return Colors.getRandomColor();
  }


  public static String getSSN() {
    // Generate the 3 numbers
    final int val1 = rn.nextInt(1000);
    final int val2 = rn.nextInt(100);
    final int val3 = rn.nextInt(10000);
    
    // Left-pad the numbers with zeros
    final String result = Utility.padString(Integer.toString(val1), 3, '0') +
        "-" + Utility.padString(Integer.toString(val2), 2, '0') + "-" +
        Utility.padString(Integer.toString(val3), 4, '0');
    
    // Return the string
    return result;
  }


  public static String getWord(final int minLength, final int maxLength,
      final boolean capitalizeFirstLetter) {
    if ((minLength < 0) || (maxLength < 0) || (maxLength < minLength)) {
      throw new RuntimeException("Invalid word length specified");
    }
    
    // Compute the word length
    final int wordLength = getRandom(minLength, maxLength);
    
    // Generate the first character
    final int lowerBound = capitalizeFirstLetter ? 65 : 97;
    final int upperBound = lowerBound + 25;
    char ch = (char) getRandom(lowerBound, upperBound);
    StringBuilder sb = new StringBuilder(wordLength);
    sb.append(ch);
    
    // Generate the rest of the word
    for (int i = 1; i < wordLength; ++i) {
      ch = (char) getRandom(97, 122);
      sb.append(ch);
    }
    
    // Return the generated string
    return sb.toString();
  }


  public static String getMaritalStatus() {
    final int value = rn.nextInt(5);
    switch (value) {
    case 0:
      return "Single";
    case 1:
      return "Married";
    case 2:
      return "Divorced";
    case 3:
      return "Separated";
    default:
    case 4:
      return "Widowed";
    }
  }


  /**
   * Returns an integer between 0 (inclusive) and maxInt (exclusive).
   * 
   * @param maxInt
   * @return
   */
  public static int getRandomInteger(final int maxInt) {
    return rn.nextInt(maxInt);
  }


  /**
   * Get a random number between min and max, inclusive.
   * 
   * @param nMinValue
   * @param nMaxValue
   * @return
   */
  public static int getRandom(final int nMinValue, final int nMaxValue) {

    // TODO Use rn

    if (nMaxValue <= nMinValue) {
      return nMinValue;
    }

    double d = Math.random();
    double r = (double) (nMaxValue + 1 - nMinValue);

    int i = nMinValue + ((int) (d * r));

    return i;
  }
  
  public static String getTime() {
    final Date date = getDateInRange();
    String str = Utility.getTimeString(date);
    return str;
  }
  
  public static String getDate() {
    final Date date = getDateInRange();
    String str = Utility.getDateString(date);
    return str;
  }
  
  public static String getDateTime() {
    final Date date = getDateInRange();
    String str = Utility.getDateTimeString(date);
    return str;
  }
  
  public static Date getDateInRange() {
    
    // Set the upper and lower bounds for the random date
    final long min = -2208970800000L; // Jan 1, 1900
    final long max =  1609304400000L; // Dec 30, 2020
    final long value = min + ((long) (rn.nextDouble() * ((double) (max - min))));
    return new Date(value);
  }
  
  public static String getFullName() {
    return getFullName(getBoolean());
  }
  
  public static String getFullName(final boolean useMale) {
    StringBuilder sb = new StringBuilder(100);
    sb.append(useMale ? getMaleName() : getFemaleName())
      .append(' ').append(getSurname());
    return sb.toString();
  }
  
  public static String getStreetAddress() {
    final int num = getRandomInteger(99) + 1;
    StringBuilder sb = new StringBuilder(50);
    sb.append(Integer.toString(num)).append(" ")
      .append(getStreet()).append(" ")
      .append(getStreetSuffix());
    return sb.toString();
  }
  
  
  public static String getCountry() {
    return FakeCache.getRandomCountry();
  }
  
  
  public static String getCity() {
    return FakeCache.getRandomCity();
  }
  
  
  public static String getState() {
    return FakeCache.getRandomState();
  }
  
  
  public static String getStreet() {
    return FakeCache.getRandomStreet();
  }
  
  
  public static String getMaleName() {
    return FakeCache.getRandomMaleName();
  }
  
  
  public static String getFemaleName() {
    return FakeCache.getRandomFemaleName();
  }
  
  
  public static String getSurname() {
    return FakeCache.getRandomSurname();
  }
  
  
  public static void main(final String[] args) {
    System.out.println(getTime());
    System.out.println(getDate());
    System.out.println(getDateTime());
  }
}