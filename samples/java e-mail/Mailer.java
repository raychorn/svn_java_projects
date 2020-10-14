package amm.mail;

import java.util.*;
import java.net.*;
import java.io.*;
import amm.mail.MailSource;

/**
 * A class that connects to an SMTP server and sends an e-mail message.
 * A class implementing MailSource must be passed to the Mailer to
 * provide the content and destination of the message.
 * @see         amm.mail.MailSource
 * @author      Aaron Mulder
 */
 public class Mailer extends Thread {
/**
 * The instance that will provide the content of the message.
 */
  protected MailSource source;
/**
 * The socket used to connect to the SMTP server.
 */
  protected Socket sock=null;
/**
 * The input stream from the SMTP server.
 */
  protected DataInputStream in=null;
/**
 * The output stream to the SMTP server.
 */
  protected PrintStream out=null;
/**
 * The current line retrieved from the SMTP server.
 */
  protected String line=null;

/**
 * Creates a Mailer with the specified data source.
 * @param source    The MailSource that will provide the content of the
 *                  message.
 */
  public Mailer(MailSource source) {
    this.source=source;
  }

/**
 * When the Mailer is started, it gathers information from the MailSource,
 * connects to the SMTP server, and sends the message.
 */
  public void run(){
    boolean test;
    String dest[];

    source.status("Contacting "+source.host()+"...");
    try {sock=new Socket(source.host(),25);}
    catch(Exception e) {
      source.status("Socket connection to "+source.host()+" failed!");
      return;
    }
    try{
      in=new DataInputStream(new BufferedInputStream(sock.getInputStream()));
      out=new PrintStream(new BufferedOutputStream(sock.getOutputStream()));
    } catch(IOException e) {
      source.status("Could not open streams to "+source.host()+"!");
      return;
    }
    source.status("Connected to SMTP socket on "+source.host()+".");

    getLine();
    out.println("HELO");
    out.flush();
    do getLine(); while(!line.regionMatches(0,"250",0,3));

    out.println("MAIL FROM: "+source.from());
    out.flush();
    getLine();
    if(!line.regionMatches(0,"250",0,3)){
      System.out.println("Sent: 'MAIL FROM: "+source.from()+"'");
      System.out.println("Received: '"+line+"'");
      source.status("Delivery error: Sender wasn't accepted!");
      close();
      return;
    }

    test=false;
    dest=source.cc();
    for(int i=0;i<dest.length;i++){
      out.println("RCPT TO: "+dest[i]);
      out.flush();
      getLine();
      if(line.regionMatches(0,"250",0,3)) test=true;
      else {
        source.status("Delivery error: Recipient "+dest[i]+" wasn't accepted!");
        System.out.println("Sent (cc): 'RCPT TO: "+dest[i]+"'");
        System.out.println("Received: '"+line+"'");
      }
    }
    dest=source.to();
    for(int i=0;i<dest.length;i++){
      out.println("RCPT TO: "+dest[i]);
      out.flush();
      getLine();
      if(line.regionMatches(0,"250",0,3)) test=true;
      else {
        source.status("Delivery error: Recipient "+dest[i]+" wasn't accepted!");
        System.out.println("Sent (to): 'RCPT TO: "+dest[i]+"'");
        System.out.println("Received: '"+line+"'");
      }
    }
    if(!test) {
      close();
      return;
    }

    out.println("DATA");
    out.flush();
    getLine();
    if(!line.regionMatches(0,"354",0,3)){
      source.status("Delivery error: Data wasn't accepted!");
      System.out.println("Sent: 'DATA'");
      System.out.println("Recieved: '"+line+"'");
      close();
      return;
    }
    out.print("To: "+dest[0]);
    for(int i=1;i<dest.length;i++) out.print(", "+dest[i]);
    out.println("");
    dest=source.cc();
    if(dest.length>0) {
      out.print("CC: "+dest[0]);
      for(int i=1;i<dest.length;i++) out.print(", "+dest[i]);
      out.println("");
    }
    out.println("Subject: "+source.subject());
    out.println("From: \""+source.name()+"\" <"+source.from()+">");
    out.println("");
    out.flush();
    source.data(out);
    out.println(".");
    out.flush();
    getLine();
    out.println("quit");
    out.flush();

    close();
    source.done(true);
  }

/**
 * Returns a line read from the SMTP server.  This is a method simply to
 * avoid numerous or unclear try/catch sequences.
 * @return      The line read from the server.
 */
  protected String getLine(){
    try{
      line=in.readLine();
    }
    catch(IOException e){
      source.status("Read error!");
      close();
      source.done(false);
      stop();
      return null;
    }
    return line;
  }

/**
 * Closes the connection to the SMTP server.
 */
  protected void close(){
    try {sock.close();}
    catch(IOException e) {source.status("Socket close error!");}
  }
}
