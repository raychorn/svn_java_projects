/***********************************************************************
Copyright Notice: This source code is (C) Copyright 1995, EarthWeb LLC.,
All Rights Reserved. Distribution of this document or it's resulting
compiled code is granted for non-commercial use, with prior approval of
EarthWeb LLC. Distribution of this document or its resulting compiled
code, for commercial use, is granted only with prior written approval of
EarthWeb, LLC. For information, send email to info@earthweb.com.
***********************************************************************/
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Event;
import java.util.Vector;
import java.util.Enumeration;

/** A general-purpose draggable object.  A Draggable must be given a paint()
 * method and must be bound to an Arena to work.
 *
 * @version 1.0 11/20/95
 * @author Carl Muckenhoupt
 */
abstract class Draggable extends Object {
  public int x=0, y=0; // location within the arena
  public int lastx=0, lasty=0; // last position at which it was drawn

  public Arena parent; // the arena containing the draggable

  public int width=0, height=0;
  int minx=0, miny=0, maxx=0, maxy=0; // bounding rectangle
  private int startx=0, starty = 0;

  /** Sets the bounding rectangle
   */
  public void setBound(int l, int t, int w, int h) {
    minx = l;
    miny = t;
    maxx = l + w - width;
    maxy = t + h - height;
  }
  /** Sets the bounding rectangle
   */
  public void setBound(Rectangle r) {
    setBound(r.x, r.y, r.width, r.height);
  }
  /** Sets the Arena.  Do not bind a Draggable to more than one Arena!
   */
  void setParent(Arena a) {
    parent = a;
    setBound(a.bounds());
  }
  public Draggable() {
  }

  /** Moves the Draggable to a new location
   */
  public void move(int newx, int newy) {
    if (newx < minx) x = minx;
    else if (newx > maxx) x = maxx;
    else x = newx;
    if (newy < miny) y = miny;
    else if (newy > maxy) y = maxy;
    else y = newy;
    parent.pieceMoved(this, newx, newy);
  }
  /** Called in response to dragging the piece to a new location
   */
  public void drag(int newx, int newy) {
    move(newx-xdiff, newy-ydiff);
  }
  /** Moves a piece back to the position it occupied before dragging
   */
  public void revert() {
    move(startx, starty);
  }

  int xdiff=0, ydiff=0; // offset from mouse
  /** The MouseDown event is handed down to the Draggables by the Arena.
   */
  public boolean mouseDown(Event evt, int mx, int my) {
    if (mx >= x && mx < x+width && my >= y && my < y+height) {
      xdiff = mx-x;
      ydiff = my-y;
      parent.grab(this);
      startx = x;
      starty = y;
      return true;
    } else return false;
  }

  /** Checks for intersection with a rectangle.  Used by drawing routines.
   */
  public boolean intersects(Rectangle cliprect) {
    if (x > (cliprect.x+cliprect.width)) return false;
    if (y > cliprect.y+cliprect.height) return false;
    if (x+width < cliprect.x) return false;
    if (y+height < cliprect.y) return false;
    return true;
  }
  /** Draws the item
   */
  abstract public void paint(Graphics g);
}

/** The most generally useful form of Draggable:  A static image that can
 * be dragged.
 *
 * @version 1.0 11/20/95
 * @author Carl Muckenhoupt
 */
class DragImage extends Draggable implements java.awt.image.ImageObserver{
  public Image image = null;
  /** Sets the image used for the piece's appearance
   */
  public void setImage(Image img) {
    int oldwidth = width, oldheight = height;
    width = img.getWidth(this);
    if (width == -1) width = 0;
    height = img.getHeight(this);
    if (height == -1) height = 0;
    image = img;
    setBound(minx, miny, maxx+oldwidth, maxy+oldheight);
    parent.domove(this);
  }
  /** Your basic asynchronous loader:  waits until all the bits are loaded,
   * then draws the image.
   */
  public boolean imageUpdate(Image img, int infoflags,
                             int x1, int y1, int w1, int h1) {
    if (infoflags == ALLBITS) {
      width = img.getWidth(this);
      height = img.getHeight(this);
      setBound(minx, miny, maxx, maxy);
      if (parent != null) parent.domove(this);
      return false;
    } else return true;
  }
  /** Draws the item
   */
  public void paint(Graphics g) {
    lastx = x;
    lasty = y;
    g.drawImage(image, x, y, this);
  }
}

/** An Arena is an applet containing Draggables.  Perhaps it should be a Canvas
 * or something instead of an applet, but this is simpler and faster for the
 * things I'm using it for.
 *
 * @version 1.0 11/20/95
 * @author Carl Muckenhoupt
 */
class Arena extends java.applet.Applet {
  /** The piece currently being dragged.  If nothing is being dragged, null.
   */
  public Draggable grabbed = null;
  /** A list of the Draggables currently in use
   */
  Vector pieces = new Vector();

  private Image snapshot;
  private Graphics snapshooter;

  /** If true, then the piece being dragged will be dropped and revert to its
   * original position when the mouse leaves the Arena.
   */
  public boolean revertOnExit = true;

  /** Adds a complete Draggable to the Arena
   */
  public void add(Draggable d) {
    d.setParent(this);
    pieces.addElement(d);
  }
  /** Creates a DragImage from the given Image, and adds it to the Arena
   */
  public Draggable add(Image img, int x1, int y1) {
    DragImage d = new DragImage();
    add(d);
    d.setImage(img);
    d.move(x1, y1);
    return d;
  }

  /** Causes the given piece to be placed above the others
   */
  void popup(Draggable d) {
    pieces.removeElement(d);
    pieces.addElement(d);
  }

  /** Called whenever a piece is moved.  Override this for special behavior.
   */
  public void pieceMoved(Draggable d, int x, int y) {}
  /** Called whenever a piece starts to be dragged.  Override this for special behavior.
   */
  public void pieceGrabbed(Draggable d, int x, int y) {}
  /** Called whenever a piece ceases to be dragged.  Override this for special behavior. */
  public void pieceDropped(Draggable d, int x, int y) {}

  /** Start dragging a piece.  Usually only called in response to mouseDown events.
   */
  public synchronized void grab(Draggable d) {
    grabbed = d;
    pieceGrabbed(d, d.x, d.y);
  }
  /** Stop dragging a piece.
   */
  public synchronized void drop() {
    Draggable d = grabbed;
    grabbed = null;
    pieceDropped(d, d.x, d.y);
  }
  public boolean mouseDown(Event evt, int x, int y) {
    for (int k = pieces.size()-1; k>=0; k--) {
      Draggable d = (Draggable)(pieces.elementAt(k));
      if (d.mouseDown(evt, x, y)) {
        if (k < pieces.size()-1) popup(d);
        domove(d);
        break;
      }
    }
    return true;
  }
  public boolean mouseUp(Event evt, int x, int y) {
    if (grabbed != null) drop();
    return true;
  }
  public boolean mouseMove(Event evt, int x, int y) {
    return mouseUp(evt, x, y);
  }
  public boolean mouseDrag(Event evt, int x, int y) {
    if (grabbed != null) {
      grabbed.drag(x, y);
      domove(grabbed);
    }
    return true;
  }
  public boolean mouseExit(Event evt, int x, int y) {
    if (grabbed != null) {
      Draggable d = grabbed;
      if (revertOnExit) {
        grabbed.revert();
        drop();
        domove(d);
      }
    }
    return true;
  }

  /** The default paint routine draws all the Draggables within the Graphics
   * object.  If you override this routine, be sure to call it using
   * super.paint(g).
   */
  public void paint(Graphics g) {
    Rectangle cr = g.getClipRect();
    if (cr == null || cr.x == Integer.MIN_VALUE)
      cr = new Rectangle (0, 0, size().width, size().height);
    for (Enumeration e = pieces.elements(); e.hasMoreElements();) {
      Draggable d = (Draggable)(e.nextElement());
      if (d.intersects(cr)) d.paint(g);
    }
  }
  /** Redraws the given piece and the place it moved from.  Used by the
   * routines that move pieces so they don't have to redraw the entire
   * applet.  Override this if, for example, you want to update at regular
   * intervals of time rather than in response to mouse events.
   */
  public void domove(Draggable d) {
    repaint(d.lastx, d.lasty, d.width, d.height);
    repaint(d.x, d.y, d.width, d.height);
  }
}

