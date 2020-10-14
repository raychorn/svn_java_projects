public class  StaticCounter {
    private static int  crucialValue;

    public void  countMe() {
        synchronized(getClass()) {   // can't directly reference StaticCounter
            crucialValue += 1;       // the (shared) class is now locked
        }
    }
}
