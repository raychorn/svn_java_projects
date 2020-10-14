/* simple applet to show very basic startup and animation stuff */

import java.awt.Graphics;
import java.awt.Color;

public class Checkers extends java.applet.Applet implements Runnable {

  Thread runner;
  int xpos;

  public void start() {
    if (runner == null); {
      runner = new Thread(this);
      runner.start();
    }
  }

  public void stop() {
    if (runner != null) {
      runner.stop();
      runner = null;
    }
  }

  public void run() {
    setBackground(Color.blue);
    while (true) {
      for (xpos = 5; xpos <= 105; xpos+=4) {
	repaint();
	try { Thread.sleep(100); }
	catch (InterruptedException e) { }
      }
      for (xpos = 105; xpos > 5; xpos -=4) {
        repaint();
	try { Thread.sleep(100); }
	catch (InterruptedException e) { }
      }
    }
  }


  public void paint(Graphics g) {
    // Draw background
    g.setColor(Color.black);
    g.fillRect(0,0,100,100);
    g.setColor(Color.white);
    g.fillRect(101,0,100,100);

    // Draw checker
    g.setColor(Color.red);
    g.fillOval(xpos,5,90,90);

  }
}






