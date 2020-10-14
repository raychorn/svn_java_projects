
A short list of the attached files:
doccnv.java	Documentation converter, Application
form.java	Form-like applet
jline.java	Text hider
pointer.java	A sign that expires
ticker.java	Animated text with optional link
waisfr.java	Interface to WAIS cgi-script

All code runs under JDK-beta and Navigator 2.0b1-3, proven with solaris 2.3
and win95. 

Thomas Wendt			MT/FB16
Tel.: ++49561 804 6428		University of Kassel
email: thwendt@mt.e-technik.uni-kassel.de
http://www.uni-kassel.de/fb16/ipm/mt/staff/thwendte.html

/*
 * Permission to use, copy, modify and distribute this software and its
 * documentation without fee is hereby granted provided that:
 * a) this notice with a reference to the original source and 
 *    the author appears in all copies or derivatives of this software.
 * b) a reference to the original source and the author appears in all files
 *    generated using copies or derivatives of this software.
 *
 * THE AUTHOR MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THIS SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. THE AUTHOR SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY ANYBODY AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

import java.io.*;
import java.lang.*;

/**
 * doccnv - converts HTML-Dokuments generated using javadoc<br>
 * <p>
 * Parameters:<pre>
 * -in  afile   from javadoc
 * -out afile   to be generated
 * -api apath   to api-documentation, usually .../java/api/ or 
.../java/apidocs/
 * -i           replaces images with its alternatives
 * -u           unix-style output (lf only)
 *</pre><p>
 * Usage:<br>
 * If api is given, it prepends all references to java.* classes and all 
references to images.
 * Because the commandline must not contain '|', ':' is used instead and 
internally replaced, if it
 * is not part of the protocol. The flag i overrides the api parameter for 
images in such a
 * manner, that the text in the 'alt' parameter of the image is used instead 
of using the image
 * itself. in and out must be given, api and the flags are optional. If 
neither api or the
 * flag exist, out would be a copy of in. in must not equal out.<br>
 *
 * <p>Example: <br>
 * java doccnv -in doccnv.html -out doccnv.htm -api 
file://h:/www/java/apidocs/ -iu<br>
 * 
 * <p>V b1.00, 11/12/95: Creation by Thomas Wendt (thw)<br>
 * V b1.01, 11/18/95: Cleaning up the code by thw<br>
 *
 * <p><a 
href="http://www.uni-kassel.de/fb16/ipm/mt/java/javae.html"><b>origin</b></a> 
of doccnv.
 *
 * @author <a 
href="http://www.uni-kassel.de/fb16/ipm/mt/staff/thwendte.html">Thomas 
Wendt</a>
 * @version b1.01, 11/18/1995
 */
class doccnv {
  // Note: All needs to be static, because this class is never instantiated.

  /** The inputstream. */
  public static DataInputStream is = null;

  /** 
   * The outputstream.
   * Using <a href="java.io.PrintStream">PrintStream</a> would be nice,
   * but it does not allow handling of errors or setting EOL.
   */
  public static DataOutputStream os = null;

  /** EOL for the lines written. Default: cr+lf, if unix flag set: lf. */
  public static String crlf = "\r\n";
    
  /* The filenames */
  static String outputfile = "";
  static String inputfile = "";

  /**
   * Do the job.
   */
  public static void main(String args[]) {
    int i = 0, f, j, p;
    String s;
    boolean replImg = false;
    boolean replApi = false;
    String jPath = "";
    
    System.out.println("doccnv by Thomas Wendt");

    // while argument exist
    while (i < args.length && args[i].startsWith("-")) {
      s = args[i++];
      if (s.equals("-out")) {  // filename of output
        if (i < args.length)
          outputfile = args[i++];
        else
          System.err.println("-out requires a filename");
      } else if (s.equals("-in")) {  // filename of input
        if (i < args.length)
          inputfile = args[i++];
        else
          System.err.println("-in requires a filename");
      } else if (s.equals("-api")) {  // pathname or URL of api, with ending /
        if (i < args.length) {
          jPath = args[i++];
   // '|' cant be given at cmdline, so use and convert ':'
          if ((p = jPath.indexOf(':',6)) > -1) {
            jPath = jPath.substring(0,p)+'|'+jPath.substring(p+1);
          }
          replApi = true;
        } else
          System.err.println("-api requires a pathname or URL");
      } else {  // check options
        for (j = 1; j < s.length(); j++) {
          switch (s.charAt(j)) {
            case 'i':   // replace image with its alt text
              replImg = true;
              break;
            case 'u':   // use unix style output with lf only
              crlf = "\n";
              break;
            default:    // nothing matches: print err msg
              System.err.println("illegal option " + s.charAt(j));
              break;
          }
        }
      }
    }   

    // Quit, if param missing or wrong
    if (inputfile.equals("") || outputfile.equals("") || 
inputfile.equals(outputfile)) {
      quit("Usage: java doccnv [-iu] [-api aURL] -out afile -in afile, out != 
in");
      return;
    } else if (!(replApi || replImg)) {
      quit("Nothing to do!");
      return;
    }

    // Open streams and exit, if error
    try { is = new DataInputStream(new FileInputStream(inputfile)); }
    catch (java.io.IOException e) { quit("Error accessing "+inputfile); }
    try { os = new DataOutputStream(new FileOutputStream(outputfile)); }
    catch (java.io.IOException e) { quit("Error creating "+outputfile); }

    // Init some constants
    String d = ""+'"';  // silly construction, isn't it? (no constructor for 
single char)
    String im = "<img ";
    String src = " src="+d;
    String ja = " href="+d+"java.";
    String alt = " alt="+d;
    String bo = "</body>";
    boolean ok;

    // while not eof
    s = readln(); 
    while (s != null) {

      // Replace or extend img tags. Use while, because multiple img tags may 
exist.
      f = -1;
      while ((f = s.indexOf(im,f+1)) > -1) {
        if (replImg) {
          p = s.indexOf(d,s.indexOf(alt,f))+1;
          s = s.substring(p,s.indexOf(d,p));
        } else if (replApi) {
          p = s.indexOf(d, s.indexOf(src,f))+1;
          s = s.substring(0,p)+jPath+s.substring(p);
        }
      }

      // Extend refs to the API. Use while, because multiple refs may exist.
      f = -1;
      while (replApi && (f = s.indexOf(ja,f+1)) > -1) {
        p = s.indexOf(d,f)+1;
        s = s.substring(0,p)+jPath+s.substring(p);
      }

      // Last line? Then output some info about the program.
      if (s.indexOf(bo) > -1) {
        String fi = "<a href="+d+"http://www.uni-kassel.de/fb16/ipm/mt/";
        String la = ".html"+d+">";
        println("<p><hr><p>Converted using "+fi+"java/javae"+la+"doccnv</a> 
by");
        println(fi+"staff/thwendte"+la+"Thomas Wendt</a>");
      }

      // Output the converted line and read in the next one.
      println(s);
      s = readln();
    }

    // Flush and/or close the streams.
    try {
      os.flush();
      os.close();
    }
    catch (java.io.IOException e) { quit("Error flushing/closing 
"+outputfile); }
    try { is.close(); }
    catch (java.io.IOException e) { }
  }

  /**
   * Write a line to outputstream with error handling
   * Exit, if error occurs.
   * @see #os
   * @param s the line to write
   */
  public static void println(String s) {
    try { os.writeBytes(s+crlf); }
    catch (java.io.IOException e) { quit("Error writing "+outputfile); }
  }

  /**
   * Read a line from inputstream with error handling
   * @see #is
   * @return The line read or null, if eof or error.
   */
  public static String readln() {
    String s;
    try { s = is.readLine(); }
    catch (java.io.IOException e) { return null; }
    return s;
  }

  /**
   * Exit
   * Print an error message, if one is given.
   * @param s The text describing the Error. If null, exitcode = 0; 1 
otherwise.
   */
  public static void quit(String s) {
    Runtime r = Runtime.getRuntime();
    if (s != null) {
      System.out.println();
      System.err.println(s);
      r.exit(1);
    } else r.exit(0);
  }

}
----------
X-Sun-Data-Type: default-app
X-Sun-Data-Description: default
X-Sun-Data-Name: form.java
X-Sun-Charset: us-ascii
X-Sun-Content-Lines: 184

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
import java.io.*;
import java.net.*;
import java.util.*;
import java.applet.*;

/**
 * form - uses java as a form-substitute<br>
 * 
 * <p>Parameters:<br>
 * desc=String         Question for Checkbox (Question)<br>
 * subj=String         Fixed subject of message (Form)<br>
 * comment=String      Label for Textarea (Comment)<br>
 * href=String         recipient script/base path (don't send)<br>
 * type=String         email, cgi (statistics)<br>
 * recip=String        email adress of recipient (don't send)<br>
 * buttons=String[2][] Button desc/transmit (Answer,x)<br>
 *
 * <p>v1.00 prebeta by Thomas Wendt (thw)<br>
 * v1.10 changed to use statistics instead of smtp by thw<br>
 * v1.20, 10/19/95 use awt-buttons and input-line by thw<br>
 * v1.21, 11/18/95 cleaning up the code by thw<br>
 *
 * <p><a 
href="http://www.uni-kassel.de/fb16/ipm/mt/java/javae.html"><b>origin</b></a> 
of form.<br>
 *
 * @author <A 
HREF="http://www.uni-kassel.de/fb16/ipm/mt/staff/thwendt.html">Thomas Wendt</A>
 * @version b1.21 , 11/18/95
 */
public class form extends Applet {
  /** The base URL or the recipient of the form. */
  public String base;

  /** The messages to send. */
  public String msgs[] = new String[10];

  /** The Text of the checkboxes. */
  public String descs[] = new String[10];

  /** The Subject of the message. */
  public String subj;

  /** The Recipient of the message. */
  public String recip;

  /** The buttons group */
  public CheckboxGroup check = new CheckboxGroup();

  /** The buttons count. */
  public int cntbu=0;

  /** The Text. */
  public TextField text;

  /** Use email? */
  public boolean doMail = false;

  /** Use cgi script? */
  public boolean doCgi = false;

  /** Read the attributes and setup the UI. */
  public void init () {
    // figure out what they want from me and set up according
    String attr = getParameter("type");
    if (attr != null) {
      if (attr.equalsIgnoreCase("cgi")) { doCgi = true; }
// not impl. yet   else if (attr.equalsIgnoreCase("email")) { doMail = true; } 
    }
    if ((base = getParameter("href")) == null && (!doMail)) { return; }
    if ((recip = getParameter("recip")) == null && (doMail || doCgi)) { 
return; }
    if ((subj = getParameter("subj")) == null) { subj = "Form"; }
    setLayout(new BorderLayout());

    // our button
    Panel pb = new Panel();
    pb.add(new Button("Send"));

    // set up the textfield
    if ((attr = getParameter("comment")) == null) { attr = "Comment"; }
    Panel pt = new Panel();
    Label la = new Label(attr, Label.LEFT);
    pt.add(la);
    pt.add(text = new TextField(30));

    // we dont transmit text with statistics, but we need the field for 
layouting
    // Note: We can transmit text, it _is_ unsecure. The people maintaining 
_our_
    // webserver will kill me, if _I_ do it in this way.
    if (!doMail && !doCgi) {
      la.disable();
      text.disable();
    }
    
    // setup checkboxes
    if ((attr = getParameter("buttons")) == null) { attr = "Answer,x"; }
    StringTokenizer st = new StringTokenizer(attr, ",");
    while (st.hasMoreTokens()) {
      descs[cntbu] = st.nextToken();
      msgs[cntbu] = st.nextToken();
      cntbu++;
    }
    if ((attr = getParameter("desc")) == null) { attr = "Question"; }
    Panel pc = new Panel();
    pc.add(new Label(attr, Label.LEFT));
    for (int i=0; i<cntbu; i++) { pc.add(new Checkbox(descs[i], check, i<1)); }

    // put all together in the applet
    add("North", pc);
    add("Center",pt);
    add("South",pb);
  }

  /** Parameter Info. */
  public String[][] getParameterInfo() {
    String[][] info = {
      {"desc",    "String",      "Question for Checkbox (Question)"},
      {"subj",    "String",      "Fixed subject of message (Form)"},
      {"comment", "String",      "Label for Textarea (Comment)"},
      {"href",    "String",      "base/script to send (dont send)"},
      {"type",    "String",      "email, cgi (statistics)"},
      {"recip",   "String",      "email of recipient (dont send)"},
      {"buttons", "String[2][]", "Button desc/transmit (Answer,x)"},
    };
    return info;
  }

  /** Applet Info. */
  public String getAppletInfo() {
    return "form.java, V b1.21, 11/18/95 by Thomas Wendt, 
http://www.uni-kassel.de/fb16/ipm/mt/staff/thwendte.html";
  }    

  /** Send the Form as email (not working now). */
  public void sendMail(String S) { ;}

  /** Send the Form using script get or statistics. */
  public void send() {
    // get label of checked box
    String s = check.getCurrent().getLabel();
    // and search, what to send in this case.
    for (int i = 0; i < cntbu; i++) { if (s.equals(descs[i])) { s = msgs[i]; } 
}
    showStatus("Connecting");
    if (doMail) { sendMail(s); }
    else {
      try {
        // calc the 'URL' of the script to get a dummy document
        // MAY ANYBODY GIVE ME A SCRIPT FOR THIS ????
        if (doCgi) { s = 
base+"&R"+recip+"&S="+subj+"&B="+s+"&T="+text.getText(); }
        // or just touch another dummy document for statistics.
        else { s = base+s+".html"; }
        URL url_ = new URL(s);
        URLConnection urlc = url_.openConnection();
        InputStream is = urlc.getInputStream();
        byte b[] = new byte[400];
        int i = is.read(b);    // get the stuff, needed ??
        is.close();
      }
      catch (Exception e) { }
    }
    // remove everything to avoid duplicates
    removeAll();
    showStatus(" ");
  }

  /** Send the form, if 'send' button pressed. */
  public boolean action(Event evt, Object obj) {
    if ("Send".equals(obj)) {
      send();
      return true;
    } else { return false; }
  }
}
----------
X-Sun-Data-Type: default
X-Sun-Data-Description: default
X-Sun-Data-Name: jline.java
X-Sun-Charset: us-ascii
X-Sun-Content-Lines: 95

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
import java.util.*;
import java.net.*;
import java.io.*;
import java.lang.*;
import java.applet.Applet;

/**
 * waisfr - Simple frontend to wais scripts<br>
 * 
 * <p>V b1.00, 10/16/95: Creation by Thomas Wendt (thw)<br>
 * V b1.01, 11/18/95: Cleaning up the code; removed appletviewer support by 
thw<br>
 *
 * <p><a 
href="http://www.uni-kassel.de/fb16/ipm/mt/java/javae.html"><b>origin</b></a> 
of waisfr.<br>
 *
 * @author 	<A 
HREF="http://www.uni-kassel.de/fb16/ipm/mt/staff/thwendte.html">Thomas 
Wendt</A>
 * @version 	b1.01, 11/18/95
 */
public class waisfr extends Applet {
  /** Flag, if currently operation is in progress. */
  public boolean atwork = false;

  /** The URL of the script (relative to the host). */
  public String cgi;

  /** The host of the script. */
  public String host;

  /** The database to search. */
  public String db;

  /** Last type of search. */
  public int lasti = -1;

  /** Last search topic. */
  public String lastq = null;

  /** The textfild for the search topic. */
  public TextField textField;

  /** Lets you choose the type of search. */
  public Choice chooser;

  public void init() {
    String at = getParameter("cgi");
    cgi = (at != null) ? at : "/cgi-bin/SFgate";
    db = ((at= getParameter("data")) != null) ? at : "www.uni-kassel.de";
    host = ((at = getParameter("host")) != null) ? at : "www.uni-kassel.de";

    //Build the left half of this applet.
    textField = new TextField(10);
    Panel left = new Panel();
    left.add(new Label("Search for", Label.CENTER));
    left.add(textField);

    //Build the right half of this panel.
    Panel right = new Panel();
    chooser = new Choice();
    chooser.addItem("Body");
    chooser.addItem("Title");
    chooser.addItem("Address");
    right.add(new Label(" at", Label.CENTER));
    right.add(chooser);

    //Put everything in this panel.
    setLayout(new BorderLayout());
    add("North", new Label("WAIS frontend", Label.CENTER));
    add("West", left);
    add("Center", right);
    add("South", new Button("Start Query"));
  }

  /** Parameter Info. */
  public String[][] getParameterInfo() {
    String[][] info = {
      {"host", "String", "Host"},
      {"cgi",  "String", "Script for searching"},
      {"data", "String", "Database to search"},
    };
    return info;
  }

  /** Applet Info. */
  public String getAppletInfo() {
    return "waisfr.java, V b1.01, 11/18/95 by Thomas Wendt, 
http://www.uni-kassel.de/fb16/ipm/mt/staff/thwendte.html";
  }    

  /** Query the server and show the Results. */
  public void query() {
    int i = chooser.getSelectedIndex();
    String sf = textField.getText();

    // first do some preprocessing
    if (i == lasti && sf.equals(lastq)) {
      showStatus("Identical Queries not allowed");
      return;
    }
    if (sf.length() < 5) {
      showStatus("Your topic is too short");
      return;
    }
    // remember this topic
    lasti = i;
    lastq = sf;
    // set up the query
    String ti = "&ti=";
    String ad = "&ad=";
    String text = "&text=";
    switch (i) {
      case 0:
        text += sf;
        break;
      case 1:
        ti += sf;
        break;
      case 2:
        ad += sf;
        break;
    }
    // build the 'URL'
    String us = cgi+"?database="+db+"%2Fwww-pages"+ti+ad+text+"&maxhits=5";
    URL u;
    try { u = new URL("http", host, -1, us); }
    catch (Exception e) { u = null; }
    // return, if error
    if (u == null) { return; }
    // else show the results. May be defunctional with early browsers.
    getAppletContext().showDocument(u);
  }

  /** Respond to user actions. */
  public boolean handleEvent(Event e) {
    // if the one and only button is pressed
    if (e.target instanceof Button) {
      // and if the user is not too fast
      if (!atwork) {
        atwork = true;
        // ask the server
        query();
        atwork = false;
      }
      return true;
    } else { return false; }
  }
}
