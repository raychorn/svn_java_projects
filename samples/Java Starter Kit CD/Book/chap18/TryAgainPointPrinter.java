public class  TryAgainPointPrinter {
    public void  print(Point p) {
        float  safeX, safeY;

        synchronized(this) {
            safeX = p.x();     // these two lines now
            safeY = p.y();     // happen atomically
        }
        System.out.print("The point's x is " + safeX
                                  + " y is " + safeY);
    }
}
