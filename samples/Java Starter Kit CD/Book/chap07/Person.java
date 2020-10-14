/* simple constructors */

class Person {
  String name;
  int age;	

  Person(String n, int a) {
    name = n;
    age = a;
  }

  void printPerson() {
    System.out.print("Hi, my name is " + name);
    System.out.println(". I am " + age + " years old.");
  }

  public static void main (String args[]) {
    Person p;

    p = new Person("Laura", 20);
    p.printPerson();
    System.out.println("--------");
    p = new Person("Tommy", 3);
    p.printPerson();
    System.out.println("--------");
  }
}


