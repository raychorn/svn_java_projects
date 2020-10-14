/*-
 * Copyright (c) 1995 by Georg Hessmann.
 * All Right Reserved.
 *
 * MEXButtonList.java	1.0   25 Aug 95
 *			1.1   13 Sep 95
 *
 */

import java.lang.Math;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.*;

import Dance;
import Step;
import Foot;

/**
 * Floor maintains one dance floor. Normaly two of them are
 * needed. One for the lady and one for the gent.
 *
 * It stores all informations about the steps which are done
 * and draws them.
 *
 * @version 1.1, 13 Sep 1995
 * @author Georg He&szlig;mann
 */
class Floor {

  /** size().width of the floor grid */
  public static final int GridXSize = 80;	// Offset zwischen horiz. Linien
  /** size().height of the floor grid */
  public static final int GridYSize = 80;	// Offset zwischen vert. Linien

  String Titel;		// Unterschrift
  int    OffsetTitel;	// wie tief unter dem Parket?

  boolean is_dame;	// Ist das ein Damen-Parkett?
  boolean debug_lines;	// sollen Hilfslinien gezeichnet werden?
  
  int g_xoff;		// X-Pos. innerhalb des Graphics Contextes
  int g_yoff;		// Y-Pos. innerhalb des Graphics Contextes

  int left;		// Abstand vert. Linie nach links
  int right;		// Abstand vert. Linie nach rechts
  int top;		// Abstand horiz. Linie nach oben
  int bottom;		// Abstand horiz. Linie nach unten
  
  int xsize;		// Groesse X-Richtung
  int ysize;		// Groesse Y-Richtung
  int hlines;		// Anzahl horiz. Linien
  int vlines;		// Anzahl vert. Linien

  Foot foots;		// Bilder/Funktionen um die Fuesse

  float TR_x;		// x-Koo des TR-Pfeiles
  float TR_y;		// y-Koo des TR-Pfeiles

  boolean showComment;	// soll der slow/quick-heel/toe String angezeigt werden?
  boolean useCommentHT;	// show heel/toe or time string?
  float   CStr_x;	// x-Koo des slow/quick Strings
  float   CStr_y;	// y-Koo des slow/quick Strings
  int     commentClearWidth;	// length of last displayed string

  int clipRect_x1;
  int clipRect_y1;
  int clipRect_x2;
  int clipRect_y2;

  Dance app;

  int num_steps = 0;		// Wieviele Schritte wurden bereits gesetzt
  int num_steps_painted = 0;	// und wieviele wurden davon schon gezeichnet

  Step    step_arr[];	// Array der gesetzten Fuesse

  boolean redraw_floor    = true;  // soll der Hintergrund auch neu gezeichnet werden?



  /**
   * Creates a dance floor.
   * @param t titel (lady/gent)
   * @param isd is this the lady-floor?
   * @param gxo left border of the floor relative to the applet origin
   * @param gyo top border of the floor relative to the applet origin
   * @param xsz x-size of the grid
   * @param ysz y-size of the grid
   * @param lborder left border of the grid
   * @param rborder right border of the grid
   * @param tborder top border of the grid
   * @param bborder bottom border of the grid
   * @param f pointer to the foot object
   * @param app pointer to the main applet
   */
  public Floor(String t, boolean isd, int gxo, int gyo,
	       int xsz, int ysz,
	       int lborder, int rborder, int tborder, int bborder,
	       Foot f, Dance app)
  {
    debug_lines = false;	// draw help-lines?

    this.app = app;

    Titel       = t;
    OffsetTitel = 20;
    
    is_dame = isd;
    
    g_xoff = gxo;
    g_yoff = gyo;
    
    left   = lborder;
    right  = rborder; 
    top    = tborder;
    bottom = bborder;

    xsize = left + right  + xsz*GridXSize;
    ysize = top  + bottom + ysz*GridYSize;

    hlines = ysz+1;
    vlines = xsz+1;

    foots  = f;

    num_steps = 0;
    step_arr  = new Step[64];
    
    //if (TRArr == null) TRArr = app.getImage(app.getCodeBase(), "images/TR.gif");

    initClipRect();
  }


  /** Returns the left border of the floor. */
  public int LeftEdge()
  {
    return g_xoff;
  }

  /** Returns the right border of the floor */
  public int RightEdge()
  {
    return g_xoff + xsize;
  }

  /** Returns the top border of the floor */
  public int TopEdge()
  {
    return g_yoff;
  }

  /** Returns the bottom border of the floor */
  public int BottomEdge()
  {
    return g_yoff + ysize + OffsetTitel;
  }

  /** Returns the bottom border without the title string of the floor */
  public int BottomEdgeParket()
  {
    return g_yoff + ysize;
  }

  /**
   * Gets a position relative to the grid and returns
   * a x-coordinate relative to the applet origin.
   */
  public int CalcXPos(float x)
  {
    return g_xoff + left + (int)(x * GridYSize);
  }

  /**
   * Gets a position relative to the grid and returns
   * a y-coordinate relative to the applet origin.
   */
  public int CalcYPos(float y)
  {
    return g_yoff + top + (int)(y * GridXSize);
  }

  /** Is this the lady floor? */
  public boolean IsDame()
  {
    return is_dame;
  }

  /**
   * Set the position of the dancing direction arrow.
   */
  public void SetTRPos(float x, float y)
  {
    // x, y ist die Spitze des Pfeiles
    TR_x = x;
    TR_y = y;
  }

  /**
   * Set the position of the comment (takt/time - heel/toe) string.
   */
  public void SetComStrPos(float x, float y)
  {
    // Koordinate des slow/quick, ... Strings
    CStr_x = x;
    CStr_y = y;
  }

  /** Show the takt/time - heel/toe string (after the next repaint()). */
  public void ShowComStr()	{ showComment = true;  }
  
  /** Hide the takt/time - heel/toe string (after the next repaint()). */
  public void HideComStr()	{ showComment = false; }

  /** Use the heel/toe string for the comment field */
  public void useComStrHT()	{ useCommentHT = true; }

  /** Use the time (quick/slow) string for the comment field */
  public void useComStrTime()	{ useCommentHT = false; }
  

  /**
   * Do the step s.
   */
  public void doStep(Step s)
  {
    step_arr[num_steps] = s;

    num_steps++;
  }

  /**
   * Take back one step. Adds this step to the clip
   * rect of this floor.
   * @see Foot#addToClipRect
   */
  public void backStep()
  {
    num_steps--;
    num_steps_painted = num_steps;
    
    /* x1, y1 -- x2, y2 */
    foots.addToClipRect(this, step_arr[num_steps]);
  }

  /** Returns the number of steps already set on this floor. */
  public int getNumSteps()	      { return num_steps; }

  /** Undo all steps on this floor */
  public void clearSteps() 	      { num_steps = num_steps_painted = 0; }

  /** Repaint all steps on the next repaint(). */
  public void repaintAllSteps()       { num_steps_painted = 0; }


  /** Initialise the clip rect */
  private void initClipRect()
  {
    clipRect_x1 = 10000;
    clipRect_y1 = 10000;
    clipRect_x2 = 0;
    clipRect_y2 = 0;
  }

    /* For BackStep: Called from class Floor.
      Creates a clipRect for the next repaint() to redraw
      only as much as needed to erase the removed steps */

  /**
   * Add the rect (x1, y1) - (x2, y2) to the clip rect of this floor.
   * Will be called from Foot.addToClipRect() which will be called
   * from Floor.takeBack().
   * @see Foot#addToClipRect
   * @see Floor#takeBack
   */
  public void addToClipRect(int x1, int y1, int x2, int y2)
  {
    if (x1 < clipRect_x1) clipRect_x1 = x1;
    if (y1 < clipRect_y1) clipRect_y1 = y1;
    if (clipRect_x2 < x2) clipRect_x2 = x2;
    if (clipRect_y2 < y2) clipRect_y2 = y2;
  }

  /**
   * Setup the clip rect of this floor onto the graphics context g.
   * Than initialize the clip rect of this floor.
   */
  public Graphics installClipRect(Graphics og)
  { 
    Graphics g = og.create();
    
    if (clipRect_x1 < LeftEdge() || clipRect_y1 < TopEdge() ||
	clipRect_x2 > RightEdge() || clipRect_y2 > BottomEdgeParket()) {

      int cx1 = 0, cy1 = 0, cx2 = 0, cy2 = 0;

      if (clipRect_x1 < LeftEdge() || RightEdge() < clipRect_x2) {
      	if (clipRect_x1  < LeftEdge()) cx1 = clipRect_x1;
	else 			       cx1 = RightEdge();

	if (RightEdge()  < clipRect_x2) cx2 = clipRect_x2;
	else 			        cx2 = LeftEdge();

	cy1 = clipRect_y1;
	cy2 = clipRect_y2;

	g.clearRect(cx1, cy1, cx2 - cx1, cy2 - cy1);
      }

      if (clipRect_y1 < TopEdge() || BottomEdgeParket() < clipRect_y2) {
	if (clipRect_y1  < TopEdge())  cy1 = clipRect_y1;
	else 			       cy1 = BottomEdgeParket();

	if (BottomEdgeParket() < clipRect_y2) cy2 = clipRect_y2;
	else 				      cy2 = TopEdge();

      	cx1 = clipRect_x1;
	cx2 = clipRect_x2;

	g.clearRect(cx1, cy1, cx2 - cx1, cy2 - cy1);
      }
    }
    
    g.clipRect(clipRect_x1, clipRect_y1,
	       clipRect_x2 - clipRect_x1, clipRect_y2 - clipRect_y1);

    initClipRect();

    return g;
  }
  

  
  private void paintBackground(Graphics g)
  {
    redraw_floor = false;
    
    g.setColor(Color.white);
    g.fillRect(g_xoff, g_yoff, xsize, ysize);
    g.setColor(Color.black);

    int x, y;

    // DEBUG LINIEN
    if (debug_lines) {
      g.setColor(Color.lightGray);
      // zeichne die vertikalen Linien
      for (y=1; y<(vlines-1)*10; y++) {
	if (y % 10 != 0) {
	  g.drawLine(g_xoff+left + (int)((float)y*(float)GridYSize/10.0f), g_yoff+top,
		     g_xoff+left + (int)((float)y*(float)GridYSize/10.0f), g_yoff+ysize-bottom);
	}
      }
      // zeichne die vertikalen Linien
      for (x=0; x<(hlines-1)*10; x++) {
	if (x % 10 != 0) {
	  g.drawLine(g_xoff+left,        g_yoff+top + (int)((float)x*(float)GridXSize/10.0f),
		     g_xoff+xsize-right, g_yoff+top + (int)((float)x*(float)GridXSize/10.0f));
	}
      }
      g.setColor(Color.black);
     // DEBUG LINIEN
    }

    // zeichne die vertikalen Linien
    if (!debug_lines) g.setColor(Color.darkGray);
    for (y=0; y<vlines; y++) {
      g.drawLine(g_xoff+left + y*GridYSize, g_yoff+top,
		 g_xoff+left + y*GridYSize, g_yoff+ysize-bottom);
    }
    // zeichne die vertikalen Linien
    for (x=0; x<hlines; x++) {
      g.drawLine(g_xoff+left,        g_yoff+top + x*GridXSize,
		 g_xoff+xsize-right, g_yoff+top + x*GridXSize);
    }
    if (!debug_lines) g.setColor(Color.black);

    // Unterschrift ausgeben
    int lt = g.getFontMetrics().stringWidth(Titel);
    g.drawString(Titel, g_xoff + (xsize-lt)/2, g_yoff + ysize + OffsetTitel);

    // TR-Pfeil zeichnen
    if (!debug_lines) g.setColor(Color.darkGray);
    x = CalcXPos(TR_x);
    y = CalcYPos(TR_y); // + TRArr.getHeight() - 5;	// -1 beachten!!!
    int y2 = CalcYPos((float)Math.floor(TR_y) + 0.9f);
    g.drawLine(x, y, x, y2);

    //g.drawImage(TRArr, x-TRArr.getWidth()/2, y-TRArr.getHeight()+5, this); // !!!
    
    g.drawLine(x-4, y+5, x, y);
    g.drawLine(x+4, y+5, x, y);

    if (app.engl) g.drawString("D.D.", x+7, y+17);
    else          g.drawString("T.R.", x+7, y+17);
    
    if (!debug_lines) g.setColor(Color.black);
}

  /**
   * Draw the floor.
   * If Floor.RedrawFloor() was called, all will be painted.
   * Else only the changed things.
   *
   * @see Floor#RedrawFloor
   */
  public void paint(Graphics g)
  {
    boolean state_drawn = false;
    
    if (redraw_floor) paintBackground(g);

    if (foots != null) {

      Color fg = g.getColor();
      g.setColor(Color.white);
      g.fillRect(CalcXPos(CStr_x), CalcYPos(CStr_y)-g.getFont().getSize(),
		 commentClearWidth, g.getFont().getSize()+4);
      g.setColor(fg);

      /* zeichnen der Fuesse */
      for (int i=0; i<num_steps_painted; i++) {
	state_drawn |= foots.redrawFoot(g, this, step_arr[i]);
      }

      for (int i=num_steps_painted; i<num_steps; i++) {
	state_drawn |= foots.drawFoot(g, this, step_arr[i]);
      }

      /* Anzeige des slow/quick Strings. Geloescht wird er auf jeden Fall. */
      if (commentClearWidth == 0) commentClearWidth = 25;
	
      if (showComment && num_steps > 0) {
	if (useCommentHT) {
	  if (step_arr[num_steps-1].ht_id >= 0) {
	    String s = Figur.HT_str[app.engl ? 0 : 1][step_arr[num_steps-1].ht_id];
	    commentClearWidth = g.getFontMetrics().stringWidth(s);
	    g.drawString(s, CalcXPos(CStr_x), CalcYPos(CStr_y));
	  }
	}
	else {
	  if (step_arr[num_steps-1].takt_str != null) {
	    String s = step_arr[num_steps-1].takt_str;
	    commentClearWidth = g.getFontMetrics().stringWidth(s);
	    g.drawString(s, CalcXPos(CStr_x), CalcYPos(CStr_y));
	  }
	}
      }
		   
      num_steps_painted = num_steps;
      
      if (state_drawn) {
	for (int i=0; i<2; i++) {
	  for (int j=0; j<2; j++) {
	    for (int k=0; k<2; k++) {
	      for (int l=0; l<16; l++) {
		if (Foot.FootState[i][j][k][l] == Foot.IMG_DRAWN) {
		  Foot.FootState[i][j][k][l] = Foot.IMG_OK;
		}
	      }
	    }
	  }
	}
      }
      
    }
  }

  /**
   * Next time Floor.paint() will be called, all (floor and steps)
   * will be painted new.
   *
   * @see Floor#paint
   * @see Floor#repaintAllSteps
   */
  public void RedrawFloor()
  {
    redraw_floor = true;
    repaintAllSteps();
  }


  /**
   * Returns a String object representing.
   */
  public String toString()
  {
    return getClass().getName() + "[Titel=" + Titel + ", Steps=" + num_steps +
      		", Steps-drawn=" + num_steps_painted +
		", redraw-all=" + redraw_floor + "]";
  }


}    


