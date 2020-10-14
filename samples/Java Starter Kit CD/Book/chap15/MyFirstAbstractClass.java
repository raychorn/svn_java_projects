public abstract class  MyFirstAbstractClass {
    int  anInstanceVariable;

    public abstract int  aMethodMyNonAbstractSubclassesMustImplement();

    public void  doSomething() {

    }
}

/* public */ class  AConcreteSubClass extends MyFirstAbstractClass {
    public int  aMethodMyNonAbstractSubclassesMustImplement() {
	return 0; // added
    }
}


/* public */ class  AbstractTester {		// added
    public static void main(String argv[]) {

//        Object  a = new MyFirstAbstractClass();    // illegal, is abstract
        Object  c = new AConcreteSubClass();       // OK, a concrete subclass

    }
}
