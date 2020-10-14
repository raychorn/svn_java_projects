/* superclass for testing method overrides */

class PrintClass {
  int x = 0;
  int y = 1;

  void printMe() {
    System.out.println("X is " + x + ", Y is " + y);
    System.out.println("I am an instance of the class" +
	   this.getClass().getName());
  }
}

