/* draw blue spots at each mouse click */

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Event;

public class Spots extends java.applet.Applet {

  final int MAXSPOTS = 10;
  int xspots[] = new int[MAXSPOTS];
  int yspots[] = new int[MAXSPOTS];
  int currspots = 0;

  public void init() {
    setBackground(Color.white);
  }

  public boolean mouseDown(Event evt, int x, int y) {
    if (currspots < MAXSPOTS) 
      addspot(x,y);
    else System.out.println("Too many spots.");      
    return true;
  }

  void addspot(int x,int y) {
    xspots[currspots] = x;
    yspots[currspots] = y;
    currspots++;
    repaint();
  }

  public void paint(Graphics g) {
    g.setColor(Color.blue);
    for (int i = 0; i < currspots; i++) {
      g.fillOval(xspots[i] -10, yspots[i] -10,20,20);
    }
  }
}






