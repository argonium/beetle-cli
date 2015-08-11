package io.miti.beetle.util;

import java.util.List;

public enum FakeType {
  BOOL, BCONST, INT, ID, ICONST, DOUBLE, DCONST, SCONST, CAR, CITY, COLOR,
  COUNTRY, DATE, DATETIME, EMAIL, FIRSTNAME, FULLNAME, GENDER, IPADDRESS,
  MARITALSTATUS, PHONE, SSN, STATE, STREETADDRESS, SURNAME, TIME, WORD, ZIPCODE,
  IRANGE, DRANGE, WORDLIST;
  
  @SuppressWarnings("unchecked")
  public static Object getValue(final FakeNode node) {
    switch (node.getFunc()) {
      case BOOL: return Boolean.valueOf(Faker.getBoolean());
      case BCONST: return node.getData();
      case INT: return Long.valueOf((long) Faker.getRandomInteger(100));
      case ID: return Long.valueOf((long) Faker.getId());
      case ICONST: return node.getData();
      case IRANGE: { LongPoint lp = (LongPoint) node.getData(); return Long.valueOf((long) Faker.getRandomLong(lp.getX(), lp.getY())); }
      case DOUBLE: return Faker.getRandomDouble(0.0, 100.0);
      case DCONST: return node.getData();
      case DRANGE: { DoublePoint dp = (DoublePoint) node.getData(); return Double.valueOf((double) Faker.getRandomDouble(dp.getX(), dp.getY())); }
      case SCONST: return node.getData();
      case CAR: return Faker.getCar();
      case CITY: return Faker.getCity();
      case COLOR: return Faker.getColor();
      case COUNTRY: return Faker.getCountry();
      case DATE: return Faker.getDate();
      case DATETIME: return Faker.getDateTime();
      case EMAIL: return Faker.getEmail();
      case FIRSTNAME: return Faker.getFirstName();
      case FULLNAME: return Faker.getFullName();
      case GENDER: return Faker.getGender();
      case IPADDRESS: return Faker.getIPAddress();
      case MARITALSTATUS: return Faker.getMaritalStatus();
      case PHONE: return Faker.getPhoneNumber();
      case SSN: return Faker.getSSN();
      case STATE: return Faker.getState();
      case STREETADDRESS: return Faker.getStreetAddress();
      case SURNAME: return Faker.getSurname();
      case TIME: return Faker.getTime();
      case WORD: return Faker.getWord(4, 7, false);
      case ZIPCODE: return Faker.getZipCode();
      case WORDLIST: return Faker.getRandomWord((List<String>) node.getData());
    }
    
    // Function was not found
    return null;
  }
  
  public static FakeType getFunc(final String func) {
    if (func.equals("bool")) {
      return BOOL;
    } else if (func.equals("bconst")) {
      return BCONST;
    } else if (func.equals("int")) {
      return INT;
    } else if (func.equals("id")) {
      return ID;
    } else if (func.equals("iconst")) {
      return ICONST;
    } else if (func.equals("double")) {
      return DOUBLE;
    } else if (func.equals("dconst")) {
      return DCONST;
    } else if (func.equals("sconst")) {
      return SCONST;
    } else if (func.equals("car")) {
      return CAR;
    } else if (func.equals("city")) {
      return CITY;
    } else if (func.equals("color")) {
      return COLOR;
    } else if (func.equals("country")) {
      return COUNTRY;
    } else if (func.equals("date")) {
      return DATE;
    } else if (func.equals("datetime")) {
      return DATETIME;
    } else if (func.equals("email")) {
      return EMAIL;
    } else if (func.equals("firstname")) {
      return FIRSTNAME;
    } else if (func.equals("fullname")) {
      return FULLNAME;
    } else if (func.equals("gender")) {
      return GENDER;
    } else if (func.equals("ipaddress")) {
      return IPADDRESS;
    } else if (func.equals("maritalstatus")) {
      return MARITALSTATUS;
    } else if (func.equals("phone")) {
      return PHONE;
    } else if (func.equals("ssn")) {
      return SSN;
    } else if (func.equals("state")) {
      return STATE;
    } else if (func.equals("streetaddress")) {
      return STREETADDRESS;
    } else if (func.equals("surname")) {
      return SURNAME;
    } else if (func.equals("time")) {
      return TIME;
    } else if (func.equals("word")) {
      return WORD;
    } else if (func.equals("zip")) {
      return ZIPCODE;
    } else if (func.equals("irange")) {
      return IRANGE;
    } else if (func.equals("drange")) {
      return DRANGE;
    } else if (func.equals("wordlist")) {
      return WORDLIST;
    }
    
    return null;
  }
}
