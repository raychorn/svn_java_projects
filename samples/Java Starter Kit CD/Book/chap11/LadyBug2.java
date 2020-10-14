/* get and drawn an image */

import java.awt.Graphics;
import java.awt.Image;

public class LadyBug2 extends java.applet.Applet {

  Image bugimg;

  public void init() {
    bugimg = getImage(getCodeBase(), "images/ladybug.gif");
  }
    
  public void paint(Graphics g) {
    int iwidth = bugimg.getWidth(this);
    int iheight = bugimg.getHeight(this);
    int xpos = 10;


    // 25 %
    g.drawImage(bugimg,xpos,10, iwidth / 4, iheight / 4, this);

    // 50 %
    xpos += (iwidth / 4) + 10;
    g.drawImage(bugimg, xpos , 10, 
		iwidth / 2, iheight / 2, this);

    // 100%
    xpos += (iwidth / 2) + 10;
    g.drawImage (bugimg,xpos, 10, this);

    // 150% x, 25% y
    g.drawImage(bugimg, 10, iheight + 30, 
		(int)(iwidth * 1.5), iheight / 4, this);
  }
}






