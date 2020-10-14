import java.awt.*;    
import java.awt.image.*;    
import controls.*;
/**
 *	This applet demonstrates scaling
 *      (c) 1996 Inductive Solutions, Inc. *
*/
public class ShowZoom extends java.applet.Applet implements Runnable { 
	BasicImageMixin	a;
	Thread	showZoom = null;
	int	w = 50;
	int	h = 50;
	int	x,y;
	Image	myBaseImage[] = new Image[10];
	ImageFilter	cropfilter = new CropImageFilter(0,0,680,504);
  	public void init() {
		setLayout( new FlowLayout(FlowLayout.CENTER,0,0));
		a = new BasicImageMixin(size().width,size().height);
		a.debugOn();
		a.setForegroundFileNames(this,getDocumentBase(),"images","8heads.gif",1);
                a.setForegroundFilePositions(this,"0@0");
                a.setForegroundScaleFactors(this,"0@0");
		a.init();
		add(a);
		a.start();
                a.allImagesLoaded(true);        // Wait for all images to load
		Image	baseImage = a.getForegroundImage(0);
                a.removeImage(0);
                for ( int j = 0; j < 10; j++ ) {
			x = (size().width - w) / 2;
			y = (size().height - h) / 2;
                        myBaseImage[j] = createImage(new FilteredImageSource(baseImage.getSource(),cropfilter));
                        try {
                                while ( prepareImage(myBaseImage[j],w,h,this) == false ) {
                                         Thread.sleep(1000);
                                }
                        } catch ( Exception e ) {
                                System.out.println("Image not loaded");
                        }
                        int ni = a.addImage(myBaseImage[j],new Point(x,y), new Dimension(w,h));
                        a.removeImage(ni);
			w = w + 25;
			h = h + 25;
                }
  	}  // end init
	//	The component is added and the start method is called in the BasicImage Mixin
    	public void start() {
		if ( showZoom == null ) {
			showZoom = new Thread(this);
			showZoom.start();
		}	
    	}
	public void run() {
		int	lx,ly;
		int	counter = 1;
		while ( showZoom != null ) {
			a.restoreImage(counter);
			try {
				Thread.sleep(500);
			} catch ( Exception e ) {
			}
			a.removeImage(counter);
			if (counter++ > 10) 
				counter = 1;
		} 
	}

//	The stop method removes the component and calls stop in the
	public void stop() {
		remove(a);
		showZoom = null;
		a.stop();
	}
}
