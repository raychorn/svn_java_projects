/*-
 * Copyright (c) 1995 by Georg Hessmann.
 * All Right Reserved.
 *
 * Button.java	1.0f 25.08f.95
 *
 */


import java.awt.Graphics;
import java.awt.Image;
import java.awt.Font;
import java.awt.Color;

import MEXButtonList;

/**
 * Button is the main class for creating buttons.
 *
 * @see ToggleButton
 * @see MEXButton
 * @see MEXButtonList
 *
 * @version 1.0f 25 Aug 1995
 * @author Georg He&szlig;mann
 */

class Button {

  /**
   * Static pointer to the main applet. Has to be set before
   * the first use of a button.
   */
  public static Dance app = null;
  
  static Button	BLst = null;

  Button        Next = null;

  int bX, bY, bW, bH;

  Font          BFont;
  String        Label;
  int           LabelWidth;

  int           BNumber;

  boolean       Selected  = false;	// mouseDown bekommen
  boolean	isActive  = false;	// bleibt auch bei mouseDrag true
  boolean	Changed   = true;


  /**
   * Creates a button.
   * @param bnum id-number for the button
   * @param l label
   * @param x x-ccordinate of the button
   * @param y y-coordinate of the button
   * @param w the size().width
   * @param h the size().height
   * @param f the font used for the label
   */
  public Button(int bnum, String l, int x, int y, int w, int h, Font f)
  {
    Label  = l;
    BFont  = f;
    bX = x;
    bY = y;
    bW = w;
    bH = h;

    BNumber = bnum;

    Next   = BLst;
    BLst   = this;

    LabelWidth = -1; 
  }

  /**
   * Deletes all buttons.
   */
  public static void freeAll()
  {
    BLst = null;
  }

  /**
   * Redraw all buttons.
   */
  static public void drawAll(Graphics g)
  {
    Button lst = BLst;

    while (lst != null) {
      lst.drawButton(g);
      lst = lst.Next;
    }
  }


  /**
   * Redraw only these buttons, which are changed since the last
   * redraw.
   */
  static public void drawChanged(Graphics g)
  {
    Button lst = BLst;

    while (lst != null) {
      if (lst.Changed) {
	lst.drawButton(g);
	lst.Changed = false;
      }
      lst = lst.Next;
    }
  }


  /**
   * Handels a mouse click. Normaly called from the applet.
   * If something has changed, java.applet.Applet.repaint() will be called.
   */
  static public boolean mouseDown(int x, int y)
  {
    boolean changed = false;
    Button lst = BLst;

    while (lst != null) {
      boolean b = lst.mouseDownButton(x, y);
      changed = changed || b;
      lst = lst.Next; 
    }

    if (changed) app.repaint();
    
    return false;
  }

  /**
   * Handels a mouse up event. Normaly called from the applet.
   * If something has changed java.applet.Applet.repaint() will be called.
   * If the mouse down and the mouse up event was over the
   * same button, the action function of this button will be called.
   */
  static public boolean mouseUp(int x, int y)
  {
    boolean changed = false;
    Button lst = BLst;

    while (lst != null) {
      boolean b = lst.mouseUpButton(x, y);
      changed = changed || b;
      lst = lst.Next;
    }

    if (changed) app.repaint();

    return false;
  }

  /**
   * Handels a mouse drag event. Normaly called from the applet.
   * If something has changed java.applet.Applet.repaint() will be called.
   */
  static public boolean mouseDrag(int x, int y)
  {
    boolean changed = false;
    Button lst = BLst;

    while (lst != null) {
      boolean b = lst.mouseDragButton(x, y);
      changed = changed || b;
      lst = lst.Next;
    }

    if (changed) app.repaint();

    return false;
  }

  /**
   * Gets the id-number of this button.
   * @return id-number of this button.
   */
  public int GetNum()
  {
    return BNumber;
  }

  protected void drawButton(Graphics g)
  {
    if (LabelWidth == -1) {
      LabelWidth = g.getFontMetrics(BFont).stringWidth(Label);
    }
    
    Font oldf   = g.getFont();
    Color fgCol = g.getColor();

    g.setFont(BFont);
    g.setColor(Color.lightGray);
    
    if (Selected) {
      g.fill3DRect(bX, bY, bW, bH, false);
      if (app.fatBorder) g.draw3DRect(bX+1, bY+1, bW-2, bH-2, false);
      g.setColor(Color.black);
      g.drawString(Label, bX + (bW - LabelWidth)/2 + 1, bY + BFont.getSize() + 2 + 1);
    }
    else {
      g.fill3DRect(bX, bY, bW, bH, true);
      if (app.fatBorder) g.draw3DRect(bX+1, bY+1, bW-2, bH-2, true);
      g.setColor(Color.black);
      g.drawString(Label, bX + (bW - LabelWidth)/2, bY + BFont.getSize() + 2);
    }
    
    g.setFont(oldf);
    g.setColor(fgCol);
  }


  private boolean mouseDownButton(int x, int y)
  {
    if (x >= bX && x < bX+bW && y >= bY && y < bY+bH) {
      Selected = true;
      isActive = true;
      Changed  = true;
    }

    return Changed;
  }


  protected boolean mouseUpButton(int x, int y)
  {
    if (x >= bX && x < bX+bW && y >= bY && y < bY+bH) {
      if (Selected) {
	Selected = false;
	Changed  = true;
	action();
      }
    }
    else {
      if (Selected) {
	Selected = false;
	Changed  = true;
      }
    }
    isActive = false;

    return Changed;
  }


  private boolean mouseDragButton(int x, int y)
  {
    if (x >= bX && x < bX+bW && y >= bY && y < bY+bH) {
      if (isActive && !Selected) {
	Selected = true;
	Changed  = true;
      }
    }
    else {
      if (Selected) {
	Selected = false;
	Changed  = true;
      }
    }

    return Changed;
  }

  /**
   * Main scheduling function. Will be called if an button
   * is correctly pressed (mouse down and up).
   */
  protected void action()
  {
    switch (BNumber) {
     case app.BslowW:
     case app.Btango:
     case app.BslowF:
     case app.Bquick:
      app.actionDanceButton(BNumber);
      break;
     case app.Bshow:
      app.actionShowButton();
      break;
     case app.Binfo:
      app.actionInfoButton();
      break;
     case app.Bpmusic:
      app.actionPlayMusicButton();
      break;
     case app.Bmhelp:
      app.actionMainHelpButton();
      break;
     case app.Bahelp:
      app.actionAnimationHelpButton();
      break;
     case app.Bstep:
      app.actionStepButton();
      break;
     case app.Bback:
      app.actionBackStepButton();
      break;
     case app.Bplay:
      app.actionPlayButton();
      break;
     case app.Bmenu:
      app.actionMenuButton();
      break;
     case app.Bsound:
      app.actionSoundButton(((ToggleButton)this).GetChoosed());
      break;
     case app.Bcomm:
      app.actionCommentButton(((ToggleButton)this).GetChoosed());
      break;
     default:
      if (BNumber >= app.Bfigur0 && BNumber < app.Bfigur0+10) {
	app.actionFigurButton(BNumber);
      }
      else if (BNumber >= app.Bmusic0 && BNumber < app.Bmusic0+10) {
	app.actionMusicButton(BNumber);
      }
      break;
    }
  }
}


/**
 * MEXButton is the class of mutual exclusive buttons.
 *
 * @see MEXButtonList
 *
 * @version 1.0f 25 Aug 1995
 * @author Georg He&szlig;mann
 */

class MEXButton extends Button {
  MEXButtonList	Father;
  boolean	choosed;
  MEXButton	MEXList;
  Font		choosedFont;
  int	  	choosedLabelWidth;

  /**
   * Creates an mutual exclusive button. Will be called only
   * from within the MEXButtonList class.
   *
   * @param father MEXButtonList head
   * @param brother previsious MEXButton
   * @param bnum id-number for the button
   * @param l label
   * @param x x-ccordinate of the button
   * @param y y-coordinate of the button
   * @param w the size().width
   * @param h the size().height
   * @param f the font used for the label
   * @param cfnt the font used for the selected label
   */
  public MEXButton(MEXButtonList father, MEXButton brother,
		   int bnum, String l, int x, int y, int w, int h, Font f,
		   Font cfnt)
  {
    super(bnum, l, x, y, w, h, f);

    Father  = father;
    choosed = false;		// wird erst nachtraeglich gesetzt
    MEXList = brother;
    choosedFont = cfnt;
    
    choosedLabelWidth = -1;
  }

  /**
   * Set this MEXButton to the selected button.
   * Will be called only via class MEXButtonList.
   * Don't call this function directly.
   */
  public void SetChoosed()
  {
    if (!choosed) {
      Changed = true;
      choosed = true;
    }
  }
  
  /**
   * Set this MEXButton to an unselected button.
   * Will be called only via class MEXButtonList.
   * Don't call this function directly.
   */
  public void ClearChoosed()
  {
    if (choosed) {
      Changed = true;
      choosed = false;
    }
  }

  /**
   * Is this button the selected button?
   * @return state of this button
   */
  public boolean IsChoosed()	{ return choosed;  }

  /**
   * Get the next MEXButton.
   * Will be called only via class MEXButtonList.
   * Don't call this function directly.
   * @return next MEXButton in the internal list.
   */
  public MEXButton NextMEX()	{ return MEXList;  }


  protected void drawButton(Graphics g)
  {
    if (LabelWidth == -1) {
      LabelWidth = g.getFontMetrics(BFont).stringWidth(Label);
    }
    if (choosedLabelWidth == -1) {
      choosedLabelWidth = g.getFontMetrics(choosedFont).stringWidth(Label);
    }
    
    Font oldf   = g.getFont();
    Color fgCol = g.getColor();

    int    labw;

    if (choosed) {
      g.setFont(choosedFont);
      labw = choosedLabelWidth;
    }
    else {
      g.setFont(BFont);
      labw = LabelWidth; 
    }

    if (choosed) g.setFont(choosedFont);
    else	 g.setFont(BFont);
      
    g.setColor(Color.lightGray);
    
    if (Selected) {
      g.fill3DRect(bX, bY, bW, bH, false);
      if (app.fatBorder) g.draw3DRect(bX+1, bY+1, bW-2, bH-2, false);
      g.setColor(Color.black);
      g.drawString(Label, bX + (bW - labw)/2 + 1, bY + BFont.getSize() + 2 + 1);
    }
    else {
      g.fill3DRect(bX, bY, bW, bH, true);
      if (app.fatBorder) g.draw3DRect(bX+1, bY+1, bW-2, bH-2, true);
      g.setColor(Color.black);
      g.drawString(Label, bX + (bW - labw)/2, bY + BFont.getSize() + 2);
    }
    
    g.setFont(oldf);
    g.setColor(fgCol);
  }


  protected boolean mouseUpButton(int x, int y)
  {
    if (x >= bX && x < bX+bW && y >= bY && y < bY+bH) {
      if (Selected) {
	Selected = false;
	Changed  = true;
	boolean dummy = Father.SetChoosedNum(BNumber);
	action();
      }
    }
    else {
      if (Selected) {
	Selected = false;
	Changed  = true;
      }
    }
    isActive = false;

    return Changed;
  }

}


/**
 * ToggleButton is the class for buttons with to states.
 *
 * @version 1.0f 25 Aug 1995
 * @author Georg He&szlig;mann
 */

class ToggleButton extends Button {
  
  boolean choosed;
  String  choosedLabel;
  int	  choosedLabelWidth;
  Font    choosedFont;

  /**
   * Creates a toggle button.
   * This button has two states represended by two labels and two fonts.
   *
   * @param bnum id-number for the button
   * @param l label for state one
   * @param cl label for state two
   * @param x x-ccordinate of the button
   * @param y y-coordinate of the button
   * @param w the size().width
   * @param h the size().height
   * @param f the font used for the label in state one
   * @param cf the font used for the label in state two
   * @param cho initialize the ToggleButton in state two?
   */
  public ToggleButton(int bnum, String l, String cl, int x, int y, int w, int h,
		      Font f, Font cf, boolean cho)
  {
    super(bnum, l, x, y, w, h, f);

    choosed      = cho;
    choosedLabel = cl;
    choosedFont  = cf;
    
    choosedLabelWidth = -1;
  }

  /**
   * Returns the state of the button.
   * @return is the button in state two?
   */
  public boolean GetChoosed()
  {
    return choosed;
  }

  protected void drawButton(Graphics g)
  {
    if (LabelWidth == -1) {
      LabelWidth = g.getFontMetrics(BFont).stringWidth(Label);
    }
    if (choosedLabelWidth == -1) {
      choosedLabelWidth = g.getFontMetrics(choosedFont).stringWidth(choosedLabel);
    }

    Font oldf   = g.getFont();
    Color fgCol = g.getColor();

    int    labw;
    String lab;

    if (choosed) {
      g.setFont(choosedFont);
      lab  = choosedLabel;
      labw = choosedLabelWidth;
    }
    else {
      g.setFont(BFont);
      lab  = Label;
      labw = LabelWidth;
    }
      
    g.setColor(Color.lightGray);
    
    if (Selected) {
      g.fill3DRect(bX, bY, bW, bH, false);
      if (app.fatBorder) g.draw3DRect(bX+1, bY+1, bW-2, bH-2, false);
      g.setColor(Color.black);
      g.drawString(lab, bX + (bW - labw)/2 + 1,
		   bY + BFont.getSize() + 2 + 1);
    }
    else {
      g.fill3DRect(bX, bY, bW, bH, true);
      if (app.fatBorder) g.draw3DRect(bX+1, bY+1, bW-2, bH-2, true);
      g.setColor(Color.black);
      g.drawString(lab, bX + (bW - labw)/2, bY + BFont.getSize() + 2);
    }
    
    g.setFont(oldf);
    g.setColor(fgCol);
  }


  protected boolean mouseUpButton(int x, int y)
  {
    if (x >= bX && x < bX+bW && y >= bY && y < bY+bH) {
      if (Selected) {
	choosed  = !choosed;
	Selected = false;
	Changed  = true;
	action();
      }
    }
    else {
      if (Selected) {
	Selected = false;
	Changed  = true;
      }
    }
    isActive = false;

    return Changed;
  }
}
