/* draw a lamp */

import java.awt.*;

public class Lamp extends java.applet.Applet {

  public void init() {
    resize(300,300);
  }
      
  public void paint(Graphics g) {
    // the platform
    g.fillRect(0,250,290,290);

    // the base of the lamp
    g.drawLine(125,250,125,160);
    g.drawLine(175,250,175,160);

    // the lamp shade; two arcs
    g.drawArc(85,157,130,50,-65,312);
    g.drawArc(85,87,130,50,62,58);

    g.drawLine(85,177,119,89);
    g.drawLine(215,177,181,89);

    // pattern on the shade
    g.fillArc(78,120,40,40,63,-174);
    g.fillOval(120,96,40,40);
    g.fillArc(173,100,40,40,110,180);
  }

}


