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
import java.lang.*;
import java.net.*;
import java.util.*;
     
/**
 * ticker - Scrolling Text with colors, speed, url and timedependant 
 expiring<br>
     
 * Some Ideas from Sven Heinicke, Author of TickerTape<br> 
 * 
 * <p>V b1.00, 10/06/95: Creation by Thomas Wendt (thw)<br> 
 * V b1.01, 11/18/95: Cleaning up the code by thw<br>
 * V b1.10, 11/28/95: Fixed mem leakage, synchronized run and update by thw<br> 
 *
 * <p><a 
 href="http://www.uni-kassel.de/fb16/ipm/mt/java/javae.html"><b>origin</b
></a> of ticker.<br>
 *
 * @author 	<A 
 HREF="http://www.uni-kassel.de/fb16/ipm/mt/staff/thwendte.html">Thomas Wendt</A>
 * @version 	b1.1, 11/28/95
 */
public class ticker extends java.applet.Applet implements Runnable {
  /** The message to be displayed. */
  public String message;
     
  /** The Font to be displayed. */
  public Font messageF;
     
  /** x-position of message. */
  public int messageX;
     
  /** y-position of message. */
  public int messageY;
     
  /** Length of the message. */
  public int messageW = 0;
     
  /** URL to switch to */
  public URL url_ = null;
     
  /** How far to skip across the screen. */ 
  int speed;
     
  /** The animating thread. */
  public Thread t = null;
     
  /** State of the applet. */
  public boolean active = false;
     
  /** The Text-color. */
  public Color txtCo;
     
  /** The backgroundcolor. */
  public Color bgCo;
     
  /**
   * The time-dependant state of the applet. 
   * True, if expired
   */
  public boolean expired = false;
     
  /** The Size used to calc the Font. */ 
  public Dimension lastS = new Dimension(1,1);
     
  /** The offscreen image. */
  Image im = null;
     
  /** The offscreen Graphics context */ 
  Graphics gr = null;
     
     
  /**
   * Initialize: Read Attributes
   * Resize to (2,2) and do nothing if expired; 
   */
  public void init () {
    String at = getParameter("msg");
    message = (at == null) ? "ticker for pre-beta" : at;
     
    // use readColor to read the Date; date and color both have 3 components. 
    bgCo = readColor(getParameter("exp"), Color.white);
    if (!bgCo.equals(Color.white)) {
      Date exp = new Date(bgCo.getRed(),bgCo.getGreen()-1,bgCo.getBlue()); 
      Date today = new Date();
      expired = today.after(exp);
    }
     
    // don't show, if expired
    if (expired) {
      resize(2,2);
      return;
    }
     
    speed = ((at = getParameter("speed")) == null) ? 10 : 
    (Integer.valueOf(at).in
tValue());
     
    if ((at = getParameter("href")) != null) { 
      try { url_ = new URL(getDocumentBase(), at); } 
      catch (Exception e) { url_ = null; }
    }
    // use default bgco = blue, if url is given; black otherwise 
    if (url_ == null) { bgCo = Color.black; }
    else { bgCo = Color.blue; }
     
    // get the colors
    txtCo = readColor(getParameter("txtco"), bgCo);
    bgCo = readColor(getParameter("bgco"), getBackground());
     
    // calc dimensions etc.
//    createParams();
  }
     
  /** Parameter Info. */
  public String[][] getParameterInfo() {
    String[][] info = {
      {"msg",    "String",  "Message to display (New!)"}, 
      {"speed",  "int",     "animation speed in pixels (10)"}, 
      {"txtco",  "int[3]",  "RGB-Color of Message (black/blue)"},
      {"bgco",   "int[3]",  "RGB-Color of background (getBackground)"},
      {"exp",    "int[3]",  "Date to expire: Y, M, D; if not set, no 
      expiration"}
,
    };
    return info;
  }
     
  /** Applet Info. */
  public String getAppletInfo() {
    return "ticker.java, V b1.1, 11/28/95 by Thomas Wendt, 
    http://www.uni-kassel.
de/fb16/ipm/mt/staff/thwendte.html";
  }    
     
  /**
   * Convert a ","-delimited String with RGB-Values to Color 
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
     
  /**
   * Create the image Parameters.
   * Called, if just created or size has changed 
   */
  public void createParams() {
    // Init some constants
    int w = size().width;
    int h = size().height;
    lastS.width = w;
    lastS.height = h;
     
    // dispose old buffer etc.
    if (gr != null) { gr.finalize(); }
    if (im != null) { im = null; }
    // Calc the font and positions. Message must fit applets area. 
    int refH = 14;
    Font tf = new Font("TimesRoman", Font.BOLD, refH); 
    setFont(tf);
    FontMetrics tfm = getFontMetrics(tf); 
    int fh = tfm.getHeight();
    fh = refH*(h-10)/fh;
    messageF = new Font("TimesRoman", Font.BOLD, fh); 
    FontMetrics fm = getFontMetrics(messageF);
    fh = fm.getHeight();
    messageX = w;
    messageY = ((h-fh) >> 1)+fm.getAscent(); 
    messageW = fm.stringWidth(message);
    im = createImage(w, h);
    gr = im.getGraphics();
  }
     
  /** Show the stuff, call update */
  public void paint(Graphics g) { update(g); }
     
  /** Show the stuff */
  public synchronized void update(Graphics g) {
    // Exit, if expired
    if (expired) { return; }
     
    // Recalc params, if something has changed
    if ((size().height != lastS.height) || (size().width != lastS.width)) { 
    creat
eParams(); }
     
    // fill area with bgcolor
    gr.setColor(bgCo);
    gr.fillRect(0,0,lastS.width-1,lastS.height-1);
     
    // if url is given, let it look like a link 
    if (url_ != null) {
      gr.setColor(Color.blue);
      gr.drawRect(0,0,lastS.width-1,lastS.height-1); 
      gr.drawRect(1,1,lastS.width-3,lastS.height-3); 
      gr.setColor(bgCo);
      gr.draw3DRect(2,2,lastS.width-5, lastS.height-5, true); 
      gr.draw3DRect(3,3,lastS.width-7, lastS.height-7, true); 
      gr.clipRect(5,4,lastS.width-10, lastS.height-8);
    }
     
    // draw the text
    gr.setColor(txtCo);
    gr.setFont(messageF);
    gr.drawString(message, messageX, messageY);
     
    // finally show all together on the screen 
    g.drawImage(im,0,0,this);
  }
     
  /** calc next position of message and call repaint */ 
  public synchronized void nextPos() {
      // decrement position
      messageX -= speed;
      // and stay in the bounds
      if ((messageX + messageW) < 0) {
	messageX = lastS.width;
      }
      // indirectly call update
      repaint();
  }
     
  /** Run the loop. This method is called by class Thread. */ 
  public void run() {
    // do nothing, if expired
    if (expired) { return; }
     
    // others might be more important
    Thread.currentThread().setPriority(Thread.MIN_PRIORITY); 
    while (active) {
      // do the job
      nextPos();
      // pause
      try {Thread.sleep(100);}
      catch (InterruptedException e){ }
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
    t = null; // give GC a chance.
  }
     
  /** Switch to url, if url is given. */
  public boolean mouseUp(Event evt, int x, int Y) {
    if (url_  != null) { getAppletContext().showDocument(url_); } // might not 
    wo
rk with some early browsers
    return true;
  }
     
}
	
----------
X-Sun-Data-Type: default-app
X-Sun-Data-Description: default
X-Sun-Data-Name: waisfr.java
X-Sun-Charset: us-ascii
X-Sun-Content-Lines: 159
