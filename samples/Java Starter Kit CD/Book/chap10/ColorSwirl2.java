/* swirly colors */

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;

public class ColorSwirl2 extends java.applet.Applet implements Runnable {

  Font f = new Font("TimesRoman",Font.BOLD,48);
  Color colors[] = new Color[50];
  Thread runThread;

  public void start() {
    if (runThread == null) {
      runThread = new Thread(this);
      runThread.start();
    }
  }

  public void stop() {
    if (runThread != null) {
      runThread.stop();
      runThread = null;
    }
  }

  public void run() {

    // initialize the color arry
    float c = 0;
    for (int i = 0; i < colors.length; i++) {
      colors[i] = Color.getHSBColor(c, (float)1.0,(float)1.0);
      c += .02;
    }

    // cycle through the colors
    int i = 0;
    while (true) {
      setForeground(colors[i]);
      repaint();
      i++;
      try { Thread.currentThread().sleep(50); }
      catch (InterruptedException e) { }
      if (i == (colors.length - 1)) i = 0;
    }
  }
        
  public void update(Graphics g) {
    paint(g);
  }

  public void paint(Graphics g) {
    g.setFont(f);
    g.drawString("All the Swirly Colors", 15,50);
  }
}
