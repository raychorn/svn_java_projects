import java.awt.*;    
import controls.*;
/**	The simplest animator with lots of images that does a Multimedia Morph.
*	(c) Inductive Solutions, Inc.
 *
*/ 
public class ShowWhoopieAnim extends java.applet.Applet { 
	AnimatorMixin	myAnimator;
  	
	String  af = new String("");
	
  	public void init() {
		for ( int index = 0; index <= 9; index++ ) {
			af = af + "wg00000" + (index) + ".gif" + "|";
		  	}

		for ( int index = 10; index <= 59; index++ ) {
			af = af + "wg0000" + (index) + ".gif" + "|";
			showStatus("Loading images " + index);
		  	}

		
		af = af + "wg000060.gif";
		myAnimator = new AnimatorMixin(200,250);

		myAnimator.setAnimationFileNames(this,getDocumentBase(),"whoopie",af,61);
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
		showStatus("(c) Inductive Solutions, Inc.");
		return (true);
    	}

}
