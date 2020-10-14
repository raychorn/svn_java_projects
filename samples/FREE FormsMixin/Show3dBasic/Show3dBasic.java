import controls.*;
import java.awt.*;    
/**
 * Use of WireFrameMixin
 *	(c) 1996 Inductive Solutions, Inc.
 *
*/ 
public class Show3dBasic extends java.applet.Applet { 
	WireFrameMixin	w;
  	public void init() {
		String s = getParameter("MODELNAME");
		if ( s == null ) 
			return;	
		w = new WireFrameMixin(this, s, true, size().width, size().height,0.8);
	
		w.start();
  	}  // end init
    	public void start() {
		add(w);
    	}
	public void stop() {
		w.stop();
	}
	public boolean mouseEnter(java.awt.Event evt, int x, int y) { 
		showStatus("(c) 1996 Inductive Solutions, Inc.");
		return (true);
    	}


}
