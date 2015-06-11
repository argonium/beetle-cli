package io.miti.beetle.util;

public enum Colors {
  Blue, Black, White, Green, Red, Cyan, Pink, Purple, Yellow, Magenta, Orange, Teal, Grey;
  
  public static String getRandomColor() {
    final int size = Colors.values().length;
    final int num = Faker.getRandomInteger(size);
    final Colors color = Colors.values()[num];
    return color.toString();
  }
}
