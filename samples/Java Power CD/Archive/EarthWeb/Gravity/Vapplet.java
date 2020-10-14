/***********************************************************************
Copyright Notice: This source code is (C) Copyright 1995-96, EarthWeb LLC.,
All Rights Reserved. Distribution of this document or it's resulting
compiled code is granted for non-commercial use, with prior approval of
EarthWeb LLC. Distribution of this document or its resulting compiled
code, for commercial use, is granted only with prior written approval of
EarthWeb, LLC. For information, send email to info@earthweb.com.
***********************************************************************/

// $Id: Vapplet.java,v 1.4 1996/04/16 20:55:23 greg Exp $

import java.awt.*;
import java.applet.*;

/*

Subclass from Vapplet instead of Applet, and you can pan around a
large, virtual screen.  Your applet can use normal drawing methods,
pretending the Applet is very large, when in fact only a portion of it
is visible.  The virtuality should be transparent to your applet.

Vapplet currently has some update troubles when the applet window is
being moved around the desktop; you often get a double image.

The default is to allow SHIFT-mouse events drag the virtual screen around.

Vapplet stands for Virtual Applet, since it originally was simply an
Applet which contained the virtual screen, but it since has acquired
double buffering as well.

*/

public class Vapplet extends Applet
{
    // The delta by which the virtual screen is panned.
    private int transx=0, transy=0;

    // The last position of the mouse, for manual panning.
    private int lastmousex, lastmousey;

    // The size of the large, virtual screen.
    private int vsizex=0, vsizey=0;

    // Are we currently scrolling (panning) around the virtual image?
    private boolean scrolling=false;

    // Are we double buffering?
    private boolean doubleBuffering=false;

    // Are we doing the virtual thing, or is it disabled?
    private boolean virtual;

    // Offscreen stuff for double buffering.
    Image offscreenImage = null;
    Graphics offscreenGraphics = null;

    // I didn't feel like typing Math.min().
    private int min( int a, int b ) { return a < b ? a : b; }
    private int max( int a, int b ) { return a > b ? a : b; }

    // Turn double buffering on or off.  If on, allocate necessary
    // offscreen things.
    public void setDoubleBuffering( boolean b ) {
	if (b==doubleBuffering)
	  return;
	doubleBuffering = b;
	if (doubleBuffering) {
	    try {
		if (offscreenImage == null)
		  offscreenImage = createImage( size().width, size().height );
		if (offscreenGraphics == null)
		  offscreenGraphics = offscreenImage.getGraphics();
	    } catch( Exception e ) {
		System.out.println( "Vapplet: can't do double buffering: "+e );
		doubleBuffering = false;
		offscreenImage = null;
		offscreenGraphics = null;
	    }
	} else {
	    offscreenImage = null;
	    offscreenGraphics = null;
	}
    }

    // Enable/disable the virtual screen feature.
    public void setVirtual( boolean b ) {
	if (b==virtual)
	  return;
	virtual = b;
    }

    // If you want to draw something that DOESN'T pan around with
    // the virtual screen, override this and put that drawing code
    // in here.  Good for, say, a nice floating logo.
    public void overlayPaint( Graphics g ) {
    }

    // Work that virtual magic, drawing-wise.
    // To get the virtual screen effect, we translate the Graphics
    // object so that our drawing operations happen in a different
    // place.  Draw the overlay stuff, however, in an UN-translated
    // state.  Do double buffering too, if that's on.
    public void update(Graphics g) {
	Graphics draw = doubleBuffering ? offscreenGraphics : g;
	draw.setColor(getBackground());
	draw.fillRect(0, 0, size().width, size().height);
	//draw.setColor(getForeground());
	draw.setColor( g.getColor() );

	// Do virtual part
	if (virtual)
	  draw.translate( transx, transy );
	paint( draw );

	// Do overlay part
	if (virtual)
	  draw.translate( -transx, -transy );
	overlayPaint( draw );
	if (virtual)
	  draw.translate( transx, transy );

	if (doubleBuffering) {
	    g.drawImage( offscreenImage, 0, 0, this );
	    draw.translate( -transx, -transy );
	}
    }

    // Duh.
    public void setVirtualSize( int x, int y ) {
	vsizex = x;
	vsizey = y;
    }

    // Translate a given point expressed in the virtual coordinate
    // space into a point expressed in real screen coordinates.
    public Point screenCoords( Point p ) {
	return new Point( p.x+transx, p.y+transy );
    }

    // Change the pannage of the virtual screen.  Don't let it
    // go too far.
    public void translate( int x, int y ) {
	transx += x;
	transy += y;
	if (transx>0) transx=0;
	if (transy>0) transy=0;
	if (transx<size().width-vsizex)
	  transx = size().width-vsizex;
	if (transy<size().height-vsizey)
	  transy = size().height-vsizey;
    }

    // Here is the other magic part of Vapplet.  Translate all events
    // so that they think their coordinates are different from what they
    // really are; this way, the events seem to be in the right position
    // in relation to the virtual screen instead of the real screen.
    // Also, do the virtual panning with SHIFT-mouse events here.
    public boolean handleEvent( Event evt ) {
	if (virtual) {
	    if (evt.id == Event.MOUSE_DOWN && (evt.modifiers&Event.SHIFT_MASK)!=0) {
		scrolling = true;
		lastmousex = evt.x;
		lastmousey = evt.y;
		return true;
	    } else if (scrolling && evt.id == Event.MOUSE_DRAG) {
		translate( evt.x-lastmousex, evt.y-lastmousey );
		lastmousex = evt.x;
		lastmousey = evt.y;
		repaint();
		return true;
	    } else if (scrolling && evt.id == Event.MOUSE_UP) {
		scrolling = false;
		return true;
	    } else {
		evt.translate( -transx, -transy );
		return super.handleEvent( evt );
	    }
	} else {
	    return super.handleEvent( evt );
	}
    }
}
