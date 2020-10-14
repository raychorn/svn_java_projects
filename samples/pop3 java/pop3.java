

/*
 * pop3.java
 * Copyright (c) 1996 John Thomas  jthomas@cruzio.com
 *      All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software and
 * its documentation for commercial or non-commercial purposes 
 * is hereby granted provided that this copyright notice
 * appears in all copies.
 *
 */
 
/** 
 *Class: pop3 
 *         
 * <p>
 * 
 * Interface to a POP3 Mail server
 * <p>
 * Based on the RFC 1725 definition for the POP 3 Mail
 * client/server protocol<br>
 *   <a href="http://ds.internic.net/rfc/rfc1725.txt">rfc1725.txt</a>
 *
 * @author	John Thomas
 * <a href="mailto:jthomas@cruzio.com>jthomas@cruzio.com</a>
 * @version	1.1 ( March 1996 )
 *
 *
 * <p><pre>
 * Methods:
 *  Constructor:
 *    void      pop3(mailhost,user,password)
 *    void      pop3()
 * * 
 * *    If mailhost,user,password are not supplied on constructor
 * *	then they must be specified on the connect and login calls
 * * 
 *  Public Methods
 * 
 *   popStatus  connect(mailhost)
 *   popStatus  connect()
 *
 *   popStatus  login(user,password)
 *   popStatus  login()
 * *
 * * The following methods try to closely implement the corresponding
 * * POP3 server commands.  See RFC1725
 * *
 *   popStatus     stat()
 *   popStatus     list()    			*
 *   popStatus     list(msgnum)
 *   popStatus     retr(msgnum)			*
 *   popStatus     dele(msgnum)
 *   popStatus     noop()
 *   popStatus     quit()
 *   popStatus     top(msgnum,numlines) *
 *   popStatus     uidl(msgnum)
 *  *
 *  The APOP command is not supported by the pop3 class.  But
 *  there is an apop.java class that extends pop3 to add APOP support.
 *
 *   
 *   *  The indicated methods have additional multiline output
 *      that can be retrieved with the get_Responses method for
 *      the popStatus object.  i.e.
 *         popStatus status = mypopserver.list()
 *         String[] list = status.get_Responses()
 *        
 *   
 *------------------------------------------------------
 *
 *-------------------------------------------------------------------
 *-- The following methods are convenience functions for the client
 *
 *   popStatus  appendFile(filename,msgnum)   
 *
 *   int		get_TotalMsgs() Number of mail messages on server
 *   int    	get_TotalSize() Total size of all mail messages
 *								_TotalSize and _TotalMsgs are set during
 *								login() by an internal stat() command
 *
 *-- The status of a POP request is returned in an instance of
 *   the popStatus class.
 *   popStatus has the following methods to extract the returned info
 *
 *   boolean    OK()		True if request had no errors
 *   String     Response()  The initial line returned by POP server
 *							that starts with either +OK or -ERR
 *   String[]   Responses() If command returns multiple lines of
 *                          data (RETR, TOP, LIST) then this method
 * 							will return the lines in an array.
 *---------------------------------------------------------------------
 *  Private Methods   
 *   void     send(String cmd) 
 *   String   recv()
 *   void	  recvN(popStatus status)   
 *---------------------------------------------------------------------
 *  Public debuging Methods.
 *
 *   void     set_DebugOn(boolean)      turn on debug output
 *   void     set_DebugFile(filename)  Set filename for debug output
 *   void     debug(String DebugInfo)  Display string on stdout
 *---------------------------------------------------------------------
 *  Simple Usage Example to display the size of each message.
 *
 *-  pop3 pop = new pop3(host,user,password);
 *-  popStatus status = pop.connect();
 *-  if ( status.OK() ) 
 *-   	status = pop.login();
 *-  if ( status.OK() ) {
 *-      status = pop.list();   
 *-      String[] responses = status.Responses();
 *-      for(int i=0; i< responses.length; i++) {
 *-        System.out.println("Message[" + i + "]='" + responses[i] + "'");
 *-      }
 *-      status = pop.quit();
 *-  } 
 *-

 * </pre>
 */
//package COM.cruzio.jthomas.pop;

import java.io.*;
import java.util.*;
import java.net.*;


public
class pop3   {

/*                            
 * State Variables
 *
 */

    int		_TotalMsgs=0; // Number of mail msgs on server

    int		_TotalSize=0; // total size of all msgs on server

   	boolean	_StatusOK=false;  // status used by Send/Recv

   	
    int		State=0;		// Session State

    final int AUTHORIZATION=1;   // Authorization State
    final int TRANSACTION=2;     // Transaction State
    final int UPDATE=3;          // Update State
    
	String  	LastCmd;
		
    String 		Host=null;

	String		User=null;

	String		Password=null;
	

	int    		Port=110;        
	

	Socket		Server;



   	boolean		debugOn=false; // Debug On switch
    
/**
 *Method: pop3 constructor
 *
 *  This will just create the object. No work is done 
 *  
 */

pop3(String host, String user, String password) {

	Host = host;
	User = user;
	Password = password;
	
}

pop3() {}

/**
 *Method: connect()
 *  <p>
 *  This will make a socket connection to the host specified
 *  in the constructor (port 110) 
 *  
 */
public popStatus
connect(String host) {
	// If method specifies the host name then save it and
	// call the default connect method
	Host = host;
	return this.connect();
}

public popStatus
connect(String host,int port) {
	// If both host and port are specified then save them
	// and then call the default connect method
	Host = host;
	Port = port;     // Normally this would be 110 (RFC 1725)
	return this.connect();
}


public synchronized popStatus
connect() {

	popStatus status = new popStatus();
	debug("Connecting to " + Host + " at port " + Port);
	if (Host == null) {
		status._Response = "-ERR Host not specified";
		status._OK = false;
		return status;
	}
	
	try {
	  Server = new Socket(Host,Port);
	  if (Server == null) {  // a failure with no exception????
	    debug("-ERR Error while connecting to POP3 server");
		status._OK = false;
		status._Response = "-ERR Error while connecting to POP3 server";
	  }
	  else  
	    debug("Connected");
	}
	catch (Exception e) {
		String msg = "Exception! " + e.toString();
		debug(msg);
		status._OK = false;
		status._Response = msg;
		Server = null;
	}
	
	if (Server != null) {
		status._OK = true;
		// POP protocol requires server to send a response on the
		// connect.  We will now get that response and parse it
	    _StatusOK = true;    // Fake doing send() before recv() 	
		status._Response = recv();
		Parse(status,2);
		debug("Response=" + status._Response);
		
	}  
	
	if (status._OK)
	  State = AUTHORIZATION;
	
	return status;
		
}

//----------------------------------------------------------
/**
 *Method: login
 *  <p>
 *  Login the specified user with the specified password<br>
 *  If the login is successful, a "STAT" command is issued
 *  to get the current number of messages.
 *  
 */
//----------------------------------------------------------

public popStatus
login(String user, String password) {
	User = user;
	Password = password;
	return login();
}


public synchronized popStatus
login() {
	popStatus status = new popStatus();
	
	if (User == null || Password == null) {
		status._Response = "-ERR Userid or Password not specified";
		return status;
	}
	if ( Server != null )  {
	  	send("USER " + User);
	  	status._Response = recv();
		Parse(status,1);
		if (status._OK) {
			send("PASS " + Password);
			status._Response = recv();
			Parse(status,1);
			if (status._OK) {
				
				State = TRANSACTION;
				// Now we will do an internal STAT function
				
			    popStatus statStatus = stat();
			    
			} 
			  
		} 
		
	}
    	
	return status;
}



//----------------------------------------------------------
/**
 *Method: stat()
 *  <p>
 *  Get the number of messages and the total size from the server
 *
 *    ...
 *  
 */  
//----------------------------------------------------------

public synchronized popStatus
stat() {
	popStatus status = new popStatus();
	if (State != TRANSACTION) {		
	  status._Response = "-ERR Server not in Transaction mode";
	  return status;
	}
	
  	send("STAT");        // Issue the STAT command
  	
  	status._Response = recv();     // read the response
  	String[] tokens = Parse(status,4);
 
	if (status._OK) {
		_TotalMsgs = Integer.parseInt(tokens[1]);
		_TotalSize = Integer.parseInt(tokens[2]);
  	}
    	
	return status;
}

//----------------------------------------------------------
/**
 *Method: quit()
 *  <p>
 *  Quit the session with the POP3 server
 *
 *    ...
 *  
 */  
//----------------------------------------------------------

public synchronized popStatus
quit() {
	popStatus status = new popStatus();
  	send("QUIT");        // Issue the STAT command
  	State = UPDATE;
  	status._Response = recv();     // read the response
  	String[] tokens = Parse(status,2);
 
    	
	return status;
}


//----------------------------------------------------------
/**
 *Method: list(n)
 *  <p>
 *  Get the size of the specified mail msg
 *  <p>
 *  If  n is not specified then get a list of msgs and
 *  the size of each one 
 *
 *    ...
 *  
 */  
//----------------------------------------------------------

public synchronized popStatus
list(int msgnum) {
	popStatus status = new popStatus();
	int i=0;
	
  	send("LIST " + msgnum);        // Issue the LIST n command
  	
  	status._Response = recv();     // read the response
  	String[] tokens = Parse(status,2);
 
	return status;
}


public synchronized popStatus
list() {
	
	popStatus status = new popStatus();
  	send("LIST");        // Issue the LIST command
  	
  	recvN(status);     // read the response
  	String[] tokens = Parse(status,2);
    
    	
	return status;
}

//----------------------------------------------------------
/**
 *Method: uidl(msgnum)
 *  <p>
 *  Get the uidl of the specified mail msg <br>
 *
 *  If  msgnum is not specified then get a list of msgs and
 *  the uidl of each one 
 *
 *    ...
 *  
 */  
//----------------------------------------------------------

public synchronized popStatus
uidl(int msgnum) {
	popStatus status = new popStatus();
	send("UIDL " + msgnum);        // Issue the UIDL msgnum command
  	
  	status._Response = recv();     // read the response
  	String[] tokens = Parse(status,2);
 
	return status;
}


public synchronized popStatus
uidl() {
	
	popStatus status = new popStatus();	
  	send("UIDL");        // Issue the UIDL command
  	
  	recvN(status);     // read the responses
  	String[] tokens = Parse(status,2);
    
    	
	return status;
}

//----------------------------------------------------------
/**
 *Method: retr(n)
 *  <p>
 *  Get the contents of a mail message
 *  <p>
 *  The array of strings obtained are the lines of the
 *  specified mail message.
 *  The lines have CR/LF striped, any leading "." fixed up
 *  and the ending "." removed. <br>
 *  The array can be retrieved with the status.Responses() method.
 *
 *  The +OK or -ERR status line is returned   
 * 
 */  
//----------------------------------------------------------

public synchronized popStatus
retr(int msgnum) {
	
	popStatus status = new popStatus();
  	send("RETR " + msgnum);        // Issue the RETR n command

  	// This may produce more than one response so we call the
  	// recvN method and save an array of strings in status._Responses.
  	recvN(status);     // read the response
  	// The initial string that contains the status is in the
  	// status._Response state variable.
  	String[] tokens = Parse(status,2);
 
	return status;
}


//----------------------------------------------------------
/**
 *Method: top(msgnum n)
 *  <p>
 *  Get the top n lines of a mail message
 *  <p>
 *  The array of strings obtained are the lines of the
 *  mail headers and the top N lines of the indicated mail msg.
 *  The lines have CR/LF striped, any leading "." fixed up
 *  and the ending "." removed. <br>
 *  The array can be retrieved with status.Responses() method.
 *
 *  The +OK or -ERR status line is returned 
 *  
 */  
//----------------------------------------------------------

public synchronized popStatus
top(int msgnum, int n) {
	
	popStatus status = new popStatus();
  	send("TOP " + msgnum + " " + n);	// Issue the TOP msgnum n command

  	// This may produce more than one response so we call the
  	// recvN method and set multiline output into _Responses 
  	recvN(status);     // read the response
  	
  	String[] tokens = Parse(status,2);
 
	return status;
}


//----------------------------------------------------------
/**
 *Method: dele(msgnum)
 *  <p>
 *  Mark the mail message for deletion 
 *  Mail message will be deleted when QUIT is issued.
 *  
 *  
 */  
//----------------------------------------------------------

public synchronized popStatus
dele(int msgnum) {
	
	popStatus status = new popStatus();	
  	send("DELE " + msgnum);        // Issue the DELE n command
  	
  	status._Response = recv();     // read the response
  	String[] tokens = Parse(status,2);
 
    	
	return status;
}

//----------------------------------------------------------
/**
 *Method: rset()
 *  <p>
 *  Reset the mail messages that have been marked for deletion
 *  Nothing will be deleted if QUIT is issued next.
 *  
 *  
 */  
//----------------------------------------------------------

public synchronized popStatus
rset() {
	
	popStatus status = new popStatus();	
  	send("RSET");        // Issue the RSET command
  	
  	status._Response = recv();     // read the response
  	String[] tokens = Parse(status,2);
 
    	
	return status;
}

//----------------------------------------------------------
/**
 *Method: noop()
 *  <p>
 *  Does not do anything but it will keep the server active
 *  
 *  
 */  
//----------------------------------------------------------

public synchronized popStatus
noop() {
	
	popStatus status = new popStatus();	
  	send("NOOP");        // Issue the NOOP command
  	
  	status._Response = recv();     // read the response
  	String[] tokens = Parse(status,2);
 
    	
	return status;
}



//----------------------------------------------------------
/**
 *Method: get_TotalMsgs()
 *  <p>
 *  Return the number/size of msgs on the server.<br>
 *  These values are set by an internal STAT issued at login
 *                      
 */  
//----------------------------------------------------------

public int 
get_TotalMsgs() {
	return _TotalMsgs;
}
public int 
get_TotalSize() {
	return _TotalSize;
}
//----------------------------------------------------------
/**
 *Method: appendFile(filename, msgnum)
 *  <p>
 *  Return the contents of a mail message and append it to the
 *  specified mail file.
 *  
 *  It will internally call RETR and then write the results to
 *  the specified file.
 */  
//----------------------------------------------------------

public synchronized  popStatus
appendFile(String filename, int msgnum) {
	popStatus status = new popStatus();
	
	String[] contents;
	
  	send("RETR " + msgnum);  	// RETR n will return the contents
  								// of message n
  	
  	recvN(status);     // read the response
  	String[] tokens = Parse(status,2);
  	if (status._OK) {
    	RandomAccessFile openfile;
    	try {
			openfile = new RandomAccessFile(filename,"rw");
    	} catch (IOException e) {
    		status._OK = false;
    		status._Response = "-ERR File open failed";
    		return status;
    	}
    	Date datestamp = new Date();
    	contents = status.Responses();
    	try {
    		openfile.seek(openfile.length());
    		openfile.writeBytes("From - " + datestamp.toString() + "\r\n");
    		for(int i=0; i<contents.length;i++) {
    		
    			openfile.writeBytes(contents[i]+"\r\n");
    			//openfile.writeByte((int)'\r');  // add CR LF 
    			//openfile.writeByte((int)'\n');
    		}
    		openfile.close();
    	
    	} catch (IOException e) {
    		status._OK = false;
    		status._Response = "-ERR File write failed";
    		return status;
    	}
  	}
  	status._OK = true;
    return status;	
}



//-----------------------------------------------------------------------
/**
 * Parse(popStatus status, int maxToParse) -> returns String[]
 * <p>
 * Parse the response to a previously sent command from the server.
 * It will set boolean status._OK true if it returned +OK and return
 * an array of strings each representing a white space delimited token
 * The remainder of the response after maxToParse is returned as a
 * single string
 */
String[]
Parse(popStatus status, int maxToParse) {
    String[] tokens = null; 
	
	status._OK = false;
	String response = status._Response;
 	if (response != null) {
 		int i=0;
 		int max;
  		if (response.trim().startsWith("+OK "))
  		  status._OK = true;
  		else
  		  debug(response);
  		// This will break the line into a set of tokens.
  		StringTokenizer st = new StringTokenizer(response);
  		//tokens = new String[st.countTokens()];
  		if (maxToParse == -1)
  			max = st.countTokens();
  		else
  			max = maxToParse;
  		tokens = new String[max+1];
  		while (st.hasMoreTokens() && i < max) {
  			tokens[i] = new String(st.nextToken());
  			//debug("Token " + i + "= '" + tokens[i] + "'");
  			i++;
  		}
  		// Now get any remaining tokens as a single string
  		if (st.hasMoreTokens()) {
  			StringBuffer rest = new StringBuffer(st.nextToken());
  		    while (st.hasMoreTokens() ) 
  			   rest.append(" " + st.nextToken());
  			tokens[max] = new String(rest);   
  			//debug("Token " + max + "= '" + tokens[max] + "'");
  			
  		}
 	}
	return tokens;
}


//------------------------------------------------------------
/**
 * send(String line)
 * <p>
 * Send the passed command to the Server.
 */	
 
void 
send (String cmdline) {
	debug(">> " + cmdline); 
	LastCmd = cmdline;    // Save command for error msg
	
	try {
		OutputStream out = Server.getOutputStream();
		DataOutputStream data = new DataOutputStream(out);  // so we can write lines of data
		data.writeBytes(cmdline + "\r\n"); // Write string as a set of Bytes
        _StatusOK = true;        
    } catch (IOException i){
    	System.err.println("Caught exception while sending command to server");
        _StatusOK = false;
        
    } catch (Exception e) {
        System.err.println("Send: Unexpected exception: " + e.toString());
        _StatusOK = false;   
    }
    
       

}

//-----------------------------------------------------------------------
/**
 * recv()
 * <p>
 * Get the next response to a previously sent command from the server.
 */
 
String 
recv() {
	//debug("entered recv");
	
	String line = "";
    if ( ! _StatusOK  ) {
    	line = "-ERR Failed sending command to Server";
    	return line;
    }
    // send() has written a command to the 
    // server so now we will try to read the result  
	try {
		InputStream in = Server.getInputStream();
		DataInputStream data = new DataInputStream(in);  // so we can read lines of data
        line = data.readLine();
    	debug("<<" + line);    
    } catch (IOException i){
    	System.err.println("Caught exception while reading");
        line = "-ERR Caught IOException while reading from server";
    } catch (Exception e) {
        System.err.println("Unexpected exception: " + e.toString());
        line = "-ERR Unexpected exception while reading from server";     
    }    
    if (line == null) 	{	// prevent crash if reading a null line
		debug("Read a null line from server");
    	line = "-ERR <NULL>";
    }
    if (line.trim().startsWith("-ERR ")) {
      debug("Result from Server has Error!");
      debug("Sent:     '" + LastCmd + "'");
      debug("Received: '" + line + "'");
      return line;
    } else {
    	if (line.trim().startsWith("+OK ")) {
      		return line;
    	} else {
    		debug("Received strange response");
    		debug("'" + line + "'");
    		line = "-ERR Invalid response";
    		return line;
    	}
    }
    
}


//-----------------------------------------------------------------------
/**
 * recvN(popStatus  status)
 * <p>
 * Get the responses to a previously sent command from the server.
 * This is used when more than one line is expected.
 * The last line of output should be ".\r\n"
 */
 
void
recvN(popStatus status) {
	debug("entered recvN");
	Vector v = new Vector();
	String line = "";
	String[] response=null;
	
    // send() has written a command to the 
    // server so now we will try to read the result  
	try {
		InputStream in = Server.getInputStream();
		DataInputStream data = new DataInputStream(in);  // so we can read lines of data
        boolean done = false;
        int linenum=0;
        while (!done) {
		  line = data.readLine();
		  linenum++;
    	  debug("<<" + line.length() + " '" + line +"'");
    	  if (linenum == 1) { // process the initial line
    	    if (line.trim().startsWith("-ERR ")) {
               debug("Result from Server has Error!");
               debug("Sent:     '" + LastCmd + "'");
               debug("Received: '" + line + "'");
               done = true;
               status._Response = line;
    		} else {
    	      if (line.trim().startsWith("+OK ")) {
      		  //Everything looks OK
      		  status._Response = line;
    	      } else {
    		    debug("Received strange response");
    		    debug("'" + line + "'");
    		    done = true;
    		    status._Response = "-ERR Invalid response";
    	      }
    		}
    	  } else {
              // process line 2 - n
              if (line.startsWith(".")) {
          	    if (line.length() == 1)
          	      done = true;
          	    else 
          	      v.addElement(line.substring(1));
              } else 
    	        v.addElement(line);
            }
            
        }   // end of while(!done)
    } catch (IOException i){
    	System.err.println("Caught exception while reading");
        status._Response = "-ERR Caught IOException while reading from server";
    } catch (Exception e) {
        System.err.println("Unexpected exception: " + e.toString());
        status._Response = "-ERR Unexpected exception while reading from server";     
    }

    status._Responses = new String[v.size()];
    v.copyInto(status._Responses);
    return;
    
    
    
}


//------------------------------------------------------
/**
 *Method: setDebugOn(boolean switch)
 *
 * Method to let caller turn the debug switch on or off
 */
public void
setDebugOn(boolean OnOff) {
	debugOn = OnOff;
}

//------------------------------------------------------
/**
 *Method: debug(String string)
 *
 *  If debugOn switch set, display debug info
 *
 */
public void
debug(String debugstr) {
	
	if ( debugOn) { 
		//System.err.println(debugstr);
		System.out.println(debugstr);
	   
	}
}
//-------------------------------------------------------
} // end of Class pop3


