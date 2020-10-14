/*
Simpletext Java Applet.
Applet displays a text file from the web in a text area.
The Text Area is as large as the APPLET Width and Height commands.
There is no animation
It loads quickly
And provides information that can be provided quickly.
Utilizes the following Params
<PARAM NAME = "filename" Value = "URL">
<PARAM NAME = "fontname" Value = "Fontname">
<PARAM NAME = "fontsize" Value = "Fontsize">
This is very useful for placing on a page where you want to have information
added without having to touch the HTML in the page.  It can also be placed into
a table.


 */

import java.awt.*;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;

public class simpletext extends java.applet.Applet implements Runnable
{

  URL theURL;
  Thread runner;
  TextArea ta = new TextArea("Getting text...",7,70);

  public void init() {
    String fname = getParameter("fontname");
    String fsize = getParameter("fontsize");
    int fs = Integer.parseInt(fsize);
    Font tafont = new Font(fname,Font.PLAIN,fs);
    String url = getParameter("filename");
    try { this.theURL = new URL(url); }
    catch ( MalformedURLException e) {
      System.out.println("Bad URL: " + theURL);
    }
  ta.setEditable(false);
  ta.setFont(tafont);
  setLayout(new BorderLayout());
    add("Center",ta);
//    string fnts = getFontList();//not yet implemented in Toolkit
  }

  
  public void start() {
    if (runner == null) {
      runner = new Thread(this);
      runner.start();
    }
  }
  
  public void stop() {
    if (runner != null) {
      runner.stop();
      runner = null;
    }
  }

  public void run() {
    URLConnection conn = null;
    DataInputStream data = null;
    String line;
    StringBuffer buf = new StringBuffer();

    try { 
      conn = this.theURL.openConnection();
      conn.connect();

      ta.setText("Connection opened...");

      data = new DataInputStream(new BufferedInputStream(
		     conn.getInputStream()));

      ta.setText("Reading data...");
      while ((line = data.readLine()) != null) {
	buf.append(line + "\n");
      } 
     ta.setText(buf.toString());

    }

    catch (IOException e) {
      System.out.println("IO Error:" + e.getMessage());
    }
  }
}

      



