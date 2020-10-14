import java.awt.*;    
import controls.*;
/**
 *	Wipe a BasicImageMixin object image off the screen 
 *	(c) 1996 Inductive Solutions, Inc.
 */ 

public class ShowBasicImageWipe extends java.applet.Applet { 
	BasicImageMixin	myImage;
	
  	public void init() {
		myImage = new BasicImageMixin(600,600);
		add(myImage);
		myImage.parseHTML(this);
		myImage.init();
  	}

//  The component is added and the start method is called.
    	public void start() {
		add(myImage);
		myImage.start();
	}

//  The stop method removes the component from the canvas and stops it.
	public void stop() {
		remove(myImage);
		myImage.stop();
	}

	public boolean mouseDown(java.awt.Event evt, int x, int y) { 
		myImage.wipe(-10,10,100);
		repaint(0);
		return ( true );
	}
//  Moving the mouse over tha applet shows authorship.
	public boolean mouseEnter(java.awt.Event evt, int x, int y) { 
		showStatus("(c) 1996 Inductive Solutions, Inc.");
		return (true);
    	}

}// end applet ShowBasicImage1h
