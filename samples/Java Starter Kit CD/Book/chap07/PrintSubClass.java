/* subclass for testing method overrides */

class PrintSubClass extends PrintClass {
  int z = 3;

  public static void main (String args[]) {
    PrintSubClass obj = new PrintSubClass();
    obj.printMe();
  }

}














