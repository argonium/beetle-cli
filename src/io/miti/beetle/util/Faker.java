package io.miti.beetle.util;

import java.util.Random;

public class Faker
{
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
}
