/* get and drawn an image */

import java.awt.Graphics;
import java.awt.Image;

public class LadyBug extends java.applet.Applet {

  Image bugimg;

  public void init() {
    bugimg = getImage(getCodeBase(), "images/ladybug.gif");
  }
    
  public void paint(Graphics g) {
    g.drawImage(bugimg,10,10,this);
  }
}






