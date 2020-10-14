import java.net.*;
import java.io.*;
import java.applet.*;
import java.awt.*;
 
 
public class ClientCounter extends Applet implements Runnable {
  String msg = "" ;
  Thread fetcher = null ;
  URL    myurl ;
  Socket s = null ;
  DataInputStream input = null ;
  DataOutputStream output = null ;
  String filename = null ;
 
 
  public void init() {
    try {
      s = new Socket (getDocumentBase().getHost(), 9999 ) ;
      input = new DataInputStream(s.getInputStream());
      output = new DataOutputStream(s.getOutputStream()) ;
    } catch (IOException e) {
      e.printStackTrace() ;
      System.err.println(e.getMessage()) ;
    }
    try {
      myurl = getDocumentBase() ;
      filename = myurl.getFile() ;
      output.writeBytes(filename) ;
      output.writeByte('\n') ;
    } catch (IOException e) {
    }
    try {
      msg = "" ;
      while (true) {
        synchronized (msg) {
          msg = msg + (char) input.readByte() ;
          repaint() ;
        }
     }
    } catch (EOFException e) {
    } catch (IOException e) {
       e.printStackTrace() ;
       System.err.println(e.getMessage());
  }
 
    //  (fetcher = new Thread(this)).start();
  }

  public void stop() {
    try {
      if ( fetcher !=null ) {
         fetcher.stop() ;
         fetcher = null ;
      }
      if ( s !=null ) {
         s.close() ;
         s = null ;
      }
      input  = null ;
      output = null ;
    } catch (IOException e) {
    }
  }
 
 
  public void run() { }
 
  public void start() {
 
   (fetcher = new Thread(this)).start();
    repaint() ;
  }
 
 
  public void paint(Graphics g) {
      Font font1, font2 ;
      FontMetrics fontMetrics1 , fontMetrics2 ;
      int i, x, y ;
      Color mycolor ;
 
      font1 = new java.awt.Font("TimeRoman", Font.PLAIN, 14 ) ;
      font2 = new java.awt.Font("System", Font.BOLD, 24 ) ;
      fontMetrics1 = getFontMetrics(font1) ;
      fontMetrics2 = getFontMetrics(font2) ;
      mycolor = new Color(83, 255, 255 ) ;
      x = 0 ;
      y = fontMetrics2.getHeight() ;
      g.setFont( font1 ) ;
      g.drawString("You are visitor number ", x, y) ;
      x += fontMetrics1.stringWidth("You are visitor number ") ;
      g.setFont( font2 ) ;
      g.setColor(mycolor) ;
      g.drawString(msg, x, y + 3 ) ;
      x += fontMetrics2.stringWidth(msg) + 8 ;
      g.setFont( font1 ) ;
      g.setColor(Color.black) ;
      g.drawString("since 2/22/1996", x, y ) ;
 
  }
}

