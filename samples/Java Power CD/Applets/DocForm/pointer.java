/*
 * Permission to use, copy, modify and distribute this software and its
 * documentation without fee for NON-COMMERCIAL purposes is hereby granted
 * provided that this notice with a reference to the original source and 
 * the author appears in all copies or derivatives of this software.
 *
 * THE AUTHOR MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THIS SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. THE AUTHOR SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY ANYBODY AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

import java.awt.*;
import java.awt.image.*;
import java.lang.*;
import java.util.*;

/**
 * pointer - Text and sliding Triangles with automatic timedependant slowdown 
and fading<br>
 * 
 * <p>V b1.00, 10/01/95: ported by Thomas Wendt (thw) to pre-beta<br>
 * V b1.01, 11/18/95: Cleaning up the code by thw<br>
 *
 * <p><a 
href="http://www.uni-kassel.de/fb16/ipm/mt/java/javae.html"><b>origin</b></a> 
of pointer.<br>
 *
 *@author 	<A 
HREF="http://www.uni-kassel.de/fb16/ipm/mt/staff/thwendte.html">Thomas 
Wendt</A>
 *@version 	b1.01, 11/18/95
 */
public class pointer extends java.applet.Applet implements Runnable {
  /** The message to be displayed. */
  public String message;
 
  /** x-position of message. */
  public int messageX;

  /** y-position of message. */
  public int messageY;

  /** Image to create with the triangles. */
  public Image img = null;

  /** x-position of image. */
  public int imgX;

  /** Minimal x-position. */
  int imgXmin;

  /** The animating thread. */
  public Thread t = null;

  /** State of the applet. */
  public boolean active = false;

  /** Animation speed: position increment. */
  public int iSpeed;

  /** Animation speed: Threads pause. */
  public int tSpeed;

  /** The direction of animation. */
  public int dir;

  /** The Text-color. */
  public Color txtCo;

  /** The color of the triangles. */
  public Color fgCo;

  /** The backgroundcolor. */
  public Color bgCo;

  /**
   * The age of the applet
   * 1 = new, 0 = old
   */
  public float age = 1.0f;

  /**
   * The time-dependant state of the applet
   * True, if expired
   */
  public boolean expired = false;

  /**
   * The size used to calc the Font.
   * Makes resizing possible.
   */
  public Dimension lastS = new Dimension(1,1);

  /**
   * Initialize: Read Attributes
   * Calc speeds to give other threads a chance.
   * Resizes to (2,2) and do nothing if expired;
   */
  public void init () {
    String at = getParameter("msg");
    message = (at == null) ? "New!" : at;

    // silly, but Color matches Date very well
    fgCo = readColor(getParameter("exp"), Color.white);
    if (!fgCo.equals(Color.white)) {
      Date exp = new Date(fgCo.getRed(),fgCo.getGreen()-1,fgCo.getBlue());
      Date today = new Date();
      expired = today.after(exp);
      fgCo = readColor(getParameter("creat"), Color.white);
      if (!fgCo.equals(Color.white)) { 
        Date creat = new Date(fgCo.getRed(),fgCo.getGreen()-1,fgCo.getBlue());
        float diff = exp.getTime() - creat.getTime();
        age = exp.getTime() - today.getTime();
        age = age/diff;
        if (age<0.01f) { age = 0.001f; }
      }
    }

    // dont show, if expired
    if (expired) {
      resize(2,2);
      return;
    }

    // calc speeds to let others a chance
    at = getParameter("speed");
    tSpeed = (at == null) ? 100 : (Integer.valueOf(at).intValue());
    dir = (tSpeed > 0) ? 1 : -1;
    tSpeed = new Float(1000*dir/(tSpeed*age)).intValue();
    if (tSpeed < 1) { tSpeed = 1; }
    iSpeed = dir*(1+20/tSpeed);
    tSpeed = dir*iSpeed*tSpeed;
    
    txtCo = readColorAge(getParameter("txtco"), Color.blue);
    fgCo = readColorAge(getParameter("fgco"), getBackground());
    bgCo = readColorAge(getParameter("bgco"), Color.yellow);

  }

  /** Parameter Info. */
  public String[][] getParameterInfo() {
    String[][] info = {
      {"msg",    "String",  "Message to display (New!)"},
      {"speed",  "int",     "animation speed (1/msec)"},
      {"txtco",  "int[3]",  "RGB-Color of Message (blue)"},
      {"fgco",   "int[3]",  "RGB-Color of triangles (getBackground)"},
      {"bgco",   "int[3]",  "RGB-Color of background (yellow)"},
      {"creat",  "int[3]",  "Date of creation: Y, M, D; if not set, no 
slowdown, fading"},
      {"exp",    "int[3]",  "Date to expire: Y, M, D; if not set, no slowdown, 
fading, expiration"},
      {"debug",  "?",    "if to print some debugging infos"},
    };
    return info;
  }

  /** Applet Info. */
  public String getAppletInfo() {
    return "pointer.java, V b1.01, 11/18/95 by Thomas Wendt, 
http://www.uni-kassel.de/fb16/ipm/mt/staff/thwendte.html";
  }    

  /**
   * Convert a ','-delimited String with RGB-Values to Color
   * Uses aDefault, if no or not enough RGB-Values
   */
  public Color readColor(String aColor, Color aDefault) {
    if (aColor == null) { return aDefault; }

    int r, g, b;
    StringTokenizer st = new StringTokenizer(aColor, ",");
    
    try {
      r = Integer.valueOf(st.nextToken()).intValue();
      g = Integer.valueOf(st.nextToken()).intValue();
      b = Integer.valueOf(st.nextToken()).intValue();
      return new Color(r,g,b);
    }
    catch (Exception e) { return aDefault; }
  }

  /** Compute a given color component with age. */
  public int coAge(float fg, float bg) {
    return new Float(fg*age+(1-age)*bg).intValue();
  }

  /**
   * Convert by calling readColor and take age into account
   * @see #readColor
   * @see #coAge
   */
  public Color readColorAge(String aColor, Color aDefault) {
    Color co = readColor(aColor, aDefault);
    int r = Color.lightGray.getRed();
    int g = Color.lightGray.getGreen();
    int b = Color.lightGray.getBlue();
    
    return new Color(coAge(co.getRed(),r), coAge(co.getGreen(), g), 
coAge(co.getBlue(), b));
  }

  /** Create the image offline. */
  public void createImg() {
    // set some constants
    int w = size().width;
    int h = size().height;
    lastS.width = w;
    lastS.height = h;

    // calc font: message must fit in applets area
    int refH = 14;
    Font tf = new Font("TimesRoman", Font.BOLD, refH);
    setFont(tf);
    FontMetrics tfm = getFontMetrics(tf);
    int fw = tfm.stringWidth(message);
    int fh = tfm.getHeight();
    fh = Math.min(refH*h/fh, refH*w/(fw << 1));
    Font f = new Font("TimesRoman", Font.BOLD, fh);
    FontMetrics fm = getFontMetrics(f);
    fw = fm.stringWidth(message);
    fh = fm.getHeight();
    setFont(f);
    messageX = (w-fw) >> 1;
    messageY = ((h-fh) >> 1)+fm.getAscent();

    // then draw offline image
    w = ((w/h)+2)*h; // round up to multipl of height
    int pixels[] = new int[w*h];
    int x;
    int y;

    // fill with background
    int co = (255 << 24) | (bgCo.getRed() << 16) | (bgCo.getGreen() << 8) | 
bgCo.getBlue();
    for (x = 0; x < (w*h); x++) { pixels[x] = co; }

    co = (255 << 24) | (fgCo.getRed() << 16) | (fgCo.getGreen() << 8) | 
fgCo.getBlue();
      
    /* 
     * Create triangles
     * Using bytearray might be faster than using offscreen Graphics
     */
    for (x = h >> 1; x < w; x += h) {
      for (y = 1; y <= (h >> 1); y++) {
        for (int i = x; (i <= (x+y)) && (i >= (x-y)) ; i += dir) {
          pixels[y*w+i] = co;
          pixels[(h-y-1)*w+i] = co;
        }
      }
    }
    // create an ImageProducer with the bytearray
    img = createImage(new MemoryImageSource(w, h, pixels, 0, w));
    imgXmin = -h;
    imgX = (dir > 0) ? imgXmin : 0;
  }

  /** Show the stuff */
  public void paint(Graphics g) { update(g); }

  /** Show the stuff */
  public void update(Graphics g) {
    // do nothing, if expired
    if (expired) { return; }

    // if something has changed, create new image
    if ((img == null) || (size().height != lastS.height) || (size().width != 
lastS.width)) { createImg(); }

    // if position in bounds
    if ((imgX <= 0) && (imgX >= imgXmin)) {
      // draw first moving image
      g.drawImage(img,imgX,0, this);
      g.setColor(txtCo);
      // then text
      g.drawString(message, messageX, messageY);
    }
  }

  /** Run the loop. This method is called by class Thread. */
  public void run() {
    // do nothing, if expired
    if (expired) { return; }

    // others may be more important
    Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
    while (active) {
      // increment position
      imgX += iSpeed;
      if (imgX > 0) {imgX = imgXmin; }
      else { if (imgX < imgXmin) { imgX = 0; } }
      // paint the applet
      repaint();
      // and pause
      try { Thread.sleep(tSpeed); }
      catch (Exception e) { }
    }
  }

  /** Start the applet by forking an animation thread. */
  public void start() {
    if (!active) {
      t = new Thread(this);
      active = true;
      t.start();
    }
  }

  /** Stop the applet. The thread will exit because run() exits. */
  public void stop() {
    active = false;
    t = null;  // give GC a chance.
  }

}
----------
X-Sun-Data-Type: default-app
X-Sun-Data-Description: default
X-Sun-Data-Name: ticker.java
X-Sun-Charset: us-ascii
X-Sun-Content-Lines: 275
