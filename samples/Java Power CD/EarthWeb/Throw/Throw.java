/***********************************************************************
Copyright Notice: This source code is (C) Copyright 1995, EarthWeb LLC.,
All Rights Reserved. Distribution of this document or it's resulting
compiled code is granted for non-commercial use, with prior approval of
EarthWeb LLC. Distribution of this document or its resulting compiled
code, for commercial use, is granted only with prior written approval of
EarthWeb, LLC. For information, send email to info@earthweb.com.
***********************************************************************/
import java.awt.*;
import java.util.Random;

/** A draggable and throwable image, for use with the Throw class.
 *
 * @version 1.0 11/20/95
 * @author Carl Muckenhoupt
 */
class Ball extends DragImage {
  int dx, dy;
  Random rnd = new Random();
  /** Moves the ball one iteration, according to its current velocity.
   * If the ball is stopped, gives it a random velocity.
   */
  void step() {
    if (dx == 0 && dy == 0) {
      dx = (int)(rnd.nextFloat()*10)-5;
      dy = (int)(rnd.nextFloat()*10)-5;
    }
    x += dx;
    if (x > maxx) { x = maxx-(x-maxx); dx = -dx; }
    if (x < minx) { x = minx+(minx-x); dx = -dx; }
    y += dy;
    if (y > maxy) { y = maxy-(y-maxy); dy = -dy; }
    if (y < miny) { y = miny+(miny-y); dy = -dy; }
  }
}

/** Throw - a throwable bouncing ball applet.  (Nothing to do with exceptions.)
 * I admit that this could be done much, much more simply.  If this were the
 * only application for the Draggable class, it would be a big waste.  As it
 * is, the Draggable class was written for another application and retrofitted
 * into the original Throw (which was Alpha only).  This was much easier than
 * writing essentially the same code for two applets.
 *
 * @version 1.0 11/20/95
 * @author Carl Muckenhoupt
 */
public class Throw extends Arena implements Runnable {
  Ball b;
  Thread thread = null;
  Graphics offscreen; // for double buffering
  Image offscreenImage;

  public void init() {
    try { // set up the double-buffering
      offscreenImage = createImage(size().width, size().height);
      offscreen = offscreenImage.getGraphics();
    } catch (Exception e) {
      offscreenImage = null;
      offscreen = null;
    }
    String at = getParameter("img"); // Parameter specifies the image
    b = new Ball();
    add(b);
    b.setImage(getImage(getDocumentBase(), at));
    b.dx = 5; // Set an initial oblique velocity.
    b.dy = 1;
  }

  public void start() { // Stops the thread when not in use
    if (thread == null) {
      thread = new Thread(this);
      thread.start();
    }
  }

  public void stop() { // Restarts the thread
    if (thread != null) {
      thread.stop();
      thread = null;
    }
  }

  public void run() {  // The body of the thread
    thread.setPriority(Thread.MIN_PRIORITY+1);
    while(true) {
      if (b == grabbed) {
        /* Here we adjust the velocity of the ball according to how fast we're
           moving it with the mouse.  We do this simply by taking the
           difference between its current position and its position at the
           time of the last update. */
        b.dx = b.x-b.lastx;
        b.dy = b.y-b.lasty;
      } else {
        b.step(); // move the ball
      }
      repaint();
      try {  // Fixed-length delay between updates
        Thread.sleep(75);
      } catch (InterruptedException e) {
        return;
      }
    }
  }

  public void domove(Draggable d) {
    // Override the default, which redraws the piece.  We want to redraw it
    // ourselves, in response to time, not input.
  }
  // Pretty standard double-buffer stuff...
  public void do_paint(Graphics g) {
    g.setColor(getBackground());
    g.fillRect(0, 0, size().width, size().height);
    super.paint(g);
  }
  public void paint(Graphics g) {
    if (offscreen != null) {
      do_paint(offscreen);
      g.drawImage(offscreenImage, 0, 0, this);
    }
  }
  public void update(Graphics g) {
    paint(g);
  }
}














