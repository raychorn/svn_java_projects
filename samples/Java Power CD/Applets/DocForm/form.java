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
