import java.awt.*;    
import controls.*;

/**	A moving BasicImageMixin object 
 *	(c) 1996 Inductive Solutions, Inc.
 *	
*/ 
public class ShowDriftingB  extends java.applet.Applet implements Runnable { 
	BasicImageMixin	i;
	Thread		myThread = null;
	String		HTMLParameter = null;
	int 		myDelay = 1000;

  	public void init() {
		i = new BasicImageMixin(size().width,size().height);
		i.parseHTML(this);
		HTMLParameter = getParameter("myDelay");
		myDelay = Integer.parseInt(HTMLParameter);
		i.init();
  	}
	//////////////////////////////////////////////////////////////
	//	The component is added and the start method is called 
	//////////////////////////////////////////////////////////////
    	public void start() {
		add(i);
		i.start();
		if ( myThread == null ) {
			myThread = new Thread(this);
			myThread.start();
		}
    	}
	public void run() {
		i.allImagesLoaded(true);
		while ( myThread != null ){
        	        try {
               			myThread.sleep(myDelay);
                	} catch (InterruptedException e) {
               			break;
                	}
			for ( int x = 0; x < size().width; x++ ) {
				i.setNewXYPosition(new Integer(0), new Point(x,x));
        	        	try {
               				myThread.sleep(myDelay);
                		} catch (InterruptedException e) {
                		}
			}
		}
	} 
	//////////////////////////////////////////////////////////////
	//	The stop method removes the component and calls stop	
	//////////////////////////////////////////////////////////////
	public void stop() {
		remove(i);
		myThread.stop();
		myThread = null;	
		i.stop();
	}
	//////////////////////////////////////////////////////////////
	//	A mouse click causes the myThread thread to resume
	//////////////////////////////////////////////////////////////
	public boolean mouseDown(java.awt.Event evt, int x, int y) { 
		myThread.resume();
		return (true);
    	}
	//////////////////////////////////////////////////////////////
	//	A keyDown causes the  myThread thread to suspend actions
	//////////////////////////////////////////////////////////////
	public boolean keyDown(java.awt.Event evt, int k) { 
		myThread.suspend();
		return (true);
    	}
	//////////////////////////////////////////////////////////////
	//  Moving the mouse over the applet shows authorship.
	//////////////////////////////////////////////////////////////
	public boolean mouseEnter(java.awt.Event evt, int x, int y) { 
		showStatus("(c) 1996 Inductive Solutions, Inc.");
		return (true);
    	}
}
