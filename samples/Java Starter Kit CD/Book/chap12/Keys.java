/* draw blue spots at each mouse click, red spots at doubleclicks */

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Event;
import java.awt.Font;

public class Keys extends java.applet.Applet {

  char currkey;
  int currx;
  int curry;

  public void init() {
    currx = (this.size().width / 2) -8;  //default
    curry = (this.size().height / 2) -16; 

    setBackground(Color.white);
    setFont(new Font("Helvetica",Font.BOLD,36));
  }

  public boolean keyDown(Event evt, int key) {
    switch (key) {
    case Event.DOWN: 
      curry += 5;
      break;
    case Event.UP: 
      curry -= 5;
      break;
    case Event.LEFT:
      currx -= 5;
      break;
    case Event.RIGHT:
      currx += 5;
      break;
    default:
      currkey = (char)key;
    }

    repaint();
    return true;
  }

  public void paint(Graphics g) {
    if (currkey != 0) {
      g.drawString(String.valueOf(currkey), currx,curry);
    }
  }
}






