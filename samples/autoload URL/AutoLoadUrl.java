/*
 * Simple Applet to auto-load URL
 *
 */

import java.net.URL;
import java.net.MalformedURLException;
import java.lang.*;
import java.applet.*;
import java.awt.*;

/**
 * An applet that can be used to auto-load an URL. Main purpose is to
 * provide a way to auto-load an URL, with Java-enabled features, if
 * running a Java-enabled browser.
 *
 * @version 1.6, 06 Mar 1996
 * @author  Jan Andersson (janne@torpa.se)
 */
public class AutoLoadUrl extends Applet implements Runnable 
{
   private Thread    thread;		// applet thread
   private String    initMessage;	// Message (before autoloaded)
   private String    loadedMessage;	// Message (after autoloaded)
   private String    errMessage;        // Error Message to display
   private Dimension lastSize;	        // last know size
   private int       lastMessageLen;    // last length of message
   private int       delay;		// delay seconds
   private Font      font;		// display font
   private Color     bgColor;		// applet bgcolor
   private Color     fgColor;		// applet fgcolor
   private URL       url = null;	// URL to load
   private boolean   urlLoaded = false;	// URL loaded or not
   private boolean   debug = false;	// debug flag
   
   /**
    * Info.
    */
   public String getAppletInfo() {
      return "AutoLoadUrl by Janne Andersson (janne@torpa.se)";
   }
   
   /**
    * Parameter Info
    */
   public String[][] getParameterInfo() {
      String[][] info = {
	 { "initmsg",    "string",  "Initial message to display"  },
	 { "loadedmsg",  "string",      "Message to display"  },
	 { "delay",      "integer",     "Delay in sec. before load (1)"  },
	 { "font",       "string",      "Message font (Courier)"  },
	 { "size",       "string",      "Message font size (18)"  },
	 { "url",        "string",      "URL"  },
	 { "loadedurl",  "string",      "Alternate URL to use if loaded"  },
	 { "bgco",       "hexadecimal", "background color (getBackground)"  },
	 { "fgco",       "hexadecimal", "foreground color (black)"  },
	 
      };
      return info;
   }
   
   /**
    * Error message
    */
   public void errorMsg(String str) {
      showStatus("Error: " + str);
      System.err.println("Error: " + str);
      System.err.flush();
   }
   
   /**
    * Poor-mans debug...
    */
   public void dbg(String str) {
      if (debug) {
	 System.out.println("Debug: " + str);
	 System.out.flush();
      }
   }    
   
   /**
    * Init - process parameters.
    */
   public void init()
   {
      dbg("init()");
      // init private variables
      errMessage= null;
      lastSize = new Dimension(1,1);
      lastMessageLen = 0;

      // check if debug, using "debug" parameter
      String dbgString = getParameter("debug");
      if (dbgString != null) 
	 debug = true;
      
      // get initial message
      initMessage = getParameter("initmsg");
      if (initMessage == null)
	 initMessage = "Loading java enabled page...";
      loadedMessage = getParameter("loadedmsg");
      if (loadedMessage == null)
	 loadedMessage = "Already loaded. Click to force re-load...";

      // get font parameters
      String fontName = getParameter("font");
      if (fontName == null)
	 fontName = "Courier";
      String fontSize = getParameter("size");
      if (fontSize == null)
	 fontSize = "18";
      int size = Integer.valueOf(fontSize).intValue();
      font = new Font(fontName, Font.BOLD, size);
      
      // get colors
      bgColor = readColor(getParameter("bgcolor"), getBackground());
      fgColor = readColor(getParameter("fgcolor"), Color.black);

      // get delay
      String delayString = getParameter("delay");
      if (delayString == null)
	 delayString = "1";
      delay = Integer.valueOf(delayString).intValue();

      String urlParam =  getParameter("url");
      if (urlParam != null) {
	 dbg("url: " + urlParam);
	 try {
	    url = new URL(getDocumentBase(), urlParam);
	 }
	 catch (MalformedURLException e) {
	    // error
	    url = null;
	 }
	 if (url == null) {
	    try {
	       url = new URL(urlParam);
	    }
	    catch (MalformedURLException e) {
	       // error
	       url = null;
	       errMessage = "Cound't create URL for: " + urlParam;
	    }
	 }
      }
      else {
	 dbg("Parameter 'url' not specified.");
	 errMessage = "Parameter 'url' not specified.";
	 url = null;
      }
   }

   /**
    * Convert a Hexadecimal String with RGB-Values to Color
    * Uses aDefault, if no or not enough RGB-Values
    */
   public Color readColor(String aColor, Color aDefault) {
      if ((aColor == null) ||
	  (aColor.charAt(0) != '#') ||
	  (aColor.length() != 7 )) {
	 return aDefault;
      }

      try {
	 Integer rgbValue = new Integer(0);
	 rgbValue = Integer.valueOf(aColor.substring(1, 7), 16);
	 return new Color(rgbValue.intValue());
      }
      catch (Exception e) {
	 return aDefault;
      }
   }


   /**
    * Load URL.
    */
   public boolean loadUrl() {
      urlLoaded = true;		// well, tried to load anyway
      if (url == null)
	 return false;

      dbg("loadUrl()");
      
      // use showDocument() to display new URL.
      // Note: Netscape 2.0b2 problems here... I don't really understand how
      // to catch errors from showDocument()!
      try {
	 getAppletContext().showDocument(url);
      }
      catch (Exception e) {
	 if (url.getRef() == null) {
	    errMessage = "Couldn't load url. Try to re-start Netscape :-(";
	 }
	 else {
	    errMessage = "Couldn't load url: " + url.getRef();
	 }
	 return false;
      }
      return true;
   }

   /**
    * Init size and resize applet.
    */
   public void initSize(Graphics g) {
      dbg("initSize()");
      int width = size().width;
      int height = size().height;

      // check and save sizes
      lastMessageLen = initMessage.length();
      FontMetrics fm = getFontMetrics(font);
      int fh = fm.getHeight();
      int fw = fm.stringWidth(initMessage);
      if (fh > height) 
	 height = fh;
      if (fw > width)
	 width = fw;
      // resize applet (doesn't work with Netscape?)
      resize(width, height);

      // save current size
      lastSize.width = size().width;
      lastSize.height = size().height;
   }
   
   /**
    * Paint the applet
    */
   public void paint(Graphics g) {
      dbg("paint()");
      if ((size().height != lastSize.height) ||
	  (size().width != lastSize.width) ||
	  (lastMessageLen != initMessage.length()))
	 // init (or re-init) sizes if changed
	 initSize(g);
      
      // set background
      g.setColor(bgColor);
      g.fillRect(0, 0, size().width, size().height);
      // set font
      g.setFont(font);
      
      if (errMessage != null) {
	 // Error!
	 initSize(g);
	 // draw error message
	 g.setColor(Color.red);
	 String msg = "Error: " + errMessage;
	 g.drawString(msg, 2, size().height/2+font.getSize()/2);
      }
      else {
	 // draw message
	 g.setColor(fgColor);
	 g.drawString(initMessage, 2, size().height/2+font.getSize()/2);
      }
   }

   /**
    * Start the applet.
    */
   public void start() {
      dbg("start()");
      thread = new Thread(this);
      thread.start();
   }

   /**
    * Stop the applet.
    */
   public void stop() {
      dbg("stop()");
      thread = null;
   }

   /**
    * run - main
    */
   public void run() {
      dbg("run()");
      // we only loop until loaded
      while (!urlLoaded) {
	 // repaint message
	 repaint();
	 // and delay before load of URL (to give message a chance to
	 // be displayed)
	 try {
	    Thread.sleep(delay*1000);
	 }
	 catch (InterruptedException e) {}
	 // load URL
	 if (!urlLoaded && errMessage == null) {
	    // Not currently loaded and no error message; load!
	    if (loadUrl()) {
	       // loaded; reset message
	       initMessage = loadedMessage;
	    }
	 }
      }
   }

   /**
    * Mouse up - load URL
    */
   public boolean mouseUp(Event evt, int x, int y) {
      if (loadUrl()) {
	 // loaded; reset message
	 initMessage = loadedMessage;
      }
      return true;
   }
}
