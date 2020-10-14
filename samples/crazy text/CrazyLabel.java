// CrazyLabel.java
// Version 2.0
// Works with JDK 1.0 beta2.
// Patrick Taylor (taylor@us.net)
// Based on Daniel Wyszynski's NervousText applet from the JDK 1.0 beta1.
// See accompanying HTML documentation for detailed information.

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.FontMetrics;
import java.awt.Color;
import java.awt.Dimension;

public class CrazyLabel extends Canvas implements Runnable {

   // parameter variables

   String	text = "CrazyText"; // string to be displayed
   int		delay = 100;	// # of milliseconds between updates
   int		delta = 5;	// "craziness" factor: max pixel offset
   int		hgap = 0;	// extra horizontal pixel spacing between chars
   int		hspace = 0;	// extra pixel spacing on left and right
   int		vspace = 0;	// extra pixel spacing on top and bottom
   boolean	clear = false;	// should background be cleared on update
   String	cycle = "whole";// "whole" or "char" or "none"
   boolean	debug = true;	// show debugging messages

   // implementation variables

   int		textLen;	// text.length()
   char		chars[];	// individual chars in 'text'
   int		xPositions[];	// base horizontal position for each char
   int		yPosition;	// base vertical position for each char
   boolean	cycleChar;
   boolean	cycleWhole;
   Thread	thread;
   Dimension	minSize;
   int		xBase;
   int		yBase;

   // constructors

   public CrazyLabel(String text) {
      if (text != null) { this.text = text; }
   }

   // set methods

   public void setText(String text)	{ this.text = text; }
   public void setDelay(int delay)	{ this.delay = delay; }
   public void setDelta(int delta)	{ this.delta = delta; }
   public void setHgap(int hgap)	{ this.hgap = hgap; }
   public void setHspace(int hspace)	{ this.hspace = hspace; }
   public void setVspace(int vspace)	{ this.vspace = vspace; }
   public void setClear(boolean clear)	{ this.clear = clear; }
   public void setCycle(String cycle)	{ this.cycle = cycle; }
   public void setDebug(boolean debug)	{ this.debug = debug; }

   // redefined methods

   public void addNotify() {
      dbg("addNotify");
      super.addNotify();
      init();
   }

   public void removeNotify() {
      dbg("removeNotify");
      stop();
      super.removeNotify();
   }

   public void run() {
      dbg("run");
      while (thread != null) {
	 try { Thread.sleep(delay); } catch (InterruptedException e) { break; }
	 repaint();
      }
   }

   public void paint(Graphics g) {
      start();
      if (cycleWhole) { setRandomColor(g); }
      for (int i = 0; i < textLen; i++) {
	 if (cycleChar) { setRandomColor(g); }
	 int x = (int)(Math.random() * delta * 2) + xBase + xPositions[i];
	 int y = (int)(Math.random() * delta * 2) + yBase + yPosition;
	 g.drawChars(chars, i, 1, x, y);
      }
   }
 
   public void update(Graphics g) {
      if (clear) {
	 super.update(g);
      } else {
         paint(g);
      }
   }

   public Dimension preferredSize() {
      return minimumSize();
   }

   public Dimension minimumSize() {
      return minSize;
   }

   public void reshape(int x, int y, int width, int height) {
      super.reshape(x, y, width, height);
      xBase = (size().width  - minSize.width ) / 2;
      yBase = (size().height - minSize.height) / 2;
      dbg("reshape: xBase = "+xBase+", yBase = "+yBase);
   }

   // other methods

   public void dbg(String msg) {
      if (debug) {
	 System.out.println(msg);
      }
   }

   public void init() {
      dbg("init");
      textLen = text.length();
      chars = new char[textLen];
      text.getChars(0, textLen, chars, 0);
      FontMetrics fm = getFontMetrics(getFont());
      yPosition = fm.getAscent() - 1 + vspace;
      xPositions = new int[textLen];
      for (int i = 0; i < textLen; i++) {
         xPositions[i] = fm.charsWidth(chars, 0, i) + (i * hgap) + hspace;
	 dbg("xPositions["+i+"] = "+xPositions[i]);
      }
      cycleChar = cycle.equals("char");
      cycleWhole = cycle.equals("whole");
      minSize = new Dimension(hgap * (textLen - 1) + 
			      (hspace + delta) * 2 + fm.stringWidth(text),
			      (vspace + delta) * 2 + fm.getHeight());
   }

   public void start() {
      if (thread == null) {
	 dbg("start");
	 thread = new Thread(this);
	 thread.start();
      }
   }

   public void stop() {
      if (thread != null) {
	 dbg("stop");
	 thread.stop();
	 thread = null;
      }
   }

   protected void setRandomColor(Graphics g) {
      g.setColor(new Color((float)Math.random(),
			   (float)Math.random(),
			   (float)Math.random()));
   }
}
