/***********************************************************
 *
 * Fade v1.05 by Giuseppe Gennaro, 1996
 *
 *	Feel free to modify or distribute this code as you 
 *	wish, just mention the author somewhere on the page
 *      you use it. It is one of my many learning attempts at
 *	the java language.  Here are the parameters for the
 * 	applet:
 *
 *	<applet code="Fade.class" width="300" height="100">
 *	<param name="bgcolor" value="ffffff">
 *	<param name="txtcolor" value="000000">
 *	<param name="changefactor" value="dx">
 *	<param name="text1" value="First text">
 *	<param name="url1" value="http://www.xxx.xxx">
 *	<param name="font1" value="FontName,FontStyle,FontSize">
 *	<param name="text2" value="Second text">
 *	<param name="url2" value="http://www.xxx.xxx">
 *	<param name="fontname2" value="FontName,FontStyle,FontSize">
 * 	...
 *	</applet>
 *      
 *	You should be able to change the width, height, background 
 * 	color, text color, etc...  Also, the text with the extension
 *	number following it is affected by all other parameters with
 * 	that same extension number. (Just stating the obvious.) 
 */

import java.awt.*;
import java.lang.*;
import java.net.*;
import java.applet.*;
import java.io.*;

//////////////////////////////////////
// OTHER CLASSES
//////////////////////////////////////

class Thoughts {
	static int MAX = 10;
	String theThoughts[] = new String[MAX];
	URL theUrls[] = new URL[MAX];
	Font theFonts[] = new Font[MAX];
	int R, G, B;
	int dr, dg, db;
	int rinit, ginit, binit;
	int rfinal, gfinal, bfinal;
	Color bgColor;
	boolean maxxed = false;
	boolean darker = false;
	int curr, count;

	Thoughts() {
		R = G = B = 0;
		dr = dg = db = 1;
		rinit = ginit = binit = 0;
		rfinal = gfinal = bfinal = 255;
		bgColor = new Color(R,G,B);
		curr = -1;
		count = 0;
	}

	public void SetBackground(int i, int j, int k) {
		R = rinit = i;
		G = ginit = j;
		B = binit = k;
		bgColor = new Color(R,G,B);
	}

	public Color GetBackground() {
		return bgColor;
	}

	public void SetTextColor(int i, int j, int k)
	{	rfinal = i;
		gfinal = j;
		bfinal = k;
	}

	public void SetChangeFactor(int i) {
		if(rfinal > rinit)
			dr = i;
		else if(rfinal == rinit)
			dr = 0;
		else
			dr = -i;
		if(gfinal > ginit)
			dg = i;
		else if(gfinal == ginit)
			dg = 0;
		else
			dg = -i;
		if(bfinal > binit)
			db = i;
		else if(bfinal == binit)
			db = 0;
		else
			db = -i;
	}

	public void AddThought(String idea, String url, String fontname, int fontstyle, int fontsize) {
		if(curr < MAX)
		{	curr++;
			count++;
			theThoughts[curr] = idea;
			try{
				theUrls[curr] = new URL(url);
			}catch(MalformedURLException e) {}
			theFonts[curr] = new Font(fontname, fontstyle, fontsize);
		}
	}

	public void Reset() {
		curr = 0;
	}

	public void Next() {
		curr++;
		if(curr >= count) Reset();
	}

	public void DrawThoughts(Fade that, Graphics g) {
		FontMetrics fm = that.getFontMetrics(theFonts[curr]);
		Color temp = new Color(R,G,B);
		g.setColor(temp);
		g.setFont(theFonts[curr]);
		g.drawString(theThoughts[curr],
				(that.size().width - fm.stringWidth(theThoughts[curr]))/2,
				(that.size().height + fm.getAscent())/2);
	}
	
	// (that.size().height + fm.getHeight())/2

	public URL GetCurrentURL() {
		return theUrls[curr];
	}

	public int ChangeColors() {
		int pause = 1;

		if(!maxxed)
		{  // Adjust colors to fade in...
			R += dr;
			G += dg;
			B += db;
			if(!((dr > 0 && R < rfinal) || (dr < 0 && R > rfinal)))
				R = rfinal;
			if(!((dg > 0 && G < gfinal) || (dg < 0 && G > gfinal)))
				G = gfinal;
			if(!((db > 0 && B < bfinal) || (db < 0 && B > bfinal)))
				B = bfinal;
			if(R == rfinal && G == gfinal && B == bfinal)
			{	maxxed = true;
				pause = 10;
			}
		}
		else
		{  // Adjust colors to fade out...
			R -= dr;
			G -= dg;
			B -= db;
			if(!((dr > 0 && R > rinit) || (dr < 0 && R < rinit)))
				R = rinit;
			if(!((dg > 0 && G > ginit) || (dg < 0 && G < ginit)))
				G = ginit;
			if(!((db > 0 && B > binit) || (db < 0 && B < binit)))
				B = binit;
			if(R == rinit && G == ginit && B == binit)
			{	maxxed = false;
				pause = 10;
				Next();
			}
		}
		return pause;
	}

}

//------------------------------------

public class Fade extends java.applet.Applet implements Runnable {
	Thoughts thoughts = new Thoughts();
	Thread runner = null;

	public void init() {
		// Set the background...
		String bgRGB = getParameter("bgcolor");
		if(bgRGB == null || bgRGB.length() != 6)
		{	thoughts.SetBackground(0,0,0);
		}
		else
		{	thoughts.SetBackground(HexToInt(bgRGB.substring(0,2)),
						HexToInt(bgRGB.substring(2,4)),
						HexToInt(bgRGB.substring(4)));
		}
		setBackground(thoughts.GetBackground());

		// Set the text color...
		String txtRGB = getParameter("txtcolor");
		if(txtRGB == null || txtRGB.length() != 6)
		{	thoughts.SetTextColor(255,255,255);
		}
		else
		{
			thoughts.SetTextColor(HexToInt(txtRGB.substring(0,2)),
						HexToInt(txtRGB.substring(2,4)),
						HexToInt(txtRGB.substring(4)));
		}

		// Set the delta for the changing color...
		String changeFactor = getParameter("changefactor");
		if(changeFactor == null)
			thoughts.SetChangeFactor(1);
		else
			thoughts.SetChangeFactor(Integer.valueOf(changeFactor).intValue());

		// Obtaining the data for the thoughts...
		GetThoughts(this);

		thoughts.Reset();
		resize(size().width, size().height);
	}

	public void start() {
		if(runner == null)
		{	runner = new Thread(this);
			runner.setPriority(runner.MIN_PRIORITY);
			runner.start();
		}
	}

	public void stop() {
		runner = null;
	}

	public void paint(Graphics g) {
	}

	public void update(Graphics g) {
		thoughts.DrawThoughts(this, g);
	}

	public boolean mouseDown(java.awt.Event evt, int x, int y) {
		super.getAppletContext().showDocument(thoughts.GetCurrentURL());
		return true;
	}

	public boolean mouseEnter(java.awt.Event evt, int x, int y) {
		// Showing who made this...
		showStatus("Fade.java by Giuseppe Gennaro");
		return true;
	}

	public void run() {
		int sleepfactor;

		while(runner != null)
		{  sleepfactor = thoughts.ChangeColors();
			repaint();
			try{runner.sleep(25*sleepfactor);}catch(InterruptedException e) {}
		}
	}

	public String getAppletInfo() {
		return "Fade by Giuseppe Gennaro";
	}

	////////////////////////////////////////
	// OTHER FUNCTIONS
	////////////////////////////////////////

	public void GetThoughts(Fade that){
		boolean done = false;
		int i=1;

		while(!done)
		{	String extension = String.valueOf(i);
			
			String fontParam = "font" + extension;
			String fontName;
			int fontSize, fontStyle;
			String textParam = "text" + extension;
			String urlParam = "url" + extension;
	
			String font = super.getParameter(fontParam);
			if(font == null)
			{	fontName = "TimesRoman";
				fontSize = 12;
				fontStyle = Font.PLAIN;
				done = true;
			}
			else
			{	int comma1 = font.indexOf(","),
				    comma2 = font.lastIndexOf(",");
				String name = font.substring(0, comma1);
				String style = font.substring(comma1+1, comma2);
				String size = font.substring(comma2+1);
				
				fontName = name;
				fontSize = Integer.valueOf(size).intValue();
				if(style.equalsIgnoreCase("PLAIN"))
					fontStyle = Font.PLAIN;
				else if(style.equalsIgnoreCase("BOLD"))
					fontStyle = Font.BOLD;
				else if(style.equalsIgnoreCase("ITALIC"))
					fontStyle = Font.ITALIC;
				else
					fontStyle = Font.PLAIN;
			}

			String theText = that.getParameter(textParam);
			if(theText == null)
			{	theText = "No Text Given.";
				done = true;
			}
			String theUrl = that.getParameter(urlParam);

			if(!done)
				thoughts.AddThought(theText, theUrl, fontName, fontStyle, fontSize);
			i++;
		}
	}

	public int HexToInt(String value) {
		int answer = 0;

		if(value.substring(0,1).equalsIgnoreCase("a"))
			answer = 160;
		else if(value.substring(0,1).equalsIgnoreCase("b"))
			answer = 176;
		else if(value.substring(0,1).equalsIgnoreCase("c"))
			answer = 192;
		else if(value.substring(0,1).equalsIgnoreCase("d"))
			answer = 208;
		else if(value.substring(0,1).equalsIgnoreCase("e"))
			answer = 224;
		else if(value.substring(0,1).equalsIgnoreCase("f"))
			answer = 240;
		else
			answer = Integer.valueOf(value.substring(0,1)).intValue() * 16;

		if(value.substring(1).equalsIgnoreCase("a"))
			answer += 10;
		else if(value.substring(1).equalsIgnoreCase("b"))
			answer += 11;
		else if(value.substring(1).equalsIgnoreCase("c"))
			answer += 12;
		else if(value.substring(1).equalsIgnoreCase("d"))
			answer += 13;
		else if(value.substring(1).equalsIgnoreCase("e"))
			answer += 14;
		else if(value.substring(1).equalsIgnoreCase("f"))
			answer += 15;
		else
			answer += Integer.valueOf(value.substring(1)).intValue();

		return answer;
	}

}
