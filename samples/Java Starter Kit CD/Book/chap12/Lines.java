/* draw blue spots at each mouse click, red spots at doubleclicks */

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Event;
import java.awt.Point;

public class Lines extends java.applet.Applet {

  Point starts[] = new Point[10]; // starting points of past lines
  Point ends[] = new Point[10];  // starting points of end lines
  Point anchor;  // start of current line
  int currline = 0; // number of lines
  Point currentpoint; // current end of line

  public void init() {
    setBackground(Color.white);
  }

  public boolean mouseDown(Event evt, int x, int y) {
    anchor = new Point(x,y);

    return true;
  }

  public boolean mouseUp(Event evt, int x, int y) {
    addline(x,y);
    return true;
  }

  public boolean mouseDrag(Event evt, int x, int y) {
    currentpoint = new Point(x,y);
    repaint();
    return true;
  }

  void addline(int x,int y) {
    Point stmp[];
    Point etmp[];
    
    // every ten spots, grow the arrays
    if (currline % 10 == 0) {
      stmp = new Point[currline + 10];
      etmp = new Point[currline + 10];
      System.arraycopy(starts, 0, stmp, 0, starts.length);
      System.arraycopy(ends, 0, etmp, 0, ends.length);      
      starts = stmp;
      ends = etmp;
    }

    starts[currline] = anchor;
    ends[currline] = new Point(x,y);
    currline++;
    currentpoint = null;
    repaint();
  }

  public void paint(Graphics g) {

    // Draw existing lines
    for (int i = 0; i < currline; i++) {
      g.drawLine(starts[i].x, starts[i].y,
		 ends[i].x, ends[i].y);
    }

    // draw current line
    g.setColor(Color.blue);
    if (currentpoint != null) 
      g.drawLine(anchor.x,anchor.y,currentpoint.x,currentpoint.y);
  }
}






