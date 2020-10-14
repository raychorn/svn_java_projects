public class  ComplexThread extends Thread {
    private int  delay;

    ComplexThread(String  name,  float  seconds) {
        super(name);
        delay = (int) seconds * 1000;  // delays are in milliseconds
        start();                       // start up ourself!
    }

    public void  run() {
        while (true) {
            System.out.println(Thread.currentThread().getName());
            try {
                Thread.sleep(delay);
            } catch (InterruptedException  ignored) {
                return;
            }
        }
    }

    public static void  main(String argv[]) {
        new ComplexThread("one potato",   1.1F);
        new ComplexThread("two potato",   0.3F);
        new ComplexThread("three potato", 0.5F);
        new ComplexThread("four",         0.7F);
    }
}
