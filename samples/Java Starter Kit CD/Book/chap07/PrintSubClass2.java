/* subclass for testing method overrides */

class PrintSubClass2 extends PrintClass {
  int z = 3;

  void printMe() {
    System.out.println("X is " + x + ", Y is " + y + ", Z is " + z);
    System.out.println("I am an instance of the class " +
	this.getClass().getName());
  }

  public static void main (String args[]) {
    PrintSubClass2 obj = new PrintSubClass2();
    obj.printMe();
  }

}














