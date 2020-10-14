/***********************************************************************
Copyright Notice: This source code is (C) Copyright 1995-96, EarthWeb LLC.,
All Rights Reserved. Distribution of this document or it's resulting
compiled code is granted for non-commercial use, with prior approval of
EarthWeb LLC. Distribution of this document or its resulting compiled
code, for commercial use, is granted only with prior written approval of
EarthWeb, LLC. For information, send email to info@earthweb.com.
***********************************************************************/

// $Id: Netris.java,v 1.13 1996/04/16 21:00:52 greg Exp $

import java.applet.*;
import java.awt.*;
import java.util.Random;

/*

This is the main class of the applet.  So it's really big, of course.

*/

public class Netris extends Applet implements Runnable
{
    // Current and previous positions of the mouse.
    int mousex=0, mousey=0, lmousex=0, lmousey=0;

    // The tetris pieces, and the piece that is currently falling.
    Grid pieces[][], currentpiece[];

    // The board, including the pieces that have dropped and the
    // current piece.
    BoardGrid board;

    // The position and rotation of the current piece.
    int currentpiecex, currentpiecey, currentrot;

    // The number of steps the current piece has taken.  If it
    // can't move and this is 0, the game is over.  Ahh, Tetris.
    int currentpiecesteps;

    // The dimensions of the board.
    static int boardx=10, boardy = 15;

    // 3D transforamtions: rotate, flip z axis, put in front of us.
    Transform cur_rotation=null, zflip=null, outfront=null;

    // For selecting the next piece.
    Random rand;

    // Syncronize the updating and drawing of the board on this object.
    Object steplock = new Object();

    // The heartbeat thread.
    Thread kicker = null;

    // Are we currently paused?
    boolean threadSuspended = false;

    public Netris() {}

    // Allocate a Grid for the board, and draw the walls.
    void initboard() {
	board = new BoardGrid( boardx, boardy );
	for (int y=0; y<boardy; ++y) {
	    board.set( 0, y, 1 );
	    board.set( boardx-1, y, 1 );
	}
	for (int x=0; x<boardx; ++x) {
	    board.set( x, 0, 1 );
	}
    }

    // Select the next piece, randomly.
    Grid[] nextpiece() {
	int i = rand.nextInt()&0xFF;
	i %= pieces.length;
	return pieces[i];
    }

    // Nice textual representations of the pieces
    // so it's easy to change them.
    String pieceplans[][] =
      { { "xxx",
	  " x" },
	{ "xxxx" },
	{ "x",
	  "xxx" },
	{ "xxx",
	  "x" },
	{ "xx",
	  "xx" },
	{ "xx",
	  " xx" },
	{ " xx",
	  "xx" }
    };

    // Generate the Grid objects from the pieceplans; make
    // four rotations of each.
    void initpieces() {
	pieces = new Grid[pieceplans.length][4];
	for (int p=0; p<pieceplans.length; ++p) {
	    pieces[p][0] = new Grid();
	    for (int s=0; s<pieceplans[p].length; ++s) {
		String str = pieceplans[p][s];
		for (int c=0; c<str.length(); ++c)
		  pieces[p][0].set( s, c, (str.charAt(c) == ' ')?0:1 );
	    }
	    for (int r=1; r<4; ++r)
	      pieces[p][r] = pieces[p][r-1].rot();
	}
	currentpiece = null;
	currentpiecex = currentpiecey = -1;
	currentrot = 0;
    }

    // Init everything.
    Color wireColor;
    public void init() {
	int wireColorr=255, wireColorg=0, wireColorb=0;
	if (getParameter("wirecolorr")!=null)
	  wireColorr = (new Integer(getParameter("wireColorr"))).intValue();
	if (getParameter("wirecolorg")!=null)
	  wireColorg = (new Integer(getParameter("wireColorg"))).intValue();
	if (getParameter("wirecolorb")!=null)
	  wireColorb = (new Integer(getParameter("wireColorb"))).intValue();
	wireColor = new Color( wireColorr, wireColorg, wireColorb );
	initboard();
	initpieces();
	init_kicker();
	init_fast();
	cur_rotation = Transform.rotx( -Transform.pi/4 ).mul( Transform.roty( -Transform.pi/8 ) );
	zflip = new Transform( new Vec(1,0,0), new Vec(0,-1,0), new Vec(0,0,-1) );
	outfront = Transform.translateTransform( new Vec( 0, 0, 20 ) );
	rand = new Random( System.currentTimeMillis() );
    }

    // The main loop.  Render a frame, wait .4 seconds, move the current
    // piece, repeat.
    public void run() {
	Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

	while (kicker != null) {
	    repaint();
	    try {
		Thread.sleep( 400 );
	    } catch( Exception e ) {
		System.out.println( "Exception caught\n" );
	    }
	    step( ' ' );
	    fast_updated=0;
	}
    }

    // Move the piece down, check for intersections -- in short,
    // one unit of Tetris action.
    void step( int key ) {
	synchronized( steplock ) {
	    // get a piece if we don't have one already
	    if (currentpiece == null) {
		currentpiece = nextpiece();
		currentpiecex = boardx/2;
		currentpiecey = boardy-1;
		currentrot = 0;
		currentpiecesteps = 0;
	    }

	    // how have we moved?
	    int dx=0, dy=0, drot=0;
	    boolean down=false;
	    switch( key ) {
	      case 'h': case 'H':
		dx=-1; dy= 0; break;
	      case 'l': case 'L':
		dx= 1; dy= 0; break;
	      case 'j': case 'J':
		drot = 1; break;
	      case 'k': case 'K':
		drot = 3; break;
	      case ' ':
		dx=0; dy=-1; down=true; break;
	    }

	    //erase
	    if (currentpiecex != -1) {
		board.eraseat( currentpiece[currentrot],
			      currentpiecex, currentpiecey );
	    }

	    // move & check collision
	    boolean hit = board.intersectat( currentpiece[(currentrot+drot)%4],
					    currentpiecex+dx, currentpiecey+dy );
	    if (hit && !down) { dx = dy = drot = 0; hit=false; }

	    if (!(hit && down)) {
		currentpiecex += dx;
		currentpiecey += dy;
		currentrot = (currentrot+drot)%4;
		currentpiecesteps++;
	    }

	    // draw new
	    if (currentpiecex != -1) {
		board.drawat( currentpiece[currentrot],
			     currentpiecex, currentpiecey );
	    }

	    if (hit && down) {
		if (currentpiecesteps == 0)
		  board.reset();
		currentpiece = null;
		currentpiecex = currentpiecey = -1;
		board.cleanfullrows();
	    }
	}
    }

    // Here is the code for the fast 3D renderer thingy.  The original
    // was separated out into difference classes, and didn't use all
    // these nasty arrays.  So, it was really slow.  So I whipped up
    // this mess.

    // Have we updated the 3D line representation of the board?
    // (we only have to do this when a piece moves.)
    int fast_updated=0;

    // The max number of points we can transform
    int max_fpts = 1000;

    // The max number of lines we can transform
    int max_flines = 1000;

    // The 3D points, and how many.  Cycles x, y, z.
    double fpts[];
    public int n_fpts;

    // The projected points.  Alternates x, y.
    int pfpts[];

    // The lines -- an array of indices into the pointlist.
    public int flines[];
    public int n_flines;

    // Allocate arrays for holding points.  *3 is for storing triplets
    // consecutively, and *2 for pairs.
    void init_fast() {
	fpts = new double[max_fpts*3];
	pfpts = new int[max_fpts*2];
	flines = new int[max_flines*2];
    }

    // Generate a set of 3D lines for the current board.
    void update_fast() {
	Lineset ls = new Lineset( flines, n_flines, fpts, n_fpts );
	Sketch.update( ls, board );
	n_flines = ls.n_lines;
	n_fpts = ls.n_points;
    }

    // Draw the lines for the current board.
    // A nasty mess o' 3D.
    void fastrender( Transform trans, Graphics g ) {
	long time = System.currentTimeMillis();
	if (fast_updated==0) {
	    update_fast();
	    fast_updated = 1;
	}
	double xx=trans.bx.x, xy=trans.bx.y, xz=trans.bx.z;
	double yx=trans.by.x, yy=trans.by.y, yz=trans.by.z;
	double zx=trans.bz.x, zy=trans.bz.y, zz=trans.bz.z;
	double tx=trans.translate.x, ty=trans.translate.y, tz=trans.translate.z;
	int width=size().width, width2=width/2;
	int height=size().height, height2=height/2;
	for (int p=0,t=0; t<n_fpts; p+=2,t+=3) {
	    double x = fpts[t], y=fpts[t+1], z=fpts[t+2];
	    double ttx = xx*x + yx*y + zx*z + tx;
	    double tty = xy*x + yy*y + zy*z + ty;
	    double ttz = xz*x + yz*y + zz*z + tz;
	    int px = (int)((ttx/ttz)*width + width2);
	    int py = (int)((tty/ttz)*height + height2);
	    pfpts[p] = px;
	    pfpts[p+1] = py;
	}
	for (int e=0; e<n_flines; e+=2) {
	    g.drawLine( pfpts[flines[e]*2], pfpts[flines[e]*2+1],
		       pfpts[flines[e+1]*2], pfpts[flines[e+1]*2+1] );
	}
    }

    public boolean mouseDown(java.awt.Event evt, int x, int y) {
	lmousex = mousex = x;
	lmousey = mousey = y;
	repaint();
	return true;
    }

    public boolean mouseDrag( java.awt.Event evt, int x, int y ) {
	mousex = x;
	mousey = y;
	repaint();
	return true;
    }

    // pause/unpause the heartbeat thread.
    void pause_unpause() {
        if (threadSuspended)
            kicker.resume();
	else
            kicker.suspend();
        threadSuspended = !threadSuspended;
    }

    public boolean keyDown(java.awt.Event evt, int key) {
	switch( key ) {
	  case 'h': case 'H':
	  case 'l': case 'L':
	  case 'j': case 'J':
	  case 'k': case 'K':
          case ' ':
	    step( key ); break;
	  case 'p': case 27: pause_unpause(); break;
	}
	fast_updated = 0;
	repaint();
	return true;
    }


    void init_kicker() {
	if (kicker == null) {
	    kicker = new Thread( this );
	    kicker.start();
	    if (threadSuspended)
	      kicker.suspend();
	}
    }

    void kill_kicker() {
	kicker.stop();
	kicker = null;
    }

    void start_kicker() {
	if (!threadSuspended)
	  init_kicker();
    }

    void stop_kicker() {
	if (!threadSuspended&&kicker!=null)
	    kill_kicker();
    }

    public void start() { start_kicker();  }
    public void stop() { stop_kicker(); }

    // Stuff for double buffering.
    Image im = null;
    Graphics offscreen = null;

    // Modified update() for double buffering.
    public void update(Graphics g) {
	if (offscreen == null)
	  g.clearRect(0, 0, size().width, size().height);
	paint(g);
    }

    // Set the 3D transform, do the rendering.
    public void paint( Graphics onscreen ) {
	Graphics g=onscreen;

	try {
	    if (im == null)
	      im = createImage( size().width, size().height );

	    if (offscreen == null)
	      offscreen = im.getGraphics();
	} catch( Exception e ) {
	    // double-buffering not available
	    offscreen = null;
	    im = null;
	}

	g = offscreen==null?onscreen:offscreen;

	setForeground( wireColor );
	setBackground( Color.blue );
	g.clipRect( 0, 0, size().width, size().height );
	onscreen.setColor( wireColor );
	onscreen.clipRect( 0, 0, size().width, size().height );
	if (offscreen != null) {	
	    offscreen.setColor( Color.green );
	    offscreen.clipRect( 0, 0, size().width, size().height );
	}

	g.setColor( Color.black );
	g.fillRect(0, 0, size().width, size().height);
	g.setColor( wireColor );

	int mx = mousex-lmousex;
	int my = mousey-lmousey;
	double yaw = 2*Transform.pi*((double)mx/size().width);
	double pitch = 2*Transform.pi*((double)my/size().height);
	lmousex = mousex;
	lmousey = mousey;

	Transform pitcht = Transform.rotx( pitch );
	Transform yawt = Transform.roty( -yaw );
	cur_rotation = cur_rotation.mul( pitcht.mul( yawt ) );
	Transform t = zflip.mul( cur_rotation.mul( outfront ) );

	synchronized( steplock ) {
	    fastrender( t, g );
	}

	// blit maybe
	if (offscreen != null) {
	    onscreen.drawImage( im, 0, 0, this );
	}
    }
}
