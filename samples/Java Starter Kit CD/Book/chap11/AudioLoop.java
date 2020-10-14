/* get and drawn an image */

import java.awt.Graphics;
import java.applet.AudioClip;

public class AudioLoop extends java.applet.Applet implements Runnable {

  AudioClip bgsound;
  AudioClip beep;
  Thread runner;

  public void start() {
    if (runner == null) {
      runner = new Thread(this);
      runner.start();
    }
  }

  public void stop() {
    if (runner != null) {
      if (bgsound != null)
	bgsound.stop();
      runner.stop();
      runner = null;
    }
  }
  
  public void init() {
    bgsound = getAudioClip(getCodeBase(), "audio/loop.au");
    beep = getAudioClip(getCodeBase(), "audio/beep.au");
  }
  
  public void run() {
    if (bgsound != null) bgsound.loop();
    while (runner != null) {
      try { Thread.sleep(5000); }
      catch (InterruptedException e) { }
      if (bgsound != null) beep.play();
    }
  }

  public void paint(Graphics g) {
    g.drawString("Playing Sounds....", 10, 10);
  }
}






