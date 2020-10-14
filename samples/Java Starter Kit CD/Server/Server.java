/*
 * Server.java -- Simple socket server app.
 *
 * Mike Fletcher, fletch@ain.bls.com
 * 951027
 *
 */

// Import what we need
import java.applet.Applet;
import java.io.*;
import java.net.*;
import java.lang.InterruptedException;

// Listen does the work
class Listen implements Runnable {
  Thread t = null;		// Server thread

  public Listen( ) {
    ;				// Nothing specific needed
  }

  public void destroy( ) {
    System.out.println( "Listen::destroy called" );
  }

  // Start thread running (allocing if needed)
  public synchronized void start( ) {
    System.out.println( "start: t = " + t );

    if( t == null ) {
      t = new Thread( this );
      t.setPriority( Thread.MAX_PRIORITY / 4 );
      t.start();
    }
  }

  // Stop thread from running if it exists
  public synchronized void stop( ) {
    System.out.println( "stop: t = " + t );

    if( t != null ) {
      t.stop( );
      t = null;
    }
  }

  // Allow join with our thread
  public final void join( ) 
    throws java.lang.InterruptedException 
  {
    try {
      if( t != null ) {
	t.join();
      } 
    } catch ( InterruptedException e ) {
      throw e;
    }

    return;
  }

  // Run method to be started by Thread
  public void run( ) {
    ServerSocket s = null;	// Socket we're listening to.
    InputStream in = null;	// Socket input stream
    PrintStream out = null;	// Socket output stream
    Socket con = null;		// Current connection

    // Open a new server socket on port 5000 or die
    try {
      s = new ServerSocket( 5000 );
    } catch ( Exception e ) {
      System.out.println( "Exception:\n" + e );
      System.exit( 1 );		// Exit with error
    }	

    // Print out our socket
    System.out.println( "ServerSocket: " + s );

    // While the thread is running . . .
    while( t != null ) {
      // Accept an incomming connection
      try {
	con = s.accept( );
      } catch ( Exception e ) {
	System.out.println( "accept: " + e );
	System.exit( 1 );	// Exit with error
      }

      // Get the I/O streams from socket
      try {
	out = new PrintStream( con.getOutputStream() );
	in = con.getInputStream();
      } catch ( Exception e ) {
	System.out.println( "building streams: " + e );
      }

      // Print welcome on socket
      out.println( "Hi there! Enter 'bye' to exit." );

      // Read what comes in on the socket and spit it back
      try {
	int nbytes;
	boolean done = false;
	byte b[] = new byte[ 1024 ];

	while(!done && ((nbytes = in.read( b, 0, 1024 )) != -1 )) {
	  String str = new String( b, 0, 0, nbytes );

	  // Spit back what we recieved
	  out.println( "Recieved:\n|" + str + "|" );

	  // Done if user says bye
	  if( str.trim().compareTo( "bye" ) == 0 ) {
	    done = true;
	  }

	  // Die if they enter "DIE!"
	  if( str.trim().compareTo( "DIE!" ) == 0 ) {
	    t.stop();
	    t = null;
	  }
	}

	// Say bye and flush output
	out.println( "Bye!" );
	out.flush();

      } catch( Exception e ) {
	System.out.println( "reading: " + e );
      }

      // Close socket
      try {
	out.close();
      } catch ( Exception e ) {
	System.out.println( "close: " + e );
      }
    }
  } 
}

// Wrapper for Listen class
public class Server extends Applet {

  public void init( ) {
    java.applet.AppletContext a = getAppletContext();

    System.out.println( "init called" );

    System.out.println( "app context: " + a );
  }

  public static void main( String args[] ) {

    Listen l = new Listen();	// Create a Listen object
    l.start();			// Start server thread running 

    System.out.println( "Thread started" );
    System.out.println( "Waiting for l to die" );

    // Join with server thread
    try {
      l.join();
    } catch ( InterruptedException e ) {
      System.out.println( "join interrupted: " + e );
      System.exit( 1 );		// Exit with error
    }

    l = null;

    System.exit( 0 );		// Exit gracefully
  }

}
