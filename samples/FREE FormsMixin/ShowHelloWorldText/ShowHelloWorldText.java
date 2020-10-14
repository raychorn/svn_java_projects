import java.awt.*;    
import controls.*;
/**	The simplest animator. Redone with package controls 12 Dec 95
 *	(c) 1996 Inductive Solutions, Inc. 
 *
*/ 
public class ShowHelloWorldText extends java.applet.Applet { 
	AnimatorMixin	myAnimator;
  	public void init() {
		String	af = new String 		
		("earth1t.gif|earth2t.gif|earth3t.gif|earth4t.gif|earth5t.gif|earth6t.gif|earth7t.gif|earth8t.gif");
		myAnimator = new AnimatorMixin(100,100);

		myAnimator.setAnimationFileNames(this,getDocumentBase(),"images",af,8);
		myAnimator.setBackgroundColor(Color.white);
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

//  Moving the mouse over tha applet shows authorship.
	public boolean mouseEnter(java.awt.Event evt, int x, int y) { 
		showStatus("(c) 1996 Inductive Solutions, Inc.");
		return (true);
    	}

}
