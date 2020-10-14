public class  SafeThreadCounter {
    int  crucialValue;

    public synchronized void  countMe() {
        crucialValue += 1;
    }

    public              int   howMany() {
        return crucialValue;
    }
}
