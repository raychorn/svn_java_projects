/*  Glenn Richard, Center for High Pressure
    Research, SUNY, Stony Brook.
    May, 1996
*/
import java.awt.*;

public class RainbowText extends java.applet.Applet
    implements Runnable {
  String str = null;
  int strlen;
  Thread runner = null;
  char theChars[];
  int charOffsets[];
  Color colors[];
  int phase = 0;
  Image offScreenImage;
  Graphics offScreenG;
  Font f;
  FontMetrics fm;

public void init() {
  String paramStr = null;
  float h;
  int xPos=20;
  str = getParameter("text");
  if (str == null) {
	str = "Museum of Java Applets";
  }
  f=new Font("TimesRoman",Font.BOLD,36);
  fm=getFontMetrics(f);
  resize(40+fm.stringWidth(str),40);

  setBackground(Color.black);
  strlen = str.length();

  theChars =  new char [strlen];
  charOffsets = new int [strlen];
  str.getChars(0,strlen,theChars,0);
  colors = new Color[strlen];
  for (int i = 0; i < strlen; i++) {
    h = ((float)i)/((float)strlen);
    colors[i] = new Color(Color.HSBtoRGB(h,1.0f,1.0f));
    charOffsets[i] = xPos;
    xPos+=fm.charWidth(theChars[i]);
  }
  offScreenImage = createImage(this.size().width,this.size().height);
  offScreenG = offScreenImage.getGraphics();
  offScreenG.setFont(f);

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
	  Thread.sleep(100);
	}
	 catch (InterruptedException e) { }
	 repaint();
    }
  }


  public void update(Graphics g) {
    int x, y;
    offScreenG.setColor(Color.black);
    offScreenG.fillRect(0,0,this.size().width,this.size().height);
    phase++;
    phase%=str.length();
    for(int i=0;i<strlen;i++)
     {
       x = charOffsets[i];
       offScreenG.setColor(colors[(phase+i)%strlen]);
       offScreenG.drawChars(theChars,i,1,x,30);
     }
    paint(g);

  }

  public void paint(Graphics g) {
     g.drawImage(offScreenImage,0,0,this);

  }


}

