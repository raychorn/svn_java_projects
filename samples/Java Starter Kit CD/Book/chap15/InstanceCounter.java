public class  InstanceCounter {
    private   static int   instanceCount = 0;   // a class variable

    protected static int   instanceCount() {    // a class method
        return instanceCount;
    }

    private   static void  incrementCount() {
        ++instanceCount;
    }

    InstanceCounter() {
        InstanceCounter.incrementCount();
    }
}



/* public */ class  InstanceCounterTester extends InstanceCounter {
    public static void  main(String argv[]) {
        for (int i = 0;  i < 10;  ++i)
            new InstanceCounter();
        System.out.println("made " + InstanceCounter.instanceCount());
    }
}

