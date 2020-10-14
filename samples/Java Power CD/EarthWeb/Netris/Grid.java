/***********************************************************************
Copyright Notice: This source code is (C) Copyright 1995-96, EarthWeb LLC.,
All Rights Reserved. Distribution of this document or it's resulting
compiled code is granted for non-commercial use, with prior approval of
EarthWeb LLC. Distribution of this document or its resulting compiled
code, for commercial use, is granted only with prior written approval of
EarthWeb, LLC. For information, send email to info@earthweb.com.
***********************************************************************/

// $Id: Grid.java,v 1.3 1996/04/16 21:00:52 greg Exp $

/*

A Grid is a bitmap into which you can draw other Grids.
If you 'draw' to a point that is outside the Grid, it auto-extends
itself (this can be turned off).  There is also a 'deleterow' method
which removes a row of bits.  So it's kind of like a dynamic bitmap.

The 'bits' themselves are ints, so you can actually use any value you
want.  This was an afterthought.

*/

class Grid
{
    // Initially, we are 1x1.
    static int initsize = 1;

    // The current size.
    public int sizex, sizey;

    // The array representing the bitmap.  It is sizex * sizey.
    int bits[][];

    // Do we auto-extend?
    boolean auto_extend = true;

    // export this value.
    public int sizey_get() { return sizey; }

    // When subclassing, put code in here to be run whenever
    // we are resized.
    void prepare() {}

    // I don't like typing Math.min().
    int min( int a, int b ) { return a<b?a:b; }
    int max( int a, int b ) { return a>b?a:b; }

    // set the initial size.
    public Grid() {
	sizex = sizey = initsize;
	bits = null;
	resize( sizex, sizey );
    }

    // A Grid of a given size.
    public Grid( int x, int y ) {
	sizex = x; sizey = y;
	bits = null;
	resize( sizex, sizey );
    }

    // Duplicate this Grid.
    public Grid( Grid g ) {
	sizex = g.sizex; sizey = g.sizey;
	bits = new int[sizey][sizex];
	g.copybits( bits );
    }

    // Copy all the bits from newbits into us.  Operates only
    // on the intersections of the two Grids.
    void copybits( int newbits[][] ) {
	for (int i=0; i<min(bits.length,newbits.length); ++i)
	  for (int j=0; j<min(bits[i].length,newbits[i].length); ++j)
	    newbits[i][j] = bits[i][j];
    }

    // Resize to the given size.  Call prepare().
    void resize( int sizex, int sizey ) {
	int newbits[][] = new int[sizey][sizex];
	if (bits != null)
	  copybits( newbits );
	bits = newbits;
	this.sizex = sizex; this.sizey = sizey;
	prepare();
    }

    // Set the bit at (x,y) to the value b.  If (x,y) is outside
    // the Grid, we autoextend if that is turned on.  We never
    // signal an error.
    public void set( int x, int y, int b ) {
	if (x<0 || y<0)
	  return;
	if (x>=sizex || y>=sizey) {
	    if (auto_extend)
	      resize( max(x+1,sizex), max(y+1,sizey) );
	    else
	      return;
	}
	bits[y][x] = b;
    }

    // Find the value at the given position.  Autoextend, perhaps.
    public int get( int x, int y ) {
	if (x<0 || y<0 || x>=sizex || y>=sizey)
	  return 0;
	else
	  return bits[y][x];
    }

    // Set or clear any bit which is set in 'g'; 'ed' controls
    // whether we set or clear.
    public void erasedrawat( Grid g, int x, int y, int ed ) {
	for (int i=0; i<g.sizex; ++i)
	  for (int j=0; j<g.sizey; ++j)
	    if (g.get( i, j )==1)
	      set( x+i, y+j, ed );
    }

    // Do the two grids have a bit on in common?
    public boolean intersectat( Grid g, int x, int y ) {
	boolean haha = false;
	for (int i=0; i<g.sizex; ++i)
	  for (int j=0; j<g.sizey; ++j)
	    if (g.get( i, j )==1 && get( x+i, y+j )==1)
	      haha = true;
	return haha;
    }

    // Convenience functions.
    public void drawat( Grid g, int x, int y ) { erasedrawat( g, x, y, 1 ); }
    public void eraseat( Grid g, int x, int y ) { erasedrawat( g, x, y, 0 ); }

    // Construct a new Grid which is like us, only
    // rotated 90 degrees.
    public Grid rot() {
	Grid g = new Grid( sizey, sizex );
	for (int i=0; i<sizex; ++i)
	  for (int j=0; j<sizey; ++j)
	    g.set( sizey-1-j, i, get( i, j ) );
	return g;
    }

    // Remove the given row from us.  This doesn't just clear
    // the bits; it actually moves everything down.
    void deleterow( int y ) {
	for (; y<sizey-1; ++y)
	  bits[y] = bits[y+1];
	bits[sizey-1] = new int[sizex];
	set( 0, sizey-1, 1 );
	set( sizex-1, sizey-1, 1 );
	prepare();
    }

    // Is this row full?
    boolean fullrow( int y ) {
	for (int x=1; x<sizex-1; ++x)
	  if (get(x,y)==0) {
	      return false;
	  }
	return true;
    }

    // Remove all rows that are full.
    public void cleanfullrows() {
	int count=0;
	for (int y=1; y<sizey; ++y)
	  while (fullrow(y)) {
	    deleterow(y);
	    count++;
	}
    }
}

/*

BoardGrid is a Grid which is specialized to have walls and a
floor at all times.  It also has auto-extending off.

*/
class BoardGrid extends Grid
{
    public BoardGrid( int x, int y ) {
	super( x, y );
	auto_extend = false;
	prepare();
    }

    void prepare() {
	for (int y=0; y<sizey; ++y) {
	    set( 0, y, 1 );
	    set( sizex-1, y, 1 );
	}
	for (int x=0; x<sizex; ++x)
	  set( x, 0, 1 );
    }

    public void reset() {
	for (int x=0; x<sizex; ++x)
	  for (int y=0; y<sizey; ++y)
	    set( x, y, 0 );
	prepare();
    }
}
