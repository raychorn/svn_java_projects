/***********************************************************************
Copyright Notice: This source code is (C) Copyright 1995-96, EarthWeb LLC.,
All Rights Reserved. Distribution of this document or it's resulting
compiled code is granted for non-commercial use, with prior approval of
EarthWeb LLC. Distribution of this document or its resulting compiled
code, for commercial use, is granted only with prior written approval of
EarthWeb, LLC. For information, send email to info@earthweb.com.
***********************************************************************/

/*
	man.java

	a test to do threaded animation

	@Author:	Alex Chaffee

*/

import java.awt.*;
import java.util.*;
import java.applet.*;
import java.lang.Math;

/**
 *	Animator

 * This class implements the thread that draws the image to the screen
 * every 500 msec.
 **/

class Animator implements Runnable {

  static int delay = 500;
  
  private Applet applet;
  private Vector images;
  private Thread thread = null;
  private int frame;

  public Animator(Applet appletIn, Vector imagesIn) {
    applet = appletIn;
    images = imagesIn;
  }

  public void start() {
    if (thread == null) {
      thread = new Thread(this);
      thread.start();
    }
  }

  public void stop() {
    System.out.println("Animator stopped");
    thread.stop();
    thread = null;
  }

  public void run() {
    thread.setPriority(Thread.MAX_PRIORITY-1);
    while (thread != null) {
      applet.repaint();
      try {
	thread.sleep(delay);
      } catch (InterruptedException e) {};

      // Increment frame 

      // Must use synchronized on the off chance that update gets called
      // between the frame++ and the frame=0

      synchronized (this) {
	frame++;
	if (frame > images.size()) {
	  frame = 0;
	}
      }

    }
  }

  public void paint(Graphics g) {
    if (frame < images.size()) {
      Image image = (Image)images.elementAt(frame);
      if (image != null) {
	g.drawImage(image, 0, 0, null);	// try it without an ImageObserver
      }
    }
  }
}

/**
 *	Renderer
 * This class progressively renders the image 
 **/

class Renderer implements Runnable {

  protected Image image;
  protected Thread thread = null;
  protected int delay = 50;
  static int count = 0;

  public Renderer(Image imageIn) {
    image = imageIn;
  }

  public void start() {
    if (thread == null) {
      thread = new Thread(this, "Renderer " + count++);
      thread.setPriority(Thread.MIN_PRIORITY+1);
      thread.start();
    }
  }

  public void stop() {
    System.out.println("Renderer stopped");
    thread.stop();
    thread = null;
  }

  public void setDelay(int delay) {
    this.delay = delay;
  }

  public void run() {}

}

class Mandelbrot extends Renderer {

  /**
   * zoom constant
   */
  protected static float kZoom = (float)1.3;
  
  /**
   * Magnification of the image -- mag 0 = full span of 4 (-2 to +2)
   * <tt>span = 4 / (K^mag)</tt>
   * @see kZoom
   */
  int mag;
  
  /**
   * point in virtual space around which we're drawing the thing
   * (i.e. where in virtual space is (0,0) in the window
   */
  float originX = 0;
  float originY = 0;
  
  public Mandelbrot(Image image, int mag, float originX, float originY) {
    super(image);
    this.mag = mag;
    this.originX = originX;
    this.originY = originY;
  }

  static float MagToSpan(int mag) {
    return 4 / (float)(java.lang.Math.pow(kZoom, mag));
  }

  /**
   * Render this image progressively. 
   */
  public void run() {

    /**
      * The graphics port of the image we're drawing to
      */
    Graphics g;
    g = image.getGraphics();

    int width, height;
    width = image.getWidth(null);
    height = image.getHeight(null);

    /**
      * The width of the window on the virtual space
      * @see mag
      */
    float span = MagToSpan(mag);

    /**
     * current resolution (how big each pixel is)
     */
    int resolution;

    /**
     * how far in do we go before we're rendering by pixel
     */
    int resolutionMax = 10;	// should calculate based on image size

    /**
      * current increment (based on resolution)
      */
    float inc;

    resolution = 1;
    int widthPixel, heightPixel;
    do {

      // the resolution determines which power of two we're dividing
      // the span by -- so it fills in by squares
      float scale = 1 /  (float)(java.lang.Math.pow(2, resolution));
      inc = span * scale;

      // pre-calculate the width and height of each "pixel" in the image
      widthPixel = (int)(scale * width) + 1;
      heightPixel = (int)(scale * height) + 1;
      
      spew("resolution " + resolution + " pixel=(" + widthPixel + ", " + heightPixel + ")");
            
      // Mandelbrot function
      Color c;
      int maxiterations = 100;
      int i;
      float temp, r, x, y;

      float minX = originX + inc/2;
      float maxX = originX + span;
      float minY = originY + inc/2;
      float maxY = originY + span;

      for (float c1= minX; c1 < maxX; c1 += inc) {
	for (float c2 = minY; c2 < maxY; c2 += inc) {

	  // Nitty gritty, merci Benoit
	  r = 0;
	  x = 0;
	  y = 0;
	  i = 0;

	  while ((i<maxiterations) && (r<4)) {
	    r = x*x + y*y;
	    temp = x*x - y*y + c1;
	    y = 2*x*y + c2;
	    x = temp;
	    i++;
	  }

	  // Now i is the level, so base color on it
	  if (i == maxiterations) {
	    c = Color.black;
	  } else {

// All sorts of different color palettes

//	    c = new Color(i*2, i*i%256, 255 - (i*i));
//	    c = new Color(i*2, i*i%256, 255 - (i*2));
	    c = new Color(i*2, ((i*i)+128)%256, 255 - (i*2));
//	    c = new Color(i*2, i, 255 - i*2);
//	    System.out.println(i + ": " + c.toString());
	  }

	  // should optimize by incrementing screen pos rather than
	  // recalculating
	  int h = (int)(((c1 - originX)*width)/span);
	  int v = (int)(((c2 - originY)*height)/span);

	  // center on the point, not upper-left
	  h -= widthPixel/2;
	  v -= heightPixel/2;
	  
	  g.setColor(c);
	  g.fillRect(h, v, widthPixel, heightPixel);

	  Thread.currentThread().yield();

	}

	// even with yield, there's a goofy bug with Netscape 2.0b5
	// and lower wherein the browser never takes time for itself
	// (how selfless). So I'll try sleeping for a millisec just to
	// see if it helps
	
	try { Thread.currentThread().sleep(1); }
	catch ( InterruptedException ie );
      } // for traverse virtual space
      resolution++;

    } while (widthPixel > 1 || heightPixel > 1);

    spew("stopped");

  } // method run

  void spew(String str) {
    System.out.println("(" + originX + "," + originY + ")x" + mag + ":" + str);
  }

  public  static int iterate(float c1, float c2) {
    
    int maxiterations = 100;
    
    // Nitty gritty, merci Benoit
    float r, x, y, temp;
    int i;
    r = 0;
    x = 0;
    y = 0;
    i = 0;
    
    while ((i<maxiterations) && (r<4)) {
      r = x*x + y*y;
      temp = x*x - y*y + c1;
      y = 2*x*y + c2;
      x = temp;
      i++;
    }
    
    return i;
  }
}


/**
 * man - the main applet class
 **/

public class man extends Applet {
  
  private int cImages = 1;
  private Vector images = null;
  private Vector renderers = null;
  private Animator animator = null;

  float originX = (float)-2.0;
  float originY = (float)-2.0;

  /**
    * magnification of the first frame in the list
    */
  int mag = 0;
  
  public void start() {

    // read the parameters
    String str;

//    System.out.println("originx " + getParameter("originx"));
//    System.out.println("originy " + getParameter("originy"));
//    System.out.println("mag " + getParameter("mag"));

    try {
      if ((str = getParameter("originx"))!=null)
	originX = new Float(str).floatValue();
      if ((str = getParameter("originy"))!=null)
	originY = new Float(str).floatValue();
      if ((str = getParameter("mag"))!=null)
	mag = new Integer(str).intValue();
    } catch (NumberFormatException e) {
      System.out.println("Number format exception!");
    }

    startThreads();

  }

  void startThreads() {

    // Fill the vector with blank images
    images = new Vector(cImages);
    for (int i=0; i<cImages; ++i) {
      Image image = createImage( size().width, size().height );
      images.addElement(image);
    }

    // start each rendering thread
    renderers = new Vector(cImages);
    for (int i=0; i<cImages; ++i) {
      Renderer renderer = new Mandelbrot((Image)images.elementAt(i), mag+i, originX, originY);
      renderer.start();
      renderers.addElement(renderer);
    }

    // start the animator
    animator = new Animator(this, images);
    animator.start();

    // a repaint for good measure
    repaint();
  }

  public void stop() {
    stopThreads();
  }

  /**
   * stop all threads 
   */
  
  public void stopThreads() {
    animator.stop();
    for (int i=0; i<cImages; ++i) {
      ((Renderer)renderers.elementAt(i)).stop();
    }
  }

  /* must override to reduce flicker */
  public void update(Graphics g) {
    paint(g);
  }

  public void paint(Graphics g) {
    animator.paint(g);
  }

  /* Events */
  public boolean keyDown(Event evt, int key) {

    switch(key) {
    case '+':
    case '=':
      // Zoom in
      zoom(mag+1);
      return true;

    case '-':
      // Zoom out
      if (mag>0)
	zoom(mag-1);
      return true;

    default:
      return false;
    } // switch
  } // keyDown

  void zoom(int magNew) {
    
    float spanOld = Mandelbrot.MagToSpan(mag);
    float centerX = originX + spanOld/2;
    float centerY = originY + spanOld/2;

    mag = magNew;

    float spanNew = Mandelbrot.MagToSpan(mag);
    originX = centerX - spanNew/2;
    originY = centerY - spanNew/2;

    stopThreads();
    startThreads();
    
  }
    

  public boolean mouseDown(Event evt, int x, int y) {
    System.out.println(evt.toString());  

    stopThreads();
    animator = null;

    float span = Mandelbrot.MagToSpan(mag);
    originX = x * span / size().width + originX - span/2;
    originY = y * span / size().height + originY - span/2;

    System.out.println("New origin: " + x + (new Dimension(x,y)).toString() + " -> " 
		       + originX + "," + originY );

    start();

    return true;
  }

  static float convert(int x, int width, int mag, float origin) {
    float span = Mandelbrot.MagToSpan(mag);
    float z = x * span/width + origin;
    return z;
  }

  public boolean mouseMove(Event evt, int x, int y) {

    float vx = convert(x, size().width, mag, originX);
    float vy = convert(y, size().height, mag, originY);
    int i = Mandelbrot.iterate(vx, vy);

    String str = "i=" + (new Integer(i)).toString();
    str = str + " (" + vx + ", " + vy + ")";
    getAppletContext().showStatus(str);

//    System.out.println((new Integer(i)).toString());

    return true;

  }

}

