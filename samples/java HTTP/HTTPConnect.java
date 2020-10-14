package amm.http;

import java.net.*;
import java.io.*;

/**
 * This class makes an HTTP connection.  It gets the
 * connection parameters from an instance of HTTPClient,
 * and returns the data received to the HTTPClient.
 * @see amm.http.HTTPClient
 * @author Aaron Mulder
 * @author ammulder@princeton.edu
 * @author http://www.princeton.edu/~ammulder/java.hmtl
 */
public class HTTPConnect extends Thread {
  /**
   * The source of the connection parameters, and the thing
   *    that processes the data returned.
   */
  protected HTTPClient info;
  /**
   * The Input Stream from the remote server.
   */
  protected DataInputStream is=null;
  /**
   * The Output Stream to the remote server.
   */
  protected PrintStream os=null;
  Socket sock=null;

  /**
   * The constructor takes the HTTPClient as a parameter.
   * @param info The source of the connection parameters,
   *    and the thing that processes the data returned.
   */
  public HTTPConnect (HTTPClient info) {
    this.info=info;
  }

  /**
   * When the thread is started, it connects to the server,
   * sends the request, reads the response, and closes the
   * connection.
   */
  public void run() {
    if(connect())
      if(request())
        if(response());
    close();
  }

  /**
   * This method gets the server name and makes a connection
   * to that server.
   * @return <STRONG>True</STRONG> if the connection was
   *        made successfully;
   */
  protected boolean connect() {
    info.status("Connecting to "+info.host());
    try {
      sock=new Socket(info.host(),80);
    }
    catch(UnknownHostException e) {
      info.status("Unknown host: "+info.host());
      return false;
    }
    catch(IOException e) {
      info.status("Socket connect failure: "+e);
      return false;
    }
    try {
      is=new DataInputStream(new BufferedInputStream(sock.getInputStream()));
      os=new PrintStream(new BufferedOutputStream(sock.getOutputStream()));
    }
    catch(IOException e) {
      info.status("Stream creation error: "+e);
      return false;
    }
    info.status("Connected to "+info.host());
    return true;
  }

  /**
   * This method gets the file name, request type, and
   * if necessary the content, and sends the request to
   * the server.
   * @return <STRONG>True</STRONG> if the request was
   *        made successfully;
   */
  protected boolean request() {
    write(info.method().trim()+" "+info.abs_path().trim()+" HTTP/1.0");
    write("User-Agent: amm.http.HTTPConnect/1.0");
    write("");
    info.status("Sent request to "+info.host());
    return true;
  }

  /**
   * This method checks the response code from the server,
   * and passes the content to the client.  It also sends
   * the content type and length, for the client to handle
   * as it sees fit.  The HTTPConnect never reads the
   * content, only the header information.
   * @return <STRONG>True</STRONG> if the header was
   *     parsed successfully, and the content passed to
   *     the client.
   */
  protected boolean response() {
    String line,field,type="unknown";
    int pos,end;
    long length=-1;
    info.status("Retrieving data from "+info.host());
    try {
      line=is.readLine();
      pos=line.indexOf(' ');
      end=line.indexOf(' ',pos+1);
      try {info.returnCode(Integer.parseInt(line.substring(pos+1,end)),line.substring(end+1).trim());}
      catch (NumberFormatException e) {
        info.status("Bad Status Line on response: "+line);
        return false;
      }
      if(line.charAt(pos+1)!='2') return true;
      while((line=is.readLine()).length()>0) {
        pos=line.indexOf(':');
        field=line.substring(0,pos);
        if(field.equalsIgnoreCase("Content-type"))
          type=line.substring(pos+1).trim();
        else if(field.equalsIgnoreCase("Content-length")) {
          try {length=new Long(line.substring(pos+1)).longValue();}
          catch(NumberFormatException e) {length=-1;}
        }
      }
    }
    catch(IOException e) {
      info.status("Exception while parsing header: "+e);
      return false;
    }
    info.response(type,length,is);
    return true;
  }

  /**
   * Closes the streams and socket used to connect to the
   *     server.
   */
  protected void close() {
    if(is!=null) {
      try {is.close();}
      catch(IOException e) {;}
    }
    if(os!=null) {
      os.close();
    }
    if(sock!=null) {
      try {sock.close();}
      catch(IOException e) {;}
    }
    is=null;
    os=null;
    sock=null;
  }

  /**
   * Writes a line to the Output Stream, using Carriage
   * Return + Linefeed to terminate the line.
   * @param line The line to be written.
   */
  protected void write(String line) {
    os.print(line+"\r\n");
    os.flush();
  }
}