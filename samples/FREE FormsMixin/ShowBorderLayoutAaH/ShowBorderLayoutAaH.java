import java.awt.*;    
import controls.*;
/**
 *	Border layout with autoscaling AnimatorMixin; parms from HTML
 *	(c) 1996 Inductive Solutions, Inc.
 *
*/ 
public class ShowBorderLayoutAaH extends java.applet.Applet { 
	AnimatorMixin	myAnimator[] = new AnimatorMixin[5];
	String	commands[] = new String[5];
  	public void init() {

		commands[0] = new String("North");
		commands[1] = new String("East");
		commands[2] = new String("South");
		commands[3] = new String("West");
		commands[4] = new String("Center");
		setLayout(new BorderLayout(0,0));
		for ( int index = 0; index < 5; index++ ) {
			myAnimator[index] = new AnimatorMixin(100,100);
			myAnimator[index].parseHTML(this);
			myAnimator[index].autoScaleON();
			myAnimator[index].init();
		}
  	}

    	public void start() {
		for ( int index = 0; index < 5; index++ ) {
			add(commands[index],myAnimator[index]);
			myAnimator[index].start();
			myAnimator[index].startTheEffect();
		}
    	}

	public void stop() {
		for ( int index = 0; index < 5; index++ ) {
			remove(myAnimator[index]);
			myAnimator[index].stop();
		}
	}

	public boolean mouseEnter(java.awt.Event evt, int x, int y) { 
		showStatus("(c) 1996 Inductive Solutions, Inc.");
		return (true);
    	}

}
