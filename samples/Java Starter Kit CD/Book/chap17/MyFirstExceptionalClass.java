public class  MyFirstExceptionalClass {

public void  anotherExceptionalMethod() throws MyFirstException {
    MyFirstExceptionalClass  aMFEC = new MyFirstExceptionalClass();

    aMFEC.anExceptionalMethod();
}



public void  responsibleMethod() {
    MyFirstExceptionalClass  aMFEC = new MyFirstExceptionalClass();

    try {
        aMFEC.anExceptionalMethod();
    } catch (MyFirstException m) {
        // do something terribly significant and responsible
    }
}



public void  responsibleExceptionalMethod() throws MyFirstException {
    MyFirstExceptionalClass  aMFEC = new MyFirstExceptionalClass();

    try {
        aMFEC.anExceptionalMethod();
    } catch (MyFirstException m) {
                     // do something responsible
        throw m;     // re-throw the exception
    }
}

    public void  anExceptionalMethod() throws MyFirstException {

        if (true /* someThingUnusualHasHappened() */) {
            throw new MyFirstException();
            // execution never reaches here
        }
    }

    public static void  main(String argv[]) {

System.out.print("Now ");
try {
    System.out.print("is ");
    throw new MyFirstException();
//    System.out.print("a ");
} catch (MyFirstException m) {
    System.out.print("the ");
}
System.out.print("time.");

    }
}
