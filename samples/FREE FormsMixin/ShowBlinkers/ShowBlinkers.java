import java.awt.*;    
import controls.*;
/**
 *	This applet demonstrates BlinkerMixin
 *	(c) 1996 Inductive Solutions, Inc.
*/ 
public class ShowBlinkers extends java.applet.Applet { 
	BlinkerMixin	a[] = new BlinkerMixin[10];
	int		i;
  	public void init() {
		setLayout(new GridLayout(4,2));
		for ( i = 1; i <= 8; i++ ) {
			a[i] = new BlinkerMixin(100,100);
			add(a[i]);
			a[i].setBlinkerFileName(this,getDocumentBase(),"images/earth" + i + ".gif");
			a[i].debugOn();
		}
  	}  // end init
	
    	public void start() {
		for ( i = 1; i <= 8; i++ ) {
			add(a[i]);
			a[i].start();
		}
    	}

	//	A mouse click causes the blinking to start
	public boolean mouseDown(java.awt.Event evt, int x, int y) { 
		for ( i = 1; i <= 8; i++ ) {
			a[i].startTheEffect();
		}
		return (true);
    	}
	//	A key click causes the animation to stop
	public boolean keyDown(java.awt.Event evt, int k) { 
		for ( i = 1; i <= 8; i++ ) {
			a[i].stopTheEffect();
		}
		return (true);
	}
	//	The stop method removes the component and calls stop in the
	//	BlinkerMixin class to stop the threads
	public void stop() {
		for ( i = 1; i <= 8; i++ ) {
			remove(a[i]);
			a[i].stop();
		}
	}
	
	public boolean mouseEnter(java.awt.Event evt, int x, int y) { 
		showStatus("(c) 1996 Inductive Solutions, Inc.");
		return (true);
    	}

}
