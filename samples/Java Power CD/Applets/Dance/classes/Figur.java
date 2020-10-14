/*-
 * Copyright (c) 1995 by Georg Hessmann.
 * All Right Reserved.
 *
 * Figur.java	1.0   25 Aug 1995
 *		1.1   13 Sep 1995
 *
 */

import java.lang.Math;
import java.io.InputStream;

import java.lang.Runnable;
import java.lang.Thread;
import java.net.URL;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.awt.Font;

import Dance;
import Step;
import Sound;


/**
 * Figur is the main class for storing/displaying the dance figures.
 * It is (should be) an abstract class. Don't create an instance of
 * it. Create only objects of the classes SlowWaltz, Tango, SlowFox
 * and Quickstep.
 *
 * @see SlowWaltz
 * @see Tango
 * @see SlowFox
 * @see Quickstep
 *
 * @version 1.1, 13 Sep 1995
 * @author Georg He&szlig;mann
 */
class Figur implements Runnable {

  Dance  app;

  /* wurde diese Figur schon jemals angezeigt? */
  boolean everPaint = false;

  /* Parket fuer den Herrn und die Dame */
  Floor  hfloor;
  Floor  dfloor;

  /* erstmal eine Beschreibung des Tanzes */

  /**
   * Is this dance a 3/4 dance (e.g. slow waltz) (=> Takte = 3)
   * or a 4/4 dance (e.g. quickstep) (=> Takte = 4)
   */
  
  public int  Takte;		// 3/4 = 3, 4/4 = 4
  int	      Tempo;		// Takte pro Minute

  /**
   * beats per minute == Takte * Tempo
   */
  public int  BPM;		// Schlaege pro Minute (== Takte * Tempo)
  
  int         AudioTempo;	// speed of the audio file
  int         SlowMotionTempo;	// speed in slow motion mode

  InputStream audio;

  /* nun eine Beschreibung der Figur */
  int    CurrentFig;		// Pointer in der FigNames Liste

  int    num_hsteps;
  Step   hsteps[];
  int    hcount;
  float  htime;
  
  int    num_dsteps;		// how much steps are maximal available?
  Step   dsteps[];
  int    dcount;		// how much steps are done?
  float  dtime;			// how much time is spend?

  Thread hthread = null;	// play-dance threads
  Thread dthread = null;

  Sound  sound;
  int    NumXPos;		// Koo. of the number-window
  int    NumYPos;
  boolean isNumBorder = false;	// ist der Nummern-Hintergrund gezeichnet?

  int addRightSpace = 0;	// zusaetzlicher Platz am rechten Rand

  String NameFigur;		// e.g. natural turn
  String NameDance;		// e.g. english waltz
  String DameStr;		// Dame/Lady
  String HerrStr;		// Herr/Gent

  boolean inSingleStep = false;
  float   SingleStepTime;			// bis dahin wurde gesteppt
  float   SingleStepTimeD, SingleStepTimeH;	// bis dahin hat Dame/Herr gesteppt


  /** comment time strings for all steps */
  final static String TS_n  = null;
  final static String TS_1  = "1";
  final static String TS_2  = "1-2";
  final static String TS_2a = "1-2-&";
  final static String TS_a3 = "1-2-&-3";
  final static String TS_3  = "1-2-3";
  final static String TS_4  = "1-2-3-4";
  final static String TS_s  = "slow";
  final static String TS_q  = "quick";
  final static String TS_qa = "quick-&";
  final static String TS_a  = "&";
  final static String TS_aq = "&-quick";

  /** comment heel/toe strings for all steps */
  final static String HT_str[][] = {
    { "heal", "toe", "heal, toe", "toe, heal",
      "inside edge toe, heal", "inside edge feet", "whole feet",
      "inside edge toe", "toe, heal, toe",
      "heal, inside edge feet, whole feet" },
    { "Ferse", "Ballen", "Ferse, Ballen", "Ballen, Ferse",
      "Innenk. Ballen, Ferse", "Innenk. Fuﬂ", "ganzer Fuﬂ",
      "Innenk. Ballen", "Ballen, Ferse, Ballen",
      "Ferse, Innenk. Fuﬂ, ganzer Fuﬂ" }
  };
  
  final static int HT_n   = -1;
  final static int HT_h   = 0;
  final static int HT_t   = 1;
  final static int HT_ht  = 2;
  final static int HT_th  = 3;
  final static int HT_ith = 4;
  final static int HT_if  = 5;
  final static int HT_wf  = 6;
  final static int HT_it  = 7;
  final static int HT_tht = 8;
  final static int HT_hiw = 9;
  

  /**
   * Constructor initialize some always needed variables.
   * @param app pointer to the main dance applet (e.g. needed
   *            for Dance.engl
   */
  protected Figur(Dance app)
  {
    this.app  = app;
    everPaint = false;

    DameStr = (app.engl) ? "Lady" : "Dame";
    HerrStr = (app.engl) ? "Gent" : "Herr";

    /* x,y Koo. of the takt window */
    NumXPos = 30;
    NumYPos = 50;
  }

  /**
   * Calculate waiting time.
   * Gets a float time number relative to the dance tact and
   * returns the waiting time in milli secs.
   * @param time time relative to the dance tact
   * @return waiting time in milli secs.
   */
  public long CalcWTime(float time)
  {
    return (long)(time * 1000.0f * 60.0f / (float)BPM + 0.5f);
  }

  
  /**
   * Next non zero (german: Null) time.
   * Looks from the current step forward for the next step with
   * waiting time greater zero.
   * @return next non zero waiting time. If no such time exists,
   *  return one.
   */
  public float NextNNTime()
  {
    int i;
    float t = 1;

    i = hcount;
    while (i<num_hsteps && hsteps[i].time_step == 0) i++;
    if (i<num_hsteps) t = hsteps[i].time_step;

    i = dcount;
    while (i<num_dsteps && dsteps[i].time_step == 0) i++;
    if (i<num_dsteps && dsteps[i].time_step < t) t = dsteps[i].time_step;

    return t;
  }
  

  /**
   * Previsious non zero (german: Null) waiting time.
   * Looks from the current step backward to find the next (smalles)
   * waiting time greater zero.
   * @return previsious waiting time greater zero. Returns zero, if no such
   *  time exists.
   */
  public float PrevNNTime()
  {
    int i;
    float td = 0;
    float th = 0;

    i = hcount-1;
    while (i>0 && hsteps[i].time_step == 0) i--;
    if (i>0) th = hsteps[i].time_step;

    i = dcount-1;
    while (i>0 && dsteps[i].time_step == 0) i--;
    if (i>0) td = dsteps[i].time_step;

    return Math.min(th, td);
  }

  /**
   * Does one step on the male (Gent) floor.
   * @return the waiting time until the next male step is to do
   *  or -1 if there was no step to do. (The last step has a waiting
   *  time > 0 too, so -1 comes, if there was already no step to do.)
   */
  public float DoHStep()
  {
    if (hcount >= num_hsteps) return -1;
    else {
      hfloor.doStep(hsteps[hcount]);
      htime += hsteps[hcount].time_step;
      return hsteps[hcount++].time_step;
    }
  }


  /**
   * Does one step on the female (Lady) floor.
   * @return the waiting time until the next female step is to do
   *  or -1 if there was no step to do. (The last step has a waiting
   *  time > 0 too, so -1 comes, if there was already no step to do.)
   */
  public float DoDStep()
  {
    if (dcount >= num_dsteps) return -1;
    else {
      dfloor.doStep(dsteps[dcount]);
      dtime += dsteps[dcount].time_step;
      return dsteps[dcount++].time_step;
    }
  }


  /**
   * Does on step forward (in single step mode)
   */
  public void stepForw()
  {
    ShowComStr();
    
    if (ThreadsAlive()) {
      // laeuft noch ein play
      StopDance();
    }

    if (!inSingleStep) {
      inSingleStep = true;
      SingleStepTime = SingleStepTimeD = SingleStepTimeH = 0;
    }

    float next = NextNNTime();
    SingleStepTime += (next > 1) ? 1 : next;

    float td = 0, th = 0;

    while (SingleStepTimeH < SingleStepTime && th >= 0) {
      th += DoHStep();
      SingleStepTimeH += th;
    }

    while (SingleStepTimeD < SingleStepTime && td >= 0) {
      td += DoDStep();
      SingleStepTimeD += td;
    }

    SetCounter((int)Math.ceil(SingleStepTime));

    if (th < 0 && td < 0) {
      inSingleStep = false;
      SetCounter(-1);
      HideComStr();
    }
  }
  
  
  /**
   * Take one step backward (in single step mode).
   * If not in single step mode, do all steps and go into single step mode.
   */
  public void stepBackw()
  {
    ShowComStr();
    
    if (ThreadsAlive() || !inSingleStep || (inSingleStep && SingleStepTime == 0)) {
      /* laeuft noch ein play, oder noch kein single step */

      StopDance();	// alle Schritte anzeigen

      inSingleStep = true;
      SingleStepTime = SingleStepTimeD = SingleStepTimeH = timeSpend();
      SetCounter((int)Math.floor(timeSpend()));
    }
    else {

      float pt = PrevNNTime();

      if (pt > 1) pt = 1;	// go only one time step at a time

      /* add current waiting to and accumulated waiting time */
      float ptd = SingleStepTimeD - SingleStepTime + pt;
      float pth = SingleStepTimeH - SingleStepTime + pt;

      int   h      = hcount-1;
      float htdiff = 0;

      while (h >= 0 && htdiff + hsteps[h].time_step <= pth) {
	htdiff += hsteps[h].time_step;
	hfloor.backStep();
	h--;
      }

      int   d      = dcount-1;
      float dtdiff = 0;

      while (d >= 0 && dtdiff + dsteps[d].time_step <= ptd) {
	dtdiff += dsteps[d].time_step;
	dfloor.backStep();
	d--;
      }

      /* h, d werden nun das neue hcount, dcount */
      /* es werden also hcount-h bzw. dcount-d Schritte zurueck genommen */

      hcount = h+1;
      dcount = d+1;

      htime -= htdiff;
      dtime -= dtdiff;

      SingleStepTime -= pt;
      SingleStepTimeD = dtime;
      SingleStepTimeH = htime;

      SetCounter((int)SingleStepTime);
    }
  }

  /** Are we in single step mode? */
  public boolean inSingleStep()	{ return inSingleStep; }

  /** Leave the single step mode. Needed initializations does the Dance class */
  public void leaveSingleStep() { inSingleStep = false; }
  

  /**
   * Clear all done steps. After the next repaint(), no feeds are on the floors.
   */
  public void clearSteps()
  {
    hfloor.clearSteps();
    dfloor.clearSteps();
    
    /* wieviele Schritte wurden gemacht */
    hcount = 0;
    dcount = 0;
    
    /* wieviel Zeit ist verstrichen */
    htime  = 0;
    dtime  = 0;
  }

  /**
   * How much time is currently spend (usefull in single step mode).
   */
  private float timeSpend()	{ return Math.max(htime, dtime);  }

  /**
   * Show the takt/time string (slow/quick or heel/toe) on both floors.
   * @see Floor#ShowComStr
   */
  public void ShowComStr()
  {
    hfloor.ShowComStr();
    dfloor.ShowComStr();
  }

  /**
   * Hide the takt/time string (slow/quick or heel/toe) on both floors.
   * @see Floor#HideComStr
   */
  public void HideComStr()
  {
    hfloor.HideComStr();
    dfloor.HideComStr();
  }


  /**
   * Use the heel/toe string for the comment field.
   * @see Floor#useComStrHT
   */
  public void useComStrHT()
  {
    hfloor.useComStrHT();
    dfloor.useComStrHT();
  }

  /**
   * Use the time (slow/quick) string for the comment field.
   * @see Floor#useComStrTime
   */
  public void useComStrTime()
  {
    hfloor.useComStrTime();
    dfloor.useComStrTime();
  }
  

  /**
   * Main function for the animation feature.
   * This function will be started twice. As well for the male
   * thread (thread name "Herr") an for the female thread (thread name "Dame").
   * It does step by step and sleep() between the steps. repaint() is
   * only needed, if the waiting time is an fraction of the takt because
   * the sound thread already does a repaint every takt beat.
   * If all steps are set, the threads does kill themself and, if it was the
   * last thread, the sound thread will also be killed.
   */
  public void run()
  {
    float wfl;				// wait time in beats
    long wtime;				// wait time in millis
    long stime = System.currentTimeMillis();	// start time in millis
    long ctime;				// current time in millis


    if (Thread.currentThread().getName().compareTo("Herr") == 0) {

      do {
	int step = 1;
	int done = 0;
	
	wfl = DoHStep();
	wtime = CalcWTime(wfl);
	step++;
	
	if (wtime > 0) {
	  done = step;
	  
	  // classs Sound already does a repaint() all x.0f timesteps
	  if (wfl != Math.floor(wfl)) app.repaint();
	  
	  ctime = System.currentTimeMillis();
	  try {
	    Thread.sleep(wtime - (ctime-stime));
	  }
	  catch (InterruptedException e) {
	    wtime = -1;
	  }
	  stime = System.currentTimeMillis();
	}
      } while(wtime >= 0);
      app.repaint();		// wg. dem letzten Fuss
      
      // ziehe mir den Boden unter den Fuessen weg -- aber ich bin nun fertig
      hthread = null;
    }
    else {
      do {
	int step = 1;
	int done = 0;

	wfl = DoDStep();
	wtime = CalcWTime(wfl);
	step++;

	if (wtime > 0) {
	  done = step;

	  // classs Sound already does a repaint() all x.0f timesteps
	  if (wfl != Math.floor(wfl)) app.repaint();

	  ctime = System.currentTimeMillis();
  	  try {
	    Thread.sleep(wtime - (ctime-stime));
	  }
	  catch (InterruptedException e) {
	    wtime = -1;
	  }
	  stime = System.currentTimeMillis();
	}
      } while(wtime >= 0);
      app.repaint();		// wg. dem letzten Fuss

      // ziehe mir den Boden unter den Fuessen weg -- aber ich bin nun fertig
      dthread = null;
    }

    if (!ThreadsAlive()) sound.StopSound(); // AfterNextTakt();
  }


  /**
   * Start the animation.
   * Set the tempo/BPM, starts the male/female threads and
   * than start the sound thread.
   */
  public synchronized void Dance()
  {
    ShowComStr();

    if (app.useAudio()) {
      Tempo = AudioTempo;
    }
    else {
      Tempo = SlowMotionTempo;
    }
    BPM = Tempo * Takte;

    dthread = new Thread(this, "Dame");
    dthread.setPriority(Thread.MAX_PRIORITY-2);
    hthread = new Thread(this, "Herr");
    hthread.setPriority(Thread.MAX_PRIORITY-2);
    
    dthread.start();
    hthread.start();

    sound.StartSound();
  }

  /**
   * Stop the animation. E.g. if the "play" button is hit while
   * an animation still runs, the animation will be aborted.
   * Than all missing steps are done quickly.
   */
  public synchronized void StopDance()
  {
    HideComStr();
    
    if (dthread != null && dthread.isAlive()) dthread.stop();
    if (hthread != null && hthread.isAlive()) hthread.stop();
    
    dthread = null;
    hthread = null;

    /* alle Schritte noch schnell zu Ende anzeigen */
    while (DoDStep() >= 0 || DoHStep() >= 0) ;

    sound.StopSound();
  }


  /**
   * Is at least one of the dancing threads (male/female) alive?
   */
  public boolean ThreadsAlive()
  {
    return (hthread != null || dthread != null);
  }

  /**
   * Set the time counter value. Needed in single step mode.
   * Counter value -1 means, hide the counter window.
   */
  public void SetCounter(int c)
  {
    if (sound != null) sound.SetCounter(c);
  }
    

  /**
   * Draw the dance and figur name on the top of the applet.
   */
  public void drawTitel(Graphics g)
  {
    Font oldf = g.getFont();
    if (app.choiceFont != null) {
      g.setFont(app.choiceFont);
    }

    g.drawString(NameDance + " / " + NameFigur, 10, 30);
    g.setFont(oldf);
  }

  /**
   * Draw the time/takt number. The value of this counter is
   * maintained from the sound class.
   * @param g actual graphics context
   * @param update only an update or an whole repaint
   * @see Sound
   * @see Sound#GetNumImg
   */
  public void drawNum(Graphics g, boolean update)
  {
    Image curImg = sound.GetNumImg();
    
    if (!update) isNumBorder = false;	// bei paint() auch Hintergrund zeichnen!
    
    if (!update || curImg != sound.getLastNumImg()) {
      if (curImg != null) {
	if (!isNumBorder) {
	  isNumBorder = true;
	  g.setColor(Color.white);
	  g.fillRect(NumXPos, NumYPos+1, sound.numMaxW+20, sound.numMaxH+10);
	  g.setColor(Color.black);
	  g.drawRect(NumXPos-1, NumYPos,sound.numMaxW+21, sound.numMaxH+11);
	}
	g.drawImage(curImg, NumXPos+1+13, NumYPos+11, sound);
      }
      else {
	isNumBorder = false;
	g.clearRect(NumXPos-1, NumYPos, sound.numMaxW+20+2, sound.numMaxH+10+2);
      }
      sound.setLastNumImg(curImg);
    }
  }

  /** Return the left edge of the male floor */
  public int LeftEdge()   { return hfloor.LeftEdge(); }
  /** Return the right edge of the female floor (plus some additional space */
  public int RightEdge()  { return dfloor.RightEdge() + addRightSpace; }
  /** Return the bottom edge of the floors. */
  public int BottomEdge() { return dfloor.BottomEdge(); }


  /**
   * Returns a String object representing.
   */
  public String toString()
  {
    return getClass().getName() +
      "[NameDance=" + NameDance + ", NameFigur=" + NameFigur +
      ", Takte=" + Takte +
      ", Tempo=" + Tempo + ", BPM=" + BPM +
      ", singel-step=" + inSingleStep + ", SSTime=" + SingleStepTime +
      ", H-Tread=" + hthread + ", D-Thread=" + dthread + "]";
  }
}


/*
 ********************************************************
 * 	SLOW WALZ					*
 ********************************************************
 */

/**
 * Slow Waltz: Stores the information of the slow waltz figures.<P>
 *
 * To initialize a spezial figur:
 * <pre>
 *   Figur figur = new SlowWaltz(app, figNum, tempo, audioURL);
 * </pre>
 * Whereat app the applet pointer is, figNum the number of the
 * slow waltz figur you want, tempo the speed of the sound defined
 * by the audioURL.<p>
 *
 * To add a new figur:
 * <ol>
 *  <li>Add the name of the figur into FigNames_{eng,ger}.
 *  <li>Add an initialization function (use initNatTurn as template)
 *  <li>Add your initialization function into the switch statement of
 *     the constructor.
 * </ol>
 * If you don't have more than 4 figures, you don't have to change anything
 * in class Dance. If you have more figures, you only have to add more space
 * for the figur-buttons.
 *
 * @version 1.0f 25 Aug 1995
 * @author Georg He&szlig;mann
 */
class SlowWaltz extends Figur {

  /** English name of this dance. */
  public final static String Name_eng       = "English Waltz";
  /** German name of this dance. */
  public final static String Name_ger       = "Langsamer Walzer";
  /** Array of the english names of all implemented figures. */
  public final static String FigNames_eng[] = { "Natural Turn",  "Whisk" };
  /** Array of the german names of all implemented figures. */
  public final static String FigNames_ger[] = { "Rechtsdrehung", "Wischer" };


  /**
   * Initialze a dance and select a special figur out of this dance.
   * @param app pointer to the dance applet.
   * @param danceNum number of the choosen figur. The number is defined
   *        relative to the names in the FigNames_* array.
   * @param tempo of the given audio file
   * @param audioURL URL description of the given audio file.
   *        Might be null if "no music" is selected.
   */
  public SlowWaltz(Dance app, int danceNum, int temp, URL audioURL)
  {
    super(app);
    
    Takte  = 3;

    CurrentFig = danceNum;
    
    SlowMotionTempo = 20;
    AudioTempo      = temp;
    
    NameDance  = (app.engl) ? Name_eng : Name_ger;
    NameFigur  = (app.engl) ? FigNames_eng[CurrentFig] : FigNames_ger[CurrentFig];
    
    sound = new Sound(audioURL, app);

    int LeftEdge = 2*NumXPos + sound.numMaxW + 20;
    int TopEdge  = NumYPos;

    /* initialize dance (floor size, steps) */
    switch (danceNum) {
     case 0:
      initNaturalTurn(LeftEdge, TopEdge);
      break;
     case 1:
      initWhisk(LeftEdge, TopEdge);
      break;
    }
  }


  /**
   * Returns a String object representing.
   */
  public String toString()
  { 
    return getClass().getName() + "[Figur=" + super.toString() + "]";
  }


  private void initNaturalTurn(int LeftEdge, int TopEdge)
  {
    hfloor = new Floor(HerrStr, false, LeftEdge, TopEdge,
		       2, 4, 30, 30, 30, 30, app.foots, app);
    dfloor = new Floor(DameStr, true,  hfloor.RightEdge()+20, TopEdge,
		       2, 4, 30, 30, 30, 30, app.foots, app);

    // Tanzrichtung-Pfeile setzen
    hfloor.SetTRPos(1.4f,  3.2f);
    dfloor.SetTRPos(1.55f, 3.2f);

    hfloor.SetComStrPos(0.2f, -0.1f);
    dfloor.SetComStrPos(0.2f, -0.1f);


    // Herren-Schritte der Figur
    num_hsteps = 11;
    hsteps = new Step[num_hsteps];

    hsteps[0]  = new Step(0, true,  false, Foot.NO, -0.25f,  4.15f, TS_n, HT_n);
    hsteps[1]  = new Step(3, false, false, Foot.NO, -0.1f,   4.25f, TS_n, HT_n);  // Aufstellung
    hsteps[2]  = new Step(1, false, false, Foot.NO,  0.82f,  3.18f, TS_1, HT_ht); // r vorw
    hsteps[3]  = new Step(0, false, true,  Foot.SO,  0.83f,  2.98f, TS_2, HT_ht); // r drehen
    hsteps[4]  = new Step(1, true,  false, Foot.SO,  1.90f,  2.05f, TS_2, HT_t);  // l vorw
    hsteps[5]  = new Step(0, true,  true,  Foot.S,   2.0f,   2.0f,  TS_3, HT_t);  // l drehen
    hsteps[6]  = new Step(1, false, false, Foot.S,   1.8f,   2.0f,  TS_3, HT_th); // r vorw schl
    hsteps[7]  = new Step(1, true,  false, Foot.SSW, 2.1f,   0.9f,  TS_1, HT_th); // l rueck
    hsteps[8]  = new Step(0, true,  true,  Foot.SWW, 2.18f,  0.95f, TS_1, HT_th); // l drehen
    hsteps[9]  = new Step(1, false, false, Foot.NW,  2.2f,   0,    TS_2, HT_t);  // r rueck
    hsteps[10] = new Step(1, true,  false, Foot.NW,  2.1f,   0.12f, TS_3, HT_th); // l rueck schl

    // Damen-Schritte der Figur
    num_dsteps = 11;
    dsteps = new Step[num_dsteps];

    dsteps[0]  = new Step(0, true,  false, Foot.SW, 0.26f, 3.89f, TS_n, HT_n);
    dsteps[1]  = new Step(3, false, false, Foot.SW, 0.14f, 3.83f, TS_n, HT_n);   // Aufstellung
    dsteps[2]  = new Step(1, true,  false, Foot.SW, 1.25f, 2.92f, TS_1, HT_th);  // l rueck
    dsteps[3]  = new Step(0, true,  true,  Foot.NW, 1.25f, 3.1f,  TS_2, HT_th);  // l drehen
    dsteps[4]  = new Step(1, false, false, Foot.N,  2,    2.5f,  TS_2, HT_t);   // r rueck
    dsteps[5]  = new Step(1, true,  false, Foot.N,  1.85f, 2.5f,  TS_3, HT_th);  // l ran
    dsteps[6]  = new Step(1, false, false, Foot.N,  1.9f,  1.3f,  TS_1, HT_ht);  // r vorw
    dsteps[7]  = new Step(0, false, true,  Foot.O,  1.8f,  1.2f,  TS_2, HT_ht);  // r drehen
    dsteps[8]  = new Step(1, true,  false, Foot.O,  1.80f,-0.1f,  TS_2, HT_t);   // l vorw
    dsteps[9]  = new Step(0, true,  true,  Foot.SO, 1.80f,-0.18f, TS_3, HT_t);   // l drehen
    dsteps[10] = new Step(1, false, false, Foot.SO, 1.72f,-0.1f,  TS_3, HT_th);  // r ran
  }


  private void initWhisk(int LeftEdge, int TopEdge)
  {
    hfloor = new Floor(HerrStr, false, LeftEdge, TopEdge,
		       3, 4, 30, 30, 30, 30, app.foots, app);
    dfloor = new Floor(DameStr, true,  hfloor.RightEdge()+20, TopEdge,
		       3, 4, 30, 30, 30, 30, app.foots, app);

    // Tanzrichtung-Pfeile setzen
    hfloor.SetTRPos(0.2f, 0.2f);
    dfloor.SetTRPos(0.2f, 0.2f);

    hfloor.SetComStrPos(0.2f, -0.1f);
    dfloor.SetComStrPos(0.2f, -0.1f);


    // Herren-Schritte der Figur
    num_hsteps = 10;
    hsteps = new Step[num_hsteps];

    hsteps[0]  = new Step(0, true,  false, Foot.NO, -0.25f, 4.15f, TS_n,  HT_n);
    hsteps[1]  = new Step(3, false, false, Foot.NO, -0.1f,  4.25f, TS_n,  HT_n);	// Aufstellung
    hsteps[2]  = new Step(1, true,  false, Foot.NO,  0.82f, 3.18f, TS_1,  HT_ht);	// l vorw
    hsteps[3]  = new Step(1, false, false, Foot.NO,  1.7f,  3.74f, TS_2,  HT_t);	// r vorw/seitw
    hsteps[4]  = new Step(1, true,  false, Foot.NO,  1.69f, 4.1f,  TS_3,  HT_th);	// l hinter
    hsteps[5]  = new Step(1, false, false, Foot.NO,  1.8f,  3.0f,  TS_1,  HT_ht);  // r vorw
    hsteps[6]  = new Step(.5f,true,  false, Foot.NO,  1.7f,  1.95f, TS_2,  HT_t);  // l vorw
    hsteps[7]  = new Step(.5f,false, false, Foot.NO,  1.75f, 2.15f, TS_2a, HT_t);  // r ran (und)
    hsteps[8]  = new Step(1, true,  false, Foot.NO,  1.7f,  1.0f,  TS_a3, HT_th); // l vorw
    hsteps[9]  = new Step(1, false, false, Foot.NNO, 2.4f,  0.0f,  TS_1,  HT_ht);  // r vorw

    // Damen-Schritte der Figur
    num_dsteps = 12;
    dsteps = new Step[num_dsteps];

    dsteps[0]  = new Step(0, true,  false, Foot.SW, 0.26f, 3.89f, TS_n,  HT_n);
    dsteps[1]  = new Step(3, false, false, Foot.SW, 0.14f, 3.83f, TS_n,  HT_n);	// Aufstellung
    dsteps[2]  = new Step(1, false, false, Foot.SW, 1.25f, 2.92f, TS_1,  HT_th);	// r rueck
    dsteps[3]  = new Step(0, false, true,  Foot.W,  1.26f, 3.0f,  TS_2,  HT_th);	// r drehen
    dsteps[4]  = new Step(1, true,  false, Foot.NW, 2.1f,  3.7f,  TS_2,  HT_t);	// l rueck
    dsteps[5]  = new Step(1, false, false, Foot.NW, 2.15f, 4.0f,  TS_3,  HT_th);	// r hinter
    dsteps[6]  = new Step(1, true,  false, Foot.NW, 2.18f, 2.95f, TS_1,  HT_ht);	// l seitw
    dsteps[7]  = new Step(.5f,false, false, Foot.W,  2.22f, 1.97f, TS_2,  HT_t);	// r seitw
    dsteps[8]  = new Step(0, false, true,  Foot.SWW,2.2f,  1.95f, TS_2a, HT_t);	// r drehen
    dsteps[9]  = new Step(.5f,true,  false, Foot.SWW,2.23f, 2.1f,  TS_2a, HT_t);	// l ran
    dsteps[10] = new Step(1, false, false, Foot.SW, 2.3f,  0.85f, TS_a3, HT_th);	// r seitw
    dsteps[11] = new Step(1, true,  false, Foot.SSW,2.8f, -0.2f,  TS_1,  HT_th);	// l rueck
  }
}


/*
 ********************************************************
 * 	TANGO						*
 ********************************************************
 */

/**
 * Tango: Stores the information of the tango figures.<P>
 *
 * To initialize a spezial figur:
 * <pre>
 *   Figur figur = new Tango(app, figNum, tempo, audioURL);
 * </pre>
 * Whereat app the applet pointer is, figNum the number of the
 * tango figur you want, tempo the speed of the sound defined
 * by the audioURL.<p>
 *
 * To add a new figur:
 * <ol>
 *  <li>Add the name of the figur into FigNames_{eng,ger}.
 *  <li>Add an initialization function (use initLeftTurn as template)
 *  <li>Add your initialization function into the switch statement of
 *     the constructor.
 * </ol>
 * If you don't have more than 4 figures, you don't have to change anything
 * in class Dance. If you have more figures, you only have to add more space
 * for the figur-buttons.
 *
 * @version 1.0f 25 Aug 1995
 * @author Georg He&szlig;mann
 */
class Tango extends Figur {
  
  /** English name of this dance. */
  public final static String Name_eng       = "Tango";
  /** German name of this dance. */
  public final static String Name_ger       = "Tango";
  /** Array of the english names of all implemented figures. */
  public final static String FigNames_eng[] = { "Left Turn", "Rock Turn",
						"Outside Swivel, Brush Tap" };
  /** Array of the german names of all implemented figures. */
  public final static String FigNames_ger[] = { "Linksdrehung",
						"Wiegeschrittdrehung",
						"Linksgedrehte Kehre + Brush Tap" };
  
  /**
   * Initialze a dance and select a special figur out of this dance.
   * @param app pointer to the dance applet.
   * @param danceNum number of the choosen figur. The number is defined
   *        relative to the names in the FigNames_* array.
   * @param tempo of the given audio file
   * @param audioURL URL description of the given audio file.
   *        Might be null if "no music" is selected.
   */
  public Tango(Dance app, int danceNum, int temp, URL audioURL)
  {
    super(app);

    Takte  = 4;

    CurrentFig = danceNum;
    
    SlowMotionTempo = 18;
    AudioTempo      = temp;
    
    NameDance  = (app.engl) ? Name_eng : Name_ger;
    NameFigur  = (app.engl) ? FigNames_eng[CurrentFig] : FigNames_ger[CurrentFig];
    
    sound = new Sound(audioURL, app);

    int LeftEdge = 2*NumXPos + sound.numMaxW + 20;
    int TopEdge  = NumYPos;

    /* initialize dance (floor size, steps) */
    switch (danceNum) {
     case 0:
      initLeftTurn(LeftEdge, TopEdge);
      break;
     case 1:
      initRockTurn(LeftEdge, TopEdge);
      break;
     case 2:
      initOutsideSwivel(LeftEdge, TopEdge);
      break;
    }
  }



  /**
   * Returns a String object representing.
   */
  public String toString()
  { 
    return getClass().getName() + "[Figur=" + super.toString() + "]";
  }


  
  private void initLeftTurn(int LeftEdge, int TopEdge)
  {
    hfloor = new Floor(HerrStr, false, LeftEdge, TopEdge,
		       2, 4, 30, 30, 30, 30, app.foots, app);
    dfloor = new Floor(DameStr, true,  hfloor.RightEdge()+20, TopEdge,
		       2, 4, 30, 30, 30, 30, app.foots, app);

    // Tanzrichtung-Pfeile setzen
    hfloor.SetTRPos(1.5f, 0.2f);
    dfloor.SetTRPos(1.5f, 0.2f);

    hfloor.SetComStrPos(0.2f, -0.1f);
    dfloor.SetComStrPos(0.2f, -0.1f);


    // Herren-Schritte der Figur
    num_hsteps = 9;
    hsteps = new Step[num_hsteps];

    hsteps[0]  = new Step(0, true,  false, Foot.NW,  2.0f,   4.23f, TS_n, HT_n);
    hsteps[1]  = new Step(4, false, false, Foot.NW,  2.24f,  4.23f, TS_n, HT_n);   // Aufstellung
    hsteps[2]  = new Step(1, true,  false, Foot.NWW, 1.4f,   3.3f,  TS_q, HT_h);   // l vorw
    hsteps[3]  = new Step(1, false, false, Foot.S,   0.7f,   2.75f, TS_q, HT_th);  // r vorw
    hsteps[4]  = new Step(0, false, true,  Foot.SO,  0.66f,  2.76f, TS_s, HT_th);  // r drehen
    hsteps[5]  = new Step(2, true,  false, Foot.SO,  0.60f,  2.0f,  TS_s, HT_ith); // l rueck
    hsteps[6]  = new Step(1, false, false, Foot.SO,  0.0f,   1.25f, TS_q, HT_th);  // r rueck
    hsteps[7]  = new Step(1, true,  false, Foot.NOO, 0.05f,  0.46f, TS_q, HT_if);  // l seitw
    hsteps[8]  = new Step(2, false, false, Foot.NOO,-0.05f,  0.69f, TS_s, HT_wf);  // r ran

    // Damen-Schritte der Figur
    num_dsteps = 9;
    dsteps = new Step[num_dsteps];

    dsteps[0]  = new Step(0, true,  false, Foot.SO,  2.0f,  3.8f,  TS_n, HT_n);
    dsteps[1]  = new Step(4, false, false, Foot.SO,  1.84f, 3.8f,  TS_n, HT_n);	// Aufstellung
    dsteps[2]  = new Step(1, false, false, Foot.SOO, 0.9f,  3.2f,  TS_q, HT_th);	// r rueck
    dsteps[3]  = new Step(0, false, true,  Foot.NNO, 0.95f, 3.3f,  TS_q, HT_th);	// r drehen
    dsteps[4]  = new Step(1, true,  false, Foot.N,   0.75f, 3.25f, TS_q, HT_wf);	// l rueck
    dsteps[5]  = new Step(2, false, false, Foot.NNW, 0.9f,  2.35f, TS_s, HT_h);	// r vorw
    dsteps[6]  = new Step(1, true,  false, Foot.NW,  0.35f, 1.65f, TS_q, HT_h);	// l vorw
    dsteps[7]  = new Step(1, false, false, Foot.SWW, 0.4f,  0.55f, TS_q, HT_it);	// r seitw
    dsteps[8]  = new Step(2, true,  false, Foot.SWW, 0.3f,  0.73f, TS_s, HT_wf);	// l ran
  }
  
  private void initRockTurn(int LeftEdge, int TopEdge)
  {
    hfloor = new Floor(HerrStr, false, LeftEdge, TopEdge,
		       1, 3, 30, 30, 30, 30, app.foots, app);
    dfloor = new Floor(DameStr, true,  hfloor.RightEdge()+20, TopEdge,
		       1, 3, 30, 30, 30, 30, app.foots, app);

    // Tanzrichtung-Pfeile setzen
    hfloor.SetTRPos(0.58f, 0.2f);
    dfloor.SetTRPos(0.58f, 0.2f);

    hfloor.SetComStrPos(-0.25f, -0.18f);
    dfloor.SetComStrPos(-0.25f, -0.18f);


    // Herren-Schritte der Figur
    num_hsteps = 9;
    hsteps = new Step[num_hsteps];

    hsteps[0]  = new Step(0, true,  false, Foot.NO, -0.06f,  3.0f,  TS_n, HT_n);
    hsteps[1]  = new Step(4, false, false, Foot.NO, -0.04f,  3.2f,  TS_n, HT_n);   // Aufstellung
    hsteps[2]  = new Step(2, false, false, Foot.NO,  0.95f,  2.2f,  TS_s, HT_h);   // r vorw
    hsteps[3]  = new Step(1, true,  false, Foot.O,   0.72f,  1.36f, TS_q, HT_ith); // l vorw
    hsteps[4]  = new Step(1, false, false, Foot.SOO, 0.95f,  2.08f, TS_q, HT_ith); // r seit/drehen
    hsteps[5]  = new Step(2, true,  false, Foot.SOO, 0.6f,   1.06f, TS_s, HT_ith); // l seit
    hsteps[6]  = new Step(1, false, false, Foot.O,  -0.34f,  0.87f, TS_q, HT_th);  // r rueck
    hsteps[7]  = new Step(1, true,  false, Foot.NOO,-0.19f,  0.03f, TS_q, HT_if);  // l seit/rueck
    hsteps[8]  = new Step(2, false, false, Foot.NOO,-0.3f,   0.24f, TS_s, HT_wf);  // r ran

    // Damen-Schritte der Figur
    num_dsteps = 9;
    dsteps = new Step[num_dsteps];

    dsteps[0]  = new Step(0, true,  false, Foot.SW,  0.3f,  2.9f,  TS_n, HT_n);
    dsteps[1]  = new Step(4, false, false, Foot.SW,  0.34f, 2.7f,  TS_n, HT_n);	// Aufstellung
    dsteps[2]  = new Step(2, true,  false, Foot.SW,  1.3f,  1.8f,  TS_s, HT_th);	// l rueck
    dsteps[3]  = new Step(1, false, false, Foot.W,   1.0f,  1.6f,  TS_q, HT_h);	// r rueck
    dsteps[4]  = new Step(1, true,  false, Foot.NW,  1.35f, 2.3f,  TS_q, HT_ith);	// l seit/drehen
    dsteps[5]  = new Step(2, false, false, Foot.NWW, 0.9f,  1.27f, TS_s, HT_h);	// r seit
    dsteps[6]  = new Step(1, true,  false, Foot.W,   0.18f, 1.0f,  TS_q, HT_h);	// l vorw
    dsteps[7]  = new Step(1, false, false, Foot.SWW, 0.35f, 0.05f, TS_q, HT_ith);	// r seitw
    dsteps[8]  = new Step(2, true,  false, Foot.SWW, 0.23f, 0.23f, TS_s, HT_wf);	// l ran
  }
  
  private void initOutsideSwivel(int LeftEdge, int TopEdge)
  {
    hfloor = new Floor(HerrStr, false, LeftEdge, TopEdge,
		       2, 4, 30, 30, 30, 30, app.foots, app);
    dfloor = new Floor(DameStr, true,  hfloor.RightEdge()+20, TopEdge,
		       2, 4, 30, 30, 30, 30, app.foots, app);

    // Tanzrichtung-Pfeile setzen
    hfloor.SetTRPos(1.3f, 2.2f);
    dfloor.SetTRPos(1.3f, 2.2f);

    hfloor.SetComStrPos(0.2f, -0.1f);
    dfloor.SetComStrPos(0.2f, -0.1f);


    addRightSpace = 15;		// rechts noch etwas mehr Platz lassen

    // Herren-Schritte der Figur
    num_hsteps = 13;
    hsteps = new Step[num_hsteps];

    hsteps[0]  = new Step(0, true,  false, Foot.NW,  2.0f,   4.23f, TS_n,  HT_n);
    hsteps[1]  = new Step(4, false, false, Foot.NW,  2.24f,  4.23f, TS_n,  HT_n);   // Aufstellung
    hsteps[2]  = new Step(1, true,  false, Foot.NWW, 1.28f,  3.35f, TS_q,  HT_h);   // l vorw
    hsteps[3]  = new Step(1, false, false, Foot.SW,  0.74f,  2.75f, TS_q,  HT_th);  // r vorw
    hsteps[4]  = new Step(0, false, true,  Foot.SO,  0.64f,  2.76f, TS_s,  HT_th);  // r drehen
    hsteps[5]  = new Step(2, true,  false, Foot.O,  -0.15f,  2.15f, TS_s,  HT_th);  // l rueck
    hsteps[6]  = new Step(0, false, true,  Foot.O,   0.66f,  2.0f,  TS_q,  HT_th);  // r axe
    hsteps[7]  = new Step(1, true,  false, Foot.NOO, 0.44f,  1.4f,  TS_q,  HT_h);   // l vorw
    hsteps[8]  = new Step(1, false, false, Foot.NOO, 0.7f,   1.5f,  TS_q,  HT_it);  // r seit
    hsteps[9]  = new Step(1, true,  false, Foot.NNO, 1.4f,   1.0f,  TS_q,  HT_n);   // l vorw
    hsteps[10] = new Step(.5f,false, false, Foot.N,   2.47f,  1.05f, TS_q,  HT_n);   // r vorw/seit
    hsteps[11] = new Step(.5f,true,  false, Foot.N,   2.25f,  1.05f, TS_a,  HT_n);   // l tap
    hsteps[12] = new Step(1, true,  false, Foot.N,   1.80f,  1.05f, TS_aq, HT_n);   // l tap

    // Damen-Schritte der Figur
    num_dsteps = 15;
    dsteps = new Step[num_dsteps];

    dsteps[0]  = new Step(0, true,  false, Foot.SO,  2.0f,  3.8f,  TS_n,  HT_n);
    dsteps[1]  = new Step(4, false, false, Foot.SO,  1.84f, 3.8f,  TS_n,  HT_n);	 // Aufstellung
    dsteps[2]  = new Step(1, false, false, Foot.SOO, 0.8f,  3.15f, TS_q,  HT_th);	 // r rueck
    dsteps[3]  = new Step(0, false, true,  Foot.NNO, 0.85f, 3.3f,  TS_q,  HT_wf);	 // r drehen
    dsteps[4]  = new Step(1, true,  false, Foot.N,   0.3f,  3.0f,  TS_q,  HT_ht);	 // l rueck
    dsteps[5]  = new Step(0, true,  true,  Foot.N,   0.1f,  2.5f,  TS_s,  HT_ht);	 // l ran
    dsteps[6]  = new Step(2, false, false, Foot.NNW, 0.25f, 2.35f, TS_s,  HT_ht);	 // r vorw
    dsteps[7]  = new Step(0, false, true,  Foot.N,   0.25f, 2.36f, TS_q,  HT_ht);	 // r drehen
    dsteps[8]  = new Step(1, true,  false, Foot.N,   1.0f,  1.7f,  TS_q,  HT_ht);	 // l vorw
    dsteps[9]  = new Step(0, true,  true,  Foot.SW,  1.08f, 1.5f,  TS_q,  HT_it);	 // l vorw
    dsteps[10] = new Step(1, false, false, Foot.SW,  1.12f, 1.25f, TS_q,  HT_it);	 // r vorw/seit
    dsteps[11] = new Step(1, false, false, Foot.SSW, 1.7f,  0.65f, TS_q,  HT_n);	 // r rueck
    dsteps[12] = new Step(.5f,true,  false, Foot.S,   2.53f, 0.6f,  TS_q,  HT_n);	 // l rueck
    dsteps[13] = new Step(.5f,false, false, Foot.S,   2.35f, 0.6f,  TS_a,  HT_n);	 // r tap
    dsteps[14] = new Step(1, false, false, Foot.S,   1.97f, 0.6f,  TS_aq, HT_n);   // r tap
  }
}



/*
 ********************************************************
 * 	SLOW FOX					*
 ********************************************************
 */

/**
 * Slowfoxtrott: Stores the information of the slowfoxtrott figures.<P>
 *
 * To initialize a spezial figur:
 * <pre>
 *   Figur figur = new SlowFox(app, figNum, tempo, audioURL);
 * </pre>
 * Whereat app the applet pointer is, figNum the number of the
 * slowfoxtrott figur you want, tempo the speed of the sound defined
 * by the audioURL.<p>
 *
 * To add a new figur:
 * <ol>
 *  <li>Add the name of the figur into FigNames_{eng,ger}.
 *  <li>Add an initialization function (use initFeatherStep as template)
 *  <li>Add your initialization function into the switch statement of
 *     the constructor.
 * </ol>
 * If you don't have more than 4 figures, you don't have to change anything
 * in class Dance. If you have more figures, you only have to add more space
 * for the figur-buttons.
 *
 * @version 1.0f 25 Aug 1995
 * @author Georg He&szlig;mann
 */
class SlowFox extends Figur {

  /** English name of this dance. */
  public final static String Name_eng       = "Slowfoxtrot";
  /** German name of this dance. */
  public final static String Name_ger       = "Slow Fox";
  /** Array of the english names of all implemented figures. */
  public final static String FigNames_eng[] = { "Feather Step", "Left Turn" };
  /** Array of the german names of all implemented figures. */
  public final static String FigNames_ger[] = { "Federschritt", "Linksdrehung" };
  
  /**
   * Initialze a dance and select a special figur out of this dance.
   * @param app pointer to the dance applet.
   * @param danceNum number of the choosen figur. The number is defined
   *        relative to the names in the FigNames_* array.
   * @param tempo of the given audio file
   * @param audioURL URL description of the given audio file.
   *        Might be null if "no music" is selected.
   */
  public SlowFox(Dance app, int danceNum, int temp, URL audioURL)
  {
    super(app);

    Takte  = 4;

    CurrentFig = danceNum;
    
    SlowMotionTempo = 18;
    AudioTempo      = temp;
    
    NameDance  = (app.engl) ? Name_eng : Name_ger;
    NameFigur  = (app.engl) ? FigNames_eng[CurrentFig] : FigNames_ger[CurrentFig];
    
    sound = new Sound(audioURL, app);
    
    int LeftEdge = 2*NumXPos + sound.numMaxW + 20;
    int TopEdge  = NumYPos;

    /* initialize dance (floor size, steps) */
    switch (danceNum) {
     case 0:
      initFeatherStep(LeftEdge, TopEdge);
      break;
     case 1:
      initLeftTurn(LeftEdge, TopEdge);
      break;
    }
  }




  /**
   * Returns a String object representing.
   */
  public String toString()
  { 
    return getClass().getName() + "[Figur=" + super.toString() + "]";
  }


  private void initFeatherStep(int LeftEdge, int TopEdge)
  {
    hfloor = new Floor(HerrStr, false, LeftEdge, TopEdge,
		       1, 5, 30, 30, 30, 30, app.foots, app);
    dfloor = new Floor(DameStr, true,  hfloor.RightEdge()+20, TopEdge,
		       1, 5, 30, 30, 30, 30, app.foots, app);

    // Tanzrichtung-Pfeile setzen
    hfloor.SetTRPos(0.6f, 4.2f);
    dfloor.SetTRPos(0.6f, 4.2f);

    hfloor.SetComStrPos(0.1f, -0.1f);
    dfloor.SetComStrPos(0.1f, -0.1f);


    // Herren-Schritte der Figur
    num_hsteps = 8;
    hsteps = new Step[num_hsteps];

    hsteps[0]  = new Step(0, true,  false, Foot.N,  -0.10f,  5.25f, TS_n, HT_n);
    hsteps[1]  = new Step(4, false, false, Foot.N,   0.11f,  5.25f, TS_n, HT_n);  // Aufstellung
    hsteps[2]  = new Step(1, false, true,  Foot.N,      0,  3.86f, TS_s, HT_ht); // r vorw
    hsteps[3]  = new Step(1, false, false, Foot.N,      0,  3.86f, TS_s, HT_ht); // r vorw
    hsteps[4]  = new Step(1, true,  false, Foot.NNO, -0.1f,  2.85f, TS_q, HT_t);  // l vorw
    hsteps[5]  = new Step(1, false, false, Foot.NNO, 0.26f,  1.8f,  TS_q, HT_th); // r vorw
    hsteps[6]  = new Step(1, true,  true,  Foot.N,    0.8f,  0.6f,  TS_s, HT_h);  // l vorw
    hsteps[7]  = new Step(1, true,  false, Foot.N,    0.8f,  0.6f,  TS_s, HT_h);  // l vorw

    /*
    hsteps[0]  = new Step(0, true,  false, Foot.N,  -0.10f,  5.25f, TS_n);
    hsteps[1]  = new Step(4, false, false, Foot.N,   0.11f,  5.25f, TS_n); // Aufstellung
    hsteps[2]  = new Step(2, false, false, Foot.N,      0,  3.86f, TS_s); // r vorw
    hsteps[3]  = new Step(1, true,  false, Foot.NNO, -0.1f,  2.85f, TS_q); // l vorw
    hsteps[4]  = new Step(1, false, false, Foot.NNO, 0.26f,  1.8f,  TS_q); // r vorw
    hsteps[5]  = new Step(2, true,  false, Foot.N,    0.8f,  0.6f), TS_s;  // l vorw
    */

    // Damen-Schritte der Figur
    num_dsteps = 8;
    dsteps = new Step[num_dsteps];

    dsteps[0]  = new Step(0, true,  false, Foot.S,   0.08f, 4.78f, TS_n, HT_n);
    dsteps[1]  = new Step(4, false, false, Foot.S,  -0.08f, 4.78f, TS_n, HT_n);	// Aufstellung
    dsteps[2]  = new Step(1, true,  true,  Foot.S,   0.08f, 3.38f, TS_s, HT_th);	// l rueck
    dsteps[3]  = new Step(1, true,  false, Foot.S,   0.08f, 3.38f, TS_s, HT_th);	// l rueck
    dsteps[4]  = new Step(1, false, false, Foot.SSW, 0.25f, 2.4f,  TS_q, HT_th);	// r rueck
    dsteps[5]  = new Step(1, true,  false, Foot.SSW, 0.6f,  1.4f,  TS_q, HT_th);	// l rueck
    dsteps[6]  = new Step(1, false, true,  Foot.S,   0.9f,  0.1f,  TS_s, HT_t);	// r rueck
    dsteps[7]  = new Step(1, false, false, Foot.S,   0.9f,  0.1f,  TS_s, HT_t);	// r rueck
  }


  private void initLeftTurn(int LeftEdge, int TopEdge)
  {
    hfloor = new Floor(HerrStr, false, LeftEdge, TopEdge,
		       2, 6, 30, 30, 30, 30, app.foots, app);
    dfloor = new Floor(DameStr, true,  hfloor.RightEdge()+20, TopEdge,
		       2, 6, 30, 30, 30, 30, app.foots, app);

    addRightSpace = 55;		// rechts noch etwas mehr Platz lassen

    // Tanzrichtung-Pfeile setzen
    hfloor.SetTRPos(0.2f, 5.2f);
    dfloor.SetTRPos(0.2f, 5.2f);

    hfloor.SetComStrPos(0.2f, -0.1f);
    dfloor.SetComStrPos(0.2f, -0.1f);


    // Herren-Schritte der Figur
    num_hsteps = 15;
    hsteps = new Step[num_hsteps];

    hsteps[0]  = new Step(0, true,  false, Foot.NW, 2.17f, 6.25f, TS_n, HT_n);
    hsteps[1]  = new Step(4, false, false, Foot.NW, 2.3f,  6.13f, TS_n, HT_n);	// Aufstellung
    hsteps[2]  = new Step(1, true,  true,  Foot.NW, 1.2f,  5.2f,  TS_s, HT_ht);	// l vorw
    hsteps[3]  = new Step(1, true,  false, Foot.NW, 1.2f,  5.2f,  TS_s, HT_ht);	// l vorw
    hsteps[4]  = new Step(0, true,  true,  Foot.SW, 1.2f,  5.0f,  TS_q, HT_t);	// l drehen
    hsteps[5]  = new Step(1, false, false, Foot.SWW,0.66f, 4.21f, TS_q, HT_t);	// r vorw
    hsteps[6]  = new Step(0, false, true,  Foot.S,  0.55f, 4.15f, TS_q, HT_th);	// r drehen
    hsteps[7]  = new Step(1, true,  false, Foot.S,  0.7f,  3.15f, TS_q, HT_th);	// l rueck
    hsteps[8]  = new Step(1, false, true,  Foot.S,  0.55f, 2.07f, TS_s, HT_tht);	// r rueck
    hsteps[9]  = new Step(1, false, false, Foot.S,  0.55f, 2.07f, TS_s, HT_tht);	// r rueck
    hsteps[10] = new Step(0, false, true,  Foot.O,  0.47f, 2.15f, TS_s, HT_t);	// r drehen
    hsteps[11] = new Step(1, true,  false, Foot.NOO,0.66f, 1.17f, TS_q, HT_t);	// l vorw
    hsteps[12] = new Step(1, false, false, Foot.NOO,1.45f, 0.5f,  TS_q, HT_th);	// r vorw
    hsteps[13] = new Step(1, true,  true,  Foot.NOO,2.6f,  0.0f,  TS_s, HT_h);	// l vorw
    hsteps[14] = new Step(1, true,  false, Foot.NOO,2.6f,  0.0f,  TS_s, HT_h);	// l vorw
    


    // Damen-Schritte der Figur
    num_dsteps = 15;
    dsteps = new Step[num_dsteps];

    dsteps[0]  = new Step(0, true,  false, Foot.SO, 1.9f,  5.79f, TS_n, HT_n);
    dsteps[1]  = new Step(4, false, false, Foot.SO, 1.8f,  5.87f, TS_n, HT_n);	// Aufstellung
    dsteps[2]  = new Step(1, false, true,  Foot.SO, 0.84f, 4.83f, TS_s, HT_th);	// r rueck
    dsteps[3]  = new Step(1, false, false, Foot.SO, 0.84f, 4.83f, TS_s, HT_th);	// r rueck
    dsteps[4]  = new Step(0, false, true,  Foot.N,  0.66f, 4.61f, TS_q, HT_ht);	// r drehen
    dsteps[5]  = new Step(1, true,  false, Foot.N,  0.5f,  4.61f, TS_q, HT_ht);	// l ran
    dsteps[6]  = new Step(1, false, false, Foot.N,  0.66f, 3.7f,  TS_q, HT_th);	// r vorw
    dsteps[7]  = new Step(1, true,  true,  Foot.N,  0.64f, 2.5f,  TS_s, HT_ht);	// l vorw
    dsteps[8]  = new Step(1, true,  false, Foot.N,  0.64f, 2.5f,  TS_s, HT_ht);	// l vorw
    dsteps[9]  = new Step(0, true,  true,  Foot.W,  0.73f, 2.4f,  TS_q, HT_th);	// l drehen
    dsteps[10] = new Step(1, false, false, Foot.NWW,1.1f,  1.3f,  TS_q, HT_th);	// r vorw
    dsteps[11] = new Step(0, false, true,  Foot.SWW,1.1f,  1.25f, TS_q, HT_th);	// r drehen
    dsteps[12] = new Step(1, true,  false, Foot.SWW,2.0f,  0.5f,  TS_q, HT_th);	// l rueck
    dsteps[13] = new Step(1, false, true,  Foot.SWW,3.0f, -0.15f, TS_s, HT_t);	// r rueck
    dsteps[14] = new Step(1, false, false, Foot.SWW,3.0f, -0.15f, TS_s, HT_t);	// r rueck
  }
}




/*
 ********************************************************
 * 	QUICKSTEP					*
 ********************************************************
 */

/**
 * Quickstep: Stores the information of the quickstep figures.<P>
 *
 * To initialize a spezial figur:
 * <pre>
 *   Figur figur = new Quickstep(app, figNum, tempo, audioURL);
 * </pre>
 * Whereat app the applet pointer is, figNum the number of the
 * slow quickstep you want, tempo the speed of the sound defined
 * by the audioURL.<p>
 *
 * To add a new figur:
 * <ol>
 *  <li>Add the name of the figur into FigNames_{eng,ger}.
 *  <li>Add an initialization function (use initNaturalTurn as template)
 *  <li>Add your initialization function into the switch statement of
 *     the constructor.
 * </ol>
 * If you don't have more than 4 figures, you don't have to change anything
 * in class Dance. If you have more figures, you only have to add more space
 * for the figur-buttons.
 *
 * @version 1.0f 25 Aug 1995
 * @author Georg He&szlig;mann
 */
class Quickstep extends Figur {

  /** English name of this dance. */
  public final static String Name_eng       = "Foxtrott";
  /** German name of this dance. */
  public final static String Name_ger       = "Quickstep";
  /** Array of the english names of all implemented figures. */
  public final static String FigNames_eng[] = { "Natural Turn",
					  "ChassÈ Reverse Turn" };
  /** Array of the german names of all implemented figures. */
  public final static String FigNames_ger[] = { "Rechtsdrehung",
					  "Wechselschritt-Linksdrehung" };
  
  /**
   * Initialze a dance and select a special figur out of this dance.
   * @param app pointer to the dance applet.
   * @param danceNum number of the choosen figur. The number is defined
   *        relative to the names in the FigNames_* array.
   * @param tempo of the given audio file
   * @param audioURL URL description of the given audio file.
   *        Might be null if "no music" is selected.
   */
  public Quickstep(Dance app, int danceNum, int temp, URL audioURL)
  {
    super(app);
    
    Takte  = 4;

    CurrentFig = danceNum;
    
    SlowMotionTempo = 22;
    AudioTempo      = temp;
    
    NameDance  = (app.engl) ? Name_eng : Name_ger;
    NameFigur  = (app.engl) ? FigNames_eng[CurrentFig] : FigNames_ger[CurrentFig];
    
    sound = new Sound(audioURL, app);
    
    int LeftEdge = 2*NumXPos + sound.numMaxW + 20;
    int TopEdge  = NumYPos;

    /* initialize dance (floor size, steps) */
    switch (danceNum) {
     case 0:
      initNaturalTurn(LeftEdge, TopEdge);
      break;
     case 1:
      initChasseRTurn(LeftEdge, TopEdge);
      break;
    }
  }

  


  /**
   * Returns a String object representing.
   */
  public String toString()
  { 
    return getClass().getName() + "[Figur=" + super.toString() + "]";
  }


  private void initNaturalTurn(int LeftEdge, int TopEdge)
  {
    hfloor = new Floor(HerrStr, false, LeftEdge, TopEdge,
		       2, 4, 30, 30, 30, 30, app.foots, app);
    dfloor = new Floor(DameStr, true,  hfloor.RightEdge()+20, TopEdge,
		       2, 4, 30, 30, 30, 30, app.foots, app);

    // Tanzrichtung-Pfeile setzen
    hfloor.SetTRPos(1.3f, 3.2f);
    dfloor.SetTRPos(1.5f, 3.2f);

    hfloor.SetComStrPos(-0.1f, -0.1f);
    dfloor.SetComStrPos(-0.1f, -0.1f);


    // Herren-Schritte der Figur
    num_hsteps = 11;
    hsteps = new Step[num_hsteps];

    hsteps[0]  = new Step(0, true,  false, Foot.NO, -0.25f, 4.15f, TS_n, HT_n);
    hsteps[1]  = new Step(4, false, false, Foot.NO, -0.1f,  4.25f, TS_n, HT_n);	// Aufstellung
    hsteps[2]  = new Step(2, false, false, Foot.NO,  0.82f, 3.18f, TS_s, HT_ht);	// r vorw
    hsteps[3]  = new Step(0, false, true,  Foot.SO,  0.83f, 2.98f, TS_q, HT_t);	// r drehen
    hsteps[4]  = new Step(1, true,  false, Foot.SO,  1.55f, 2.1f,  TS_q, HT_t);	// l vorw
    hsteps[5]  = new Step(0, true,  true,  Foot.S,   1.65f, 2.07f, TS_q, HT_th); // l drehen
    hsteps[6]  = new Step(1, false, false, Foot.S,   1.45f, 2.07f, TS_q, HT_th); // r vorw schl
    hsteps[7]  = new Step(2, true,  false, Foot.S,   1.65f, 1.17f, TS_s, HT_th);	// l rueck
    hsteps[8]  = new Step(0, true,  true,  Foot.NW,  1.63f, 0.8f,  TS_s, HT_hiw);	// l drehen
    hsteps[9]  = new Step(2, false, false, Foot.NW,  1.74f, 0.7f,  TS_s, HT_hiw);	// r rueck ran
    hsteps[10] = new Step(2, true,  false, Foot.NW,  0.85f, 0.19f, TS_s, HT_h);	// l vorw

    // Damen-Schritte der Figur
    num_dsteps = 11;
    dsteps = new Step[num_dsteps];

    dsteps[0]  = new Step(0, true,  false, Foot.SW, 0.26f, 3.89f, TS_n, HT_n);
    dsteps[1]  = new Step(4, false, false, Foot.SW, 0.14f, 3.83f, TS_n, HT_n);	// Aufstellung
    dsteps[2]  = new Step(2, true,  false, Foot.SW, 1.17f, 2.82f, TS_s, HT_th);	// l rueck
    dsteps[3]  = new Step(0, true,  true,  Foot.NW, 1.18f, 3.06f, TS_q, HT_t);	// l drehen
    dsteps[4]  = new Step(1, false, false, Foot.N,  1.6f,  2.55f, TS_q, HT_t);	// r rueck
    dsteps[5]  = new Step(1, true,  false, Foot.N,  1.45f, 2.55f, TS_q, HT_th);	// l ran
    dsteps[6]  = new Step(2, false, false, Foot.N,  1.5f,  1.55f, TS_s, HT_ht);	// r vorw
    dsteps[7]  = new Step(0, false, true,  Foot.O,  1.4f,  1.45f, TS_s, HT_th);	// r drehen
    dsteps[8]  = new Step(2, true,  false, Foot.O,  1.4f,  0.47f, TS_s, HT_th);	// l vorw
    dsteps[9]  = new Step(0, true,  true,  Foot.SO, 1.42f, 0.42f, TS_s, HT_t);	// l drehen
    dsteps[10] = new Step(2, false, false, Foot.SO, 0.65f,-0.25f, TS_s, HT_t);	// r rueck
  }
  
  private void initChasseRTurn(int LeftEdge, int TopEdge)
  {
    hfloor = new Floor(HerrStr, false, LeftEdge, TopEdge,
		       2, 4, 30, 30, 30, 30, app.foots, app);
    dfloor = new Floor(DameStr, true,  hfloor.RightEdge()+20, TopEdge,
		       2, 4, 30, 30, 30, 30, app.foots, app);

    // Tanzrichtung-Pfeile setzen
    hfloor.SetTRPos(1.5f, 1.2f);
    dfloor.SetTRPos(1.5f, 1.2f);

    hfloor.SetComStrPos(0.2f, -0.1f);
    dfloor.SetComStrPos(0.2f, -0.1f);


    // Herren-Schritte der Figur
    num_hsteps = 12;
    hsteps = new Step[num_hsteps];

    hsteps[0]  = new Step(0, true,  false, Foot.NW, 2.17f, 4.25f, TS_n, HT_n);
    hsteps[1]  = new Step(4, false, false, Foot.NW, 2.3f,  4.13f, TS_n, HT_n);	// Aufstellung
    hsteps[2]  = new Step(2, true,  false, Foot.NW, 1.2f,  3.2f,  TS_s, HT_ht);	// l vorw
    hsteps[3]  = new Step(0, true,  true,  Foot.SW, 1.2f,  3.0f,  TS_q, HT_t);	// l drehen
    hsteps[4]  = new Step(1, false, false, Foot.SW, 0.45f, 2.37f, TS_q, HT_t);	// r vorw
    hsteps[5]  = new Step(0, false, true,  Foot.S,  0.3f,  2.35f, TS_q, HT_th);	// r drehen
    hsteps[6]  = new Step(1, true,  false, Foot.S,  0.5f,  2.35f, TS_q, HT_th);	// l ran
    hsteps[7]  = new Step(2, false, false, Foot.S,  0.3f,  1.40f, TS_s, HT_th);	// r rueck
    hsteps[8]  = new Step(0, false, true,  Foot.SSO,0.22f, 1.35f, TS_q, HT_h);	// r drehen
    hsteps[9]  = new Step(1, true,  false, Foot.NO, 0.22f, 1.05f, TS_q, HT_h);	// l rueck
    hsteps[10] = new Step(1, false, false, Foot.NO, 0.33f, 1.16f, TS_q, HT_h);	// r rueck ran
    hsteps[11] = new Step(2, true,  false, Foot.NO, 1.0f,  0.35f, TS_s, HT_h);	// l vorw

    // Damen-Schritte der Figur
    num_dsteps = 12;
    dsteps = new Step[num_dsteps];

    dsteps[0]  = new Step(0, true,  false, Foot.SO, 1.9f,  3.79f, TS_n, HT_n);
    dsteps[1]  = new Step(4, false, false, Foot.SO, 1.8f,  3.87f, TS_n, HT_n);	// Aufstellung
    dsteps[2]  = new Step(2, false, false, Foot.SO, 0.84f, 2.83f, TS_s, HT_th);	// r rueck
    dsteps[3]  = new Step(0, false, true,  Foot.NO, 0.84f, 3.0f,  TS_q, HT_t);	// r drehen
    dsteps[4]  = new Step(1, true,  false, Foot.N,  0.3f,  2.7f,  TS_q, HT_t);	// l seit
    dsteps[5]  = new Step(1, false, false, Foot.N,  0.45f, 2.7f,  TS_q, HT_th);	// r ran
    dsteps[6]  = new Step(2, true,  false, Foot.N,  0.36f, 1.77f, TS_s, HT_ht);	// l vorw
    dsteps[7]  = new Step(0, true,  true,  Foot.W,  0.47f, 1.63f, TS_q, HT_t);	// l drehen
    dsteps[8]  = new Step(1, false, false, Foot.W,  0.45f, 0.78f, TS_q, HT_t);	// r vorw/seit
    dsteps[9]  = new Step(0, false, true,  Foot.SW, 0.45f, 0.75f, TS_q, HT_th);	// r drehen
    dsteps[10] = new Step(1, true,  false, Foot.SW, 0.55f, 0.82f, TS_q, HT_th);	// l ran
    dsteps[11] = new Step(2, false, false, Foot.SW, 1.36f, 0.1f,  TS_s, HT_t);	// r rueck
  }
}




