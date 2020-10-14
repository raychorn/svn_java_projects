/* lines */

import java.awt.Graphics;


public class MyArc2 extends java.applet.Applet {
  
  public void paint(Graphics g) {
    g.drawArc(10,20,150,50,25,-130);
    g.fillArc(10,80,150,50,25,-130);
  }

}


