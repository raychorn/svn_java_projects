/* print the date */

import java.awt.Graphics;
import java.awt.Font;
import java.util.Date;

public class Digital2 extends java.applet.Applet {

  Font theFont = new Font("TimesRoman",Font.BOLD,24);
  Date theDate;
  boolean stopped = false;

  public void start() {
    stopped = false;
	while (stopped != true) {
      theDate = new Date();
      repaint();
      try { Thread.sleep(1000); }
      catch (InterruptedException e) { }
    }
  }

  public void stop () {
    stopped = true;
  }

  public void paint(Graphics g) {
    g.setFont(theFont);
    g.drawString(theDate.toString(),10,50);
  }
}




