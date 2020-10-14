public class  SafePointPrinter {
    public void  print(Point p) {
        float  safeX, safeY;

        synchronized(p) {     // no one can change p
            safeX = p.x();    // while these two lines
            safeY = p.y();    // are happening atomically
        }
        System.out.print("The point's x is " + safeX
                                  + " y is " + safeY);
    }
}
