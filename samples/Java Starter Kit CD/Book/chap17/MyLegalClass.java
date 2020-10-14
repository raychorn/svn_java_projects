public class  MyLegalClass {
    public String  toString() {
        try {
            /* someReallyExceptionalMethod(); */
	    if (Math.random() > 0.5)
	    	throw new java.io.IOException();
	    else
	    	throw new MyFirstException();
        } catch (java.io.IOException e) {	// added package ref
        } catch (MyFirstException m) {
        }
        return ""; // added
    }
}
