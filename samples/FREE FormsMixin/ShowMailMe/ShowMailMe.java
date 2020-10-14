import controls.*;
import java.applet.*;
import java.awt.*;
import java.net.*;
import java.io.*;

/**
 *	Display an input box.  Mouse Down exposes it.
 *	(c) 1996 Inductive Solutions, Inc. 
*/
public class ShowMailMe extends Applet {
	FormMixin 	myInputBox = null;
	int		MsgCount = 0;	
	
	String		ServerName = new String("audrey");
	String		To, From, Subject;

	int		PortNumber = 25;
	NetworkMixin	myNetworkConnection;
	
	public void init () {
		String	param;
        	param = getParameter("MAILPORTNUMBER");
        	if (param != null)
        		PortNumber = Integer.parseInt(param);
        	param = getParameter("MAILSERVERNAME");
        	if (param != null)
        		ServerName = param;
	}
	//////////////////////////////////////////////////	
	//	A mouseDown causes the Dialog to appear 	
	//////////////////////////////////////////////////	
	public boolean mouseDown(java.awt.Event evt, int x, int y) { 
                System.out.println("MsgCount " + MsgCount);
		if (MsgCount > 0) 
			return(true);
		myInputBox = new FormMixin(this, 450, 450);
		myInputBox.setAllButtonNames("OK","Cancel","Help");

	        myInputBox.setDialogLayout(0, "Helvetica",Font.PLAIN , 14 , Color.magenta);	// only for input
					//columns, font family, style, size, color 
		myInputBox.setInput("Email Address","YourName@WheverYouAre.com",myInputBox.NORMAL, Label.LEFT,28);
		myInputBox.setInput("Secret Password","123456789",myInputBox.NOECHO, Label.CENTER,18);
		myInputBox.setInput("Telephone Number","555-1212",myInputBox.NORMAL, Label.RIGHT,25); 
		myInputBox.setInput("Mail Server",ServerName,myInputBox.NORMAL, Label.RIGHT,25); 
		myInputBox.setMessage(
			"Enter The ARF ART Contest!",	// Msg
			new Point (10,40),		// Position
			"TimesRoman",			// font family
			Font.BOLD,			// style
			14,				// size
			Color.red);			// color - blinks alternate with black
		//////////////////////
		// Color and Shape	
		//////////////////////
		myInputBox.setShapeColor(Color.green);
		myInputBox.setShapeDepth(10);
		//////////////////////
		// Sounds
		//////////////////////
		try {
        		myInputBox.setDescriptionSoundfile(new URL(getCodeBase(),"sounds/cuckoo.au"));
                } catch (MalformedURLException e) {
			System.out.println("Running without Audio");
                }
		//////////////////////
		// images
		//////////////////////
		myInputBox.setForegroundFileName(this,getDocumentBase(),"images/earth1.gif");
		myInputBox.setForegroundPosition(new Point(0, 0) );
		//////////////////////
		//	Help URL
		//////////////////////
		URL	url = null;
		try {
        		url = new URL(getDocumentBase(),"ShowMailMeHelp.html");
        		myInputBox.setHelpURL(url);
		} catch (MalformedURLException e) {
			System.out.println(url);
		}
		//////////////////////
		// Add It
		//////////////////////
      		myInputBox.CreateUserInput("Mail using the Mixins"); 
		setLayout(new FlowLayout(FlowLayout.RIGHT,0,0));
                add(myInputBox);
		validate();
		MsgCount++;
		return ( true );
	}

        public boolean action(Event evt, Object arg) {
		System.out.println(evt);

            	if ("OK".equals(arg)) {
              		System.out.println("OK button pressed");
			To = myInputBox.getResponse(0);
			From = myInputBox.getResponse(0);
			Subject = "FYI";

			String mystr = myInputBox.getResponse(1);
			mystr = mystr + "\n" + myInputBox.getResponse(2);
			mystr = mystr + "\nEND";
			WriteToMail(mystr);
			MsgCount = MsgCount-1;
			remove(myInputBox);
				
			
               	} else if ("Cancel".equals(arg)) {
                   System.out.println("Cancel button pressed");

               	} else if ("Help".equals(arg)) {
                  System.out.println("Help button pressed");
                }

                return ( true );
        }
	///////////////////////////////////////////////////////////
	//  Moving the mouse over the applet shows authorship.
	///////////////////////////////////////////////////////////
	public boolean mouseEnter(java.awt.Event evt, int x, int y) { 
		showStatus("(c) 1996 Inductive Solutions, Inc.");
		return (true);
    	}

	public void stop() {
		myInputBox.stop();
	}

	public void WriteToMail(String myStr) {
		Panel	p = null;
		myNetworkConnection = new NetworkMixin(this,myInputBox.getResponse(3),PortNumber,false,true);
		try {
			p = myNetworkConnection.start(true);    // Is the server there?
		} catch ( IOException e ) {
			return;	// Exception raised only for false setting
		}
		System.out.println("Add panel");
		if ( p != null ) {
			remove(myInputBox);
			add(p);
			validate();
		} else {
        		myNetworkConnection.sendMailMessage(From, To, "Cont", Subject, new StringBuffer(myStr));
			remove ( myInputBox );
			validate();
		}
		System.out.println("Panel added");
	}

}
