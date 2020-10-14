public class  ACorrectClass {
    private   String  aUsefulString;

    public    String  aUsefulString() {            // "get" the value
        return aUsefulString;
    }

    protected void    aUsefulString(String s) {    // "set" the value
        aUsefulString = s;
        /* performSomeImportantBookkeepingOn(s); */
    }
}
