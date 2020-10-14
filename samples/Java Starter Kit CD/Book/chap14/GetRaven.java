/* scrolling lists */

import java.awt.*;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;

public class GetRaven extends java.applet.Applet implements Runnable {

  URL theURL;
  Thread runner;
  TextArea ta = new TextArea("Getting text...",30,70);

  public void init() {

    String url = "http://www.lne.com/Web/Java/raven.txt";
    try { this.theURL = new URL(url); }
    catch ( MalformedURLException e) {
      System.out.println("Bad URL: " + theURL);
    }

    add(ta);
  }

  public Insets insets() {
    return new Insets(10,10,10,10);
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

      

