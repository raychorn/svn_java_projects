public class  RunnablePotato2 implements Runnable {
    public void  run() {
        while (true) {
            System.out.println(Thread.currentThread().getName());
            Thread.yield();  // let another thread run for a while
        }
    }
}

/* public */ class  PriorityThreadTester {
    public static void  main(String argv[]) {
        RunnablePotato2  aRP = new RunnablePotato2();
        Thread           t1  = new Thread(aRP, "one potato");
        Thread           t2  = new Thread(aRP, "two potato");

        t2.setPriority(t1.getPriority() + 1);
        t1.start();
        t2.start();   // at priority Thread.NORM_PRIORITY + 1
    }
}
