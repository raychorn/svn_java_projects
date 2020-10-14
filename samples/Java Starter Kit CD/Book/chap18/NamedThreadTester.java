public class  NamedThreadTester {
    public static void  main(String argv[]) {
        SimpleRunnable  aSR = new SimpleRunnable();

        for (int  i = 1;  true;  ++i) {
            Thread  t = new Thread(aSR, "" + (100 - i) 
                                           + " threads on the wall...");

            System.out.println("new Thread() " + (t == null ?
                                             "fail" : "succeed") + "ed.");
            t.start();
            try { t.join(); } catch (InterruptedException  ignored) { }
        }
    }
}
