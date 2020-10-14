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
import java.applet.*;
import java.lang.*;
import java.net.*;

/**
 * jline - Text (with optional url) which is hidden for all Browsers except 
HotJava.<br>
 * use align=top<br>
 * 
 * <p>V b1.00, 10/01/95: Ported by Thomas Wendt (thw) to pre-beta<br>
 * V b1.01, 11/18/95: Cleaning up the code by thw; renamed from javaline to 
jline<br>
 *
 * <p><a 
href="http://www.uni-kassel.de/fb16/ipm/mt/java/javae.html"><b>origin</b></a> 
of jline.<br>
 *
 * @author <A 
HREF="http://www.uni-kassel.de/fb16/ipm/mt/staff/thwendte.html">Thomas 
Wendt</A>
 * @version b1.01, 11/18/95
 */
public class jline extends Applet {
  /** The text to be displayed. */
  public String message;

  /** y-position of the text. */
  public int messageY;

  /** URL to switch to. */
  public URL url_ = null;

  /**
   * Initialize the applet.
   * Get parameters and resize font- and message-dependant.
   */
  public void init () {
    // Get parameters.
    message = ((message = getParameter("msg")) != null) ? message : "jline 
1.01";
    String attr = getParameter("msgh");
    Integer msgH = new Integer((attr == null) ? "14" : attr);
    if ((attr = getParameter("href")) != null) {
      try { url_ = new URL(getDocumentBase(), attr); }
      catch (Exception e) { url_ = null; }
    }

    // Set up the stuff needed for displaying.
    Font f = new Font("TimesRoman", Font.PLAIN, msgH.intValue());
    FontMetrics fm = getFontMetrics(f);
    setFont(f);
    resize(fm.stringWidth(message), fm.getHeight()); // resize doesn't work 
with some browsers.
    messageY = fm.getAscent();
  }

  /** Parameter Info. */
  public String[][] getParameterInfo() {
    String[][] info = {
      {"msg",   "String",  "Message to display (jline 1.0)"},
      {"msgh",  "int",     "Fontsize of Message (14)"},
      {"href",  "Url",     "optional url to switch to"},
    };
    return info;
  }

  /** Applet Info. */
  public String getAppletInfo() {
    return "javaline.java, V b1.01, 11/18/95 by Thomas Wendt, 
http://www.uni-kassel.de/fb16/ipm/mt/staff/thwendte.html";
  }    

  /**
   * Paint the applet.
   * use blue, if url is given; black otherwise.
   */
  public void paint(Graphics g) {
    if (url_ != null) { g.setColor(Color.blue); }
    else { g.setColor(Color.black); }
    g.drawString(message, 0, messageY);
  }

  /** switch to the document referenced by url_. */
  public boolean mouseUp(Event evt, int x, int Y) {
    if (url_  != null) { getAppletContext().showDocument(url_); } // may be 
defunctional with some browsers.
    return true;
  }
}
	
----------
X-Sun-Data-Type: default-app
X-Sun-Data-Description: default
X-Sun-Data-Name: pointer.java
X-Sun-Charset: us-ascii
X-Sun-Content-Lines: 306