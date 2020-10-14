/*
 * @(#)URLLink.java	(RCS strings here)
 *
 * Copyright (c) 1996 Dimension X, Inc. All Rights Reserved.
 *
 * Dimension X MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. Dimension X SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 *	Revision History:
 *	8 Apr 96 - PLS	Pushed debug back down to 0, and added additional status
 *					message when Link is activated.
 * 
 */

package dnx.jack.behaviors;
import	dnx.jack.*;
import java.lang.*;
import java.net.URL;
import java.awt.Event;
import java.io.PrintStream;
import java.applet.Applet;		// for the scanning interface
import java.applet.AppletContext;
import java.awt.Component;		// for the scanning interface

/**
 * The URLLink class supports hyperlinking within a browser.
 *	It simply invokes the AppletContext.showDocument() method.
 *	Note that the AppletContext class does not guarantee to make the link!
 * @version 	0.01, 1/19/96
 * @author	Patrick Schmitz
 */
public class URLLink extends ActionBehavior {

	/**
	 * The applet context that allows us to link.
	 *	This will be null for all application contexts (e.g. the editor!).
	 */
	protected AppletContext appletCtxt = null;

	/**
	 * New document to show, when the behavior is activated.
	 */
	protected URL linkTo = null;

	/** The Event type (i.e. Event.id) on which to invoke the link	 */
	protected int linkEvent = Event.MOUSE_DOWN;

	/** The Event type (i.e. Event.id) on which to show the link in the status line	 */
	protected int showLinkEvent = Event.MOUSE_ENTER;

	/** The Event type (i.e. Event.id) on which to clear the status line	 */
	protected int hideLinkEvent = Event.MOUSE_EXIT;

	private static int debugLevel = 0;
	private static final void debug(  int lev, String str ) {
		if( lev > debugLevel ) return;
		System.out.println( "URLLink: " + str ); }

    /**
     * Constructs a simple scaling time behavior with name "ScaledTime".
     */
    public URLLink() 
	{
		super( "URLLink" );
    }

	/**
	 * Sets the link URL.
	 * @param linkTo is the URL to link to.
	 */
	public synchronized void setLinkURL( URL linkTo ) 
	{
		this.linkTo = linkTo;
	}

	/**
	 * Returns the scale factor for the behavior.  
	 * @return url that this will link to.
	 */
	public final synchronized URL getLinkURL() 
	{
		return this.linkTo;
	}


	/**
	 * Sets the link Event id.
	 * @param linkTo is the Event.id on which to invoke the URL link.
	 */
	public synchronized void setLinkEvent( int linkEvent ) 
	{
		this.linkEvent = linkEvent;
	}

	/**
	 * Returns the link Event id for the behavior.  
	 */
	public final synchronized int getLinkEvent() 
	{
		return this.linkEvent;
	}


	/**
	 * Sets the link Event id.
	 * @param linkTo is the Event.id on which to invoke the URL link.
	 */
	public synchronized void setShowLinkEvent( int showLinkEvent ) 
	{
		this.showLinkEvent = showLinkEvent;
	}

	/**
	 * Returns the link Event id for the behavior.  
	 */
	public final synchronized int getShowLinkEvent() 
	{
		return this.showLinkEvent;
	}

    /**
     * Handles events to the Behavior.
	 * This just looks for the configured event ids, and handles the actions.
	 * Note that it will do nada if there is no applet context,
	 * but will claim to have handled the events.
	 *
	 * @param evt	the Event to handle (note extensions in AnimEvent).
	 * @param seq	the sequence that this behavior is associated with.
	 * @returns  boolean indicating whether the event was handled
     */
    public boolean handleEvent( java.awt.Event evt, Sequence seq )
	{
		if( linkTo == null )
			return false;
		if( evt.id == linkEvent )
		{
			debug( 1, "Got a linkEvent to: " + linkTo.toString() );
			if( appletCtxt != null )
			{
				appletCtxt.showStatus( "Requesting URL: " + linkTo.toString() );
				appletCtxt.showDocument( linkTo );
			}
			return true;
		}
		if( evt.id == showLinkEvent )
		{
			debug( 1, "Got a showLinkEvent to: " + linkTo.toString() );
			if( appletCtxt != null )
				appletCtxt.showStatus( linkTo.toString() );
			return true;
		}
		if( evt.id == hideLinkEvent )
		{
			debug( 1, "Got a hideLinkEvent" );
			if( appletCtxt != null )
				appletCtxt.showStatus( "" );
			return true;
		}
		return false; 
	};


	/**
	 * Provide a description of the URLLink Behavior in a String.
	 * Overrides the toString method in Object.
	 */
	public String toString()
	{
		StringBuffer buff = new StringBuffer( 100 );	// Just a guess
		buff.append( super.toString() +'\n' );
		buff.append( "      Link to: " + linkTo.toString() + '\n' );
		buff.append( "      Link on event id: " + linkEvent + 
						" ShowLink on: " + showLinkEvent +
						" HideLink on: " + hideLinkEvent + '\n' );
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
			super.save( ps, false, true, true, strInset );
			ps.println( strInset + "  linkTo \"" + linkTo.toString() +'"' );
			ps.println( strInset + "  linkEvent " + linkEvent );
			ps.println( strInset + "  showLinkEvent " + showLinkEvent );
			ps.println( strInset + "  hideLinkEvent " + hideLinkEvent );
		}

		if( fClose )
			ps.println( strInset + "}" );
	}


	/**
	 * Scan the Scaled Time Behavior
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
		float scale;

		try {
			if( fScanMain )
			{
				String temp;
				super.scan( sf, aplt, comp, false, true, true );
				temp = sf.matchString( "linkTo" );
				linkTo = new URL( temp );
				linkEvent = sf.matchInt( "linkEvent" );
				showLinkEvent = sf.matchInt( "showLinkEvent" );
				hideLinkEvent = sf.matchInt( "hideLinkEvent" );
				if( aplt != null )
					appletCtxt = aplt.getAppletContext();
			}

			if( fClose )
				sf.passChar( '}' );
		}
		catch( java.lang.Exception exc )	// catch IO and MalformedURL Exceptions
		{
			System.out.println( "Bad scan for URLLink Behavior: " + exc );
			throw new JackException( "Bad scan for URLLink Behavior: " + exc.getMessage() );
		}
	}
        
}
