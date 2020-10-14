import controls.*;
import java.applet.Applet;
import java.awt.*;
import java.net.*;
/**
 *	The types of Tickers supported by the TickerMixin.  Images are selectable.
 *	(c) 1996 Inductive Solutions, Inc.
 */
public class ShowTicker extends Applet {
	TickerMixin	tickerDefault = null;
	TickerMixin	tickerModified = null;
	TickerMixin	tickerImage = null;
	Panel	tPanel = new Panel();
	String  af = new String("earth5t.gif|earth6t.gif|earth7t.gif|earth8t.gif") ;

  	public void init() {
		tPanel.setLayout( new BorderLayout() );
		tickerDefault = new TickerMixin(this,400,50);
		tickerModified = new TickerMixin(this,400,50);
		tickerImage = new TickerMixin(this,400,100);
		tPanel.add("North",tickerDefault);
		tPanel.add("Center",tickerModified);
		tPanel.add("South",tickerImage);
		tickerDefault.setTickerText("ARF ART Brings you the GREATEST SHOW ON EARTH!  Happy Birthday, Stacy!");
		tickerModified.setTickerText("Change colors, sizes and family");
        	tickerModified.setBoxMessage("Courier",Font.PLAIN,20 , Color.green, Color.red);
                tickerImage.setTickerFileNames(this,getDocumentBase(),"images",af,8);
		add ( tPanel );

  	}

	public void start() {
		tickerDefault.start();
		tickerModified.start();
		tickerImage.start();
	}

	public void stop() {
		tickerDefault.stop();
		tickerModified.stop();
		tickerImage.stop();
	}

        public boolean action(Event evt, Object arg) {
		System.out.println(evt);
		System.out.println(arg);
		if ( evt.target == tickerDefault ) {
			System.out.println("Event from Default ticker top one  the event is " + arg);
		} else if ( evt.target == tickerModified ) {
			System.out.println("Event from Modified ticker middle one  the event is " + arg);
		} else if ( evt.target == tickerImage ) {
			System.out.println("Event from Image ticker bottom one  the event is " + arg);
		}
		return ( true );
	}

	public boolean mouseEnter(java.awt.Event evt, int x, int y) { 
		showStatus("(c) 1996 Inductive Solutions, Inc.");
		return (true);
	}

}
