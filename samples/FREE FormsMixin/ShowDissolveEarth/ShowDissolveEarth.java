import java.awt.*;    
import controls.*;
/**
 *	This applet demonstrates the DissolveToImageMixin
 *	(c) 1996 Inductive Solutions, Inc.
*/ 

public class ShowDissolveEarth  extends java.applet.Applet { 
	DissolveToImageMixin	d;

  	public void init() {
		d = new DissolveToImageMixin(size().width,size().height);
		add(d);
		d.parseHTML(this);
		d.init();
  	}  

	public boolean mouseDown(java.awt.Event evt, int x, int y) { 
		d.startTheEffect();
		return (true);
    	}
	public boolean keyDown(java.awt.Event evt, int k) { 
		d.restoreToOriginal();
		return (true);
	}
	public void start() {
		d.start();
	}
	public void stop() {
		remove(d);
		d.stop();
	}

	public boolean mouseEnter(java.awt.Event evt, int x, int y) { 
		showStatus("(c) 1996 Inductive Solutions, Inc.");
		return (true);
    	}

}
