/***********************************************************************
Copyright Notice: This source code is (C) Copyright 1995-96, EarthWeb LLC.,
All Rights Reserved. Distribution of this document or it's resulting
compiled code is granted for non-commercial use, with prior approval of
EarthWeb LLC. Distribution of this document or its resulting compiled
code, for commercial use, is granted only with prior written approval of
EarthWeb, LLC. For information, send email to info@earthweb.com.
***********************************************************************/

// $Id: Figure.java,v 1.3 1996/04/16 20:55:23 greg Exp $

import java.lang.*;
import java.awt.*;
import java.io.*;
import java.net.*;

/*

A Figure is a generic line drawing.  It consists of a list of line
segments in the plane.  It can be drawn to a Graphics object
(with transformations), intersected with another Figure, read from a
stream, and written to a stream.

*/

public class Figure
{
    Color color = null;
    int lines[][][];
    int n_lines;

    Figure() {
	this.lines = new int[2][2][2];
	n_lines = 2;
    }

    Figure( int lines[][][] ) {
	this.lines = lines;
	n_lines = lines.length;
    }

    // Translate and rotate the lines.
    public Figure transform( int ox, int oy, double angle ) {
	double s = Math.sin( angle );
	double c = Math.cos( angle );
	int tlines[][][] = new int[n_lines][2][2];
	for (int i=0; i<n_lines; ++i) {
	    int sx=lines[i][0][0], sy=lines[i][0][1];
	    int ex=lines[i][1][0], ey=lines[i][1][1];
	    int sxt = (int)(ox+sx*c-sy*s);
	    int syt = (int)(oy+sx*s+sy*c);
	    int ext = (int)(ox+ex*c-ey*s);
	    int eyt = (int)(oy+ex*s+ey*c);
	    tlines[i][0][0] = sxt;
	    tlines[i][0][1] = syt;
	    tlines[i][1][0] = ext;
	    tlines[i][1][1] = eyt;
	}
	return new Figure( tlines );
    }

    // The default rendering routine is to simply
    // draw all the lines.
    public void render( Graphics g ) {
	if (color != null) g.setColor( color );
	for (int i=0; i<n_lines; ++i)
	  g.drawLine( lines[i][0][0], lines[i][0][1],
		     lines[i][1][0], lines[i][1][1] );
    }

    // display a line.
    void pline( int a[][] ) {
	System.out.println( ""+a[0][0]+" "+a[0][1]+" "+a[1][0]+" "+a[1][1] );
    }

    // intersectLine1() checks for intersection between one line segment and
    // the infinte line containing the other line segment.
    public boolean intersectLine1( int a[][], int b[][] ) {
	int sx=a[0][0], sy=a[0][1], ex=a[1][0], ey=a[1][1];
	return
	  (((sy-ey)*(b[0][0]-sx) + (ex-sx)*(b[0][1]-sy)) > 0) !=
	    (((sy-ey)*(b[1][0]-sx) + (ex-sx)*(b[1][1]-sy)) > 0);
    }

    // intersectLine() checks for intersection between two line segments.
    public boolean intersectLine( int a[][], int b[][] ) {
	if (intersectLine1( a, b ) && intersectLine1( b, a )) {
	    return true;
	}
	return false;
    }

    // Check for intersection between the given line and the lines
    // in this Figure.
    public boolean intersectLines( int b[][] ) {
	for (int i=0; i<n_lines; ++i)
	  if (intersectLine( lines[i], b ))
	    return true;
	return false;
    }

    // Check for intersection between the given Figure and us.
    public boolean intersectFigure( Figure f ) {
	for (int i=0; i<f.n_lines; ++i)
	  if (intersectLines( f.lines[i] ))
	    return true;
	return false;
    }

    // Clear out all the lines.
    public void reset() {
	n_lines = 0;
    }

    // Add a line, extending the array if necessary
    public void addLine( int sx, int sy, int ex, int ey ) {
	if (n_lines >= lines.length) {
	    int newlines[][][] = new int[lines.length*2][2][2];
	    for (int i=0; i<lines.length; ++i)
	      newlines[i] = lines[i];
	    lines = newlines;
	}
	lines[n_lines][0][0] = sx;
	lines[n_lines][0][1] = sy;
	lines[n_lines][1][0] = ex;
	lines[n_lines][1][1] = ey;
	n_lines++;
    }

    // Remove a line.
    public void removeLine( int which ) {
	int tmp[][] = lines[which];
	lines[which] = lines[--n_lines];
	lines[n_lines] = tmp;
    }

    // Calculate the distance between two points.
    double dist( double x0, double y0, double x1, double y1 ) {
	x1 -= x0; y1 -= y0;
	return Math.sqrt( x1*x1 + y1*y1 );
    }

    // Calculate the distance from (x,y) to line.
    double distToLine( double x, double y, int line[][] ) {
	if ((line[1][0]-line[0][0])*(x-line[0][0])
	    + (line[1][1]-line[0][1])*(y-line[0][1]) < 0) {
	    return dist( x, y, (double)line[0][0], (double)line[0][1] );
	}
	else if ((line[0][0]-line[1][0])*(x-line[1][0])
		 + (line[0][1]-line[1][1])*(y-line[1][1]) < 0) {
	    return dist( x, y, (double)line[1][0], (double)line[1][1] );
	}
	else {
	    int ax = line[1][0] - line[0][0];
	    int ay = line[1][1] - line[0][1];
	    double bx = x - line[0][0];
	    double by = y - line[0][1];
	    return Math.abs( (-ay*bx + ax*by)/Math.sqrt(ax*ax+ay*ay) );
	}
    }

    // Find the nearest line in this figure to (x,y).
    public int nearestLine( double x, double y ) {
	int best=0;
	double dist = distToLine( x, y, lines[0] );

	for (int i=0; i<n_lines; ++i) {
	    double d = distToLine( x, y, lines[i] );

	    if (best==-1 || d<dist) {
		best = i;
		dist = d;
	    }
	}
	return best;
    }

    // Write this Figure to a stream.
    public void write( DataOutputStream out ) throws IOException {
	out.writeInt( n_lines );
	for (int i=0; i<n_lines; ++i) {
	    out.writeInt( lines[i][0][0] );
	    out.writeInt( lines[i][0][1] );
	    out.writeInt( lines[i][1][0] );
	    out.writeInt( lines[i][1][1] );
	}
    }

    // Write this Figure to a file.
    public void write( String fname ) {
	try {
	    FileOutputStream fout = new FileOutputStream( fname );
	    DataOutputStream out = new DataOutputStream( fout );
	    write( out );
	    fout.close();
	    System.out.println( "Wrote." );
	} catch( Exception e ) {
	    System.out.println( "Write: " + e );
	}
    }

    // Read this Figure from a stream.
    public void read( DataInputStream in ) throws IOException {
	reset();
	int n = in.readInt();
	for (int i=0; i<n; ++i) {
	    int sx = in.readInt();
	    int sy = in.readInt();
	    int ex = in.readInt();
	    int ey = in.readInt();
	    addLine( sx, sy, ex, ey );
	}
    }

    // Read this Figure from a file.
    public void read( String fname ) {
	try {
	    FileInputStream fin = new FileInputStream( fname );
	    DataInputStream in = new DataInputStream( fin );
	    read( in );
	    System.out.println( "Read." );
	    fin.close();
	} catch( Exception e ) {
	    System.out.println( "Read: " + e );
	}
    }

    // Read this Figure from a URL.
    public void read( URL url ) {
	try {
	    URLConnection uc = url.openConnection();
	    InputStream in = uc.getInputStream();
	    read( new DataInputStream( in ) );
	} catch( IOException e ) {
	    System.out.println( "read " + e );
	}
    }
}
