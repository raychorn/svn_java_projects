public class  SimpleRunnable implements Runnable {
    public void  run() {
        System.out.println("in thread named '"
                             + Thread.currentThread().getName() + "'");
    }  // any other methods run() calls are in current thread as well
}

/* public */ class  ThreadTester {
    public static void  main(String argv[]) {
        SimpleRunnable  aSR = new SimpleRunnable();

        while (true) {
            Thread  t = new Thread(aSR);

            System.out.println("new Thread() " + (t == null ?
                                             "fail" : "succeed") + "ed.");
            t.start();
            try { t.join(); } catch (InterruptedException  ignored) { }
        }
    }
}
