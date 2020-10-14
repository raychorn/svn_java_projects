/* lines */

import java.awt.Graphics;
import java.awt.Polygon;

public class MyPoly2 extends java.applet.Applet {
  
  public void paint(Graphics g) {
    int exes[] = { 39,94,97,142,53,58,26 };
    int whys[] = { 33,74,36,70,108,80,106 };
    int pts = exes.length;
	Polygon poly = new Polygon(exes,whys,pts);

    g.fillPolygon(poly);
		    
  }

}


