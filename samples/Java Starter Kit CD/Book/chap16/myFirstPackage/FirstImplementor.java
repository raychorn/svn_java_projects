package  myFirstPackage; // added

abstract class  FirstImplementor /* extends SomeClass */ implements MySecondInterface {
  // abstract added so we don't have to implement everything here
}

abstract class  SecondImplementor implements MyFirstInterface, MySecondInterface {
  // abstract added so we don't have to implement everything here
}
