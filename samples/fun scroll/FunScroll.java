/*
 * Copyright (c) 1995 by Jan Andersson, Torpa Konsult AB.
 *
 * Permission to use, copy, and distribute this software for
 * NON-COMMERCIAL purposes and without fee is hereby granted
 * provided that this copyright notice appears in all copies.
 */
import java.applet.*;
import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.io.*;
import java.net.*;


/**
 * FunScroll - A Funnier (?) scrolling text applet.
 *
 * @version 1.15 96/05/16
 * @author  Jan Andersson (janne@torpa.se)
 * 
 */
public class FunScroll extends Applet implements Runnable 
{
   static int MAX_LINES = 50;	// maximum no. of line parameters
   Image bgImage = null;	// backgound image
   MediaTracker mediaTracker;   // to track loading of backgound image
   Thread thread = null;	// animation thread
   boolean suspended = false;	// animation thread suspended
   int threadDelay = 100;	// animation thread delay 
   Vector animatedTexts = null;	// animated texts
   Vector urlStrings = null;	// associated url's
   String urlTarget = null;	// target widow or frame
   int noOfTexts = 0;		// number of texts
   int currentTextIndex = 0;	// current text index
   FunScrollAnimatedText currentText;	// current text instance
   static boolean debug = false;// debug flag
   				// "off-screen"...
   Image offImage;		// ... image
   Graphics offGraphics;	// ... graphics
   Dimension offSize;		// ... size
   
   /**
    * Init applet
    */
   public void init()
   {
      Vector lineVector = new Vector();
      urlStrings = new Vector();
      
      String par = getParameter("lineData");
      if (par != null) 
	 initLineParametersFromInputURL(par, lineVector, urlStrings);
      else 
	 initLineParameters(lineVector, urlStrings);

      // get frame/window target
      urlTarget = getParameter("target");

      // get background image
      par = getParameter("bgImage");
      if (par != null) {
	 bgImage = getImage(getCodeBase(), par);
	 mediaTracker = new MediaTracker(this);
	 mediaTracker.addImage(bgImage, 0);
      }

      // get color parameters
      Color fg = readColor(getParameter("fgColor"), Color.black);
      Color bg = readColor(getParameter("bgColor"), getBackground());
      setBackground(bg);
      setForeground(fg);

      // get font parameters
      String fontName = getParameter("font");
      if (fontName == null)
	 fontName = "TimesRoman";
      String fontSize = getParameter("size");
      if (fontSize == null)
	 fontSize = "22";
      int size = Integer.valueOf(fontSize).intValue();
      String fontStyle = getParameter("style");
      int style = Font.BOLD;
      if (fontStyle != null) {
	 if (fontStyle.equalsIgnoreCase("plain"))
	    style = Font.PLAIN;
	 else if (fontStyle.equalsIgnoreCase("bold"))
	    style = Font.BOLD;
	 else if (fontStyle.equalsIgnoreCase("italic"))
	    style = Font.ITALIC;
      }
      Font font = new Font(fontName, style, size);

      // get animation thread delay time
      par = getParameter("delay");
      if (par != null) 
	 threadDelay = Integer.valueOf(par).intValue();

      // get dx/dy movement
      int dx = 3;
      int dy = 2;
      par = getParameter("dx");
      if (par != null) 
	 dx = Integer.valueOf(par).intValue();
      par = getParameter("dy");
      if (par != null) 
	 dy = Integer.valueOf(par).intValue();

      // get delimiters string
      String delim = getParameter("delim");

      // create animated texts
      createAnimatedTexts(lineVector, font, fg, bg, dx, dy, delim);
   }

   /**
    * Gets a parameter of the applet.
    *
    * Use this function to overload java.applet.Applet.getParameter
    * to handle ISO Latin 1 characters correctly in Netscape 2.0.
    * Following a suggestion from Peter Sylvester,
    * Peter.Sylvester@edelweb.fr.
    *
    * Note: this is a work-a-round for a bug in Netscape and should
    *       be removed!
    */
   public String getParameter(String s) {
      String tmp = super.getParameter(s);
      if (tmp == null)
	 return null;
      char ec[] = tmp.toCharArray();
      for (int i=0; i < ec.length; i++) {
	 if (ec[i] >= 0xff00) 
	    ec[i] &= 0x00ff ;
      }
      return(new String(ec)) ;
   }

   /**
    * Init unparsed line parameters (Vector of Strings) and
    * (possibly) associated url's.
    */
   protected void initLineParameters(Vector lineVector, Vector urlVector)
   {
      // get unparsed line parameters
      dbg("get line parameters...");
      for (int i=0; i<MAX_LINES; i++) {
	 String lineParName = "line" + i;
	 String linePar = getParameter(lineParName);
	 String urlParName = "url" + i;
	 String urlPar = getParameter(urlParName);
	 if (linePar != null) {
	    dbg("  " + lineParName + ":" + linePar);
	    lineVector.addElement(linePar);
	    dbg("  " + urlParName + ":" + urlPar);
	    urlVector.addElement(urlPar);
	 }
      }

      if (lineVector.size() <= 0)
	 // assume no line parameter provided; use default
	 initDefaultLineParameters(lineVector);
   }

   /**
    * Init unparsed line parameters (Vector of Strings) and
    * (possibly) associated url's from input file.
    */
   protected void initLineParametersFromInputURL(
      String urlString, Vector lineVector, Vector urlVector) {
      // create URL
      URL url = null;
      DataInputStream is = null;
      
      // 1'st, try URL in context of document
      try {
	 url = new URL(getDocumentBase(), urlString);
	 is = new DataInputStream(url.openStream());
      } catch (Exception e) {
	 is = null;
      }
      
      if (is == null) {
	 // then try URL directly
	 try {
	    url = new URL(urlString);
	    is = new DataInputStream(url.openStream());
	 } catch (Exception e) {
	    initURLErrorLineParameters(urlString, lineVector);
	    return;
	 }
      }

      // read from input stream
      try {
	 String line = null;
	 line = is.readLine();
	 while (line != null) {
	    // add to line vector
	    if (line.length() > 0)
	       lineVector.addElement(line);
	    line = is.readLine();
	    // add to url vector
	    if (line != null && line.length() > 4 &&
		line.substring(0, 4).equalsIgnoreCase("URL:")) {
	       urlVector.addElement(line.substring(4));
	       line = is.readLine();
	    }
	    else {
	       urlVector.addElement(null);
	    }
	 }
	 is.close();
      }
      catch (IOException e) {
      }

      if (lineVector.size() <= 0)
	 // assume no line parameter provided; use error message
	 initURLErrorLineParameters(urlString, lineVector);
   }
   
   /**
    * Init default line parameters (Vector of Strings).
    * Used when not line parameters specified.
    */
   protected void initDefaultLineParameters(Vector v) {
      v.addElement("<25>" + getAppletInfo());
      v.addElement("<Text can be displayed as normal text");
      v.addElement("<nervous><color=#FF0000><25>or as this - nervous" +
	 "(and in another color)");
      v.addElement("<sine-wave><25>or as this - sine-wave");
      v.addElement("<up>We can scroll up, down, left and right<up>");
      v.addElement("We can start like this, and move down<down>");
      v.addElement("<up>We can also use more than one line\\nlike this.<up>");
      v.addElement("<down><20>Another feature -" +
	 "\"exploding\" chacters...<explode>");
      v.addElement("<left><10><sine-wave>Fun? I think so..." +
	 " at least a little bit.");
   }

   /**
    * Init error line parameters (Vector of Strings).
    * Used at error, when trying to get input from URL.
    */
   protected void initURLErrorLineParameters(String url, Vector v) {
      v.addElement("<nervous><color=#FF0000>Error!");
      v.addElement("<40>Could not read url: " + url);
   }
   
   /**
    * Applet Info.
    */
   public String getAppletInfo() {
      return "FunScroll.java 1.15 96/05/16, by janne@torpa.se.";
   }   

   /**
    * Parameter Info.
    */
   public String[][] getParameterInfo() {
      // More should be added...
      String[][] info = {
	 {"line0",   "string", "Message line 0" },
	 {"line<n>", "string", "Message line <n>" },
	 {"url0",    "string", "URL 0" },
	 {"url<n>",  "string", "URL <n>" },
	 {"lineData","string", "Message line data file" },
	 {"delim",   "string", "Delimiter string (<>)" },
	 {"font",    "string", "Message font (TimesRoman)" },
	 {"style",   "string", "Message font style (bold)" },
	 {"size",    "int",    "Message font size (22)" },
	 {"delay",   "int",    "Animation delay time in millisec. (100)" },
	 {"dx",      "int",
	        "No of pixels to move horizontally for each animation (2)" },
	 {"dy",      "int",
	        "No of pixels to move vertically for each animation (1)" },
	 {"fgColor", "hex",    "Foreground Color" },
	 {"bgColor", "hex",    "Background Color" },
      };
      return info;
   }
   
   /**
    * Convert a Hexadecimal String with RGB-Values to Color
    * Uses aDefault, if no or not enough RGB-Values
    */
   public Color readColor(String aColor, Color aDefault) {
      if (aColor == null)
	 return aDefault;

      Integer rgbValue = null;
      try {
	 if (aColor.startsWith("#")) 
	    rgbValue = Integer.valueOf(aColor.substring(1), 16);
	 else if (aColor.startsWith("0x")) 
	    rgbValue = Integer.valueOf(aColor.substring(2), 16);
      } catch (NumberFormatException e) {
	 rgbValue = null;
      }
      
      if (rgbValue == null)
	 return aDefault;
      
      return new Color(rgbValue.intValue());
   }

   
   
   /**
    * Create animated text vector. I.e vector with FunScrollAnimatedText
    * instances.
    */
   public void createAnimatedTexts(Vector lines, Font font,
				   Color fg, Color bg,
				   int dx, int dy,
				   String delim)
   {
      noOfTexts = 0;
      animatedTexts = new Vector(lines.size());
      dbg("Creating Animated Text...");
      for (int i=0; i<lines.size(); i++) {
	 dbg("  " + (String) lines.elementAt(i));
         animatedTexts.addElement(
            new FunScrollAnimatedText(
	       this, (String) lines.elementAt(i), font,
	       fg, bg, dx, dy, delim));
	 noOfTexts++;
      }
      currentTextIndex = 0;
      currentText = (FunScrollAnimatedText)
	 animatedTexts.elementAt(currentTextIndex);
   }
   

   /**
    * Animate the texts
    */
   public void animate(Graphics g, int width, int height) {
      // update current text
      if (currentText.update(g)) {
	 // done; get next text
	 currentTextIndex++;
	 if (currentTextIndex >= noOfTexts)
	    currentTextIndex = 0;
	 currentText = (FunScrollAnimatedText)
	    animatedTexts.elementAt(currentTextIndex);
	 currentText.reset(width, height);
      }
   }

   /**
    * Paint tiled background image.
    * Based on code by Tony Kolman, 02/20/96.
    *
    * Note: there are performance problems here.
    */
   public void paintTiledImage(Graphics g, Image im, int width, int height) {
      int imgw = im.getWidth(null);
      int imgh = im.getHeight(null);
      if (imgw > 0 && imgh > 0) {
	 for (int x = 0; x < width; x += imgw) {
	    for (int y = 0; y < height; y += imgh) {
	       g.drawImage(im, x, y, null );
	    }
	 }
      }
   }

   /**
    * Paint last animation
    */
   public void paint(Graphics g) {
      if (offImage != null)
	 g.drawImage(offImage, 0, 0, null);
   }

   /**
    * Update a frame of animation
    *
    */
   public final synchronized void update (Graphics g)
   {
      Dimension d = size();
      
      // Create the offscreen graphics context
      if((offImage == null) ||
	 (d.width != offSize.width) ||
	 (d.height != offSize.height)) {

	 // create off-screen graphics context
	 offSize = d;
	 offImage = createImage(d.width, d.height);
	 offGraphics = offImage.getGraphics();
	    
	 // reset Animated Text item
	 currentText.reset(d.width, d.height);

      }
      
      // erase previous image
      offGraphics.setColor(getBackground());
      offGraphics.fillRect(0, 0, d.width, d.height);
      
      if ((bgImage != null) && 
	  (mediaTracker.statusID(0, true) & MediaTracker.COMPLETE) != 0) {
	 // background image loaded; paint it
	 long tm = System.currentTimeMillis();
	 paintTiledImage(offGraphics, bgImage, d.width, d.height);
	 dbg("time to paint image:" + (System.currentTimeMillis() - tm));
      }
      
      // animate text
      animate(offGraphics, d.width, d.height);
      
      // Paint the image onto the screen
      g.drawImage(offImage, 0, 0, null);
   }

   /**
    * Run the loop. This method is called by class Thread.
    */
   public void run() {
      // Remember the starting time
      long tm = System.currentTimeMillis();
      while (Thread.currentThread() == thread) {
	 // Repaint. I.e call update() to go trough one frame of
	 // animation.
	 repaint();
	 
	 // Delay depending on how far we are behind (to assure
	 // we really delay as much as requested).
	 try {
	    tm += threadDelay;
	    Thread.sleep(Math.max(0, tm - System.currentTimeMillis()));
	 } catch (InterruptedException e) {
	    break;
	 }
      }
   }
   
   /**
    * Start the applet by forking an animation thread.
    */
   public void start() {
      if (thread == null) {
	 thread = new Thread(this);
	 thread.start();
      }
   }
   
   /**
    * Stop the applet. The thread will exit because run() exits.
    */
   public void stop() {
      thread = null;
   }
   
   /**
    * Take care of mouse-up event to handle Suspend/Resume.
    */
   public boolean mouseUp(Event evt, int x, int y) {
      String urlString = null;
      if (currentTextIndex < urlStrings.size())
	 urlString = (String) urlStrings.elementAt(currentTextIndex);
      
      // handle Suspend/Resume
      if (suspended) {
	 thread.resume();
	 suspended = false;
      }
      else if (urlString == null) {
	 thread.suspend();
	 suspended = true;
      }

      if (suspended)
	 // show version when suspended (sneak promotion ;-)
	 showStatus(getAppletInfo());
      else if (urlString != null)
	 // show document as specified in urlString
	 showDocument(urlString);
      
      return true;
   }

   /**
    * Take care of mouse-enter event to handle show URL (if specified)
    */
   public boolean mouseEnter(Event evt, int x, int y) {
      showUrl();
      return true;
   }

   /**
    * Display current url in status line.
    */
   void showUrl() {
      // display current url if specified
      if (currentTextIndex < urlStrings.size()) {
	 String urlString =
	    (String) urlStrings.elementAt(currentTextIndex);
	 if (urlString != null) {
	    String tmp = urlString.toUpperCase();
	    int tIndex = tmp.indexOf("TARGET=");
	    if (tIndex > 0) 
	       urlString = urlString.substring(0, tIndex);
	    showStatus(urlString);
	 }
	 else
	    showStatus("");
      }
   }
   
   /**
    * Show document as specified in URL string
    */
   void showDocument(String urlString) {
      // check if target option specified in URL string
      String target = null;
      String tmp = urlString.toUpperCase();
      int tIndex = tmp.indexOf("TARGET=");
      if (tIndex > 0) {
	 target = urlString.substring(tIndex+7);
	 urlString = urlString.substring(0, tIndex);
	 target = target.trim();
	 dbg("target:" + target);
      }

      if (target == null)
	 // use target provided as parameter
	 target = urlTarget;

      // try to get url in context of current document
      URL url = null;
      try {
         url = new URL(getDocumentBase(), urlString);
      }
      catch (MalformedURLException e) {
         showStatus(e.getMessage());
         url = null;
      }
      if (url == null) {
         // next, try to get url directly 
         try {
            url = new URL(urlString);
         }
	 catch (MalformedURLException e) {
            showStatus(e.getMessage());
            url = null;
         }
      }
      
      // Load URL, using showDocument()
      if (url != null) {
	 showStatus("Loading: " + urlString + "...");
	 if (target == null)
	    getAppletContext().showDocument(url);
	 else
	    getAppletContext().showDocument(url, target);
      }
   }
   

   /**
    * Simple debug...
    */
   static public void dbg(String str) {
      if (debug) {
	 System.out.println("Debug: " + str);
	 System.out.flush();
      }
   }  
}

/**
 * Attributes of FunScroll Animated Text
 */
class FunScrollTextAttr
{
				// scroll styles:
   static final int NONE = 0;	// no scrolling (default)
   static final int LEFT = 1;	// scroll left
   static final int RIGHT = 2;	// ... right
   static final int UP = 3;	// ... up
   static final int DOWN = 4;	// ... down
   static final int EXPLODE = 5;// explode (only for endScroll)
				  // text styles:
   static final int NORMAL = 0;	  // normal (default)
   static final int NERVOUS = 1;  // "nervous" text
   static final int SINEWAVE = 2; // sine-wave text

   String msg = "";		// message line
   String delimiters = "<>";	// used delimiters (default is "<>")
   int startScroll = NONE;	// start scroll style
   int endScroll = NONE;	// end scroll style
   int showDelay = 10;		// start delay
   int endDelay = -1;		// end delay
   int style = NORMAL;		// text style
   String color = null;		// text color
   
   public FunScrollTextAttr(String line, String delim)
   {
      if (delim != null) {
	 // used specified delimiter
	 delimiters = delim;
      }
      parse(line);
   }

   public String msg()
   {
      return msg;
   }
   
   public int startScroll()
   {
      return startScroll;
   }
   
   public int endScroll()
   {
      return endScroll;
   }
   
   public int showDelay()
   {
      return showDelay;
   }
   
   public int endDelay()
   {
      return endDelay;
   }

   public int style()
   {
      return style;
   }

   public String color()
   {
      return color;
   }
   
   void parse(String line)
   {
      StringTokenizer st = new StringTokenizer(line, delimiters);
      boolean gotText = false;
      while (st.hasMoreTokens()) {
	 int scroll = -1;
	 String token = st.nextToken();

	 // get scroll style
	 if (token.equalsIgnoreCase("left")) 
	    scroll = LEFT;
	 else if (token.equalsIgnoreCase("right")) 
	    scroll = RIGHT;
	 else if (token.equalsIgnoreCase("up")) 
	    scroll = UP;
	 else if (token.equalsIgnoreCase("down")) 
	    scroll = DOWN;
	 else if (gotText && token.equalsIgnoreCase("explode")) 
	    scroll = EXPLODE;
	 if (scroll >= 0) {
	    if (!gotText)
	       startScroll = scroll;
	    else
	       endScroll = scroll;
	    continue;
	 }

	 // get text style
	 if (token.equalsIgnoreCase("nervous")) {
	    style = NERVOUS;
	    continue;
	 }
	 if (token.equalsIgnoreCase("sine-wave")) {
	    style = SINEWAVE;
	    continue;
	 }
	 
	 // get color
	 if (token.length() > 6 &&
	     token.substring(0,6).equalsIgnoreCase("color=")) {
	    color = token.substring(6);
	    continue;
	 }

	 // check if integer, if so assume delay value
	 boolean isInt = true;
	 for (int i=0; i<token.length(); i++) {
	    int digit = Character.digit(token.charAt(i), 10);
	    if (digit < 0) {
	       // not a digit
	       isInt = false;
	       break;
	    }
	 }
	 if (isInt) {
	    try {
	       if (!gotText)
		  showDelay = Integer.parseInt(token);
	       else
		  endDelay = Integer.parseInt(token);
	    } catch (NumberFormatException ne) {}
	    continue;
	 }
	 else {
	    // assume text string parsed
	    if (!gotText) {
	       msg = token;
	       gotText = true;
	    }
	 }
      }
   }

}

/**
 * FunScrollAnimatedText - FunScroll Animated text class
 */
class FunScrollAnimatedText
{
				// states:
   static final int START = 0;	// start sequence
   static final int SHOW = 1;	// show sequence
   static final int END = 2;	// end sequence
   static final int DONE = 3;	// done sequence
   int state = START;		// animate state
   FunScroll appl;		// FunScroll applet
   FunScrollTextAttr attr;	// attributes
   char chars[];		// the characters
   int noOfChars;		// number of characters
   int noOfLines = 1;		// number of lines
   int xPos[];			// the x positions
   int yPos[];			// the y positions
   boolean visible[];		// flags set to true if character visible
   int delayCount = 0;		// used to delay for a while
   int width;			// the applet width
   int height;			// the applet height
   int textHeight;		// text height
   int lineHeight;		// text line height
   Color bg;			// background color
   Color fg;			// foreground color
   Font font;			// font
   int maxWidth;		// max width
   int sinDegree;		// used for sin-wave text
   int xStart;			// starting X pos
   int yStart;			// starting Y pos
   int dx;			// x distance to move
   int dy;			// y distance to move

   public FunScrollAnimatedText(FunScroll appl, String line,
				Font font, Color fg, Color bg,
				int dx, int dy, String delim)
   {
      this.appl = appl;
      this.font = font;
      this.fg = fg;
      this.bg = bg;
      this.dy = dy;
      this.dx = dx;

      // parse message line and init attributes
      attr = new FunScrollTextAttr(line, delim);

      appl.dbg("Parsed Attributes:");
      appl.dbg("         msg:" + attr.msg());
      appl.dbg(" startScroll:" + attr.startScroll());
      appl.dbg("   endScroll:" + attr.endScroll());
      appl.dbg("   showDelay:" + attr.showDelay());
      appl.dbg("    endDelay:" + attr.endDelay());
      appl.dbg("       style:" + attr.style());
      appl.dbg("      color:" + attr.color());
      appl.dbg("dy:" + dy + " dx:" + dx);
      
      // get color (if specified)
      if (attr.color() != null) 
	 this.fg = appl.readColor(attr.color(), fg);
       appl.dbg("      color:" + fg);
      
      // init character related varaiables
      String msg = attr.msg();
      noOfChars = msg.length();
      chars = new char[noOfChars];
      msg.getChars(0, noOfChars, chars, 0);
      xPos = new int[noOfChars];
      yPos = new int[noOfChars];
      visible = new boolean[noOfChars];
      FontMetrics fm = appl.getFontMetrics(font);
      if (attr.style() == FunScrollTextAttr.NERVOUS ||
	 attr.style() == FunScrollTextAttr.SINEWAVE)
	 // need some extra space here!
	 textHeight = 4 + font.getSize();
      else
	 textHeight = font.getSize();
      lineHeight = font.getSize();
      int currXPos = 0;
      int currYPos = fm.getAscent();
      boolean escape = false;
      boolean newLine = false;
      for (int i = 0; i < noOfChars; i++) {
	 if (escape) {
	    // we already have an escape character
	    if (chars[i] == 'n') {
	       // got "\n" - line break; i.e line really consists
	       // of more than one line
	       chars[i-1] = ' ';
	       chars[i] = ' ';
	       newLine = true;
	    }
	    escape = false;
	 }
	 else if (chars[i] == '\\') {
	    // escaped characted; wait for next character
	    escape = true;
	 }
	 else {
	    if (newLine) {
	       // we have a new line
	       noOfLines++;
	       textHeight += dy * 2 + font.getSize();
	       currXPos = fm.charsWidth(chars, 0, i);
	       currYPos += fm.getDescent() + fm.getAscent();
	       newLine = false;
	    }
	    if (i > 0)
	       xPos[i] = fm.charsWidth(chars, 0, i) - currXPos;
	    else
	       xPos[i] = currXPos;
	    
	    maxWidth = Math.max(maxWidth, xPos[i]);
	    yPos[i] = currYPos;
	 }
      }
   }

   /**
    * Reset array of x positions 
    */
   void resetX()
   {
      FontMetrics fm = appl.getFontMetrics(font);
      int currXPos = 0;
      int currYPos = (noOfChars > 0) ? yPos[0] : 0;
      for (int i = 0; i < noOfChars; i++) {
	 if (currYPos != yPos[i]) {
	    // new line
	    currXPos = fm.charsWidth(chars, 0, i);
	    currYPos = yPos[i];
	 }
	 if (i > 0)
	    xPos[i] = fm.charsWidth(chars, 0, i) - currXPos;
	 else
	    xPos[i] = currXPos;
      }
   }
   

   /**
    * Reset width and height
    */
   void reset(int width, int height)
   {
      this.width = width;
      this.height = height;
      int scroll = attr.startScroll();
      switch (scroll) {
	 case FunScrollTextAttr.NONE:
	    xStart = (width-maxWidth)/2;
	    yStart = (height-textHeight)/2;
	    break;
	 case FunScrollTextAttr.LEFT:
	    xStart = width-dx;
	    yStart = (height-textHeight)/2;
	    break;
	 case FunScrollTextAttr.RIGHT:
	    xStart = dx;
	    yStart = (height-textHeight)/2;
	    break;
	 case FunScrollTextAttr.UP:
	    xStart = (width-maxWidth)/2;
	    yStart = height;
	    break;
	 case FunScrollTextAttr.DOWN:
	    xStart = (width-maxWidth)/2;
	    yStart = 0-textHeight;
	    break;
      }

      // Reset array of x positions
      resetX();
      
      // reset state
      state = START;
      FunScroll.dbg("State: START");
   }

    /**
    * Update. I.e move and paint.
    */
   boolean update(Graphics g) 
   {
      move();
      paint(g);
      if (state == DONE && delayCount < 0)
	 return true;		// we are done!
      else
	 return false;
   }

   /**
    * Move characters
    */
   void move()
   {
      boolean switchState = false;
      int scroll = FunScrollTextAttr.NONE;
      switch (state) {
	 case START:
	    // start sequence
	    scroll = attr.startScroll();
	    if (scroll == FunScrollTextAttr.NONE) {
	       // no animation;	 just switch state
	       switchState = true;
	    }
	    else {
	       // some kind of animation; check if all characters displ.
	      if (textDisplayed(scroll)) {
		  // yupp; switch state
		  switchState = true;
	       }
	    }
	    if (switchState == true) {
	       // switch state
	       updateVisible(scroll);
	       state = SHOW;
	       FunScroll.dbg("State: SHOW");
	       delayCount = attr.showDelay();
	    }
	    else {
	       // just move text (scroll)
	       moveText(scroll);
	       updateVisible(scroll);
	    }
	    break;
	    
	 case SHOW:
	    // show sequence
	    if (delayCount-- < 0) {
	       // switch state
	       state = END;
	       FunScroll.dbg("State: END");
	    }
	    break;

	 case END:
	    // end sequence
	    // check if all characters still visible
	    if (updateVisible(attr.endScroll()) == 0 ||
		attr.endScroll() == FunScrollTextAttr.NONE) {
	       // none visible or no end animation; switch state
	       state = DONE;
	       FunScroll.dbg("State: DONE");
	       delayCount = attr.endDelay();
	       return;
	    }
	    else {
	       moveText(attr.endScroll());
	    }
	    break;

	 case DONE:
	    // done sequence; just delay
	    delayCount--;
	    break;
      }
   }

   /**
    * Return true if (all) text is displayed
    */
   boolean textDisplayed(int scroll)
   {
      switch (scroll) {
	 case FunScrollTextAttr.LEFT:
	    // scrolling left
	    if (maxWidth > width) {
	       // text is wider that applet width
	       if (maxWidth+xStart < width-4*dx)
		  return true;
	    }
	    else {
	       int appletMidPoint = width/2;
	       int textMidPoint = xStart+maxWidth/2;
	       if (textMidPoint <= appletMidPoint)
		  return true;
	    }
	    break;
	    
	 case FunScrollTextAttr.RIGHT:
	    // scrolling right
	    if (maxWidth > width) {
	       // text is wider that applet width
	       if (xPos[0]+xStart > 4*dx)
		  return true;
	    }
	    else {
	       int appletMidPoint = width/2;
	       int textMidPoint = xStart+maxWidth/2;
	       if (textMidPoint >= appletMidPoint)
		  return true;
	    }
	    break;
	    
	 case FunScrollTextAttr.UP:
	    // scrolling up
	    if (yStart <= (height-textHeight)/2)
	       return true;
	    break;
	    
	 case FunScrollTextAttr.DOWN:
	    // scrolling down
	    if (yStart >= (height-textHeight)/2) 
	       return true;
	    break;
      }
      return false;
   }
   
   /**
    * update array with flags if characters are visible. Return
    * number of visible characters.
    */
   int updateVisible(int scroll)
   {
      int visibleCount = 0;
      
      for (int i = 0; i < noOfChars; i++) {
	 visible[i] = (xPos[i]+xStart > 0 &&
		       xPos[i]+xStart < width &&
		       yPos[i]+yStart+lineHeight > 0  &&
		       yPos[i]+yStart-lineHeight < height);
	 if (visible[i])
	    visibleCount++;
      }
      // special treatment of explode animation
      if (scroll == FunScrollTextAttr.EXPLODE) {
	 // if only 5 or less chars visible (per line) consider this as done
	 if (visibleCount <= (noOfLines*5))
	    visibleCount = 0;
      }
      return visibleCount;
   }
      
   void moveText(int scroll)
   {
      switch (scroll) {
	 case FunScrollTextAttr.LEFT:
	    xStart -= dx;
	    break;
	 case FunScrollTextAttr.RIGHT:
	    xStart += dx;
	    break;
	 case FunScrollTextAttr.UP:
	    yStart -= dy;
	    break;
	 case FunScrollTextAttr.DOWN:
	    yStart += dy;
	    break;
	 case FunScrollTextAttr.EXPLODE:
	    moveExplodeText();
	 break;
      }
   }

   /**
    * Move exploding text
    */
   void moveExplodeText() {
      int mid = noOfChars/2;
      float maxDist = maxWidth/5;
      for (int i = 0; i < mid; i++) {
	 // move to the left
	 float percentOfMax = (float)(mid-i)/mid;
	 xPos[i] -= (int) (percentOfMax * maxDist);
      }
      for (int i = mid; i < noOfChars; i++) {
	 // move to the right
	 float percentOfMax = (float) (i-mid)/mid;
	 xPos[i] += (int) (percentOfMax * maxDist);
      }
   }
   
   /**
    * Paint characters
    */
   void paint(Graphics g)
   {
      g.setFont(font);
      g.setColor(fg);
      
      int style = attr.style();
      if (style == FunScrollTextAttr.SINEWAVE) {
	 // special handling of sine-wave text
	 paintSineWave(g);
      }
      else {
	 for (int i = 0; i < noOfChars; i++) {
	    if (visible[i]) {
	       switch (style) {
		  case FunScrollTextAttr.NERVOUS:
		     // paint as nervous text
		     drawNervousChar(g, i);
		     break;
		  default:
		     // paint as normal text
		     drawNormalChar(g, i);
	       }
	    }
	 }
      }
   }
   
   /**
    * Paint sine-wave text line
    */
   void paintSineWave(Graphics g) {
      int currYPos = (noOfChars > 0) ? yPos[0] : 0;
      int degree = sinDegree;
      for (int i = noOfChars-1; i >= 0; i--) {
	 if (currYPos != yPos[i]) {
	    // new line
	    currYPos = yPos[i];
	    degree = sinDegree;
	 }
	 if (visible[i]) {
	    // draw character
	    int sinHeight = lineHeight/3;
	    int y = (int) (Math.sin(degree*3.1414/180) * sinHeight);
	    g.drawChars(chars, i, 1, xPos[i]+xStart, yPos[i]+yStart+y);
	 }
	 degree -= 15;
	 if (degree <= 0)
	    degree = 360;
      }
      sinDegree -= 15;
      if (sinDegree <= 0)
	 sinDegree = 360;
   }
   
   /**
    * Draw nervous character
    */
   void drawNervousChar(Graphics g, int index) 
   {
      int x = (int)(Math.random() * 2) + xPos[index];
      int y = (int)(Math.random() * 4) + yPos[index];
      g.drawChars(chars, index, 1, x+xStart, y+yStart);
   }

   /**
    * Draw normal character
    */
   void drawNormalChar(Graphics g, int index) 
   {
      g.drawChars(chars, index, 1,
		  xPos[index]+xStart, yPos[index]+yStart);
   }
}

