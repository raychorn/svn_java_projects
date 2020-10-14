/*-
 * Copyright (c) 1995 by Georg Hessmann.
 * All Right Reserved.
 *
 * MEXButtonList.java	1.0f 25.08f.95
 *
 */

import java.awt.Font;

import Button;

/**
 * MEXButtonList is a header for a group of mutual
 * exclusive buttons of type MEXButton.
 *
 * @see MEXButton
 *
 * @version 1.0f 25 Aug 1995
 * @author Georg He&szlig;mann
 */

class MEXButtonList {

  MEXButton	List;
  Font		choFont;

  /**
   * Create the MEXButton list header.
   * @param fnt font used for the selected element
   */
  public MEXButtonList(Font fnt)
  {
    List    = null;
    choFont = fnt;
  }

  /**
   * Insert a new MEXButton into the list of mutual exclusive buttons.
   * @param bnum id number
   * @param l label
   * @param x x-ccordinate of the button
   * @param y y-coordinate of the button
   * @param w the size().width
   * @param h the size().height
   * @param f the font used for the not selected buttons
   */
  public void NewMEXButton(int bnum, String l, int x, int y, int w, int h, Font f)
  {
    List = new MEXButton(this, List, bnum, l, x, y, w, h, f, choFont);
  }

  /**
   * Select MEXButton with given id-number.
   *
   * @return Returns, if something has changed and an repaint() is needed.
   */
  public boolean SetChoosedNum(int num)
  {
    MEXButton lst   = List;
    boolean changed = false;
    
    while (lst != null) {
      if (lst.IsChoosed() && lst.GetNum() != num) {
	lst.ClearChoosed();
	changed = true;
      }
      if (!lst.IsChoosed() && lst.GetNum() == num) {
	lst.SetChoosed();
	changed = true;
      }
      lst = lst.NextMEX();
    }
    return changed;
  }

  /**
   * @return id-number of the selected button
   */
  public int GetChoosedNum()
  {
    MEXButton lst = List;
    MEXButton cho = null;
    
    while (lst != null && cho == null) {
      if (lst.IsChoosed()) cho = lst;
      lst = lst.NextMEX();
    }

    return (cho != null) ? cho.GetNum() : -1;
  }
}

