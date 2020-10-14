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
