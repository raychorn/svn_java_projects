import java.awt.*;    
import controls.*;
/**
 *	Drags a BasicImageMixin object
 *	(c) 1996 Inductive Solutions, Inc. *
*/ 
public class ShowWriteOnImage4 extends java.applet.Applet { 
	BasicImageMixin	i;
	Graphics	gnew;
	Image		imageSelectionofBackground;	
	int		rx=200, ry=200, dim = 10;
	int		selected = -1;	//	Start with nothing selected
	int		whichImage;	//	Will identify the image selected
        String  af = new String ("earth1t.gif|earth2t.gif|earth3t.gif|earth4t.gif|earth5t.gif|earth6t.gif|earth7t.gif|earth8t.gif");

  	public void init() {
		i = new BasicImageMixin(585,480);
		add(i);
		i.parseHTML(this);	// not needed if there is no html doesn't hurt you can change
					// apps without recompiling.
                i.setForegroundFileNames(this,getDocumentBase(),"images",af,8);
                i.setForegroundFilePositions(this,"0@0|110@0|220@0|330@0|0@110|110@110|220@110|330@110");
                i.setForegroundScaleFactors(this,"10@10|20@20|40@40|50@50|100@50|50@100|100@100|50@200");

		i.init();
  	}  // end init

    	public void start() {
		i.start();
		i.allImagesLoaded(true);					// Optionally wait for all images to load
		System.out.println("All Images loaded");
		imageSelectionofBackground = i.getArea(new Point(rx,ry), new Dimension(300,50));	// Will include the image under it
		System.out.println("Background selected");
										// at the point and Dimension
                gnew = imageSelectionofBackground.getGraphics();			

                gnew.setColor(Color.green);					// graphics context to draw in
                gnew.setFont(new Font("TimesRoman", Font.BOLD, 36));		
                gnew.drawString("TimesRoman, Font.BOLD, 36", 0,45);		// Notice that (x,y) are relative to getArea

                gnew.setColor(Color.yellow);					// Put a second object on it
                gnew.fillRect(0,0,10,10);
                i.addImage(imageSelectionofBackground, new Point(rx,ry),new Dimension(300,50));

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
			gnew.setColor(Color.red);
			gnew.fillRect(0,0,10,10);
			i.setNewXYPosition(new Integer(whichImage),new Point(rx,ry));
			//i.restoreToOriginal(true);	// Not calling this allows you to leave images on Canvas
		}
		return (true);
	}

//	Let go of the object
 	public boolean mouseUp(java.awt.Event evt, int x, int y) { 
		selected = -1;
		return ( true );
	}

	public void stop() {
		remove(i);
		i.stop();
	}

	public boolean mouseEnter(java.awt.Event evt, int x, int y) { 
		showStatus("(c) 1996 Inductive Solutions, Inc.");
		return (true);
    	}

}
