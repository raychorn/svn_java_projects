public class  Point {
    private float  x, y;

    public  Point(float  myX,  float  myY) {  // added
        x = myX;
	y = myY;
    }

    public  float  x() {        // needs no synchronization
        return x;
    }

    public  float  y() {        // ditto
        return y;
    }

    public synchronized void  setXAndY(float  newX,  float  newY) {
        x = newX;
        y = newY;
    }
}

/* public */ class  UnsafePointPrinter {
    public void  print(Point p) {
        System.out.println("The point's x is " + p.x()
                                + " and y is " + p.y() + ".");
    }
}
