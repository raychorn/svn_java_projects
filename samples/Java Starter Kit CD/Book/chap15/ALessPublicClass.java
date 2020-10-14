public class  ALessPublicClass {
    int     aPackageInt    = 2;
    String  aPackageString = "a 1 and a ";

    float   aPackageMethod() {     // no access modifier means "package"
	return 0.0F; // added
    }
}

/* public */ class  AClassInTheSamePackage {
    public  void testUse() {
        ALessPublicClass  aLPC = new ALessPublicClass();

        System.out.println(aLPC.aPackageString + aLPC.aPackageInt);
        aLPC.aPackageMethod();           // all of these are A.O.K.
    }
}
