/*-
 * Copyright (c) 1995 by Georg Hessmann.
 * All Right Reserved.
 *
 * Dance.java	1.0,  25 Aug 1995
 *              1.1,  13 Sep 1995
 *
 *
 */

import java.io.InputStream;
import java.lang.Math;
import java.net.URL;
import java.util.Stack;

import java.awt.*;

import java.applet.AudioClip;

import Floor;
import Foot;
import Figur;
import Button;
import MEXButtonList;

import SlowWaltz;
import Tango;
import SlowFox;
import Quickstep;

// import ChoiceWin;



/**
 * Dance - java.applet.Applet<P>
 *
 * java.applet.Applet for learning how to dance.<P>
 *
 * You can choose one of four dances and for every
 * dance one of some figures and one of some music
 * titels and view the animation of the figur
 * synchron to the music.<P>
 *
 * The applet uses three threads for the animation
 * and does an gread effort to make the animation
 * as flickerless as possible.<P>
 *
 * Three attributes are currently supported:<br>
 * <tt>english</tt> - use english texts<br>
 * <tt>audio</tt> - default audio on/off<br>
 * <tt>fatborder</tt> - use fat or thin border for the buttons.<P>
 *
 * <dl><dt><b>Disclaimer:</b>
 * <dd>Sorrie, my Java is better than my english.
 *             (My dancing is even better. :)
 * </dl>
 *
 * @see Figur
 * @see Floor
 * @see Foot
 * @see Button
 * @see MEXButtonList
 * @see MEXButton
 * @see ToggleButton
 * @see Step
 *
 * @version 1.1, 13 Sep 1995
 * @author Georg He&szlig;mann
 */

public class Dance extends java.applet.Applet {

  /** current version */
  static final float version = 1.1f;

  /** info about the implemented parameters */
  static String[][] paramInfo_eng = {
    { "english",   "boolean", "use english strings" },
    { "audio",     "boolean", "default music on or off" },
    { "fatborder", "boolean", "draw the buttons a bit heavier" }
  };
  
  static String[][] paramInfo_ger = {
    { "english",   "boolean", "verwende englische Texte" },
    { "audio",     "boolean", "Musik per Default an oder aus" },
    { "fatborder", "boolean", "draw the buttons a bit heavier" }
  };
  

  /** Use english or german for the strings */
  public boolean engl = false;		// soll englischer Text erscheinen?

  /** Foot object. Needed for drawing foots. */
  public Foot    foots;

  /** Font used for the applet titel */
  public Font    titleFont;

  /** Font used for the sub-titels*/
  public Font	 choiceFont;

  /** Small font, currently not used. */
  public Font	 smallFont;

  /** Small font bold. Used for showing the current selection. */
  public Font	 smallFontBld;

  /** Font used for the buttons. */
  public Font	 buttonFont;

  /** Bold font used for the buttons. */
  public Font	 buttonFontBld;

  static final String serverHost = "tech-www.informatik.uni-hamburg.de";

  /* resize the applet if, the internal size has changed */
  /* or use always the biggest size. */
  final boolean noResize = false;
  final int initXSize = 575;
  final int initYSize = 545;

  /* bisherige maximale Groesse */
  int maxXsize = 0;
  int maxYsize = 0;

  boolean clear_floor_border = false;	// soll der Bereich um das Parket
  // neu gezeichnet werden? (Fuesse die ueberstanden)

  boolean repaintAll  = true;
  boolean repaintText    = false;  // nur der veraenderliche Text ist betroffen
  boolean repaintButtons = false;  // Main-Buttons sind neu zu zeichnen

  boolean takeStepBack = false;

  /**
   * Currently active figur.
   */
  public Figur figur;


  
  // Nummern der einzelnen Buttons (Tanznummern == Array-Index!)
  static final int BslowW  = 0;
  static final int Btango  = 1;
  static final int BslowF  = 2;
  static final int Bquick  = 3;
  static final int Bshow   = 4;
  static final int Binfo   = 5;
  static final int Bmhelp  = 6;	// main help
  static final int Bahelp  = 7;	// animation help
  static final int Bpmusic = 8;	// play/test music
  static final int Bstep   = 10;
  static final int Bback   = 11;
  static final int Bplay   = 12;
  static final int Bmenu   = 13;
  static final int Bsound  = 14;
  static final int Bcomm   = 15;
  static final int Bfigur0 = 20;
  static final int Bmusic0 = 30;

  static String danceNames[];
  static String figurNames[][];
  static String musicNames[][];

  /*
  static final String figurNames_ger[][] = {
    SlowWaltz.FigNames_ger, Tango.FigNames_ger,
    SlowFox.FigNames_ger, Quickstep.FigNames_ger
  };

  static final String figurNames_eng[][] = {
    SlowWaltz.FigNames_eng, Tango.FigNames_eng,
    SlowFox.FigNames_eng, Quickstep.FigNames_eng
  };
  */

  static final String musicNames_ger[][] =
  {
    /* Langsamer Walzer */
       { "Ohne Musik", "I Wonder Why (Curtis Stigers)",
         "True Love (Ambros Seelos)" } ,
    /* Tango */
       { "Ohne Musik", "Adios Pampa Mia (Ambros Seelos)" } ,
    /* Slow-Fox */
       { "Ohne Musik", "Java Jive (Manhattan Transfer)",
         "Witchcraft (Frank Sinatra)", "Far Gone Now (Vaya Con Dios)",
	 "New York New York (Frank Sinatra)", "Magic Love (Ambros Seelos)" } ,
    /* Quickstep */
       { "Ohne Musik", "Nah Neh Nah (Vaya Con Dios)",
         "I Get A Kick Out Of You (Frank Sinatra)",
         "Dancing Fever (Ambros Seelos)"  }
  };


  static final String musicNames_eng[][] =
  {
    /* Langsamer Walzer */
       { "No Music", "I Wonder Why (Curtis Stigers)", "True Love (Ambros Seelos)" } ,
    /* Tango */
       { "No Music", "Adios Pampa Mia (Ambros Seelos)" } ,
    /* Slow-Fox */
       { "No Music", "Java Jive (Manhattan Transfer)",
         "Witchcraft (Frank Sinatra)", "Far Gone Now (Vaya Con Dios)",
         "New York New York (Frank Sinatra)", "Magic Love (Ambros Seelos)" } ,
    /* Quickstep */
       { "No Music", "Nah Neh Nah (Vaya Con Dios)",
         "I Get A Kick Out Of You (Frank Sinatra)",
         "Dancing Fever (Ambros Seelos)" }
  };

  static final String musicFiles[][] =
  {
    /* Langsamer Walzer */
       { null, "audio/IWonderWhy", "audio/TrueLove" } ,
    /* Tango */
       { null, "audio/AdiosPampaMia" } ,
    /* Slow-Fox */
       { null, "audio/JavaJive", "audio/witch", "audio/FarGoneNow",
	 "audio/NewYorkNewYork", "audio/MagicLove" } ,
    /* Quickstep */
       { null, "audio/NahNehNah", "audio/GetAKick",
         "audio/DancingFever" }
  };

  static final int musicTempo[][] =
  {
    /* Langsamer Walzer */
       { 0, 32, 33 } ,		// eigentlich 30
    /* Tango */
       { 0, 36 } ,
    /* Slow-Fox */
       { 0, 26, 30, 33, 29, 32 } ,
    /* Quickstep */
       { 0, 28, 22, 26 }      	// 52
  };

  AudioClip audioAnim = null;
  AudioClip audioPlay = null;

  MEXButtonList mexDance;
  MEXButtonList mexFigur;
  MEXButtonList mexMusic;

  //Stack layoutStack;        // alle Layout-Dinge, die bisher add()'ed wurden
  Panel innerPanel = null;

  // ChoiceWin cWin = null;
  

  int choosedDanceNum = 0;  // welcher Tanz wurde ausgewaehlt?
  int choosedFigurNum = 0;  // welche Figur (des Tanzes) wurde ausgewaehlt?
  int choosedMusicNum = 0;  // dito. Musik

  boolean audioON;	  	// soll Musik gespielt werden?
  boolean defAudioON;		// attribut value

  boolean useCommentHT;		// show heal/toe or slow/quick
  

  /** Should have the buttons a two pixel border or only a one pixel border. */
  public boolean fatBorder;	// Buttons with fat border?


  /**
   * java.applet.Applet initialization function.
   * Loads some fonts, reads the attributes "english", "audio" and
   * "fatborder".
   */
  public void init()
  {
    titleFont     = new java.awt.Font("Helvetica", Font.BOLD, 24);
    choiceFont	  = new java.awt.Font("Helvetica", Font.PLAIN, 18);
    smallFont     = new java.awt.Font("Helvetica", Font.PLAIN, 14);
    smallFontBld  = new java.awt.Font("Helvetica", Font.BOLD, 14);
    buttonFont 	  = new java.awt.Font("Helvetica", Font.PLAIN, 12);
    buttonFontBld = new java.awt.Font("Helvetica", Font.BOLD, 12);

    /* hm, gibt erstmal (ohne g) keinen default Font mehr
    if (titleFont     == null) titleFont     = font;
    if (choiceFont    == null) choiceFont    = font;
    if (smallFont     == null) smallFont     = font;
    if (smallFontBld  == null) smallFontBld  = font;
    if (buttonFont    == null) buttonFont    = font;
    if (buttonFontBld == null) buttonFontBld = font;
    */

    /* static variable -> not reentrant :( */
    Button.app        = this;

    /* english or german texts? */
    /*
    String attr = getParameter("english");
    if (attr != null) {
      engl = (attr.compareTo("false") != 0);	// nur false schaltet engl aus
    }
    else {
      engl = false;				// oder halt gar kein Attr
    }
    */
    engl = true;

    /* initializes (german/english) some strings */
    /*
    danceNames[0] = (engl) ? SlowWaltz.Name_eng : SlowWaltz.Name_ger;
    danceNames[1] = (engl) ? Tango    .Name_eng : Tango    .Name_ger;
    danceNames[2] = (engl) ? SlowFox  .Name_eng : SlowFox  .Name_ger;
    danceNames[3] = (engl) ? Quickstep.Name_eng : Quickstep.Name_ger;
    */
    danceNames = new String[4];
    danceNames[0] = (engl) ? "English Waltz" : "Langsamer Walzer";
    danceNames[1] = (engl) ? "Tango" : "Tango";
    danceNames[2] = (engl) ? "Slowfoxtrot" : "Slow Fox";
    danceNames[3] = (engl) ? "Foxtrott" : "Quickstep";

    figurNames = new String[4][];
    figurNames[0] = new String[2];
    figurNames[0][0] = (engl) ? "Natural Turn" : "Rechtsdrehung";
    figurNames[0][1] = (engl) ? "Whisk" : "Wischer";
    figurNames[1] = new String[3];	// Tango.FigNames_eng;
    figurNames[1][0] = (engl) ? "Left Turn" : "Linksdrehung";
    figurNames[1][1] = (engl) ? "Rock Turn" : "Wiegeschrittdrehung";
    figurNames[1][2] = (engl) ? "Outside Swivel, Brush Tap" : "Linksgedrehte Kehre + Brush Tap";
    figurNames[2] = new String[2];
    figurNames[2][0] = (engl) ? "Feather Step" : "Federschritt";
    figurNames[2][1] = (engl) ? "Left Turn" : "Linksdrehung";
    figurNames[3] = new String[2];
    figurNames[3][0] = (engl) ? "Natural Turn" : "Rechtsdrehung";
    figurNames[3][1] = (engl) ? "Chassé Reverse Turn" : "Wechselschritt-Linksdrehung" ;
    if (engl) {
      // figurNames = figurNames_eng;
      musicNames = musicNames_eng;
      // figurNames = new String[4][];
      // figurNames[0] = SlowWaltz.FigNames_eng;
      // figurNames[1] = Tango.FigNames_eng;
      // figurNames[2] = SlowFox.FigNames_eng;
      // figurNames[3] = Quickstep.FigNames_eng;
    }
    else {
      //figurNames = figurNames_ger;
      musicNames = musicNames_ger;
      // figurNames = new String[4][];
      // figurNames[0] = SlowWaltz.FigNames_ger;
      // figurNames[1] = Tango.FigNames_ger;
      // figurNames[2] = SlowFox.FigNames_ger;
      // figurNames[3] = Quickstep.FigNames_ger;
    }

    /* should be "no music" or the first music title the default? */
    /*
    attr = getParameter("audio");
    if (attr != null) {
      defAudioON = (attr.compareTo("false") != 0);	// nur false schaltet audio aus
    }
    else {
      defAudioON = false;				// oder halt gar kein Attr
    }
    */
    defAudioON = false;

    if (defAudioON) choosedMusicNum = 1;	// first music file
    else            choosedMusicNum = 0;	// no music

    audioON = defAudioON;

    /* buttons with 1 or 2 pixel border? */
    /*
    attr = getParameter("fatborder");
    if (attr != null) {
      fatBorder = (attr.compareTo("false") != 0);
    }
    else {
      fatBorder = false;			// oder halt gar kein Attr
    }
    */
    fatBorder = false;
    
    /* always start with displaying the toe/heal string */
    useCommentHT = true;

    /* initialize the Foot object (load it first time a figur is allocated) */
    foots = null;

    /* we are in the main menu, no figur loaded */
    figur = null;

    innerPanel = null;

    setBackground(Color.lightGray);

    /* some initializations, which occures after every page change */
    /* (main page <--> animation page) 				   */
    init_post();
    
    // getAppletContext().showStatus("Done.");
  }

  /**
   * Initialization function which will called after every page-change.
   * (There are two 'pages': main-menu page and animation page.
   */
  private void init_post()
  {
    if (innerPanel != null) {
      remove(innerPanel);
      innerPanel.removeAll();
      innerPanel = null;
    }
    
    if (figur != null) {
      /* figur != null  ->  animation page */
      
      int xsize = figur.RightEdge() + 20;
      int ysize = figur.BottomEdge() + 20;

      /* niemals verkleinern? */
      if (noResize) {
	if (xsize > maxXsize) maxXsize = xsize;
	if (ysize > maxYsize) maxYsize = ysize;
      }
      else {
	maxXsize = xsize;
	maxYsize = ysize;
      }
      
      resize(maxXsize, maxYsize);

      /* free all buttons (of main page) */
      Button.freeAll();

      int ypos    = figur.hfloor.BottomEdgeParket();
      int bheight = 20;
      
      Button dummy;

      ypos -= bheight;
      dummy = new Button(Bmenu, (engl) ? "menu" : "Menü", 15, ypos,
			 figur.hfloor.LeftEdge() - 30, bheight, buttonFontBld);
      ypos -= bheight + 10;
      dummy = new Button(Bback, (engl) ? "step backw." : "rückwärts", 15, ypos,
			 figur.hfloor.LeftEdge() - 30, bheight, buttonFont);
      ypos -= bheight + 5;
      dummy = new Button(Bstep, (engl) ? "step forw." : "vorwärts", 15, ypos,
			 figur.hfloor.LeftEdge() - 30, bheight, buttonFont);
      ypos -= bheight + 5;
      dummy = new Button(Bplay, (engl) ? "play" : "Animation", 15, ypos,
			 figur.hfloor.LeftEdge() - 30, bheight, buttonFontBld);

      if (choosedMusicNum != 0) {
	ypos -= bheight + 10;
	dummy = new ToggleButton(Bsound,
				 (engl) ? "use audio"   : "Ton an",
				 (engl) ? "slow motion" : "Zeitlupe",
				 15, ypos,
				 figur.hfloor.LeftEdge() - 30, bheight,
				 buttonFont, buttonFont, audioON);
	ypos -= bheight + 5;
      }
      else {
	ypos -= bheight + 10;
      }

      /* setup the comment string */
      if (useCommentHT) figur.useComStrHT();
      else      	figur.useComStrTime();

      dummy = new ToggleButton(Bcomm,
			       (engl) ? "footwork" : "Fußarbeit",
			       (engl) ? "rhythm" : "Rhythmus", 
			       15, ypos,
			       figur.hfloor.LeftEdge() - 30, bheight,
			       buttonFont, buttonFont, useCommentHT);

      ypos -= bheight + 10;
      dummy = new Button(Bahelp, (engl) ? "help" : "Hilfe", 15, ypos,
			 figur.hfloor.LeftEdge() - 30, bheight, buttonFont);

    }
    else {
      /* figur == null  ->  main page */
      
      int xsize = initXSize;
      int ysize = initYSize;
      
      /* niemals verkleinern...? */
      if (noResize) {
	if (xsize > maxXsize) maxXsize = xsize;
	if (ysize > maxYsize) maxYsize = ysize;
      }
      else {
	maxXsize = xsize;
	maxYsize = ysize;
      }

      resize(maxXsize, maxYsize);

      audioON = defAudioON;
      CreateMainButtons();
    }
  }

  public void start()
  {
    /*
    if (figur == null) {
      initMainLayout();
    }
    if (cWin == null) {
      cWin = new ChoiceWin(this);
    }
    */
  }


  /**
   * java.applet.Applet function: paint()
   * Redraw completly the main (figur==null) or animation page (figur!=null).
   */
  public void paint(Graphics g)
  {
    //System.out.println("paint");
    
    setBackground(Color.lightGray);

    if (figur != null) paint_figur(g);
    else 	       paint_main(g);
    
    Button.drawAll(g);
  }

  /**
   * java.applet.Applet function: update()
   * Redraw only the changed things.
   */
  public void update(Graphics g)
  {
    //System.out.println("update");

    setBackground(Color.lightGray);
    
    if (figur != null) update_figur(g);
    else 	       update_main(g);

    Button.drawChanged(g);
  }
    


  /**
   * total redraw of the animation page.
   */
  private void paint_figur(Graphics g)
  {
    if (!figur.everPaint) {
      figur.everPaint = true;
    }

    // Bug? clearRect() doesn't respect clipRect().
    if (!takeStepBack) g.clearRect(1,1,size().width-2,size().height-2);
    // loesche alles ausser dem Rahmen

    g.draw3DRect(0, 0, size().width-1, size().height-1, true);
    figur.drawTitel(g);

    figur.dfloor.RedrawFloor();	// die muessen nun auch den Hintergrund
    figur.hfloor.RedrawFloor();	// neu zeichnen.

    figur.dfloor.paint(g);
    figur.hfloor.paint(g);

    figur.drawNum(g, false);
  }

  /**
   * updtae of the animation page. Draw only new things.
   */
  private void update_figur(Graphics g)
  {
    // ich loesche nun nie mehr den gesamten Hintergrund
    // d.h. wenne s etwas zu loeschen gibt, dann muss ich das expliziet tun
    // siehe clear_floor_border

    if (!figur.everPaint) {
      paint_figur(g);
    }
    
    if (clear_floor_border) {
      // oben rueber loeschen
      g.clearRect(figur.hfloor.LeftEdge()-10, figur.hfloor.TopEdge()-10,
		  figur.RightEdge()-figur.hfloor.LeftEdge()+20,
		  10);
      // unten drunter loeschen
      g.clearRect(figur.hfloor.LeftEdge()-10, figur.hfloor.BottomEdgeParket(),
		  figur.RightEdge()-figur.hfloor.LeftEdge()+20,
		  11);
      // links
      g.clearRect(figur.hfloor.LeftEdge()-20, figur.hfloor.TopEdge()-10,
		  20,
		  figur.hfloor.BottomEdgeParket()-figur.hfloor.TopEdge()-175);
      g.clearRect(figur.hfloor.LeftEdge()-15, figur.hfloor.TopEdge()-10,
		  15,
		  figur.hfloor.BottomEdgeParket()-figur.hfloor.TopEdge()+20);
      // Mitte
      g.clearRect(figur.hfloor.RightEdge(), figur.hfloor.TopEdge()-10,
		  figur.dfloor.LeftEdge()-figur.hfloor.RightEdge(),
		  figur.hfloor.BottomEdgeParket()-figur.hfloor.TopEdge()+20);
      // rechts
      g.clearRect(figur.dfloor.RightEdge(),figur. hfloor.TopEdge()-10,
		  figur.RightEdge()-figur.dfloor.RightEdge()+15,
		  figur.hfloor.BottomEdgeParket()-figur.hfloor.TopEdge()+20);

      clear_floor_border = false;
      figur.dfloor.RedrawFloor();	// die muessen nun auch den Hintergrund
      figur.hfloor.RedrawFloor();	// neu zeichnen.
    }

    figur.hfloor.paint(g);
    figur.dfloor.paint(g);

    figur.drawNum(g, true);

    
    if (takeStepBack) { 
      Graphics dg = figur.dfloor.installClipRect(g);
      paint_figur(dg);

      Graphics hg = figur.hfloor.installClipRect(g);
      paint_figur(hg);
      
      takeStepBack = false;
    }
    
  }

  /**
   * total redraw of the main-menu page.
   */
  private void paint_main(Graphics g)
  {
    Font oldf = g.getFont();
      
    g.clearRect(0, 0, size().width, size().height);

    //g.draw3DRect(0, 0, size().width, size().height, true);
    g.draw3DRect(0, 0, initXSize-1, initYSize-1, true);

    g.setFont(titleFont);

    String s;
    
    if (engl) s = "How to Learn Dance with HotJava";
    else      s = "Tanzen lernen leicht gemacht";

    int l = g.getFontMetrics(titleFont).stringWidth(s);
    g.drawString(s, 295-l/2, 35);

    g.setFont(oldf);
    if (engl) {
      g.drawString("Long time ago, you have had a dancing course, but" +
		   " because of no practice you have", 20, 70);
      g.drawString("forgotten most of the dancing steps?", 20, 87);
      g.drawString("No problem! With Java-Dance you can watch an animation" +
		   " of different figures,", 30, 104);
      g.drawString("and get information about rhythm and footwork." +
		   " Come on and dance!", 20, 121);
    }
    else {
      g.drawString("Sie haben vor Urzeiten mal einen Tanzkurs gemacht," +
		   " haben aber mangels Übung alles", 20, 70);
      g.drawString("wieder vergessen? Kein Problem! Mit Java-Dance können" +
		   " Sie sich verschiedene ", 20, 87);
      g.drawString("Standard Figuren ansehen (animiert mit Ton" +
		   " oder Schritt für Schritt).", 20, 104);
      g.drawString("Dazu gibt es zu jedem Schritt Informationen" +
		   " über Rythmus und Fußarbeit.", 20, 121);
    }
      
    g.setFont(choiceFont);

    if (engl) {
      g.drawString("Current selection:", 20, 150);
      g.drawString("Available dances:", 20, 220);
      g.drawString("Available dance-figures for current dance:", 20, 300);
      g.drawString("Available music for current dance:", 20, 410);
    }
    else {
      g.drawString("Derzeit ausgewählt:", 20, 150);
      g.drawString("Verfügbare Tänze:", 20, 220);
      g.drawString("Verfügbare Figuren für diesen Tanz:", 20, 300);
      g.drawString("Verfügbare Musik für diesen Tanz:", 20, 410);
    }

    g.setFont(oldf);

    repaintText = true;
    update_main(g);
  }

  /**
   * update of the main-menu page. Redraw only the texts or buttons.
   */
  private void update_main(Graphics g)
  {
    if (repaintAll) {
      repaintAll     = false;
      repaintText    = true;
      repaintButtons = true;
      paint_main(g);
    }

    if (repaintText) {
      repaintText = false;
    
      Font oldf = g.getFont();

      g.setFont(smallFontBld);
      g.clearRect(40, 160, initXSize-41, 30);
      g.drawString(danceNames[choosedDanceNum] + ", " +
		   ((figurNames[choosedDanceNum].length > 0)
		      ? figurNames[choosedDanceNum][choosedFigurNum]
		      : "(none)") + ", " +
		   ((musicNames[choosedDanceNum].length > 0)
		      ? musicNames[choosedDanceNum][choosedMusicNum]
		      : "(none)"), 40, 180);
      g.setFont(oldf);
    }
    
    if (repaintButtons) {
      repaintButtons = false;

      // Figur-Buttons loeschen
      g.clearRect(40, 310, initXSize-41, 60);

      // Musik-Buttons loeschen
      g.clearRect(40, 415, initXSize-41, 85);

      //Button.drawAll(g);
    }
  }


  /*
  private void initMainLayout()
  {
    if (innerPanel != null) return;
    
    innerPanel = new Panel();

    try {
      //innerPanel.setLayout(new GridLayout(10, 5, 5, 5));
      String s;
    
      if (engl) s = "How to Learn Dance with HotJava";
      else      s = "Tanzen lernen leicht gemacht";

      Label l = new Label(s, Label.CENTER);
      l.setFont(titleFont);
      innerPanel.add(l);
      //l.move(100, 100);
    }
    catch (IllegalArgumentException e)
    {
      ;
    }
    add(innerPanel);
    show();
  }
  */

  /*
  public synchronized Component add(Component c)
  {
    layoutStack.push(c);
    return super.add(c);
  }

  public synchronized Component add(String s, Component c)
  {
    layoutStack.push(c);
    return super.add(s, c);
  }

  public synchronized void removeAllComponents()
  {
    while (!layoutStack.empty()) {
      Component c = (Component)layoutStack.pop();
      remove(c);
    }
  }
  */
  


  /**
   * java.applet.Applet function: stop()
   * Stops the dancing and the audio.
   */
  public void stop()
  {
    if (figur != null) {
      figur.StopDance();
    }
    else if (audioPlay != null) {
      audioPlay.stop();
      audioPlay = null;
    }
  }


  /** returns some info about author, version and copyright of the applet */
  public String getAppletInfo()
  {
    return "JDance. Author: Georg Heßmann, Version: " + version +
           ", Copyright (c) 1995, All Rights Reserved.";
  }

  /** returns info about the parameters */
  public String[][] getParameterInfo()
  {
    return (engl) ? paramInfo_eng : paramInfo_ger;
  }

  /**
   * java.applet.Applet function: mouseDown()
   * Calls button.mouseDown() to check for button hits.
   * @see Button#mouseDown
   */
  public boolean mouseDown(java.awt.Event evt, int x, int y)
  {
    requestFocus();

    return Button.mouseDown(x, y);
  }

  /**
   * java.applet.Applet function: mouseUp()
   * Calls button.mouseUp() to check for button hits.
   * @see Button#mouseUp
   */
  public boolean mouseUp(java.awt.Event evt, int x, int y)
  {
    return Button.mouseUp(x, y);
  }

  /**
   * java.applet.Applet function: mouseDrag()
   * Calls button.mouseDrag() to check for button hits.
   * @see Button#mouseDrag
   */
  public boolean mouseDrag(java.awt.Event evt, int x, int y)
  {
    return Button.mouseDrag(x, y);
  }

  /**
   * Initialize the buttons of the main-menu page.
   */
  private void CreateMainButtons()
  {
    Button.freeAll();

    mexDance = new MEXButtonList(buttonFontBld);
    for (int i=0; i<danceNames.length; i++) {
      mexDance.NewMEXButton(BslowW + i, danceNames[i],
			    40+130*i, 240, 120, 20, buttonFont);
    }
    mexDance.SetChoosedNum(choosedDanceNum);

    mexFigur = new MEXButtonList(buttonFontBld);
    for (int i=0; i<figurNames[choosedDanceNum].length; i++) {
      mexFigur.NewMEXButton(Bfigur0 + i, figurNames[choosedDanceNum][i],
			    40+260*(i%2), 320+25*(i/2), 250, 20, buttonFont);
    }
    mexFigur.SetChoosedNum(choosedFigurNum+Bfigur0);
      
    mexMusic = new MEXButtonList(buttonFontBld);
    for (int i=0; i<musicNames[choosedDanceNum].length; i++) {
      mexMusic.NewMEXButton(Bmusic0 + i, musicNames[choosedDanceNum][i],
			    40+260*(i%2), 425+25*(i/2), 250, 20, buttonFont);
    }
    mexMusic.SetChoosedNum(choosedMusicNum+Bmusic0);
    
    audioON = (choosedMusicNum != 0);
    
    Button dummy;
    dummy = new Button(Binfo,
		       (engl) ? "dance info" : "Tanz Info.",
		       295 - 100 - 100, 515,
		       100, 20, buttonFont);
    dummy = new Button(Bmhelp,
		       (engl) ? "help" : "Hilfe",
		       295 + 100, 515,
		       100, 20, buttonFont);
    dummy = new Button(Bshow,
		       (engl) ? "show dance" : "Tanz anzeigen",
		       295-50, 515,
		       100, 20, buttonFontBld);

    dummy = new Button(Bpmusic,
		       (engl) ? "test music" : "Musik testen",
		       550-80, 390+6,
		       80, 20, buttonFont);

    repaintButtons = true;
  }


  public int getCurDance() { return choosedDanceNum; }
  public int getCurFigur() { return choosedFigurNum; }
  public int getCurMusic() { return choosedMusicNum; }

  public void setCurDance(int num)
  {
    choosedDanceNum = num;
    choosedFigurNum = 0;
    choosedMusicNum = (defAudioON) ? 1 : 0;
    
    CreateMainButtons();
    repaintText    = true;

    //if (cWin != null) cWin.setupMusicMenu();
  }

  public void setCurFigur(int num)
  {
    choosedFigurNum = num; 
    repaintText = true;
  }

  public void setCurMusic(int num)
  {
    choosedMusicNum = num;
    audioON = (choosedMusicNum != 0);
    repaintText = true;
  }
    

  /**
   * Change from the animation page back to the main page.
   */
  public void killFigur()
  {
    figur.StopDance();
    figur = null;

    init_post();

    repaintAll = true;		// muss komplett neu aufgebaut werden
    repaint();
  }


  /**
   * Change from the main page to the animation page.
   * Creates an object for the chooseen dance/figur.
   */
  public synchronized void newFigur()
  {
    // getAppletContext().showStatus("Load new dance...");
    if (figur != null) killFigur();

    if (audioPlay != null) {
      audioPlay.stop();
      audioPlay = null;
    }

    /* (allocates the foot array, but don't load any feets).*/
    if (foots == null) {
      /* verzoegere das Laden der Klasse moeglichst lange */
      foots = new Foot(this);
    }


    URL audioURL;
    try {
      audioURL = (choosedMusicNum != 0 && musicFiles[choosedDanceNum].length > 0)
	? new URL(getCodeBase(),
		  musicFiles[choosedDanceNum][choosedMusicNum] + "-part.au")
	: null;
    }
    catch (java.net.MalformedURLException e) {
      audioURL = null;
    }
    
    int tempo = musicTempo[choosedDanceNum][choosedMusicNum];

    switch (choosedDanceNum) {
     case BslowW:
      figur = new SlowWaltz(this, choosedFigurNum, tempo, audioURL);
      break;

     case Btango:
      figur = new Tango    (this, choosedFigurNum, tempo, audioURL);
      break;

     case BslowF:
      figur = new SlowFox  (this, choosedFigurNum, tempo, audioURL);
      break;
      
     case Bquick:
      figur = new Quickstep(this, choosedFigurNum, tempo, audioURL);
      break;
    }

    // resize - create buttons - ...
    init_post();

    // Figur erstmal komplett anzeigen
    while (figur.DoHStep() >= 0 && figur.DoDStep() >= 0);
    figur.HideComStr();

    // getAppletContext().showStatus("Done.");
    repaint();
  }

  /** Should audio played while the animation runs? */
  public boolean useAudio()
  {
    return audioON;
  }

  /**
   * Function will be called if the "menu" button is hit.
   * Change from the animation page back to the main page.
   * @see Button#action
   */
  public void actionMenuButton()
  {
    killFigur();
  }

  /**
   * Function will be called if the "step forw." button is hit.
   * Does one step forward.
   * @see Figur#stepForw
   * @see Button#action
   */
  public void actionStepButton()
  {
    if (!figur.inSingleStep()) {
      figur.clearSteps();
      figur.ShowComStr();
      clear_floor_border = true;
      repaint();
    }

    figur.stepForw();
    repaint();
  }

  /**
   * Function will be called if the "step backw." button is hit.
   * Does one step backward.
   * @see Figur#stepBackw
   * @see Button#action
   */
  public void actionBackStepButton()
  {
    figur.stepBackw();
    takeStepBack = true;
    repaint();
  }

  /**
   * Function will be called if the "play" button is hit.
   * Starts the animation. If an animation is already running, stops it.
   * @see Button#action
   */
  public void actionPlayButton()
  {
    if (figur.inSingleStep()) {
      figur.leaveSingleStep();
      figur.clearSteps();
      clear_floor_border = true;
      repaint();
    }

    if (!figur.ThreadsAlive()) {
      // Alles tot, also komplett neu beginnen
      figur.clearSteps();
      clear_floor_border = true;
      repaint();
      //try {Thread.sleep(500);} catch (InterruptedException e){}

      figur.Dance();
    }
    else {
      figur.StopDance();
      repaint();
    }
  }

  /**
   * Function will be called if one of the dance buttons are hit.
   * Sets the other (figur/music) buttons new.
   * @see Button#action
   */
  public void actionDanceButton(int Bnum)
  {
    setCurDance(mexDance.GetChoosedNum());
  }

  /**
   * Function will be called, if one of the figur buttons are hit.
   * @see Button#action
   */
  public void actionFigurButton(int Bnum)
  {
    setCurFigur(mexFigur.GetChoosedNum() - Bfigur0);
  }

  /**
   * Function will be called, if one of the music buttons are hit.
   * @see Button#action
   */
  public void actionMusicButton(int Bnum)
  {
    setCurMusic(mexMusic.GetChoosedNum() - Bmusic0);
  }

  /**
   * Function will be called if the "show dance" button is hit.
   * Changes from the main page to the animation page.
   * @see Button#action
   */
  public void actionShowButton()
  {
    newFigur();
  }

  /**
   * Function will be called if the "dance info" button is hit.
   * Opens an URL with information about the currently selected dance.
   * @see Button#action
   */
  public void actionInfoButton()
  {
    URL url = null;

    try {
      switch (choosedDanceNum) {
       case BslowW:
	url = new URL("http://" + serverHost + "/dance/LW.html");
	break;

       case Btango:
	url = new URL("http://" + serverHost + "/dance/Tango.html");
	break;

       case BslowF:
	url = new URL("http://" + serverHost + "/dance/Slowfox.html");
	break;

       case Bquick:
	url = new URL("http://" + serverHost + "/dance/Quick.html");
	break;
      }
    }
    catch (java.net.MalformedURLException e) {
      url = null;
    }
    if (url != null) getAppletContext().showDocument(url);
  }

  /**
   * Function will be called if the "test music" button is hit.
   * Start playing the selected music.
   * @see Button#action
   */
  public void actionPlayMusicButton()
  {
    if (audioPlay != null) {
      audioPlay.stop();
      audioPlay = null;
    }
    
    String music = (choosedMusicNum != 0 && musicFiles[choosedDanceNum].length > 0)
      		    ? (musicFiles[choosedDanceNum][choosedMusicNum] + ".au")
		    : null;
    if (music != null) {
      URL url;
      try {
        url = new URL("http://" + serverHost + "/dance/" + music);
      }
      catch (java.net.MalformedURLException e) {
	url = null;
      }
      audioPlay = getAudioClip(url);
      if (audioPlay != null) {
	audioPlay.play();
      }
    }
  }

  /**
   * Function will be called if the "help" button within the main page is hit.
   * Open an URL to a help page.
   * @see Button#action
   */
  public void actionMainHelpButton()
  {
    URL url;
    try {
      url = new URL("http://" + serverHost + "/dance/java/jhelp-main" +
		      ((engl) ? "-e" : "") + ".html");
    }
    catch (java.net.MalformedURLException e) {
      url = null;
    }
    getAppletContext().showDocument(url);
  }

  /**
   * Function will be called if the "help" button within the animation page is hit.
   * Open an URL to a help page.
   * @see Button#action
   */
  public void actionAnimationHelpButton()
  {
    URL url;
    try {
      url = new URL("http://" + serverHost + "/dance/java/jhelp-anim" +
		      ((engl) ? "-e" : "") + ".html");
    }
    catch (java.net.MalformedURLException e) {
      url = null;
    }
    getAppletContext().showDocument(url);
  }

  /**
   * Function will be called if the "audio on | slow motion" button is hit.
   * @param on state of the toggle button.
   * @see Button#action
   */
  public void actionSoundButton(boolean on)
  {
    audioON = on;
  }

  /**
   * Function will be called if the "slow/quick | toe/heal" button is hit.
   * @param usehb state of the toggle button (use heal/toe).
   * @see Button#action
   * @see Figur@useComStrHT
   * @see Figur@useComStrTime
   */
  public void actionCommentButton(boolean usehb)
  {
    useCommentHT = usehb;
    if (usehb) figur.useComStrHT();

    else       figur.useComStrTime();
  }



  /**
   * Returns a String object representing.
   */
  public String toString()
  {
    return getClass().getName() + "[version=" + version + "]";
  }

}



class DebugDance
{

  public static void main(String args[])
  {
    Dance dance = new Dance();
    dance.init();
    dance.start();
  }
}

