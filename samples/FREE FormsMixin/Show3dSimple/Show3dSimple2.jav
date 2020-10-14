import controls.*;
import java.awt.*;    
/**
 *	The WireFrameMixin can be used to display arbitray text strings as statis
 *	3d Wireframes or as animated 3d wireframes with random rotations  or rotations
 *	about the x, or y axis at a specified rate.
  *	(c) 1996 Inductive Solutions, Inc.
 *
*/ 
public class Show3dSimple extends java.applet.Applet { 
	WireFrameMixin	w;
  	public void init() {
		String s = getParameter("TEXTTOMAKE3d");
		if ( s == null ) 
			s = new String("JaVa");
		w = new WireFrameMixin(this, s, false, 300, 300,1.0,0);
        	w.setAnimation(10, 0, 1000 );
		w.start();
  	} 

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
