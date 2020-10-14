public class  AProtectedClass {
    protected int     aProtectedInt    = 4;
    protected String  aProtectedString = "and a 3 and a ";

    protected float   aProtectedMethod() {
        return 0.0F; // added
    }
}

/* public */ class  AProtectedClassSubclass extends AProtectedClass {
    public  void testUse() {
        AProtectedClass  aPC = new AProtectedClass();

        System.out.println(aPC.aProtectedString + aPC.aProtectedInt);
        aPC.aProtectedMethod();           // all of these are A.O.K.
    }
}

/* public */ class  AnyClassInTheSamePackage {
    public  void testUse() {
        AProtectedClass  aPC = new AProtectedClass();

        System.out.println(aPC.aProtectedString + aPC.aProtectedInt);
        aPC.aProtectedMethod();           // NONE of these are legal
    }
}
