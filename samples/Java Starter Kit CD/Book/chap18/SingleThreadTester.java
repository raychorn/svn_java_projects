public class  SingleThreadTester {
    public static void  main(String argv[]) {
        Thread  t = new Thread(new SimpleRunnable());

        try {
            t.start();
            /* someMethodThatMightStopTheThread(t); */
        } catch (ThreadDeath  aTD) {
                           // do some required cleanup
            throw aTD;     // re-throw the error
        }
    }
}
