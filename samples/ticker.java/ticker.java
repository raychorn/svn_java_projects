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
import java.net.URL;
import java.util.*;

/**
 * ticker - Scrolling Text with colors, speed, url, timedependant starting and expiring<br>
 * Some Ideas from Sven Heinicke, Author of TickerTape<br>
 * 
 * <p>V b1.00, 10/06/95: Creation by Thomas Wendt (thw)<br>
 * V b1.01, 11/18/95: Cleaning up the code by thw<br>
 * V b1.10, 11/28/95: Fixed mem leakage, synchronized run and update by thw<br>
 * V b1.20, 12/31/95: Added new features by thw<br>
 * V b1.21, 01/03/96: bugfix: stop thread by thw<br>
 * V b1.30, 02/07/96: Added support for multilingual use by thw; netscapes bug in doing this
 * and a workaround found by <a href="mailto:Peter.Sylvester@edelweb.fr">Peter Sylvester</a><br>
 *
 * <p><a href="http://www.uni-kassel.de/fb16/ipm/mt/java/javae.html"><b>origin</b></a> of ticker.<br>
 *
 * @author 	<A HREF="http://www.uni-kassel.de/fb16/ipm/mt/staff/thwendte.html">Thomas Wendt</A>
 * @version 	b1.30, 02/07/96
 */
public class ticker extends java.applet.Applet implements Runnable {
  /** The offscreen image */
  public Image im = null;

  /** The offscreen drawing context */
  public Graphics gr = null;

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

  /** The Color of the Text. */
  public Color txtCo;

  /** The textstyle. */
  public int txtStyle;

  /** The Color of the first textshadow. */
  public Color shCo1;

  /** The Color of the second textshadow. */
  public Color shCo2;

  /** The Color of the frame. */
  public Color hrefCo;

  /** The backgroundcolor. */
  public Color bgCo;

  /** True, if to be filled with bgCo after expiration */
  public boolean ExFill;

  /**
   * The time-dependant state of the applet.
   * True, if not started or expired
   */
  public boolean expired = false;

  /** Flag for using shadow */
  public boolean useShadow;

  /** Flag for using frame if url is given */
  public boolean useFrame;

  /** The Size used to calc the Font. */
  public Dimension lastS = new Dimension(1,1);

  /**
   * Fix a netscape bug: We get 0xffcc, if we should
   * get 0x00cc. To make it possible to display non-ascii
   * characters, we need a workaround and forget uni-code.<br>
   * Author of the following method is
   * <a href="mailto:Peter.Sylvester@edelweb.fr">Peter Sylvester</a>
   */
  public String fixedgetParameter(String s) {
    char ec[] = s.toCharArray();
    for (int i=0; i < ec.length; i++)
      ec[i] &= 0x00ff ;
    return(new String(ec)) ;
  }

  /**
   * Initialize: Read Attributes
   * Resize to (2,2) and do nothing if expired;
   */
  public void init () {
    Date today = new Date();
    Date anyDay;
    String at = getParameter("msg");
    message = (at == null) ? "ticker for beta" : fixedgetParameter(at);

    // use readColor to read the Date; date and color both have 3 components.
    bgCo = readColor(getParameter("exp"), Color.white);
    if (!bgCo.equals(Color.white)) {
      anyDay = new Date(bgCo.getRed(),bgCo.getGreen()-1,bgCo.getBlue());
      expired = today.after(anyDay);
    }

    if (!expired) {
      bgCo = readColor(getParameter("start"), Color.black);
      if (!bgCo.equals(Color.black)) {
        anyDay = new Date(bgCo.getRed(),bgCo.getGreen()-1,bgCo.getBlue());
        expired = anyDay.after(today);
      }
    }
    // don't show, if expired
    ExFill = (getParameter("exfill") != null);
    if (expired && !ExFill) {
      resize(2,2);
      return;
    }
    
    speed = ((at = getParameter("speed")) == null) ? 10 : (Integer.valueOf(at).intValue());

    if ((at = getParameter("href")) != null) { 
      try { url_ = new URL(getDocumentBase(), at); }
      catch (Exception e) { url_ = null; }
    }
    // use default txtco = blue, if url is given; black otherwise
    if (url_ == null) { bgCo = Color.black; }
    else { bgCo = Color.blue; }
    
    // get the colors
    txtCo = readColor(getParameter("txtco"), bgCo);
    bgCo = readColor(getParameter("bgco"), getBackground());
    shCo2 = readColor(getParameter("shco"), bgCo);
    useShadow = !(shCo2.equals(bgCo));
    hrefCo = readColor(getParameter("hrefco"), Color.blue);
    useFrame = !(hrefCo.equals(bgCo));

    txtStyle = useShadow ? Font.PLAIN : Font.BOLD;
    if (useShadow) {
      int r = (shCo2.getRed()+txtCo.getRed()) >> 1;
      int g = (shCo2.getGreen()+txtCo.getGreen()) >> 1;
      int b = (shCo2.getBlue()+txtCo.getBlue()) >> 1;
      shCo1 = new Color(r,g,b);
    }
  }

  /** Parameter Info. */
  public String[][] getParameterInfo() {
    String[][] info = {
      {"msg",     "String",  "Message to display"},
      {"href",    "String",  "url to switch to"},
      {"speed",   "int",     "animation speed in pixels (10)"},
      {"txtco",   "int[3]",  "RGB-Color of Message (black/blue)"},
      {"hrefco",  "int[3]",  "RGB-Color of Frame (blue)"},
      {"bgco",    "int[3]",  "RGB-Color of background (getBackground)"},
      {"shco",    "int[3]",  "RGB-Color of Message (black/blue)"},
      {"start",   "int[3]",  "Date to start: Y, M, D; if not set, show"},
      {"exp",     "int[3]",  "Date to expire: Y, M, D; if not set, no expiration"},
      {"exfill",  "",        "If exist, fill with bgco, if expired"},
    };
    return info;
  }

  /** Applet Info. */
  public String getAppletInfo() {
    return "ticker.java, V b1.30, 02/07/96 by Thomas Wendt, http://www.uni-kassel.de/fb16/ipm/mt/staff/thwendte.html";
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

    // Calc the font and positions. Message must fit applets area.
    int refH = 14;
    Font tf = new Font("TimesRoman", txtStyle, refH);
    setFont(tf);
    FontMetrics tfm = getFontMetrics(tf);
    int fh = tfm.getHeight();
    fh = refH*(h-10)/fh;
    messageF = new Font("TimesRoman", txtStyle, fh);
    FontMetrics fm = getFontMetrics(messageF);
    fh = fm.getHeight();
    messageX = w;
    messageY = ((h-fh) >> 1)+fm.getAscent();
    messageW = fm.stringWidth(message);

    // Use double buffering to avoid flicker.
    if (gr != null)
      gr.dispose();
    im = createImage(lastS.width, lastS.height);
    gr = im.getGraphics();
  }

  /** Show the stuff, call update */
  public void paint(Graphics g) { update(g); }

  /** Show the stuff */
  public synchronized void update(Graphics g) {
    int w = size().width;
    int h = size().height;
    // Exit, if expired
    if (expired) {
      if (ExFill) {
        g.setColor(bgCo);
        g.fillRect(0,0,w,h);
      }
      return;
    }

    // Recalc params, if something has changed
    if ((h != lastS.height) || (w != lastS.width))
      createParams();

    // fill area with bgcolor
    gr.setColor(bgCo);
    gr.fillRect(0,0,w,h);

    // if url is given, let it look like a link
    if (url_ != null && useFrame) {
      gr.setColor(hrefCo);
      gr.clipRect(0,0,w,h);
      gr.drawRect(0,0,w,h);
      gr.drawRect(1,1,w-2,h-2);
      gr.setColor(bgCo);
      gr.draw3DRect(2,2,w-4, h-4, true);
      gr.draw3DRect(3,3,w-6, h-6, true);
      gr.clipRect(4,4,w-8, h-8);
    }

    // draw the text
    gr.setFont(messageF);
    if (useShadow) {
      gr.setColor(shCo2);
      gr.drawString(message, messageX+2, messageY+1);
      gr.setColor(shCo1);
      gr.drawString(message, messageX+1, messageY);
    }
    gr.setColor(txtCo);
    gr.drawString(message, messageX, messageY);

    // finally show all together on the screen
    g.drawImage(im,0,0,this);
  }

  public void calcPos() {  
    // decrement position
    messageX -= speed;
    // and stay in the bounds
    if ((messageX + messageW) < 0)
      messageX = size().width;
  }

  /** Run the loop. This method is called by class Thread. */
  public void run() {
    // do nothing, if expired
    if (expired)
      return;

    // others might be more important
    Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
    while (t != null) {
      // indirectly call update
      repaint();
      calcPos();
      // pause
      try {Thread.sleep(100);}
      catch (InterruptedException e){}
    }
  }

  /** Start the applet by forking an animation thread. */
  public void start() {
    if (t == null) {
      t = new Thread(this);
      t.start();
    }
  }

  /** Stop the applet. The thread will exit because run() exits. */
  public void stop() {
    if (t != null) {
      t.stop();
      t = null;
    }
    im = null;
    if (gr != null) {
      gr.dispose();
      gr = null;
    }
    lastS = new Dimension(1,1);
  }

  /** Switch to url, if url is given. */
  public boolean mouseUp(Event evt, int x, int Y) {
    if (url_  != null)
      getAppletContext().showDocument(url_); // might not work with some early browsers
    return true;
  }

  /** Status: show URL */
  public boolean mouseEnter(Event evt, int x, int y) {
    if (url_ != null) { showStatus(url_.toExternalForm()); }
    return true;
  }

  /** clear status */
  public boolean mouseExit(Event evt, int x, int y) {
    if (url_ != null) { showStatus("  "); }
    return true;
  }

}
	
