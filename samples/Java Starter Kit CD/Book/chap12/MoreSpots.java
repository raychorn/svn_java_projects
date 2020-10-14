/* draw blue spots at each mouse click, red spots at doubleclicks */

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Event;

public class MoreSpots extends java.applet.Applet {

  final long dctime = 500;  // millisecond between mouse clicks;
  Event pmc;
  
  int xspots[] = new int[10];
  int yspots[] = new int[10];
  boolean bluespot[] = new boolean[10];
  int currspots = 0;

  public void init() {
    setBackground(Color.white);
  }

  public boolean mouseDown(Event evt, int x, int y) {
    // first click only
    if (this.pmc != null) { // null equals first click
      // test for double click
      if ( (Math.abs(pmc.x - x) < 10) &&  
	  (Math.abs(pmc.y - y) < 10) &&   
	  ((evt.when - pmc.when) < dctime)) {  
	currspots--; //remove previous blue spot
	addspot(x,y,false);
      } 
      else addspot(x,y,true); // single click
    }
    else {
      // fist click ever 
      addspot(x,y,true);
    }

    this.pmc = evt; // current event is now past event;
    return true;
  }

  void addspot(int x,int y, boolean isBlue) {
    int xtmp[];
    int ytmp[];
    boolean ctmp[];
    
    // every ten spots, grow the arrays
    if (currspots % 10 == 0) {
      xtmp = new int[currspots + 10];
      ytmp = new int[currspots + 10];
      ctmp = new boolean[currspots + 10];
      System.arraycopy(xspots, 0, xtmp, 0, xspots.length);
      System.arraycopy(yspots, 0, ytmp, 0, yspots.length);      
      System.arraycopy(bluespot, 0, ctmp, 0, bluespot.length);      
      xspots = xtmp;
      yspots = ytmp;
      bluespot = ctmp;
    }

    xspots[currspots] = x;
    yspots[currspots] = y;
    bluespot[currspots] = isBlue;
    currspots++;
    repaint();
  }

  public void paint(Graphics g) {
    for (int i = 0; i < currspots; i++) {
      if (bluespot[i]) 
	g.setColor(Color.blue);
      else g.setColor(Color.red);

      g.fillOval(xspots[i] -10, yspots[i] -10,20,20);
    }
  }
}






