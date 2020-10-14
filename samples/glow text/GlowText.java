/**
 * GlowText.java
 *
 * by Edd Dumbill 1996
	Last change:  EJA   2 Apr 96   10:19 pm
 *
 * This source code may be distributed freely for any ethical purpose.
 * The author disclaims responsibility for any harmful consequences
 * of use of this program or parts thereof. The use of this program or
 * parts thereof constitutes an acceptance of these conditions.
 */

import java.awt.*;
import java.applet.*;
import java.net.*;

/**
 *
 */
public class GlowText extends Applet implements Runnable
{
    private Thread glower = null;
    private boolean isGlowing = false;
    private String theText;
    private Image theImage=null;
    private Color theBackground;
    private Color theForeground;
    private Color curBackground;
    private Color curForeground;
    private Color toColor;
    private Color colorArray[];
    private int steps=7;
    private int currentStep=1;
    private int stepDelta=1;
    private int	delay=100;
    private Font theFont;
    private FontMetrics theFontMetrics;
    private URL theURL;

    Color parseColor(String str) {
	Integer r=null;
	Integer g=null;
	Integer b=null;
	Color col=null;

	if ( str==null ) {
	    return null;
	}
	if ( str.length()<6 ) {
	    return null;
	}
	if ( str.charAt(0)=='#' ) {
	    str=str.substring(1);
	    try {
	    	r=Integer.valueOf(str.substring(0,2),16);
	    	g=Integer.valueOf(str.substring(2,4),16);
	    	b=Integer.valueOf(str.substring(4,6),16);
		col=new Color(r.intValue(),g.intValue(),b.intValue());
	    } catch (NumberFormatException ne) {
		System.out.println("Error in color specification '" + str + "'");
		return null;
	    }
	}

	return col;
    }

    public void init() {
	theText=getParameter("text");
	if ( theText==null ) {
	    theText="GlowText";
	}
	if ( (theForeground=parseColor(getParameter("FGCOLOR")))==null) {
	    theForeground=Color.black;
	}
	if ( (theBackground=parseColor(getParameter("BGCOLOR")))==null) {
 	    theBackground=getBackground();
	}
	if ( (toColor=parseColor(getParameter("TOCOLOR")))==null ) {
	    toColor=Color.white;
	}
	String urlText=getParameter("HREF");
	if ( urlText!=null ) {
	    try {
		theURL=new URL(urlText);
	    } catch (MalformedURLException me) {
		System.out.println("Malformed URL '" + urlText + "'");
	    }
	}
	String fName=getParameter("FONT");
	String fSize=getParameter("FONTSIZE");
	String styleS=getParameter("FONTSTYLE");
	int fStyle=Font.PLAIN;
	
        if (styleS!=null) {
	    styleS=styleS.toUpperCase();
	    if ( styleS.indexOf("BOLD")>=0) {
	        fStyle+=Font.BOLD;
	    }
	    if ( styleS.indexOf("ITALIC")>=0) {
	    	fStyle+=Font.ITALIC;
	    }
    	}
	if ( fSize!=null && fName!=null ) {
	    theFont=new Font(fName, fStyle, (Integer.valueOf(fSize)).intValue());
	} 
	if ( theFont==null ) {
            theFont=getFont();
	}
	curForeground=theForeground;
	curBackground=theBackground;
	setBackground(curBackground);
	
	theFontMetrics=getFontMetrics(theFont);
	initColorArray();
	repaint();
    }

    void initColorArray() {
	int i;
	colorArray = new Color[steps];
	int dr,dg,db;

	colorArray[0]=theForeground;

	dr=(toColor.getRed()-colorArray[0].getRed())/(steps-1);
	dg=(toColor.getGreen()-colorArray[0].getGreen())/(steps-1);
	db=(toColor.getBlue()-colorArray[0].getBlue())/(steps-1);

	for(i=1; i<steps; i++) {
	    Color oc=colorArray[i-1];
	    colorArray[i]=new Color(oc.getRed()+dr, oc.getGreen()+dg, 
		oc.getBlue()+db);
	}
    }

    public synchronized void  update(Graphics g)
    {
     	if (theImage==null || theImage.getWidth(this)!=bounds().width ||
		theImage.getHeight(this)!=bounds().height)
	{
	    theImage=createImage(bounds().width, bounds().height);
	}
	paint(theImage.getGraphics());
	g.drawImage(theImage, 0, 0, this);
    }

    public synchronized void paint(Graphics g) {
	g.setColor(curBackground);
	g.fillRect(0,0, bounds().width, bounds().height);
	g.setColor(curForeground);
	g.setFont(theFont);
	g.drawString(theText, 0, bounds().height-theFontMetrics.getDescent());
    }

    public void start() {
    }

    public boolean handleEvent(Event ev) {
	if ( ev.id==Event.MOUSE_ENTER ) {
	    startGlowing();
	    if ( theURL!=null ) {
		showStatus(theURL.toString());
	    }
	} else if ( ev.id==Event.MOUSE_EXIT ) {
	    stop();
	} else if ( ev.id==Event.MOUSE_UP && theURL!=null) {
	    getAppletContext().showDocument(theURL);
	} else {
	    return super.handleEvent(ev);
	}
	return true;
    }

    public void startGlowing() {
	if ( glower==null )  {
	    glower=new Thread(this);
	    glower.start();
	}
    }

    public void stop() {
	if ( glower!=null ) {
	    glower.stop();
	    glower=null;
	    curForeground=theForeground;
	    repaint();
	}
    }

    private void updateColors() {
	if ( currentStep==0 || currentStep==steps-1) {
	     stepDelta=-stepDelta;
	}
	currentStep+=stepDelta;
	curForeground = colorArray[currentStep];
    }

    public void run() {
	while(true) {
	    try {
		Thread.currentThread().sleep(delay);
		updateColors();
		repaint();
	    } catch (InterruptedException e) {
	    }
	}
    }
}

