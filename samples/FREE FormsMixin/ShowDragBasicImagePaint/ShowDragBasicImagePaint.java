import java.awt.*;    
import controls.*;
/**
 *	Drag and Drop withBasicImageMixin
 *	(c) 1996 Inductive Solutions, Inc.
 *
*/ 
public class ShowDragBasicImagePaint extends java.applet.Applet { 
	BasicImageMixin	i;
	Graphics	gnew;
	Image		imageSelectionofBackground;	
	int		rx=200, ry=200, dim = 10;
	int		selected = -1;	//	Start with nothing selected
	int		whichImage;	//	Will identify the image selected
  	public void init() {
		System.out.println(this);
		i = new BasicImageMixin(585,480);
		add(i);
		i.parseHTML(this);	// not needed if there is no html doesn't hurt you can change
					// apps without recompiling.
                i.setForegroundScaleFactors(this,"10@10|20@20");	// two HTML images
		i.init();
  	}  // end init

//	The component is added and the start method is called in the BasicImageMixin
    	public void start() {
		i.start();
		i.allImagesLoaded(true);					// Optionally wait for all images to load
		imageSelectionofBackground = i.getArea(new Point(rx,ry), new Dimension(300,50));	// Will include the image under it
										// at the point and Dimension
                gnew = imageSelectionofBackground.getGraphics();			

                gnew.setColor(Color.green);					// graphics context to draw in
                gnew.setFont(new Font("TimesRoman", Font.BOLD, 36));		
                gnew.drawString("TimesRoman, Font.BOLD, 36", 0,45);		// Notice that (x,y) are relative to getArea

                gnew.setColor(Color.yellow);					// Put a second object on it
                gnew.fillRect(0,0,10,10);
                i.addImage(imageSelectionofBackground, new Point(rx,ry),new Dimension(300,50));

    	}

//	A keyDown causes the display to revert to the original display
	public boolean keyDown(java.awt.Event evt, int k) {
		i.restoreToOriginal(true);
		return (true);
    	}
	

 	public boolean mouseDrag(java.awt.Event evt, int x, int y) { 
		// Is the mouse on the object
		if ( selected == -1 ) 
			whichImage = i.selectedObject(x,y);
		if ( (whichImage != -1 ) || ( selected != -1) ) {
			selected = whichImage;
			i.setUpdateRate(200);		// Mouse Sensitivity ( 100-- ) are good values
			rx = x;
			ry = y;
			i.turnPaintON();
			i.setNewXYPosition(new Integer(whichImage),new Point(rx,ry));
			int	nw = (int)(Math.random() * 300) + 10;
			int	nh = (int)(Math.random() * 50) + 10;
		}
		return (true);
	}
 	public boolean mouseUp(java.awt.Event evt, int x, int y) { 
		selected = -1;
		return ( true );
	}

//	The stop method removes the component and calls stop in the
	public void stop() {
		remove(i);
		i.stop();
	}

	public boolean mouseEnter(java.awt.Event evt, int x, int y) { 
		showStatus("(c) 1996 Inductive Solutions, Inc.");
		return (true);
    	}

}
