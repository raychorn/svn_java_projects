package amm.mail;

import java.util.*;
import java.io.*;
import java.net.*;

/**
 * This class implements the connection to a POP3 server.  It must be
 * given a MailSink to supply the server and account information.  The
 * MailSink also controls the commands issued to the server.
 * @see amm.mail.MailSink
 * @author <A HREF=ammulder@princeton.edu>Aaron Mulder</A>
 */
public class POPHandler extends Thread {
/**
 * The instance that will control the connection to the POP3 server.
 */
  protected MailSink state=null;
/**
 * The socket used to connect to the POP3 server.
 */
  protected Socket sock=null;
/**
 * The input stream from the POP3 server.
 */
  protected DataInputStream in=null;
/**
 * The output stream to the POP3 server.
 */
  protected PrintStream out=null;
/**
 * The output stream used to pass retrieved messages to the MailSink.
 */
  protected PipedOutputStream message=null;
/**
 * The last line read from the POP3 server.
 */
  protected String line=null;
/**
 * The position of the last message found by the MailSink while checking
 * for new mail, and the number of messages on the POP3 server.
 */
  int lastFound=-1,waiting=0;

/**
 * Creates a POPHandler with the specified controller.
 * @param state     The MailSink that will control transactions with the
 *                  POP3 server.
 */
  public POPHandler(MailSink state) {
    this.state=state;
  }
/**
 * Creates a POPHandler with the specified controller and output stream.
 * @param state     The MailSink that will control transactions with the
 *                  POP3 server.
 * @param message   The output stream used to send mail retrieved from the
 *                  POP3 server to the MailSink.
 */
  public POPHandler(MailSink state,PipedOutputStream message) {
    this.message=message;
    this.state=state;
  }

/**
 * When the POPHandler is started, it calls runNext() on the MailSink, and
 * processes the next pending request.  This is repeated until the MailSink
 * doesn't have any more requests.
 * @see amm.mail.MailSink#runNext
 */
  public void run(){
    int code;

    connect();
    while((code=state.runNext())>0) {
      if(code==MailSink.GET_NEW) {
        state.status("Retrieving new messages...");
        if(newMsg()) break;
      }
      else if(code==MailSink.DELETE){
        state.status("Removing messages from server...");
        if(delMsg()) break;
      }
      else {
        System.out.println("POPHandler doesn't handle code "+code+"!");
        break;
      }
    }
    state.status("Closing connection to "+state.pop3Host()+"...");

    out.println("quit");
    out.flush();
    getLine();
    close();
    state.handlerDone();
  }

/**
 * The POPHandler uses this method to connect to the POP3 Server.  It uses
 * several MailSink methods to make the connection.
 * @see amm.mail.MailSink#pop3Host
 * @see amm.mail.MailSink#pop3User
 * @see amm.mail.MailSink#pop3Pass
 */
  protected void connect(){
    state.status("Contacting "+state.pop3Host()+"...");
    try {sock=new Socket(state.pop3Host(),110);}
    catch(Exception e) {
      state.status("Socket connection to "+state.pop3Host()+" failed ("+e+")!");
      close();
      return;
    }
    try{
      in=new DataInputStream(new BufferedInputStream(sock.getInputStream()));
      out=new PrintStream(new BufferedOutputStream(sock.getOutputStream()));
    } catch(IOException e) {
      state.status("Could not open streams to "+state.pop3Host()+"!");
      return;
    }
    state.status("Connected to POP3 socket on "+state.pop3Host()+".");

    do getLine(); while(!line.regionMatches(0,"+OK",0,3));

    out.println("USER "+state.pop3User());
    out.flush();
    getLine();
    if(!line.regionMatches(0,"+OK",0,3)){
      state.status("Retrieval error: User wasn't accepted!");
      close();
      return;
    }

    out.println("PASS "+state.pop3Pass());
    out.flush();
    getLine();
    if(!line.regionMatches(0,"+OK",0,3)){
      state.status("Retrieval error: Password wasn't accepted!");
      close();
      return;
    }
  }

/**
 * The POPHandler uses this method to check the number of messages on the
 * POP3 server.
 */
  protected void stat(){
    out.println("STAT");
    out.flush();
    getLine();
    if(!line.regionMatches(0,"+OK",0,3)){
      state.status("Retrieval error: POP3 server error on STAT!");
      close();
      return;
    }
    waiting=Integer.parseInt(line.substring(4,line.indexOf(' ',4)));
  }

/**
 * The POPHandler uses this method to retrieve the SMTP IDs of the
 * messages on the server.
 * @return      A Vector containing Strings, each of which is the SMTP ID
 *              of a message (in the order they're on the server).
 */
  protected Vector uidl(){
    int loc;
    Vector popID=new Vector(20,10);

    out.println("UIDL");
    out.flush();
    getLine();
    if(!line.regionMatches(0,"+OK",0,3)){
      state.status("Retrieval error: POP3 server error on UIDL!");
      return popID;
    }
    getLine();
    while(!line.equals(".")){
      popID.addElement(line);
      getLine();
    }
    return popID;
  }

/**
 * The POPHandler calls this method to retrieve new messages from
 * the server.  It uses <TT>verify()</TT> and <TT>remove()</TT> on the
 * MailSink to handle this correctly.
 * @return      <TT>True</TT> indicates an error, which will cause
 *              the POPHandler to close.
 * @see amm.mail.MailSink#verify
 * @see amm.mail.MailSink#remove
 */
  protected boolean newMsg(){
    Vector ids;
    int j;
    String i;
    PrintStream cover;

    if(message==null) {
      state.status("POPHandler wasn't given a PipedOS in time!");
      return true;
    }
    stat();
    state.numThere(waiting);
    cover=new PrintStream(message);

    ids=uidl();
    for(j=0;j<ids.size();j++){
      try {
        if((lastFound=state.verify(((String)ids.elementAt(j)).substring(((String)ids.elementAt(j)).indexOf(' ')).trim(),lastFound))<0){
          i=((String)ids.elementAt(j)).substring(0,((String)ids.elementAt(j)).indexOf(" ")).trim();
          state.status("Retrieving message "+i+" of "+waiting);
          out.println("RETR "+i);
          out.flush();
          getLine();
          if(line.substring(0,3).equalsIgnoreCase("+OK")){
            while(!line.equals(".")) {
              getLine();
              cover.println(line);
              cover.flush();
            }
            if(state.remove()) {
              out.println("DELE "+i);
              out.flush();
              getLine();
              if(!line.regionMatches(0,"+OK",0,3))
                state.status("Retrieval error: no such message ("+i+")!");
            }
          }
          else {
            state.status("Could not retrieve message "+i);
            System.out.println("Response: '"+line+"'");
          }
        }
      }
      catch(StringIndexOutOfBoundsException e){;}
    }
    cover.close();
    return false;
  }

/**
 * The POPHandler calls this method to remove messages from the server.
 * It uses <TT>delWhich()</TT> on the MailSink to determine which messages
 * to remove.
 * @return      <TT>True</TT> indicates an error, which will cause
 *              the POPHandler to close.
 * @see amm.mail.MailSink#delWhich
 */
  protected boolean delMsg(){
    String targetID[],num,test;
    Vector popID;
    int i,j,loc;

    stat();
    popID=uidl();

    targetID=state.delWhich();
    for(j=0;j<targetID.length; j++)
      for(i=1;i<=popID.size();i++) {
        loc=((String)popID.elementAt(i-1)).indexOf(" ");
        test=((String)popID.elementAt(i-1)).substring(loc+1);
        if(test.equals(targetID[j])) {
          num=((String)popID.elementAt(i-1)).substring(0,loc).trim();
          out.println("DELE "+num);
          out.flush();
          getLine();
          if(!line.regionMatches(0,"+OK",0,3)){
            state.status("Delete Error: POP3 didn't accept DELE "+num+"!");
            return true;
          }
          else state.status("Message "+num+" deleted!");
          popID.removeElementAt(i-1);
          i=popID.size()+1;
        }
      }
    return false;
  }

/**
 * Returns a line read from the POP3 server.  This is a method simply to
 * avoid numerous or unclear try/catch sequences.
 * @return      The line read from the server.
 */
  protected String getLine(){
    try{
      line=in.readLine();
    }
    catch(IOException e){
      state.status("Read error!");
      return null;
    }
    return line;
  }

/**
 * Registers the output stream used to send retrieved mail to the MailSink.
 * @param message   The output stream to the MailSink.
 */
  public void setPipe(PipedOutputStream message){
    this.message=message;
  }

/**
 * Closes the connection to the POP3 server.
 */
  protected void close(){
    state.status("Connection to "+state.pop3Host()+" closed.");
    try {sock.close();}
    catch(IOException e) {state.status("Socket close error!");}
  }
}
