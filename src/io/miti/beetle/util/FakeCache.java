package io.miti.beetle.util;

import java.util.List;

public final class FakeCache
{
  private static List<String> countries = null;
  private static List<String> states = null;
  private static List<String> cities = null;
  private static List<String> femaleNames = null;
  private static List<String> maleNames = null;
  private static List<String> surnames = null;
  private static List<String> streets = null;
  private static List<String> cars = null;
  
  private FakeCache() {
    super();
  }
  
  public static String getRandomCountry() {
    loadCountries();
    final int index = Faker.getRandomInteger(countries.size());
    return countries.get(index);
  }
  
  private static void loadCountries() {
    if (countries != null) {
      return;
    }
    
    countries = readFile("countries.txt");
  }
  
  public static String getRandomState() {
    loadStates();
    final int index = Faker.getRandomInteger(states.size());
    return states.get(index);
  }
  
  private static void loadStates() {
    if (states != null) {
      return;
    }
    
    states = readFile("us-states.txt");
  }
  
  public static String getRandomCity() {
    loadCities();
    final int index = Faker.getRandomInteger(cities.size());
    return cities.get(index);
  }
  
  private static void loadCities() {
    if (cities != null) {
      return;
    }
    
    cities = readFile("cities.txt");
  }
  
  public static String getRandomStreet() {
    loadStreets();
    final int index = Faker.getRandomInteger(streets.size());
    return streets.get(index);
  }
  
  private static void loadStreets() {
    if (streets != null) {
      return;
    }
    
    streets = readFile("streets.txt");
  }
  
  public static String getRandomMaleName() {
    loadMaleNames();
    final int index = Faker.getRandomInteger(maleNames.size());
    return maleNames.get(index);
  }
  
  private static void loadMaleNames() {
    if (maleNames != null) {
      return;
    }
    
    maleNames = readFile("male-names.txt");
  }
  
  public static String getRandomFemaleName() {
    loadFemaleNames();
    final int index = Faker.getRandomInteger(femaleNames.size());
    return femaleNames.get(index);
  }
  
  private static void loadFemaleNames() {
    if (femaleNames != null) {
      return;
    }
    
    femaleNames = readFile("female-names.txt");
  }
  
  public static String getRandomSurname() {
    loadSurnames();
    final int index = Faker.getRandomInteger(surnames.size());
    return surnames.get(index);
  }
  
  private static void loadSurnames() {
    if (surnames != null) {
      return;
    }
    
    surnames = readFile("surnames.txt");
  }
  
  public static String getRandomCar() {
    loadCars();
    final int index = Faker.getRandomInteger(cars.size());
    return cars.get(index);
  }
  
  private static void loadCars() {
    if (cars != null) {
      return;
    }
    
    cars = readFile("cars.txt");
  }

  private static List<String> readFile(final String filename) {
    final List<String> lines = Content.getContent(filename);
    return lines;
  }
}
