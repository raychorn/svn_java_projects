public class  ACorrectFinalClass {
    private         String  aUsefulString;

    public    final String  aUsefulString() {    // now faster to use
        return aUsefulString;
    }

    protected final void    aUsefulString(String s) {  // also faster
        aUsefulString = s;
    }
}

