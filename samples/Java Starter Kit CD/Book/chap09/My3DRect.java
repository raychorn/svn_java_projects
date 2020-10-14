
import java.awt.Graphics;

public class My3DRect extends java.applet.Applet {

  public void paint(Graphics g) {
    g.draw3DRect(20,20,60,60,true);
    g.draw3DRect(120,20,60,60,false);
  }

}


