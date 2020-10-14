/***********************************************************************
Copyright Notice: This source code is (C) Copyright 1995-96, EarthWeb LLC.,
All Rights Reserved. Distribution of this document or it's resulting
compiled code is granted for non-commercial use, with prior approval of
EarthWeb LLC. Distribution of this document or its resulting compiled
code, for commercial use, is granted only with prior written approval of
EarthWeb, LLC. For information, send email to info@earthweb.com.
***********************************************************************/

// $Id: Sketch.java,v 1.4 1996/04/16 21:00:52 greg Exp $

/*

Generate a 3D wire structure that represents a Grid.  Specifically,
generate a line segment at the border of any two cells whose values
differ.  This causes touching pieces to seem to be connected.

This is an all-static class.

*/

// Bugs: the x-direction segments don't share points with the
// y-direction segments.

class Sketch
{
    // Number of points/lines we have generated.
    static int n_points, n_lines;

    // The x,y,z values of the points, packed in order into an array.
    static double points[]=null;

    // The indices into the point array for the endpoints of each line,
    // packed in pairs into an array.
    static int lines[]=null;

    // Shift everything in space by these values.
    static int xgain=-5, ygain=-7, zgain=0;

    // Add a line between the given points to the arrays.  Put all six
    // coordinates into the point array, and the indices to these points
    // into the line array.
    static void drop_line( int sx, int sy, int sz, int ex, int ey, int ez ) {
	sx += xgain; sy += ygain;
	ex += xgain; ey += ygain;
	sz += zgain; ez += zgain;
	int start_points = n_points;
	points[n_points++] = sx;
	points[n_points++] = sy;
	points[n_points++] = sz;
	points[n_points++] = ex;
	points[n_points++] = ey;
	points[n_points++] = ez;
	lines[n_lines++] = start_points/3;
	lines[n_lines++] = start_points/3+1;
    }

    // Generate the actual line segments.  A segment is generated
    // at the border of any two cells which have different values.
    static public void update( Lineset ls, Grid g ) {
	n_points = 0;
	n_lines = 0;
	points = ls.points;
	lines = ls.lines;

	for (int x=0; x<=g.sizex; ++x) {
	    boolean on=false;
	    int sx=-1, sy=-1;
	    for (int y=0; y<g.sizey+1; ++y) {
		int left, right;
		if (y<g.sizey) {
		    left=(x==0?0:g.get(x-1,y));
		    right=(x==g.sizex?0:g.get(x,y));
		} else {
		    left = right = 0;
		}
		if (!on) {
		    if (left != right) { // start
			on = true;
			sx = x;
			sy = y;
			drop_line( x, y, 0, x, y, 1 );
		    }
		} else {
		    if (left == right) { // end
			on = false;
			int ex = x;
			int ey = y;
			// This didn't work
			//drop_line( sx, sy, ex, ey );
			drop_line( sx, sy, 0, x, y, 0 );
			drop_line( sx, sy, 1, x, y, 1 );
			drop_line( x, y, 0, x, y, 1 );
		    }
		}
	    }
	}

	for (int y=0; y<=g.sizey; ++y) {
	    boolean on=false;
	    int sy=-1, sx=-1;
	    for (int x=0; x<g.sizex+1; ++x) {
		int left, right;
		if (x<g.sizex) {
		    left=(y==0?0:g.get(x,y-1));
		    right=(y==g.sizey?0:g.get(x,y));
		} else {
		    left = right = 0;
		}
		if (!on) {
		    if (left != right) { // start
			on = true;
			sy = y;
			sx = x;
		    }
		} else {
		    if (left == right) { // end
			on = false;
			int ey = y;
			int ex = x;
			// This didn't work
			//drop_line( sy, sx, ey, ex );
			drop_line( sx, sy, 0, x, y, 0 );
			drop_line( sx, sy, 1, x, y, 1 );
		    }
		}
	    }
	}

	ls.n_points = n_points;
	ls.n_lines = n_lines;
	points = null;
	lines = null;
    }
}
