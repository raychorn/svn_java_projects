/*
  Growing Text v1.2

  v1.0 - 01/96 - Original
  v1.1 - 03/96 - Added blur parameter
  v1.2 - 04/96 - Added key control of delay factor
  
  Feel free to re-use any part of this code.
  
  Jamie M. Hall
  jamihall@mhv.net
  http://www.mhv.net/~jamihall

  Takes text, delay, fontName, fontBold, fontItalic, bgColor,
  fgColor, and blur as parameters.  The defaults are:

  text        -   String displayed in applet      -  Growing Text
  delay       -   Milliseconds between updates    -  500 
  fontName    -   Font style                      -  TimesRoman
  fontBold    -   Font boldness                   -  true
  fontItalic  -   Font italics                    -  false
  bgColor     -   Background color (hex. number)  -  light Gray
  fgColor     -   Foreground color (hex. number)  -  black 
  blur        -   Blurring effect                 -  false
 
  'random' can be used as the background or foreground color to
  generate a random color on each update.  Pressing a mouse button
  anywhere in the applet will suspend/resume it.  Pressing the UP 
  arrow or 'u' keys anywhere in the applet will increase the delay, 
  while the DOWN arrow or 'd' keys will decrease the delay.
*/

import java.awt.*;
import java.applet.*;

public class GrowingText extends Applet implements Runnable {
  String fontName = "TimesRoman", text = "Growing Text", bgColor, fgColor;
  Thread killme = null;
  boolean threadSuspended = false, blur = false;
  int fonts[] = { 8, 12, 14, 18, 24, 36 };
  int delay = 500, numFonts = 6, fontIndex = 0, fontStyle;
  Font appFont;

  public void init() {
    String param;
    boolean fontBold = true, fontItalic = false;

    param = getParameter("text");
    if (param != null) { text = param; }

    param = getParameter("delay");
    if (param != null) { delay = Integer.parseInt(param); }

    param = getParameter("fontName");
    if (param != null) { fontName = param; }

    param = getParameter("fontBold");
    if (param != null) { fontBold = param.equals("true"); }

    param = getParameter("fontItalic");
    if (param != null) { fontItalic = param.equals("true"); }

    fontStyle = (fontBold ? Font.BOLD : Font.PLAIN) + 
      (fontItalic ? Font.ITALIC : Font.PLAIN);

    bgColor = getParameter("bgColor");
    if (bgColor == null) { bgColor = "Color.lightGray"; }
    setBackground(colorFromString(bgColor, Color.lightGray));

    fgColor = getParameter("fgColor");
    if (fgColor == null) { fgColor = "Color.black"; }
    setForeground(colorFromString(fgColor, Color.black));

    param = getParameter("blur");
    if (param != null) { blur = param.equals("true"); }

    /* Resize applet to fit string with largest font.
       Only works in JDK appletviewer, Netscape ignores it */
    /* FontMetrics fm = getFontMetrics(new Font(fontName, fontStyle,
					     fonts[numFonts-1]));
    resize(fm.stringWidth(s) + 20, appFont.getSize() + 20); */
  }

  public void start() {
    if (killme == null) {
      killme = new Thread(this);
      killme.start();
    }
  }

  public void stop() {
    if (killme != null) {
      killme.stop();
      killme = null;
    }
  }

  public void run() {
    while (killme != null) {
      repaint();
      try { Thread.sleep(delay); } catch (InterruptedException e) {};
    }
    killme = null;
  }

  public void update(Graphics g) {
    if (blur) {
      if (fontIndex > numFonts - 1 ) {
	g.clearRect(0, 0, size().width, size().height);
      }
      paint(g);
    } else {
      g.clearRect(0, 0, size().width, size().height);
      paint(g);
    }
  }

  public void paint(Graphics g) {
    if (bgColor.equalsIgnoreCase("random")) {
      setBackground(colorFromString(bgColor, Color.lightGray));
    } 

    if (fgColor.equalsIgnoreCase("random")) {
      setForeground(colorFromString(fgColor, Color.black));
    }

    if (fontIndex > numFonts - 1 ) {
      fontIndex = 0;
    }
    g.setFont(appFont = new Font(fontName, fontStyle, fonts[fontIndex++]));
    FontMetrics fm = getFontMetrics(appFont);

    g.drawString(text, (size().width - fm.stringWidth(text))/2,
                 (size().height/2)+10);
  }
  
  public boolean mouseDown(Event evt, int x, int y) {
    if (threadSuspended) {
      killme.resume();
    } else {
      killme.suspend();
    }
    threadSuspended = !threadSuspended;
    return true;
  }

  public boolean keyDown(Event evt, int key) {
    if (key == Event.UP || key == 117) {
      delay += 100;
      return true;
    } else if (key == Event.DOWN || key == 100) {
      if (delay - 100 > 0) {
       delay -= 100;
      }
      return true;
    } 
    return false;
  }

  public Color colorFromString(String str, Color defaultColor) {
    if (str.equalsIgnoreCase("random")) {
      return new Color((int)(Math.random() * 256), (int)(Math.random() * 256),
		       (int)(Math.random() * 256));
    } else {
      try {
	Integer i = Integer.valueOf(str, 16);
	return new Color(i.intValue());
      } catch (NumberFormatException e) {
	return defaultColor;
      }
    }
  }

  public String getAppletInfo() {
    return "GrowingText by Jamie M. Hall, 1996";
  }

  public String[][] getParameterInfo() {
    String[][] info = {
      {"text", "string", "String displayed in applet"},
      {"delay", "integer", "Milliseconds between updates"},
      {"fontName", "string", "Font style"},
      {"fontBold", "boolean", "Font boldness"},
      {"fontItalic", "boolean", "Font italics"},
      {"bgColor", "hexadecimal number", "Background color"},
      {"fgColor", "hexadecimal number", "Foreground color"},
      {"blur", "boolean", "Blurring effect"}
    };
    return info;
  }

}
