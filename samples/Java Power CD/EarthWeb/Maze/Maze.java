/***********************************************************************
Copyright Notice: This source code is (C) Copyright 1995-96, EarthWeb LLC.,
All Rights Reserved. Distribution of this document or it's resulting
compiled code is granted for non-commercial use, with prior approval of
EarthWeb LLC. Distribution of this document or its resulting compiled
code, for commercial use, is granted only with prior written approval of
EarthWeb, LLC. For information, send email to info@earthweb.com.
***********************************************************************/

// $Id: Maze.java,v 1.2 1996/04/16 20:58:51 greg Exp $

import java.util.Random;
import java.io.*;

public class Maze
{
    public int sx, sy;
    int ax[], ay[];
    boolean walls[][][];
    boolean used[][];
    boolean solvepath[][];
    int an;
    int numFree;
    static int xdir[] = { 1, 0, -1, 0 }, ydir[] = { 0, 1, 0, -1 };
    static int opp[] = { 2, 3, 0, 1 };
    Random rand;
    float turn, punt;

    void sp( String s ) { System.out.println( s ); }

    Maze( int sx, int sy, float turn, float punt ) {
	this.sx = sx;
	this.sy = sy;
	ax = new int[1];
	ay = new int[1];
	an = 0;
	walls = new boolean[sx+1][sy+1][2];
	for (int i=0; i<sx+1; ++i)
	  for (int j=0; j<sy+1; ++j)
	    walls[i][j][0] = walls[i][j][1] = true;
	used = new boolean[sx][sy];
	for (int i=0; i<sx; ++i)
	  for (int j=0; j<sy; ++j)
	    used[i][j] = false;
	solvepath = new boolean[sx][sy];
	numFree = sx*sy;
	rand = new Random();
	this.turn = turn;
	this.punt = punt;
	generate();
    }

    int [] realloc( int a[] ) {
	int na[] = new int[a.length*2];
	for (int i=0; i<a.length; ++i)
	  na[i] = a[i];
	return na;
    }

    int addActive( int x, int y ) {
	if (an>=ax.length) {
	    ax = realloc( ax );
	    ay = realloc( ay );
	}
	ax[an] = x;
	ay[an] = y;
	numFree--;
	used[x][y] = true;
	return an++;
    }

    void removeActive( int i ) {
	--an;
	ax[i] = ax[an];
	ay[i] = ay[an];
    }

    int joe=0;
    int nextDir() {
	return joe++%4;
	//return (Math.abs(rand.nextInt()))%4;
    }

    void removeWall( int x, int y, int dir ) {
	if (dir==0)
	  removeWall( x+1, y, 2 );
	else if (dir==1)
	  removeWall( x, y+1, 3 );
	else
	  walls[x][y][dir-2] = false;
    }

    boolean isWall( int x, int y, int dir ) {
	if (dir==0)
	  return isWall( x+1, y, 2 );
	else if (dir==1)
	  return isWall( x, y+1, 3 );
	else
	  return walls[x][y][dir-2];
    }

    public boolean getWall( int x, int y, int dir ) {
	if (dir==0)
	  return walls[x+1][y][0];
	else if (dir==1)
	  return walls[x][y+1][1];
	else
	  return walls[x][y][dir-2];
    }

    public void asciiDraw( OutputStream out ) {
	PrintStream p = new PrintStream( out );
	String s = "";
	for (int y=0; y<sy; ++y) {
	    s = "";
	    for (int x=0; x<sx; ++x)
	      s += getWall( x, y, 3 ) ? "****" : "*   ";
	    s += "*";
	    p.println( s );
	    s = "";
	    for (int x=0; x<sx; ++x)
	      s += (getWall( x, y, 2 ) ? "* " : "  ") + (solvepath[x][y] ? "+ " : "  ");
	    s += getWall( sx-1, y, 0 ) ? "*" : " ";
	    p.println( s );
	}
	s = "";
	for (int x=0; x<sx; ++x)
	  s += getWall( x, sy-1, 1 ) ? "****" : "*   ";
	s += "*";
	p.println( s );
    }

    boolean roll( float chance ) {
	return (Math.abs(rand.nextInt())%1000)/1000.0 < chance;
    }

    void generate() {
	int x=sx/2, y=sy/2;
	int cur = addActive( x, y );
	int dir=nextDir();
	while (numFree > 0) {
	    if (roll( turn ))
	      dir = nextDir();
	    //sp( "XY " + x + " " + y );
	    //String zub[] = { "right", "down", "left", "up" };
	    int c=0;
	    int nx=0, ny=0;
	    while (c<4) {
		nx = x + xdir[dir];
		ny = y + ydir[dir];
		if (nx>=0 && ny>=0 && nx<sx && ny<sy && !used[nx][ny])
		  break;
		c++;
		dir = nextDir();
	    }
	    //sp( "c " + c + " " + dir + " " + zub[dir] + " " + nx + " " + ny );
	    if (c>=4)
	      removeActive( cur );
	    else {
		removeWall( x, y, dir );
		cur = addActive( nx, ny );
	    }
	    if (c>=4 || roll( punt )) {
		cur = (Math.abs(rand.nextInt())%an);
		x = ax[cur];
		y = ay[cur];
	    } else {
		x = nx;
		y = ny;
	    }
	}
    }

    int stackx[] = new int[1], stacky[] = new int[1];
    int stackdir[] = new int[1], stackfrom[] = new int[1], stackp;
    void solve( Callback c ) {
	stackp=0;
	int x=0, y=sy-1, gx=sx-1, gy=0;
	int dir=0, from=-1;
	while (x != gx || y != gy) {
	    while (dir<4) {
		if (dir==from || isWall( x, y, dir ))
		  dir++;
		else {
		    // Dynamically extend the stack as
		    // we add stuff.  The '+1' means
		    // make sure there is an extra one
		    // at the end.
		    if (stackp+1 >= stackx.length) {
			stackx = realloc( stackx );
			stacky = realloc( stacky );
			stackdir = realloc( stackdir );
			stackfrom = realloc( stackfrom );
		    }
		    stackx[stackp] = x;
		    stacky[stackp] = y;
		    stackdir[stackp] = dir;
		    stackfrom[stackp] = from;
		    stackp++;
		    x = x + xdir[dir];
		    y = y + ydir[dir];
		    from = opp[dir];
		    dir = 0;
		    if (c != null) c.callback();
		    break;
		}
	    }
	    if (dir >= 4) {
		stackp--;
		if (stackp<0) throw new RuntimeException( "yikes" );
		x = stackx[stackp];
		y = stacky[stackp];
		dir = stackdir[stackp]+1;
		from = stackfrom[stackp];
		if (c != null) c.callback();
	    }
	}

	// Stuff in the last spot in the path: the endpoint
	// (we made sure above that there would be room for
	// one more.
	stackx[stackp] = gx;
	stacky[stackp] = gy;
	stackp++;
	if (c != null) c.callback();

	for (int i=0; i<stackp; ++i)
	  solvepath[stackx[i]][stacky[i]] = true;
	solvepath[x][y] = true;
    }
}
