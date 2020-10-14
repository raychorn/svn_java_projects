public class  ThreadCounter {
    int  crucialValue;

    public void  countMe() {
        crucialValue += 1;
    }

    public int   howMany() {
        return crucialValue;
    }
}
