/***********************************************************************
Copyright Notice: This source code is (C) Copyright 1995-96, EarthWeb LLC.,
All Rights Reserved. Distribution of this document or it's resulting
compiled code is granted for non-commercial use, with prior approval of
EarthWeb LLC. Distribution of this document or its resulting compiled
code, for commercial use, is granted only with prior written approval of
EarthWeb, LLC. For information, send email to info@earthweb.com.
***********************************************************************/

// $Id: Gravity.java,v 1.4 1996/04/16 20:55:23 greg Exp $

import java.awt.*;
import java.applet.*;
import java.net.*;
import java.lang.*;

// Main class

public class Gravity extends Vapplet implements Runnable, TakesABullet
{
    // Last mouse position
    int mx, my;

    // The line we are currently drawing the editor, if any
    int currentline[][];

    // Are we currently drawing a line in the editor?
    boolean drawing = false;

    // The Figure that represents the ground.
    Planet planet;

    // The size of the scrolling virtual screen.
    int vsx, vsy;

    // The rotational position of the player.
    double shipang = 0;

    // The Figure representing the player.
    Ship ship;

    // The thread in which the game is run.
    Thread heartbeat;

    // The last time a frame started.
    long lastTime;

    // What user controls are currently active?
    boolean turnLeft = false, turnRight = false, engine = false;

    // The acceleration of the ship's engine.
    double engineAccel = 190.0;

    // The acceleration downwards due to gravity.
    double gravityAccel = 20.0;

    // The current position and the first derivative.
    double vx=0, vy=0, x, y;

    // Arrays containing stats about the bullets.
    double bulx[], buly[], bulage[], bulvx[], bulvy[];
    double bullx[], bully[];

    // The number of bullets right now
    int n_buls;

    // The maxiumum # of bullets allowed onscreen at once.
    static int max_buls=30;

    // The speed of a bullet.
    public static double bul_vel = 100.0;

    // How long a bullet last before just disappearing.
    static double max_bul_age = 8.0;

    // The maximum angular velocity of the ship.
    static double rot_vel = Math.PI;

    // The max number of bullets the ship can shoot per second.
    static int buls_per_sec = 2;

    // Has the ship hit something?
    boolean exploded = false;

    // The time when the ship hit something, if it did.
    long explodedTime;

    // The starting position
    static int startx = 200, starty = 220;

    // Is the shield on?
    boolean shield = false;

    // Are we in edit mode?
    boolean editmode = false;

    // The first endpoint of the line we are drawing in editmode,
    // if any.
    int selectedLinex, selectedLiney=0;

    // Time to give up the CPU, so that other threads
    // can do things.
    static int sleepDelay = 15;

    // The longest amount of time between frames that the physics
    // calculations are allowed to perceive.  If the frame rate gets
    // really low, this causes the ship to move more slowly.  Otherwise,
    // physics is properly governed.  This is here so that a CPU burp
    // won't sent you through a wall.
    static double maxTimeStep = 0.1;

    // the soundtrack loops continously in the background
    private String soundtrack;
    private AudioClip soundtrackClip;

    // individual snippets play at triggers
    private String thrust, shieldOn, shieldOff, iSplode, theySplode, iFire,
    theyFire;
    private AudioClip thrustClip, shieldOnClip, shieldOffClip, iSplodeClip,
    theySplodeClip, iFireClip, theyFireClip;
    private String scene;
    boolean playing;

    // Initialize everything.
    public void init() {
	x = startx;
	y = starty;
	shipang = (Math.PI*3)/2;
	int lines[][][] = {{{2,2},{2,2}}};
	planet = new Planet( this, lines );
	currentline = new int[2][2];
	setDoubleBuffering( true );
	setVirtual( true );
	vsx = size().width*2;
	vsy = size().width*2;
	setVirtualSize( vsx, vsy );
	ship = new Ship();
	heartbeat = new Thread( this );
	heartbeat.start();
	bulx = new double[max_buls];
	buly = new double[max_buls];
	bulvx = new double[max_buls];
	bulvy = new double[max_buls];
	bullx = new double[max_buls];
	bully = new double[max_buls];
	bulage = new double[max_buls];
	planet.addLine( 0, 0, vsx-1, 0 );
	planet.addLine( vsx-1, 0, vsx-1, vsy-1 );
	planet.addLine( vsx-1, vsy-1, 0, vsy-1 );
	planet.addLine( 0, vsy-1, 0, 0 );
	planet.addLine( 2, 2, vsx-3, 2 );
	planet.addLine( vsx-3, 2, vsx-3, vsy-3 );
	planet.addLine( vsx-3, vsy-3, 2, vsy-3 );
	planet.addLine( 2, vsy-3, 2, 2 );
	loadWorldFromNet();

	soundtrack = getParameter("soundtrack");
	thrust = getParameter("thrust");

	shieldOn = getParameter("shieldOn");
	shieldOff = getParameter("shieldOff");
	iSplode = getParameter("iSplode");
	theySplode = getParameter("theySplode");
	iFire = getParameter("iFire");
	theyFire = getParameter("theyFire");

	soundtrackClip = getAudioClip(getDocumentBase(), soundtrack);
	thrustClip = getAudioClip(getDocumentBase(), thrust);
	shieldOnClip = getAudioClip(getDocumentBase(), shieldOn);
	shieldOffClip = getAudioClip(getDocumentBase(), shieldOff);
	iSplodeClip = getAudioClip(getDocumentBase(), iSplode);
	theySplodeClip = getAudioClip(getDocumentBase(), theySplode);
	iFireClip = getAudioClip(getDocumentBase(), iFire);
	theyFireClip = getAudioClip(getDocumentBase(), theyFire);

	if (getParameter( "nocredits" ) != null)
	  do_credits = false;
    }

    // Soundtrack loops
    public void playSoundtrack(AudioClip clip) {
	try {
	    clip.loop();
	} catch(Exception e);
	playing = true;
    }

    // stop the soundtrack
    public void stopSoundtrack(AudioClip clip) {
	try {
	    clip.stop();
	} catch(Exception e);
	playing = false;
    }

    // sound effects don't loop
    public void playEffect(AudioClip clip) {
	if (clip != null) {
	    try {
		clip.play();
	    } catch(Exception e);
	}
    }

    boolean do_credits = true;
    Font helv10 = new Font( "Helvetica", Font.PLAIN, 10 );
    static String creditString0 = "GRAVITY by Greg Travis and Patricia Ju.";
    static String creditString1 = "Copyright (c) EarthWeb (http://www.earthweb.com), all rights reserved";
    static String creditString2 = "Music Copyright (c) Adam Cohen, all rights reserved.";

    // Draw the credits strings to a Graphics.
    void credits( Graphics g ) {
	int ht10 = g.getFontMetrics( helv10 ).getHeight();
	int bot = size().height-1;
	g.setColor( Color.white );
	g.setFont( helv10 );
	g.drawString( creditString0, 10, bot-ht10-ht10-ht10 );
	g.drawString( creditString1, 10, bot-ht10-ht10 );
	g.drawString( creditString2, 10, bot-ht10 );
    }

    // Draw the credits at the bottom, and don't scroll them with
    // the virtual screen.
    public void overlayPaint( Graphics g ) {
	if (do_credits)
	  credits( g );
    }

    // Draw everything.
    public void paint( Graphics g ) {
	g.setColor( Color.white );
	setBackground( Color.black );
	ship.render( (int)x, (int)y, shipang, g, engine, exploded, shield );
	g.setColor( Color.white );
	renderBullets( g );
	planet.render( g );
	if (drawing)
	  g.drawLine( currentline[0][0], currentline[0][1],
		     currentline[1][0], currentline[1][1] );
	if (editmode)
	  editPaint( g );
    }

    // Draw stuff that's only for edit mode.
    void editPaint( Graphics g ) {
	g.fillOval( selectedLinex-5, selectedLiney-5, 10, 10 );
    }

    // The main loop of the game.  Pause a tiny bit, then
    // calculate physics (based on the time since the last frame
    // started) and then draw stuff.  Cap the physics timestep.
    public void run() {
	Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
	while (true) {
	    try {
		Thread.sleep( sleepDelay );
	    } catch( Exception e );
	    if (!editmode) {
		long time = System.currentTimeMillis();
		double delta = (time-lastTime)/1000.0;
		if (delta > maxTimeStep)
		  delta = maxTimeStep;
		if (delta < 0) delta = maxTimeStep;
		frame( delta );
		lastTime = time;
	    }
	    repaint();
	}
    }

    // Things to do while in editmode.
    void editStuff() {
	hilightNearestLine();
    }

    // Find the line nearest to the cursor and make a note of it.
    // When we redraw, it will be hilit.
    int lastLine = -1;
    void hilightNearestLine() {
	int line = planet.nearestLine( (double)mx, (double)my );
	int ll[][] = planet.lines[line];
	selectedLinex = (ll[0][0]+ll[1][0])/2;
	selectedLiney = (ll[0][1]+ll[1][1])/2;
	if (lastLine != line)
	  repaint();
	lastLine = line;
    }

    // In editmode, remove the line closest to the cursor.
    void removeNearestLine() {
	if (lastLine == -1) return;
	planet.removeLine( lastLine );
	planet.removeNearbyFriend( selectedLinex, selectedLiney );
	hilightNearestLine();
	repaint();
    }

    // In edit mode, add a new Friend (enemy) to the world, sitting
    // on the line that is closest to the cursor.
    void addFriend() {
	if (lastLine == -1) return;
	planet.addFriend( lastLine );
	repaint();
    }

    // If the cursor moves to or past the edge of the screen,
    // automatically slide the virtual screen around to keep the cursor 
    // towards the middle.
    public void autoslide() {
	Point sp = screenCoords( new Point( (int)x, (int)y) );
	int tx = sp.x - size().width/4;
	if (tx>0) {
	    tx = sp.x - (size().width*3)/4;
	    if (tx<0)
	      tx = 0;
	}
	int ttx=tx;
	int ty = sp.y - size().height/4;
	int tty=ty;
	if (ty>0) {
	    ty = sp.y - (size().height*3)/4;
	    if (ty<0)
	      ty = 0;
	}
	translate( -tx, -ty );
    }

    // Check to see if things have hit each other.
    public void intersections() {
	boolean wereHit = intersectBullets();
	if (shield) {
		wereHit = false;
		playEffect( theyFireClip );
	}
	//wereHit = false;
	if (!exploded && (ship.intersectFigure( planet ) || wereHit)) {
	    playEffect( iSplodeClip );
	    exploded = true;
	    explodedTime = System.currentTimeMillis();
	    vx = vy = 0;
	    turnLeft = turnRight = engine = false;
	}
	if (exploded && (System.currentTimeMillis()-explodedTime)>3000) {
	    exploded = false;
	    x = startx;
	    y = starty;
	    shipang = (Math.PI*3)/2;
	    vx = vy = 0;
	}
    }

    // move the ship, move the bullets, remove really old
    // bullets, draw stuff, run the AI for the enemies,
    // check for hits, and automatically slide the virtual
    // screen.  In short, do everything.
    public void frame( double delta ) {
	//System.out.println( "Delta: " + delta );
	rotation( delta );
	move( delta );
	moveBullets( delta );
	ageBullets( delta );
	repaint();
	planet.fireBulletsMaybe( delta*buls_per_sec, x, y );
	intersections();
	autoslide();
    }

    // Rotate the ship.
    public void rotation( double delta ) {
	if (exploded) return;
	if (turnLeft)
	  shipang -= delta * rot_vel;
	else if (turnRight)
	  shipang += delta * rot_vel;
    }

    // Move the ship forward.
    public void move( double delta ) {
	if (exploded) return;
	if (engine) {
	    vx += delta * engineAccel * Math.cos( shipang );
	    vy += delta * engineAccel * Math.sin( shipang );
	}
	vy += delta * gravityAccel;
	x += delta * vx;
	y += delta * vy;
    }

    // Stop the ship hard.
    public void stopp() {
	vx = vy = 0;
    }

    // Fire a new bullet into the world, at (x,y), moving in
    // direction (vx,vy).
    void addBullet( double x, double y, double vx, double vy ) {
	if (n_buls >= max_buls) return;
	bulx[n_buls] = bullx[n_buls] = x;
	buly[n_buls] = bully[n_buls] = y;
	bulvx[n_buls] = vx;
	bulvy[n_buls] = vy;
	bulage[n_buls] = 0;
	n_buls++;
    }

    // for interface TakesABullet.
    public void take( double x, double y, double vx, double vy ) {
	addBullet( x, y, vx, vy );
    }

    // The player fires a bullet.
    void addShipBullet() {
	if (exploded) return;
	double s = Math.sin( shipang );
	double c = Math.cos( shipang );
	addBullet( x+c*((Ship.size*4)/3), y+s*((Ship.size*4)/3),
		  vx+bul_vel*c, vy+bul_vel*s );
    }

    // Remove a bullet from the world.
    void removeBullet( int i ) {
	n_buls--;
	bulx[i] = bulx[n_buls];
	buly[i] = buly[n_buls];
	bulvx[i] = bulvx[n_buls];
	bulvy[i] = bulvy[n_buls];
	bullx[i] = bullx[n_buls];
	bully[i] = bully[n_buls];
	bulage[i] = bulage[n_buls];
    }

    // Note the inevitable aging of the bullets.
    void ageBullets( double delta ) {
	for (int i=0; i<n_buls; ++i)
	  bulage[i] += delta;
	for (int i=0; i<n_buls; ++i) {
	    if (bulage[i] > max_bul_age)
	      removeBullet( i );
	}
    }

    // move each bullet ahead.
    void moveBullets( double delta ) {
	for (int i=0; i<n_buls; ++i) {
	    bullx[i] = bulx[i];
	    bully[i] = buly[i];
	    bulx[i] += delta * bulvx[i];
	    buly[i] += delta * bulvy[i];
	}
    }

    // Draw the bullets to a Graphics
    void renderBullets( Graphics g ) {
	for (int i=0; i<n_buls; ++i) {
	    //g.drawRect( (int)bulx[i], (int)buly[i], 1, 1 );
	    g.drawLine( (int)bulx[i], (int)buly[i],
		       (int)bullx[i], (int)bully[i] );
	}
    }

    // Check for intersection between any bullet and the player.
    // Returns true if player is hit.
    boolean intersectBullets() {
	boolean wereHit = false;
	int bline[][] = new int[2][2];
	for (int i=0; i<n_buls; ++i) {
	    bline[0][0] = (int)bulx[i];
	    bline[0][1] = (int)buly[i];
	    bline[1][0] = (int)bullx[i];
	    bline[1][1] = (int)bully[i];
	    if (planet.intersectLines( bline ))
	      removeBullet( i );
	    else
	      planet.intersectFriends( bline );
	    if (ship.intersectLines( bline ))
	      wereHit = true;
	}
	return wereHit;
    }

    // Turn edit mode on or off.
    // When on, turn the game heartbeat off (i.e. pause).
    void editmodeOnOff() {
	editmode = !editmode;
	if (editmode)
	  heartbeat.suspend();
	else
	  heartbeat.resume();
	repaint();
    }

    // Load the world from the URL 'world'.
    public void loadWorldFromNet() {
	try {
	    planet.read( new URL( getDocumentBase(), "world" ) ); 
	} catch( MalformedURLException e ) {
	    System.out.println( e );
	}
    }

    public boolean keyDown( Event evt, int key ) {
	switch( key ) {
	  case 'z': turnLeft = true; break;
	  case 'x': turnRight = true; break;
	  case '.': { engine = true; playEffect( thrustClip ); break; }
	  case ',': { addShipBullet(); playEffect( iFireClip ); break; }
	  case ' ': shield = true; break;
	  case 'e': editmodeOnOff(); break;
	  case '\t': stopp(); break;
	  case 'd': if (editmode) removeNearestLine(); break;
	  case 'a': if (editmode) addFriend(); break;
	  case 'S': if (editmode) planet.write( "world" ); repaint(); break;
	  case 'L': if (editmode) planet.read( "world" ); repaint(); break;
	  case 'l': if (editmode) loadWorldFromNet(); repaint(); break;
	}
	return true;
    }

    public boolean keyUp( Event evt, int key ) {
	switch( key ) {
	  case 'z': turnLeft = false; break;
	  case 'x': turnRight = false; break;
	  case '.': engine = false; break;
	  case ' ': shield = false; break;
	}
	return true;
    }

    public boolean mouseUp(Event e, int x, int y) {
	if (playing) {
	    stopSoundtrack(soundtrackClip);
	} else {
	    playSoundtrack(soundtrackClip);
	}
	return true;
    }

    public boolean mouseDown( Event evt, int x, int y ) {
	if (drawing) {
	    drawing = false;
	    planet.addLine( currentline[0][0], currentline[0][1],
		      currentline[1][0], currentline[1][1] );
	} else {
	    drawing = true;
	    currentline[1][0] = currentline[0][0] = x;
	    currentline[1][1] = currentline[0][1] = y;
	}
	repaint();
	return true;
    }
    public boolean mouseDrag( Event evt, int x, int y ) {
	if (editmode) {
	    mx = x;
	    my = y;
	    editStuff();
	}
	if (drawing) {
	    currentline[1][0] = x;
	    currentline[1][1] = y;
	    repaint();
	}
	return true;
    }
    public boolean mouseMove( Event evt, int x, int y ) {
	if (editmode) {
	    mx = x;
	    my = y;
	    editStuff();
	}
	if (drawing) {
	    currentline[1][0] = x;
	    currentline[1][1] = y;
	    repaint();
	}
	return true;
    }

    public void start() {
	playSoundtrack(soundtrackClip);
    }

    public void stop() {
	stopSoundtrack(soundtrackClip);
    }
}
