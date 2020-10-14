/* testing references; change one, the other changes.  For chapter 4. */

import java.awt.Point;

class ReferencesTest {

  public static void main (String args[]) {
    Point pt1, pt2;

    pt1 = new Point(100,100);
    pt2 = pt1;

    pt1.x = 200;
    pt1.y = 200;

    System.out.println("Point1: " + pt1.x + "," + pt1.y);
    System.out.println("Point2: " + pt2.x + "," + pt2.y);

  }
}

