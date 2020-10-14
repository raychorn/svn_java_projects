import java.awt.*;    
import controls.*;
import java.applet.*;	// Need for applet context
import java.net.*; 	// need for URL saavy

/**	The simplest animator that links to a URL.
 *	Redone with package controls 12 Dec 95
 *	(c) 1996 Inductive Solutions, Inc.
 *
*/ 
public class ShowHelloWorldURL extends java.applet.Applet { 
	AnimatorMixin	myAnimator;
	String	HTMLMessage;
	String	HTMLSound;
	String 	HTMLLink;
	URL	myUrl;
	AppletContext	myApplet;


    	public void init() {
		HTMLSound = getParameter("mySound");
		HTMLLink = getParameter("myLink");

		myAnimator = new AnimatorMixin(size().width,size().height);
		myAnimator.parseHTML(this);
		myAnimator.init();
    	}

// Add myAnimator to the canvas.  Call the AnimatorMixin start method.
    	public void start() {
		add(myAnimator);
		myAnimator.start();
		myAnimator.startTheEffect();
    	}

//	The stop method removes the component from the canvas and calls stop.
	public void stop() {
		remove(myAnimator);
		myAnimator.stop();
	}


//	A mouse down causes the animation to start	
	public boolean mouseDown(java.awt.Event evt, int x, int y) { 
		myAnimator.startTheEffect();
		return (true);
	}

//	A key click plays the sound and causes the animation to stop
	public boolean keyDown(java.awt.Event evt, int k) {
		myAnimator.stopTheEffect();

		myApplet = getAppletContext();
		System.out.println("The applet Context is " + myApplet);

		try {	myUrl =  new URL(getDocumentBase(),HTMLLink);
			play(getDocumentBase(), HTMLSound);
			System.out.println (myUrl);
			myApplet.showDocument(myUrl);

                } catch (MalformedURLException e) {
                        myApplet.showStatus("OOPS!  BAD URL!");
			play(getDocumentBase(), "sounds/cuckoo.au");

                } 
		return (true);
	}
	
	public boolean mouseEnter(java.awt.Event evt, int x, int y) { 
		showStatus("(c) 1996 Inductive Solutions, Inc.");
		return (true);
    	}



}
