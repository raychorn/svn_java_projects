/* simple applet to show very basic startup and animation stuff */

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Image;

public class Checkers extends java.applet.Applet implements Runnable {

  Thread runner;
  int xpos,xold;
  Image offscreenImg;
  Graphics offscreenG;

  public void init() {
    offscreenImg = createImage(this.size().width, this.size().height);
    offscreenG = offscreenImg.getGraphics();
  }

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
	xold = xpos;
      }
      for (xpos = 105; xpos > 5; xpos -=4) {
        repaint();
	try { Thread.sleep(100); }
	catch (InterruptedException e) { }
	xold = xpos -5;
      }
    }
  }

  public void update(Graphics g) {
    g.clipRect(xold,5,95,95);
    paint(g);
  }

  public void paint(Graphics g) {
    // Draw background
    offscreenG.setColor(Color.black);
    offscreenG.fillRect(0,0,100,100);
    offscreenG.setColor(Color.white);
    offscreenG.fillRect(100,0,100,100);

    // Draw checker
    offscreenG.setColor(Color.red);
    offscreenG.fillOval(xpos,5,90,90);

    g.drawImage(offscreenImg,0,0,this);
  }
}

