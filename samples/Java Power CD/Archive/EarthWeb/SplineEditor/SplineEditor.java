/***********************************************************************
Copyright Notice: This source code is (C) Copyright 1995-96, EarthWeb LLC.,
All Rights Reserved. Distribution of this document or it's resulting
compiled code is granted for non-commercial use, with prior approval of
EarthWeb LLC. Distribution of this document or its resulting compiled
code, for commercial use, is granted only with prior written approval of
EarthWeb, LLC. For information, send email to info@earthweb.com.
***********************************************************************/

/*
 * Framework taken from SampleApplet.java by Arthur van Hoff, Sun.
 * greg@earthweb.com
 */

import java.awt.Graphics;

/**
 * A sample applet that shows off some features and
 * prints lots of debugging statements.
 * @author 	Arthur van Hoff
 * @version 	1.5f, 14 Mar 1995
 */
public class SplineEditor extends java.applet.Applet {
    /**
     * java.applet.Applet methods
     */

    int bootx[], booty[], current=-1;
    int numpoints, currentx, currenty;

    void resetpoints()
      {
	numpoints = 2;
	bootx[0] = booty[0] = 10;
	bootx[1] = booty[1] = 90;
      }

    public void init() {
	bootx = new int[20];
	booty = new int[20];
	resetpoints();
    }
    public void start() {
    }
    public void stop() {
    }
    public void destroy() {
    }

    double len( int x0, int y0, int x1, int y1 )
      {
	int x = x1-x0, y = y1-y0;
	double a;
	a = java.lang.Math.sqrt( (double)((double)x*x + y*y) );
	return a;
      }

    int whichClosest( int x, int y ) {
      double best_dist=0, dist=0;
      int best=-1;
      for (int i=0; i<numpoints; ++i) {
	dist = (int)len(bootx[i],booty[i],x,y);	
	if (best==-1 || dist<best_dist) {
	  best_dist=dist;
	  best=i;
	}
      }
      return best;
    }

    void delpoint( int x, int y )
      {
	int c = whichClosest( x, y );
	for (int i=c; i<numpoints-1; ++i)
	  {
	    bootx[i] = bootx[i+1];
	    booty[i] = booty[i+1];
	  }
	numpoints--;
      }

    double dot( int x0, int y0, int x1, int y1 )
      {
	return x0*x1 + y0*y1;
      }

    /* returns 0 for infinity */
    double dist_to_line( int px, int py, int ax, int ay, int bx, int by )
      {
	int bpx = ax-(by-ay), bpy = ay+(bx-ax);
	if (dot(px-ax,py-ay,bx-ax,by-ay)<0 || dot(px-bx,py-by,ax-bx,ay-by)<0)
	  return 0;
	else return
	  java.lang.Math.abs(
	    dot(px-ax,py-ay,bpx-ax,bpy-ay)/len(ax,ay,bpx,bpy));
      }

    void addpoint( int x, int y )
      {
	int best=-1;
	double dist=0, best_dist=0; 
	if (numpoints>=20)
	  return;
	for (int i=0; i<numpoints-1; ++i)
	  {
	    dist = dist_to_line( x, y, bootx[i], booty[i],
				bootx[i+1], booty[i+1] );
	    if (dist==0) continue;
	    if (best == -1 || dist<best_dist)
	      {
		best = i;
		best_dist = dist;
	      }
	  }
	best++;
	for (int i=numpoints; i>best; --i)
	  {
	    bootx[i] = bootx[i-1];
	    booty[i] = booty[i-1];
	  }
	bootx[best] = x;
	booty[best] = y;
	numpoints++;
	repaint();
      }

    int interpolate( double t, int a, int b )
      {
	return (int)(a + t * (b-a) );
      }

    int interpolate_many( double t, int v[], int a, int b )
      {
	if (a==b) return v[a];
	else return interpolate( t,
				interpolate_many( t, v, a, b-1 ),
				interpolate_many( t, v, a+1, b ) );
      }

    void spline_( Graphics g, int n )
      {
	int x, y;
	for (double t=0; t<1.0f; t+=1.0f/(float)n)
	  {
	    x = interpolate_many( t, bootx, 0, numpoints-1 );
	    y = interpolate_many( t, booty, 0, numpoints-1 );
	    g.drawLine( x, y, x, y );
	  }
      }

    void spline( Graphics g, int nt )
      {
	int lastx=-1, lasty=-1;
	int xs[] = new int[numpoints], ys[] = new int[numpoints];
	for (double t=0; t<1.0f; t+=1.0f/(float)nt)
	  {
	    for (int i=0; i<numpoints; ++i)
	      {
		xs[i] = bootx[i];
		ys[i] = booty[i];
	      }
	    for (int n=numpoints; n>1; --n)
	      for (int i=0; i<n-1; ++i)
		{
		  xs[i] = interpolate( t, xs[i], xs[i+1] );
		  ys[i] = interpolate( t, ys[i], ys[i+1] );
		}
	    if (lastx==-1)
	      g.drawLine( xs[0], ys[0], xs[0], ys[0] );
	    else
	      g.drawLine( lastx, lasty, xs[0], ys[0] );
	    lastx = xs[0]; lasty = ys[0];
	  }
      }

    /**
     * Paint...
     */

    public void paint(Graphics g) {
	g.drawRect(0, 0, size().width - 1, size().height - 1);
	for (int i=0; i<numpoints; ++i)
	  g.fillRect( bootx[i]-1, booty[i]-1, 3, 3 );
	for (int i=0; i<numpoints-1; ++i)
	  g.drawLine( bootx[i], booty[i], bootx[i+1], booty[i+1] );
	spline( g, 40 );
    }

    /**
     * Mouse methods
     */

    public boolean mouseDown(java.awt.Event evt, int x, int y) {
	current = whichClosest( x, y );
	bootx[current] = x;
	booty[current] = y;
	repaint();
	requestFocus();
	return true;
    }

    public boolean mouseDrag(java.awt.Event evt, int x, int y) {
      currentx = x;
      currenty = y;
      if (current != -1) {
	bootx[current] = x-4;
	booty[current] = y-4;
      }
      repaint();
	return true;
    }

    public boolean mouseUp(java.awt.Event evt, int x, int y) {
	current = -1;
	return true;
    }

    public boolean mouseMove(java.awt.Event evt, int x, int y) {
      currentx = x;
      currenty = y;
	return true;
    }

    public boolean mouseEnter(java.awt.Event evt, int x, int y ) {
	return true;
    }
    public boolean mouseExit(java.awt.Event evt, int x, int y ) {
	return true;
    }

    /**
     * Key methods
     */

    public boolean keyDown(java.awt.Event evt,  int key )
      {
	switch( key )
	  {
	  case 'a': case 'A':
	    addpoint( currentx, currenty );
	    break;
	  case 'd': case 'D':
	    delpoint( currentx, currenty );
	    break;
	  case 'r': case 'R':
	    resetpoints();
	    break;
	  }
	repaint();
	return true;
      }

    /**
     * Focus methods
     */

    public void gotFocus() {
    }
    public void lostFocus() {
    }
}
