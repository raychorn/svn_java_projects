/*
 * @(#)Wander.java	(RCS strings here)
 *
 * Copyright (c) 1996 Dimension X, Inc. All Rights Reserved.
 *
 * Dimension X MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. Dimension X SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */


package dnx.jack.behaviors;
import	dnx.jack.*;

import	java.lang.String;
import	java.lang.Math;
import	java.applet.Applet;
import	java.awt.Component;
import	java.io.PrintStream;
import	java.util.Random;

/**
 * The Wander class is a simple motion behavior that makes an object
 * randomly wander around within a bounding cube.
 * @version 	0.01, 2/1/96
 * @author	Patrick Schmitz
 */
public class Wander extends MotionBehavior
{

	private static int debugLevel = 0;
	private static final void debug(  int lev, String str ) {
		if( lev > debugLevel ) return;
		System.out.println( "Wander: " + str ); }

    static final int focus = (int)(0.9f * (float)(Integer.MAX_VALUE));
    static final int focusC = Integer.MAX_VALUE - focus;

    protected int stepLength;

    protected boolean xadd, yadd, zadd;

	/**
	 * This is the bounding cube, which constrains the origin of the object.
	 *  If you are using the viewCube of a Scene, remember to reduce the size
	 *	of the bounds by the size of the rendered object, if you want it stay onscreen.
	 */
	protected Cube bounds;

	/**
	 * This controls whether the parent Scene's viewCube should be the bounds.
	 */
	protected boolean fSceneConstrain;

	/**
	 * This is our random generator instance, for jumping.
	 */
	protected Random rand;

	/**
	 * This is the update period of the Wander.
	 * If set to -1, it will default to the period of the associated ImageSequence,
	 * or 1 millisecond for other Sequences.
	 */
	protected int period;

    /**
     * Constructs a default Wander that will use the parent Scene as bounds
     */
    public Wander() {
		this( new Cube(), true, -1, 10 );
    }

    /**
     * Constructs a Wander with a default bounding cube, and a period
     */
    public Wander( int period ) {
		this( new Cube(), true, period, 10 );
	}


    /**
     * Constructs a Wander with a name and a bounding rectangle
     */
    public Wander( int x, int y, int w, int h) {
		this( new Cube( x, y, 0, w, h, Dimension.INFINITE ), false, -1, 10 );
    }

    /**
     * Constructs a Wander with a name and a Dimension
	 * @param name	the name of the Wander behavior for
     */
    public Wander( Dimension size ) {
		this( new Cube( size ), false, -1, 10 );
    }

    /**
     * Constructs a Wander with a bounding cube
     */
    public Wander( Cube bounds, boolean fSceneConstrain, int period, int stepLength ) {
		super( "Wander" );
		this.bounds = bounds;
		this.fSceneConstrain = fSceneConstrain;
		this.period = period;
		this.stepLength = stepLength;
		this.xadd = false;
		this.yadd = false;
		this.zadd = false;
		rand = new Random( System.currentTimeMillis() );
    }

    /**
     * Returns the real-time duration of the Behavior. This
	 *	must be overridden by subclasses. Note that it is reasonable
	 *	to return Sequence.INFINITE, for looping or function sequences.
	 *	Note that TimeBehaviors should <em>always</em> return Sequence.INFINITE.
	 * @return real-time count of milliseconds, or Sequence.INFINITE
	 * @see jack.Sequence#INFINITE
     */
    public int getDuration() { return Sequence.INFINITE; }


    /**
     * Provides update of parent Scene dimensions, for behaviors
	 *	that are based upon constraints of the Scene cube.
	 *	Can be overridden by subclasses to use constraints.
	 * This is called whenever the parent Scene resizes.
	 *	or when the viewCube is otherwise adjusted.
	 *
	 * @param viewCube	the Cube that constrains the view.
	 * @param seqDims	the sequence max dimensions
     */
    public void setConstraints( Cube viewCube, Dimension seqDims )
	{
		if( fSceneConstrain )
		{
			// Adjust the cube, and then update all behaviors
			// we subtract the maximum sequence dimensions from the parent scene
			// viewCube, to keep the image fully onscreen.
			bounds.reshape( viewCube );
			bounds.size.subtract( seqDims );
		}
	}


	/**
     * Sets the period for the behavior.
	 * @param period the new period to set.
     */
    public synchronized void setPeriod( int period ) {
		this.period = period;
    }


    /**
     * Returns the period for the behavior.
     */
    public synchronized int getPeriod() {
		return period;
    }


	protected int lastUpdate = -1;


    /**
     * Calculates new location for location
	 *
	 * @param pos	        the old location
	 * @param delta	        the delta motion (will be added).
	 * @param boundstart	bounds begin.
	 * @param boundend	    bounds end.
	 * @param fConstrain	constrain to bounds.
	 * @param add	        was any motion added.
	 * @return		        new position.
     */
    private static int constrain( int pos, int delta, int boundstart, int boundend,
                                  boolean fConstrain )
	{
		int newpos = pos + delta;
		if( fConstrain && ( newpos <= boundstart || newpos >= boundend ) )
		    return pos;

		return newpos;
	} // constrain


    /**
     * Requests Wander to update for the passed tc.
	 *
	 * @param tc	the time context (ignored)
	 * @param seq	the Sequence upon which the Wander MotionBehavior acts.
	 * @return		true if Behavior made any real change.
     */
    public boolean update( TimeContext tc, Sequence seq )
	{
		// first determine if we should update this time...
		if( period <= 0 )
		{
			if( seq instanceof ImageSequence )
				period = ((ImageSequence)seq).getPeriod();
			else
				period = 1;
		}
		int index = tc.time / period;
		if( index != lastUpdate )
		{
			int xaprob = xadd ? focus : focusC;
			int yaprob = yadd ? focus : focusC;
			int zaprob = zadd ? focus : focusC;
			int xDelta, yDelta, zDelta;
			int xNew, yNew, zNew;

			Position pos = seq.getPosition();

			xDelta = Math.abs( rand.nextInt() ) % stepLength;
			yDelta = Math.abs( rand.nextInt() ) % stepLength;
			zDelta = Math.abs( rand.nextInt() ) % stepLength;

			Boolean add = new Boolean(false);

			if( Math.abs( rand.nextInt() ) >= xaprob )
			    xDelta = -xDelta;
			if( Math.abs( rand.nextInt() ) >= yaprob )
			    yDelta = -yDelta;
			if( Math.abs( rand.nextInt() ) >= zaprob )
                zDelta = -zDelta;

			xNew = constrain( pos.x, xDelta, bounds.base.x, bounds.base.x + bounds.size.x,
                                  fSceneConstrain );
		    if ( xNew == pos.x )
		        xadd = !xadd;

			yNew = constrain( pos.y, yDelta, bounds.base.y, bounds.base.y + bounds.size.y,
                                  fSceneConstrain );
		    if ( yNew == pos.y )
		        yadd = !yadd;

			zNew = constrain( pos.z, zDelta, bounds.base.z, bounds.base.z + bounds.size.z,
                                  fSceneConstrain );
		    if ( zNew == pos.z )
		        zadd = !zadd;

			seq.move( xNew, yNew, zNew );
			debug( 2, "move x: "+xNew+" y: "+yNew+" z: "+zNew );
			debug( 4, " -- xadd: "+xadd+" yadd: "+yadd+" zadd: "+zadd );
			lastUpdate = index;
			return true;
		}
		return false;
	}


	/**
	 * Provide a description of the Wander behavior in a String.
	 * Overrides the toString method in Object.
	 */
	public String toString()
	{
		StringBuffer buff = new StringBuffer( 100 );	// Just a guess
		buff.append( super.toString() );
		buff.append( "\n     bounds: " + bounds.toString() );
		if( fSceneConstrain )
			buff.append( " (Constrained to scene)\n" );
		else
			buff.append( '\n' );
		return buff.toString();
	}


	/**
	 * Save a scannable version of the Behavior to a file.
	 * @param ps the printstream to save to
	 * @param fClose controls whether to print the closing curly bracket
	 * @param fSaveMain	if true, saves info about the sequence object
	 * @param fSaveVectors if true, saves info about any vectors
	 * @param strInset the inset string (white space) to preprend to each line
	 */
	public void save( PrintStream ps, boolean fClose,
					boolean fSaveMain, boolean fSaveVectors, String strInset )
	{
		if( fSaveMain )
		{
			super.save( ps, false, true, false, strInset );
			ps.print( strInset + "bounds " );
			bounds.save( ps );
			ps.println();
			ps.println( strInset + "constrain " + (fSceneConstrain?'1':'0') );
			ps.print( strInset + "period " + period );
		}
		if( fSaveVectors )
		{
			super.save( ps, false, false, true, strInset );
		}

		if( fClose )
			ps.println( strInset + "}" );
	}


	/**
	 * Scan the Behavior
	 * This assumes that the class name has already been scanned off.
	 * @param sf the ScanFile to scan from
	 * @param aplt unused here - we create no images, etc.
	 * @param comp unused here - we create no images, etc.
	 * @param fClose controls whether to scan the closing curly bracket
	 * @param fScanMain	if true, scans info about the sequence object
	 * @param fScanVectors if true, scans info about any vectors
	 * @exception JackException for scan errors
	 */
	public void scan( ScanFile sf, Applet aplt, Component comp,
					boolean fClose, boolean fScanMain, boolean fScanVectors )
		throws JackException
	{
		try
		{
			if( fScanMain )
			{
				super.scan( sf, aplt, comp, false, true, false );
				sf.passString( "bounds" );
				bounds.scan( sf );
				fSceneConstrain = sf.matchBoolean( "constrain" );
				period = sf.matchInt( "period" );
			}
			if( fScanVectors )
			{
				super.scan( sf, aplt, comp, false, false, true );
			}

			if( fClose )
				sf.passChar( '}' );
		}
		catch( java.io.IOException exc )
		{
			System.out.println( "Bad scan for Wander: " + exc );
			throw new JackException( "Bad scan for Wander Behavior: " + exc.getMessage() );
		}
	}

} // Wander
