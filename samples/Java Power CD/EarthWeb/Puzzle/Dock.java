/***********************************************************************
Copyright Notice: This source code is (C) Copyright 1995-96, EarthWeb LLC.,
All Rights Reserved. Distribution of this document or it's resulting
compiled code is granted for non-commercial use, with prior approval of
EarthWeb LLC. Distribution of this document or its resulting compiled
code, for commercial use, is granted only with prior written approval of
EarthWeb, LLC. For information, send email to info@earthweb.com.
***********************************************************************/

import java.awt.Point;
import java.awt.Graphics;

class Dock {
/** A dock is a place where a draggable can be put.  The idea is to have
  pieces jump to the nearest available dock. */
  Point p;
  int width=0, height=0;
  public boolean occupied = false;
  public Draggable occupant = null;

  public Dock(int x, int y) {
    p = new Point(x, y);
  }
  public Dock(int x, int y, int w, int h) {
    p = new Point(x, y);
    width = w;
    height = h;
    sync = new Object();
  }
  public float distance(Draggable n) {
/** The distance function used to find the nearest dock. */
    return (float)(Math.sqrt((n.x-p.x)*(n.x-p.x) + (n.y-p.y)*(n.y-p.y)));
  }
  public void anchor(Draggable n) {
/** Sets a signal that the dock is occupied by a certain draggable. */
    synchronized(this) {
      occupied = true;
      occupant = n;
      n.move(p.x, p.y);
    }
  }
  public void unanchor() {
/** Sets a signal that the dock is unoccupied. */
    synchronized(this) {
      occupied = false;
    }
  }
  public void paint(Graphics g) {
    g.drawRect(p.x, p.y, width, height);
  }
}

