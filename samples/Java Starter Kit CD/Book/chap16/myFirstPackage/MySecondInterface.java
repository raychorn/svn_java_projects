package  myFirstPackage; // added

public interface  MySecondInterface {
    public static final int  theAnswer = 42;  // both lines OK
    public abstract     int  lifeTheUniverseAndEverything();

    long  bingBangCounter = 0;  // OK, becomes public, static, final
    long  ageOfTheUniverse();   // OK, becomes public and abstract

//    protected int  aConstant;   // not OK
    private   int  getAnInt();  // not OK
}
