/*-
 * Copyright (c) 1995 by Georg Hessmann.
 * All Right Reserved.
 *
 * Foot.java	1.0   25 Aug 1995
 *              1.1   13 Sep 1995
 *
 */

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.*;

import Floor;
import Step;
import Dance;

/**
 * Class foot implements the functions for drawing a foot
 * and for getting the size of one foot.
 *
 * @version 1.1, 13 Sep 1995
 * @author Georg He&szlig;mann
 */
class Foot implements ImageObserver {

  /** Array der Fuss-Bilder:
   *  Dame ja/nein | Links ja/nein | 8 Richtungen
   */
  static Image FootImages [][][][];
  static int   FootState  [][][][];

  static final int IMG_NONE    = 0;	// nothing
  static final int IMG_COMMING = 1;	// some bits are here
  static final int IMG_READY   = 2;	// complete here, but not drawn
  static final int IMG_DRAWN   = 3;	// complete here and drawn
                                        // (one of maybe some feets)
  static final int IMG_OK      = 4;	// complete here and drawn
  
  static final int N    = 1;
  static final int NNO  = 2;
  static final int NO   = 3;
  static final int NOO  = 4;
  static final int O    = 5;
  static final int SOO  = 6;
  static final int SO   = 7;
  static final int SSO  = 8;
  static final int S    = 9;
  static final int SSW  = 10;
  static final int SW   = 11;
  static final int SWW  = 12;
  static final int W    = 13;
  static final int NWW  = 14;
  static final int NW   = 15;
  static final int NNW  = 16;

  Dance app;

  /**
   * Creates the object Foot. Only one object is needed.
   * @param a pointer to the main applet.
   */
  public Foot(Dance a)
  {
    app = a;
    FootImages = new Image [2][2][2][16];
    FootState  = new int   [2][2][2][16];

    for (int i=0; i<2; i++) {
      for (int j=0; j<2; j++) {
	for (int k=0; k<2; k++) {
	  for (int l=0; l<16; l++) {
	    FootImages[i][j][k][l] = null;
	    FootState [i][j][k][l] = IMG_NONE;
	  }
	}
      }
    }
  }

  public boolean imageUpdate(Image img,
			     int infoflags, int x, int y,
			     int width, int height)
  {
    boolean ret        = true;
    boolean doRepaint  = false;
    int     updateTime = 0;

    int i, j, k, l;
    i = j = k = l = 0;

    all:
    for (i=0; i<2; i++) {
      for (j=0; j<2; j++) {
	for (k=0; k<2; k++) {
	  for (l=0; l<16; l++) {
	    if (FootImages[i][j][k][l] == img) {
	      break all;
	    }
	  }
	}
      }
    }

    if (l == 16) {
      System.out.println("ERROR: in Foot.imageUpdate()");
    }

    FootState[i][j][k][l] = IMG_COMMING;

    if ((infoflags & WIDTH) != 0 || (infoflags & HEIGHT) != 0) {
      /* Erst wenn man ein drawImage() versucht werden auch die
       * Bits geladen. Ich mach das aber erst, wenn Breite und Hoehe
       * da sind. Deswegen muss ich schon beim eintrudeln dieser
       * Werte ein repaint() machen (und so das drawImage() erzwingen).
       */
      updateTime = 100;
      doRepaint  = true;
    }
    
    if ((infoflags & (FRAMEBITS | ALLBITS)) != 0) {
      if (FootState[i][j][k][l] != IMG_OK && FootState[i][j][k][l] != IMG_DRAWN) {
	FootState[i][j][k][l] = IMG_READY;
      }
      doRepaint  = true;
      ret = false;
    }
    else if ((infoflags & SOMEBITS) != 0) {
      doRepaint = true;
      updateTime = 250;
    }

    if ((infoflags & ERROR) != 0) {
      FootState[i][j][k][l] = IMG_NONE;
      ret = false;
    }

    if (doRepaint) {
      app.repaint(updateTime);
    }

    return ret;
  }

  /**
   * Draws the foot for step s on floor f within graphics context g.
   * @return Has some foot-state changed to IMG_DRAWN.
   */
  public boolean drawFoot(Graphics g, Floor f, Step s)
  {
    boolean ret = false;	// some state changed to img_drawn
    Image img = getImage(f, s);

    if (img != null && img.getWidth(this) != -1 && img.getHeight(this) != -1) {
      int xpos = f.CalcXPos(s.x) - img.getWidth(this)/2;
      int ypos = f.CalcYPos(s.y) - img.getHeight(this)/2;

      if (getImageState(f, s) == IMG_READY) {
	setImageState(f, s, IMG_DRAWN);
	ret = true;
      }
      // alles geladen und nun auch neu gezeichnet

      g.drawImage(img, xpos, ypos, this);
    }

    return ret;
  }

  /**
   * Redraw the foot for step s on floor f within graphics context g.
   * Draw the foot only, if the image isn't IMG_OK. If it's IMG_READY,
   * set it to IMG_DRAWN.
   */
  public boolean redrawFoot(Graphics g, Floor f, Step s)
  {
    boolean ret = false;	// some state changed to img_drawn
    int imgState = getImageState(f, s);
    
    if (imgState != IMG_OK) {
      Image img = getImage(f, s);

      if (img != null && img.getWidth(this) != -1 && img.getHeight(this) != -1) {

        int xpos = f.CalcXPos(s.x) - img.getWidth(this)/2;
        int ypos = f.CalcYPos(s.y) - img.getHeight(this)/2;

	if (imgState == IMG_READY) {
	  setImageState(f, s, IMG_DRAWN);
	  ret = true;
	}
	
	g.drawImage(img, xpos, ypos, this);
      }
    }

    return ret;
  }

  /**
   * Add the step s into the clipping rect for floor f.
   * @see Floor#addToClipRect
   */
  public void addToClipRect(Floor f, Step s)
  {
    Image img = getImage(f, s);

    int xpos = f.CalcXPos(s.x) - img.getWidth(this)/2;
    int ypos = f.CalcYPos(s.y) - img.getHeight(this)/2;

    f.addToClipRect(xpos, ypos, xpos+img.getWidth(this), ypos+img.getHeight(this));
  }
  
  private Image getImage(Floor f, Step s)
  {
    Image img;
    
    boolean left   = s.is_left;
    boolean dotted = s.is_dotted;
    int     deg    = s.deg;

    img = FootImages[f.IsDame() ? 0 : 1][left ? 0 : 1][dotted ? 0 : 1][deg - 1];

    if (img == null) {
      String str = "images/" + (f.IsDame() ? "d" : "h") + "leg-"
	+ (left ? "l" : "r") + "-" + deg + (dotted ? "-d" : "") + ".gif";

      img = app.getImage(app.getCodeBase(), str);
      FootImages[f.IsDame() ? 0 : 1][left ? 0 : 1][dotted ? 0 : 1][deg - 1] = img;
    }

    return img;
  }
  
  private int getImageState(Floor f, Step s)
  {
    boolean left   = s.is_left;
    boolean dotted = s.is_dotted;
    int     deg    = s.deg;

    return FootState[f.IsDame() ? 0 : 1][left ? 0 : 1]
      		    [dotted ? 0 : 1][deg - 1];
  }
  
  private void setImageState(Floor f, Step s, int state)
  {
    boolean left   = s.is_left;
    boolean dotted = s.is_dotted;
    int     deg    = s.deg;

    FootState[f.IsDame() ? 0 : 1][left ? 0 : 1][dotted ? 0 : 1][deg - 1] = state;
  }
  
  /**
   * Returns a String object representing this Foot's value.
   */
  public String toString()
  {
    return getClass().getName() + "[dance=" + app + "]";
  }

}
