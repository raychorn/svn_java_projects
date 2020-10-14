import controls.*;
import java.applet.Applet;
import java.awt.*;
import java.net.*;
/*
*	(c) 1996 Inductive Solutions, Inc.
*/


class MyLoginPopClass extends Dialog {
	public MyLoginPopClass(	Frame myWindowFrame, 	// Mix in a window frame
				boolean modal, 		// a movement switch
				FormMixin LoginPanel ) 	// with a FormsMixin object
	{
		super(myWindowFrame,modal);	// create a (moveable)window frame
		add("North",LoginPanel);	// add my FormsMixinObject to its border layout
		resize(	LoginPanel.preferredSize().width+10,	// set its size to its default preference
			LoginPanel.preferredSize().height+50 );	// set its size to its default preference
		setResizable(false);		// We don't  want anyone to resize it.
	}
}

public class ShowSimpleLoginPop extends Applet {
	FormMixin LoginPanel = null;
 	Dialog popUpWindow = null;
	Frame	f = new Frame();

	//	
	//	Put up a LoginPanel with login: and password: or user defined
	//
	public void init () {
		int	i;
		//
		//	Create a LoginPanel with ok cancel and help and an icon
		//	Put buttons at Top
		//
		LoginPanel = new FormMixin(this);	//Blank Form Mixin
	        LoginPanel.CreateLoginDialog("LOGIN");
		LoginPanel.makeTextBlink(Color.black,500);
               	LoginPanel.setBackgroundFileName(this,getDocumentBase(),"images/clouds.gif");
		LoginPanel.setShapeDepth(10);
		LoginPanel.setMessage("Please Sign On!",// Msg
			new Point (10,40),		// Position
			"TimesRoman",			// font family
			Font.BOLD,			// style
			12,				// size
			Color.red);

                try {
        		LoginPanel.setDescriptionSoundfile(new URL(getCodeBase(),"sounds/cuckoo.au"));
        		LoginPanel.setStartupSoundfile(new URL(getCodeBase(),"sounds/joy.au"));
                } catch (MalformedURLException e) {
			System.out.println("Running without Audio");
                }
		//
		//	Display the LoginPanel on the browser window
		//
		LoginPanel.allImagesLoaded();

		popUpWindow = new MyLoginPopClass(f,true,LoginPanel);
                add(popUpWindow);
	}

        public boolean action(Event evt, Object arg) {
                System.out.println(evt);
                if ("OK".equals(arg)) {
                        System.out.println("OK button pressed");
             		System.out.println ( "The login name is:" +  LoginPanel.getLoginName() );
			System.out.println ( "The password is:" + LoginPanel.getPassword() );
			popUpWindow.dispose();
                } else if ("CANCEL".equals(arg)) {
                        System.out.println("Cancel button pressed");
                } else if ("HELP".equals(arg)) {
                        System.out.println("Help button pressed");
                }

                return ( true );
        }
   	 /////////////////////////
        // Stop the form thread
        /////////////////////////
        public void stop() {
                LoginPanel.stop();
        }



}
