/*-
 * Copyright (c) 1995 by Georg Hessmann.
 * All Right Reserved.
 *
 * Sound.java	1.0  25 Aug 1995
 *		1.1  13 Sep 1995
 *
 */

import java.io.InputStream;
import java.lang.Runnable;
import java.lang.Thread;
import java.net.URL;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.*;
import java.applet.AudioClip;

import Dance;

/**
 * Sound implements the thread, which is playing the sound and
 * updating the takt (time) numbers. It also calls every time
 * step java.applet.Applet.repaint().
 *
 * @see Dance
 *
 * @version 1.1  13 Sep 1995
 * @author Georg He&szlig;mann
 */
class Sound implements Runnable, ImageObserver {

  Dance app;

  AudioClip  audio     = null;
  URL        audioURL;

  int Counter;
  static Image numbers[];

  Image lastNumImg = null;	// letzte gezeichnete Nummer

  /** Maximal size().width of the takt numbers. */
  public int numMaxW;
  /** Maximal size().height of the takt numbers. */
  public int numMaxH;

  Thread  pthread;
  boolean StopIt;	// wenn true, dann beendet sich der Sound
  // boolean StopItAfter;	// spiele den Sound noch einen Takt lang
  

  /**
   * Creates the sound object.
   * If Dance.useAudio() is set, it preloads the audio data defined
   * by the URL.
   * @param audioURL pointer to the audio data
   * @param app pointer to the main applet.
   */
  public Sound(URL audioURL, Dance app)
  {
    this.audioURL = audioURL;
    this.app      = app;

    Counter = -1;

    numMaxW = 32;
    numMaxH = 60;

    if (numbers == null) {
      numbers = new Image[4];
      for (int i=0; i<4; i++) {
	numbers[i] = app.getImage(app.getCodeBase(),
				  "images/nums-" + (i+1) + ".gif");
	if (numbers[i].getWidth(this) >numMaxW) numMaxW = numbers[i].getWidth(this);
	if (numbers[i].getHeight(this)>numMaxH) numMaxH = numbers[i].getHeight(this);
      }
    }

    if (app.useAudio() && audioURL != null) {
      // preload audio data into browser cache
      audio = app.getAudioClip(audioURL);
    }
    audio = null;
  }

  /**
   * Start the sound thread.
   */
  public synchronized void StartSound()
  {
    if (pthread != null) {
      StopSound();
    }
    Counter = 0;
    StopIt = false;
    
    pthread = new Thread(this, "Sound");
    pthread.start();
    pthread.setPriority(Thread.MAX_PRIORITY-1);

    if (audio != null) audio.stop();
    audio = null;
    
    if (app.useAudio() && audioURL != null) {
      audio = app.getAudioClip(audioURL);
    }
    
    if (audio != null) audio.play();
  }

  /**
   * Delete the sound thread.
   */
  public synchronized void KillSound()
  {
    if (pthread != null) {
      if (pthread.isAlive()) pthread.stop();
      pthread = null;
      Counter = -1;	// hide counter box
    }
    if (audio != null) {
      audio.stop();
      audio = null;
    }
  }

  /**
   * Stop the Sound thread.
   */
  public void StopSound()
  {
    StopIt = true;
  }

  /**
   * Test if the sound thread is still alive.
   */
  public boolean SoundAlive()
  {
    return (pthread != null);
  }

  /**
   * Main function of the sound thread.
   * Sleeps one timestep, update the takt counter and does a repaint.
   * Runs until StopSound() or KillSound() is called.
   */
  public void run()
  {
    try {
      Thread.sleep(100);	// 1/10'tel hinter den anderen Threads laufen
    } catch (InterruptedException e) {
      return;
    }
    
    long wtime;					// wait time in millis
    long stime = System.currentTimeMillis();	// start time in millis
    long ctime;					// current time in millis

    wtime = 1000 * 60 / app.figur.BPM;

    while (!StopIt) {
      Counter++;
      app.repaint();
      ctime = System.currentTimeMillis();
      try {
	Thread.sleep(wtime - (ctime-stime));
      } catch (InterruptedException e) {
	break;
      }
      stime = System.currentTimeMillis();
    }
    Counter = -1;

    if (audio != null) {
      audio.stop();
      audio = null;
    }

    if (app.figur != null) {
      app.figur.HideComStr();
      app.repaint();
    }
    
    pthread = null;
  }

  /**
   * Set the takt/time counter to c.
   * Needed for the single step mode.
   */
  public void SetCounter(int c)
  {
    Counter = c;
  }

  /**
   * Get the number image suitable to the Counter value
   * and the number of beats for one takt of the current dance.
   */
  public Image GetNumImg()
  {
    if (Counter > 0) {
      return numbers[(Counter-1) % app.figur.Takte];
    }
    else return null;
  }


  /**
   * Set last painted number.
   * @see Figur#drawNum
   */
  public void setLastNumImg(Image img)
  {
    lastNumImg = img;
  }

  /**
   * Returns the last drawn image.
   * @see Figur#drawNum
   */
  public Image getLastNumImg()
  {
    return lastNumImg;
  }

  /**
   * Manages infos about the number-images.
   * @see ImageObserver#imageUpdate
   */
  public boolean imageUpdate(Image img,
			     int infoflags, int x, int y,
			     int width, int height)
  {
    // hier geht es um die Nummern
    boolean ret     = true;
    boolean dopaint = false;

    long updatetime = 0;	// sofort

    if ((infoflags & WIDTH) != 0) {
      if (width  > numMaxW) numMaxW = width;
    }
    if ((infoflags & HEIGHT) != 0) {
      if (height > numMaxH) numMaxH = height;
    }
    
    if ((infoflags & (FRAMEBITS | ALLBITS)) != 0) {
      dopaint = true;
      ret     = false;
    }
    else if ((infoflags & SOMEBITS) != 0) {
      dopaint    = true;
      updatetime = 100;
    }
    
    if ((infoflags & ERROR) != 0) {
      ret = false;
    }

    if (dopaint) {
      if (lastNumImg == img) {
	lastNumImg = null;	// force repaint
      }
      app.repaint(updatetime);
    }

    return ret;
  }

}



