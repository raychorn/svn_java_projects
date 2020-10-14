
import java.awt.Graphics;
import java.awt.Color;

public class ColorBoxes extends java.applet.Applet {

  public void paint(Graphics g) {
    int rval, gval, bval;

    for (int j = 30; j < (this.size().height -25); j += 30) 
      for (int i = 5; i < (this.size().width -25); i+= 30) {
	rval = (int)Math.floor(Math.random() * 256);
	gval = (int)Math.floor(Math.random() * 256);
	bval = (int)Math.floor(Math.random() * 256);
	
	g.setColor(new Color(rval,gval,bval));
	g.fillRect(i,j,25,25);
	g.setColor(Color.black);
	g.drawRect(i-1,j-1,25,25);
      }
  }
}
