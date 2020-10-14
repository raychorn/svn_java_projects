import controls.*;
import java.applet.Applet;
import java.awt.*;
import java.net.*;
/**
 *	Display an input box.  Mouse Down exposes it.
 *	(c) 1996 Inductive Solutions, Inc. 
*/
	

public class ShowInputBoxA30 extends Applet {
	FormMixin 	myInputBox = null;
	int		MsgCount = 0;	
	
	public void init () {

		}
//	A mouseDown causes the Dialog to appear 	
	public boolean mouseDown(java.awt.Event evt, int x, int y) { 
                System.out.println("MsgCount " + MsgCount);
		if (MsgCount > 0) return(true);
		myInputBox = new FormMixin(this, 450, 320); // alert or login dont matter; other 
	        myInputBox.setDialogLayout(0, "Helvetica",Font.PLAIN , 16 , Color.magenta);	// only for input
					//columns, font family, style, size, color 
		myInputBox.setInput("Email Address","YourName@WheverYouAre.com",myInputBox.NORMAL, Label.LEFT,28);
		myInputBox.setInput("Secret Password","123456789",myInputBox.NOECHO, Label.LEFT,18);
		myInputBox.setInput("Telephone Number","555-1212",myInputBox.NORMAL, Label.LEFT,25); 
				// 	^Label		^Default Text,		 ^Echo Input ^placement	  ^width
		myInputBox.setMessage(
			"Enter The ARF ART Contest!",	// Msg
			new Point (10,40),		// Position
			"TimesRoman",			// font family
			Font.BOLD,			// style
			24,				// size
			Color.red);			// color - blinks alternate with black
	// Color and Shape	
		myInputBox.setShapeColor(Color.green);
		myInputBox.setShapeDepth(10);
	// Sounds

		try {
        		myInputBox.setDescriptionSoundfile(new URL(getCodeBase(),"sounds/cuckoo.au"));
                    } 	catch (MalformedURLException e) {
			System.out.println("Running without Audio");
                }
	// images
		myInputBox.setForegroundFileName(this,getDocumentBase(),"images/earth1.gif");
		myInputBox.setForegroundPosition(new Point(0, 0) );

	// Add It
      		myInputBox.CreateUserInput(""); 
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
			for ( int i = 0; i < 1; i++ )
				System.out.println("Input is " + myInputBox.getResponse(i));

               	} else if ("Cancel".equals(arg)) {
                   System.out.println("Cancel button pressed");

               	} else if ("Help".equals(arg)) {
                  System.out.println("Help button pressed");
                }

      		remove(myInputBox);

		MsgCount = MsgCount-1;
                return ( true );
        }

//  Moving the mouse over the applet shows authorship.
	public boolean mouseEnter(java.awt.Event evt, int x, int y) { 
		showStatus("(c) 1996 Inductive Solutions, Inc.");
		return (true);
    	}

	public void stop() {
		myInputBox.stop();
	}
}
