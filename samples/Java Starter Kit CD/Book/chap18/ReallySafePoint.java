public class  ReallySafePoint {
    private float  x, y;

    public synchronized Point  getUniquePoint() {
        return new Point(x, y);     // can be a less safe Point
    }                               // because only the caller has it

    public synchronized void   setXAndY(float  newX,  float  newY) {
        x = newX;
        y = newY;
    }

    public synchronized void   scale(float  scaleX,  float  scaleY) {
        x *= scaleX;
        y *= scaleY;
    }

    public synchronized void   add(ReallySafePoint  aRSP) {
        Point  p = aRSP.getUniquePoint();

        x += p.x();
        y += p.y();
    }  // Point p is soon thrown away by GC; no one else ever saw it
}
