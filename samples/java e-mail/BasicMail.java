// Author: Aaron Mulder (ammulder@princeton.edu)
//
// You're welcome to use this yourself - it will work on
//   any server that also runs SMTP (on port 25).  Just
//   don't forget the 3 parameters:
//            to: the address to send the mail to
//          host: the web server running this and SMTP
//       success: the page to display when the mail
//                has been sent successfully.

import java.applet.*;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.net.*;
import amm.mail.*;

public class BasicMail extends Applet implements MailSource {
  TextField sub,address;
  TextArea message;
  String dest,host,done;
  Label show;
  Mailer mail;

  public void init(){
    mail=new Mailer(this);
    dest=getParameter("to");
    host=getParameter("host");
    done=getParameter("success");
    if(dest==null)dest="no address";
    if(host==null)host="no host";
    if(done==null)done="no URL";
    setLayout(new BorderLayout());
    Panel p=new Panel();
    p.setLayout(new GridLayout(5,1));
    p.add(new Label("Your e-mail address"));
    address=new TextField();
    p.add(address);
    p.add(new Label("Subject"));
    sub=new TextField();
    p.add(sub);
    p.add(new Label("Message text"));
    add("North",p);
    message=new TextArea();
    add("Center",message);
    p=new Panel();
    p.setLayout(new BorderLayout());
    p.add("West",new Button("Clear"));
    p.add("East",new Button("Submit to "+dest));
    Panel p2=new Panel();
    p2.setLayout(new GridLayout(2,1));
    p2.add(p);
    show=new Label("Composing message...");
    p2.add(show);
    add("South",p2);
  }

  public void start(){
    clear();
    status("Composing message...");
  }

  public boolean handleEvent(Event evt){
    if((evt.target instanceof Button)&&(evt.id==Event.ACTION_EVENT)){
      if("Clear".equals(evt.arg)) clear();
      else mail.start();
      return true;
    }
    return false;
  }

  public String host(){return host;}

  public String[] to(){
    String temp[]={dest,};
     return temp;
  }

  public String[] cc(){
    String temp[]=new String[0];
    return temp;
  }

  public String from(){ return address.getText();}

  public String name() {return address.getText();}

  public String subject() {return sub.getText();}

  public void data(PrintStream out){out.println(message.getText());}

  public void done(boolean succeeded) {
    if(succeeded){
      status("Message sent successfully.");
      try {getAppletContext().showDocument(new URL(done));}
      catch (Exception e) {status("mail submitted, but failed to load "+done);}
    }
  }

  public void status(String s){
    show.setText(s);
    showStatus(s);
  }

  private void clear(){
    address.setText("");
    sub.setText("");
    message.setText("");
  }
}
