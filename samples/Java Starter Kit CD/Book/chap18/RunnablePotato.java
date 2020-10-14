public class  RunnablePotato implements Runnable {
    public void  run() {
        while (true)
            System.out.println(Thread.currentThread().getName());
    }
}

/* public */ class  PotatoThreadTester {
    public static void  main(String argv[]) {
        RunnablePotato  aRP = new RunnablePotato();

        new Thread(aRP, "one potato").start();
        new Thread(aRP, "two potato").start();
    }
}
