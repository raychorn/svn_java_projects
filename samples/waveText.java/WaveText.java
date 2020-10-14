/*  Glenn Richard, Center for High Pressure
    Research, SUNY, Stony Brook.
    May, 1996
*/
import java.awt.*;

public class WaveText extends java.applet.Applet
    implements Runnable {
  String str = null;
  int direction = 1; // 1 is clockwise, -1 is counterclockwise
  int horizontalRadius = 10;
  int verticalRadius = 10;
  Thread runner = null;
  char theChars[];
  int phase = 0;
  Image offScreenImage;
  Graphics offScreenG;

public void init() {
  String paramStr = null;
  str = getParameter("text");
  paramStr = getParameter("direction");
  setBackground(Color.black);
  if (paramStr != null)
    direction = Integer.parseInt(paramStr);
  paramStr = getParameter("horizontalRadius");
  if (paramStr != null)
    horizontalRadius = Integer.parseInt(paramStr);
  paramStr = getParameter("verticalRadius");
  if (paramStr != null)
    verticalRadius = Integer.parseInt(paramStr);
  setFont(new Font("TimesRoman",Font.BOLD,36));
  if (str == null) {
	str = "Museum of Java Applets";
  }
  resize(30+25*str.length()+2*horizontalRadius,80+2*verticalRadius);

  theChars =  new char [str.length()];
  str.getChars(0,str.length(),theChars,0);
  offScreenImage = createImage(this.size().width,this.size().height);
  offScreenG = offScreenImage.getGraphics();
  offScreenG.setFont(new Font("TimesRoman",Font.BOLD,36));
 }

public void start() {
  if(runner == null) {
    runner = new Thread(this);
    runner.start();
  }
}

public void stop() {
  if (runner != null) {
    runner.stop();
    runner = null;
  }
}

public void run() {
  while (runner != null) {
	try {
	  Thread.sleep(120);
	}
	catch (InterruptedException e) { }
	repaint();
  }
}

  public void update(Graphics g) {
    int x, y;
    double angle;
    offScreenG.setColor(Color.black);
    offScreenG.fillRect(0,0,this.size().width,this.size().height);
    phase+=direction;
    phase%=8;
    for(int i=0;i<str.length();i++) {
  	  angle = ((phase-i*direction)%8)/4.0*Math.PI;
	  x = 20+25*i+(int) (Math.cos(angle)*horizontalRadius); // Horizontal motion
	  y = 60+  (int) (Math.sin(angle)*verticalRadius); // Vertical motion
      if (i==0 || theChars[i-1]==' ')  // Each word starts in blue
        offScreenG.setColor(Color.blue);
      else
        offScreenG.setColor(Color.red);  // Each word continues in red
	  offScreenG.drawChars(theChars,i,1,x,y);
	}
    paint(g);
  }

  public void paint(Graphics g) {
    g.drawImage(offScreenImage,0,0,this);
  }
}

