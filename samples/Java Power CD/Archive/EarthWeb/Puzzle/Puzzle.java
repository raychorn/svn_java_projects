/***********************************************************************
Copyright Notice: This source code is (C) Copyright 1995-96, EarthWeb LLC.,
All Rights Reserved. Distribution of this document or it's resulting
compiled code is granted for non-commercial use, with prior approval of
EarthWeb LLC. Distribution of this document or its resulting compiled
code, for commercial use, is granted only with prior written approval of
EarthWeb, LLC. For information, send email to info@earthweb.com.
***********************************************************************/
import Draggable;

import DragImage;
import Arena;
import Dock;
import java.awt.*;
import java.util.Random;
import java.util.Vector;

public class Puzzle extends Arena implements java.awt.image.ImageObserver {
/** This presents the user with a number of rectangular pieces to assemble.
*/
  int rows=4, columns=4;
  int imagew, imageh;
  Image src;
  Graphics offscreen = null;
  Image offscreenimage = null;
  boolean imageready = false;
  Draggable solution[];
  Dock docks[];
  int ndocks;

  public boolean imageUpdate(Image i, int info, int x, int y, int w, int h) {
/** Since we have to process the image as a whole, we have to block until
    it's completely loaded.  Since the applet is an ImageObserver, we can
    keep tabs on its loading.
*/
    if ((info & ALLBITS) == ALLBITS) {
      imageready = true;
      return false;
    } else {
      return true;
    }
  }

  public void init() {
/**  This loads the image, stretches it to fit, chops it up, and scrambles it.
*/
    revertOnExit = false;
    Random rnd = new Random();
    offscreenimage = createImage(size().width, size().height);
    offscreen = offscreenimage.getGraphics();
    imagew = size().width/2;
    imageh = size().height;

/** The "rows" parameter can take a range of values, for the sake of variety.
   We use the '-' character as a delimitor and parse it out. */
    String rowstring  = getParameter("rows");
    if (rowstring != null) {
      int n = rowstring.indexOf('-');
      if (n == -1) rows = Integer.valueOf(rowstring).intValue();
      else {
        int start=Integer.valueOf(rowstring.substring(0, n)).intValue();
        int end=Integer.valueOf(rowstring.substring(n+1, rowstring.length())).intValue();
        rows = start+(int)(rnd.nextFloat()*(end-start+1));
      }
    }
/** Columns behave similarly. */
    String columnstring  = getParameter("columns");
    if (columnstring != null) {
      int n = columnstring.indexOf('-');
      if (n == -1) columns = Integer.valueOf(columnstring).intValue();
      else {
        int start=Integer.valueOf(columnstring.substring(0, n)).intValue();
        int end=Integer.valueOf(columnstring.substring(n+1, columnstring.length())).intValue();
        columns = start+(int)(rnd.nextFloat()*(end-start+1));
      }
    }
/** If a single image is specified via the "img" parameter, it is used.  If
    multiple images are specified via the "imgs" parameter, we select one
    at random. */
    String gifname = getParameter("img");
    if (gifname == null) {
      String gifnames = getParameter("imgs");
      if (gifnames == null) gifname = "fang.gif";
      else {
        int nspaces=0, idx;
        for (idx=0; idx != -1; idx=gifnames.indexOf(' ', idx+1)) nspaces++;
        if (idx == 0) gifname = gifnames;
        else {
          int n = (int)(rnd.nextFloat()*(nspaces+1));
          int start=0, end;
          for (int k=0; k<n; k++) start = gifnames.indexOf(' ', start+1);
          if (n != 0) start++;
          end = gifnames.indexOf(' ', start);
          if (end  == -1) end = gifnames.length();
          gifname = gifnames.substring(start, end);
        }
      }
    }
    showStatus("Puzzle: Loading picture...");
    src = getImage(getCodeBase(), gifname);
    Image scalei = createImage(imagew, imageh);
/** The image is scaled to fit in the window, then chopped up by drawing it
    into small canvases. */
    Graphics scaleg = scalei.getGraphics();
    scaleg.setColor(getBackground());
    scaleg.fillRect(0, 0, imagew, imageh);
    if (!(scaleg.drawImage(src, 0, 0, imagew, imageh, this))) {
      while (!imageready) {
        try java.lang.Thread.sleep(10);
        catch (java.lang.InterruptedException e);
      }
      scaleg.drawImage(src, 0, 0, imagew, imageh, this);
    }
    showStatus("Puzzle: scrambling picture...");
    src = scalei;
    solution = new DragImage[rows*columns];
    for (int k=0; k<columns; k++) for (int l=0; l<rows; l++) {
      int left, right, top, bottom, w, h;
      left = k*imagew/columns;
      right = (k+1)*imagew/columns;
      top = l*imageh/rows;
      bottom = (l+1)*imageh/rows;
      w = right-left;
      h = bottom-top;
      Image pieceimg = createImage(w, h);
      Graphics pieceg = pieceimg.getGraphics();
      pieceg.setColor(Color.blue); pieceg.fillRect(0, 0, w, h);
      pieceg.drawImage(src, -left, -top, this);
      left = (int)(rnd.nextFloat()*(imagew-w));
      top = (int)(rnd.nextFloat()*(imageh-h));
      solution[(k*rows)+l] = add(pieceimg, left, top);
    }
    // Scramble the order so we don't get the pieces stacked sequentially
    Vector newpieces = new Vector(rows*columns);
    while (!(pieces.isEmpty())) {
      int here = (int)(rnd.nextFloat()*(pieces.size()));
      newpieces.addElement(pieces.elementAt(here));
      pieces.removeElementAt(here);
    }
    pieces = newpieces;

    // Docks are places where the pieces will stick.
    ndocks = rows*columns;
    docks = new Dock[ndocks];
    for (int k=0; k<columns; k++) for (int l=0; l<rows; l++) {
      int left, top;
      left = k*imagew/columns;
      top = l*imageh/rows;
      docks[(k*rows)+l] = new Dock(left+imagew, top);
    }
    showStatus("Puzzle: Ready");
  }

  public void start(){
/** Just start up the show by repainting */
    repaint();
  }

  void dock(Draggable d) {
/** When we drop a piece, this sends it to the nearest available unoccupied
    puzzle position. */
    if (d.x + d.width < imagew) return;
    float dist, mindist;
    int k, l;
    for (k=0; k<ndocks; k++)
      if (!(docks[k].occupied)) {
        mindist = docks[k].distance(d);
        for (l=k+1; l<ndocks; l++)
          if (!(docks[l].occupied)) {
            dist = docks[l].distance(d);
            if (dist < mindist) {
              mindist = dist;
              k = l;
            }
          }
//        DockNoise.play();
        docks[k].anchor(d);
        domove(d);
        return;
      }
//    RejectNoise.play();
    d.revert();
    dock(d);
    domove(d);
  }


  public void pieceDropped(Draggable d, int x, int y) {
/** Called when a piece is dropped */
    dock(d);
    checkwin();
    domove(d);
  }
  public void pieceGrabbed(Draggable d, int x, int y) {
/** Called when a piece it picked up.  Frees up a dock, if necessary. */
    int k;
    for (k=0; k<ndocks; k++)
      if (docks[k].occupied && (docks[k].occupant == d))
        docks[k].unanchor();
  }

  boolean won = false;
  public boolean checkwin() {
/** Checks to see if the puzzle is complete */
    int k;
    for (k=0; k<ndocks; k++) {
      if (!(docks[k].occupied) || docks[k].occupant != solution[k]) {
        won = false;
        repaint();
        return won;
      }
    }
    won = true;
    showStatus("Puzzle: You win!");
    repaint();
//    System.out.println("Puzzle completed.");
    return won;
  }

  public void dopaint(Graphics g) {
/** Draws the frame and victory message, if applicable */
    g.setColor(Color.white);
    g.fillRect(0, 0, size().width, size().height);
    g.setColor(Color.black);
    g.drawRect(size().width-imagew, 0, imagew-1, imageh-1);
    if (won) {
      g.setFont(new Font("Times", Font.BOLD,16));
      FontMetrics fm = g.getFontMetrics();
      String s = "You win!";
      int top = (imageh-fm.getHeight())/2;
      int left = (imagew-fm.stringWidth(s))/2;
      g.drawString(s, left, top);
    }
    super.paint(g);
  }
  public void paint(Graphics g) {
/** Standard double-buffering */
//    dopaint(g);
    dopaint(offscreen);
    g.drawImage(offscreenimage, 0, 0, null);
  }
  public void update(Graphics g) {
/** Calls paint() without clearing first */
    paint(g);
  }
  
}

