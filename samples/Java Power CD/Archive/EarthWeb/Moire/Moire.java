/* Moire.java - a graphical excursion */
/***********************************************************************
Copyright Notice: This source code is (C) Copyright 1995-96, EarthWeb LLC.,
All Rights Reserved. Distribution of this document or it's resulting
compiled code is granted for non-commercial use, with prior approval of
EarthWeb LLC. Distribution of this document or its resulting compiled
code, for commercial use, is granted only with prior written approval of
EarthWeb, LLC. For information, send email to info@earthweb.com.
***********************************************************************/


import java.io.*;
import java.awt.*;
import java.applet.*;
import java.lang.*;

public class Moire extends Applet implements Runnable {
	int Xoffset, Yoffset = 0;
	boolean backwards, up = false;
	int colconst = 0;
	Color newcol[] = {Color.pink, Color.green, Color.black, Color.cyan,
		Color.red, Color.white, Color.blue, Color.darkGray, Color.gray,
		Color.lightGray, Color.magenta, Color.orange, Color.yellow};
	Thread mythread;
	boolean on = true;

	public void init() {
		mythread = new Thread(this);
	}

	public void start() {
		mythread.start();
	}

	public void update(Graphics g) {
		paint(g);
	}

	public void paint(Graphics g) {
		g.setColor(newcol[colconst]);

		g.drawLine(0 + Xoffset, 0, size().width - Xoffset, size().height);
		g.drawLine(size().width - Xoffset, 0, 0 + Xoffset, size().height);

		g.drawLine(0, size().height/2 - Yoffset, size().width, size().height/2 +
			Yoffset);
		g.drawLine(0, size().height/2 + Yoffset, size().width, size().height/2 - Yoffset);
	}

	public void draw() {
		int tmp;
		if (backwards) {	/* right to left */
			Xoffset = Xoffset - 2;
			if (Xoffset < 0) {	/* if past left border... */
				backwards = false; /* change direction */
				Xoffset = 0;
				do {
					tmp = (int)(Math.random() * 12);
				} while (tmp == colconst);
				colconst = tmp;
			}
		} else {	/* left to right */
			Xoffset = Xoffset + 2;
			if (Xoffset > size().width/2) { /* if past right border... */
				backwards = true; /* change direction */
				Xoffset = size().width/2;
				do {
					tmp = (int)(Math.random() * 12);
				} while (tmp == colconst);
				colconst = tmp;
			}
		}
		if (up) { /* bottom to top*/
			Yoffset = Yoffset - 2;
			if (Yoffset < 0) { /* above top border */
				up = false; /* change direction */
				Yoffset = 0;
			}
		} else {
			Yoffset = Yoffset + 2;
			if (Yoffset > size().height/2) { /* below bottom border */
				up = true; /* change direction */
				Yoffset = size().height/2;
			}
		}
		repaint();
	}

	public void run() {
		while (true) {
			draw();
			try {
				Thread.sleep(50);
			} catch(Exception e);
		}
	}

	public boolean mouseUp(Event e, int x, int y) {
		if (on) {
			mythread.suspend();
		} else {
			mythread.resume();
		}
		on = on ? false:true;
		return true;
	}

	public void stop() {
		mythread.stop();
	}
}

