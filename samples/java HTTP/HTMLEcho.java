package amm.http;

import java.io.*;
import java.awt.*;

/**
 * This class shows a frame that allows the user to enter
 * a URL.  It will connect to that URL and if the content
 * type is text/html or text/plain it will print the
 * content to the Standard Output.
 * @see amm.http.HTTPConnect
 * @see amm.http.HTTPClient
 * @author Aaron Mulder
 * @author ammulder@princeton.edu
 * @author http://www.princeton.edu/~ammulder/java.hmtl
 */
public class HTMLEcho extends Frame implements HTTPClient {
  /**
   * The status string to be displayed when no connections
   * are underway.
   */
  protected static final String READY="Ready to open connection.";
  /**
   * The TextField that holds to URL to be retrieved.
   */
  protected TextField url;
  /**
   * The Label that displays status messages.
   */
  protected Label stat;
  /**
   * The Checkbox that indicates whether status strings
   * should be displayed to the Standard Output.
   */
  protected Checkbox display;
  
  /**
   * The constructor simply lays out the Frame by calling 
   * arrange(), and show()s it.
   * @param title The title of the Frame.
   * @see amm.http.HTMLEcho#arrange
   */
  public HTMLEcho(String title) {
    super(title);
    arrange();
    show();
  }

  /**
   * This method simply lays out the Frame.
   */
  protected void arrange() {
    setLayout(new BorderLayout());
    Panel p=new Panel();
    p.setLayout(new FlowLayout());
    p.add(new Label("URL:  "));
    url=new TextField(40);
    p.add(url);
    add("Center",p);
    Panel p0=new Panel();
    p0.setLayout(new GridLayout(2,1));
    p=new Panel();
    p.setLayout(new FlowLayout());
    display=new Checkbox("Display status to stdout");
    display.setState(true);
    p.add(display);
    p.add(new Button("Grab URL"));
    p0.add(p);
    stat=new Label(READY);
    p0.add(stat);
    add("South",p0);
    pack();
  }

  /**
   * If the button is pressed (or the <TT>Enter</TT> key
   * while typing), a connection is made to the specified
   * URL by starting a new HTTPConnect with this instance as
   * the HTTPClient.  If the window is closed, the program
   * exits.
   */
  public boolean handleEvent(Event evt) {
    if((evt.id==Event.ACTION_EVENT)&&((evt.target instanceof Button)||(evt.target instanceof TextField))) {
      new HTTPConnect(this).start();
      return true;
    }
    if(evt.id==Event.WINDOW_DESTROY) {
      hide();
      dispose();
      System.exit(0);
      return true;
    }
    return super.handleEvent(evt);
  }

  /**
   * The host method assumes an absolute URL, and it returns
   * the string between the "//" and the following "/" as
   * the host name.
   * @return The host name.
   */
  public String host() {
    String temp=url.getText();
    int begin,end;
    begin=temp.indexOf("//");
    end=temp.indexOf("/",begin+2);
    return temp.substring(begin+2,end);
  }

  /**
   * This method assumes an absolute URL, and it returns
   * the path of the file on the server, beginning with
   * the first single "/" in the URL.
   * @return The path to the desired file.
   */
  public String abs_path() {
    String temp=url.getText();
    int begin,end;
    begin=temp.indexOf("//");
    end=temp.indexOf("/",begin+2);
    return temp.substring(end);
  }

  /**
   * The method is always GET.
   */
  public String method() {return HTTPClient.GET;}

  /**
   * This method displays status messages to the status
   * Label at the bottom of the window, and to the Standard
   * Output is the checkbox is set.
   */
  public void status(String line) {
    stat.setText(line);
    if(display.getState()) System.out.println("Status: "+line);
  }

  /**
   * If the content type is text/html or text/plain, the
   * content is displayed to the Standard Output.  Otherwise,
   * it is ignored.  Since this method is the last thing
   * called before the connection is closed, it resets the
   * status string before it returns.
   */
  public void response(String cType, long cLength, DataInputStream in) {
    if(!(cType.equalsIgnoreCase("text/html")||cType.equalsIgnoreCase("text/plain"))) {
      status("Unhandled MIME Type: "+cType);
      status(READY);
      return;
    }
    String line;
    try {
      while((line=in.readLine())!=null) {
        System.out.println("Read: "+line);
      }
    }
    catch(IOException e) {
      status("Read error: "+e);
    }
    status(READY);
  }

  /**
   * This method displays any error messages it encounters,
   * and ignores success messages.
   */
  public void returnCode(int code,String desc) {
    if((code>199)&&(code<300)) return;
    else {
      status("Error "+code+" retrieving file: "+desc);
      status(READY);
    }
  }

  /**
   * This starts the application.  No arguments are necessary.
   */
  public static void main(String args[]) {
    new HTMLEcho("HTML Echo Client");
  }
}