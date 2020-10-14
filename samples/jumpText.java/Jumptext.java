/*
	Jump text
*/

import java.awt.*;
import java.util.*;
import java.lang.*;

/*
*/
public class Jumptext extends java.applet.Applet implements Runnable {
	Thread	blinker;
	String	message1, message2;
	Font 	font1, font2;
	int 	speed,
		lastX1, lastY1, directX1, directY1,
		lastX2, lastY2, directX2, directY2;

	public void init() {
		String att;
		Dimension d = size();

		att = getParameter("speed");
		speed = (att == null) ? 50 : Integer.valueOf(att).intValue();

		font1 = new java.awt.Font("TimesRoman", Font.ITALIC, 24);
		att = getParameter("message1");
		message1 = (att == null) ? "CATNET Internet Service" : att;
		lastX1 = (int)(Math.random() * (d.width - 1));
		lastY1 = (int)((d.height - font1.getSize() - 1) * Math.random());
		directX1 = 3;
		directY1 = 3;

		font2 = new java.awt.Font("TimesRoman", Font.PLAIN, 20);
		att = getParameter("message2");
		message2 = (att == null) ? "System Intelligent" : att;
		lastX2 = (int)(Math.random() * (d.width - 1));
		lastY2 = (int)((d.height - font2.getSize() - 1) * Math.random());
		directX2 = -3;
		directY2 = -3;
	}
    
	public void start() {
		/* Set background color */
		this.setBackground(Color.black);

		/* Start thread */
		if (blinker == null) {
			blinker = new Thread(this, "Blink");
			blinker.start();
		}
	}
	public void paint(Graphics g) {
		int	x,
			y,
			space;
		Dimension d = size();
		StringTokenizer t;
		FontMetrics fm;

		g.setColor(Color.black);
		g.setFont(font1);
		fm = g.getFontMetrics();
		space = fm.stringWidth(" ");
		x = lastX1;
		y = lastY1;
		for (t = new StringTokenizer(message1) ; t.hasMoreTokens() ; ) {
			String word = t.nextToken();
			int w = fm.stringWidth(word) + space;
			if (x > d.width) {
				x = x - d.width;
			}
			g.setColor(new java.awt.Color((int)(Math.random() * 256),
				(int)(Math.random() * 256), (int)(Math.random() * 256)));
			g.drawString(word, x, y);
			x += w;
		}

		if (Math.random() > 0.99) {
			directX1 = -directX1;
		}
		lastX1 += directX1;
		if (lastX1 >= d.width) {
			lastX1 = 0;
		} else if (lastX1 < 0) {
			lastX1 = d.width - 1;
		}
		lastY1 += directY1;
		if (lastY1 >= d.height) {
			directY1 = -3;
		} else if (lastY1 < font1.getSize()) {
			directY1 = 3;
		}

		g.setColor(Color.black);
		g.setFont(font2);
		fm = g.getFontMetrics();
		space = fm.stringWidth(" ");
		x = lastX2;
		y = lastY2;
		for (t = new StringTokenizer(message2) ; t.hasMoreTokens() ; ) {
			String word = t.nextToken();
			int w = fm.stringWidth(word) + space;
			if (x > d.width) {
				x = x - d.width;
			}
			g.setColor(new java.awt.Color((int)(Math.random() * 256),
				(int)(Math.random() * 256), (int)(Math.random() * 256)));
			g.drawString(word, x, y);
			x += w;
		}

		if (Math.random() > 0.99) {
			directX2 = -directX2;
		}
		lastX2 += directX2;
		if (lastX2 >= d.width) {
			lastX2 = 0;
		} else if (lastX2 < 0) {
			lastX2 = d.width - 1;
		}
		lastY2 += directY2;
		if (lastY2 >= d.height) {
			directY2 = -3;
		} else if (lastY2 < font1.getSize()) {
			directY2 = 3;
		}
	}

	public void stop() {
		blinker = null;
		blinker.stop();
	}

	public void run() {
		while (blinker != null) {
			repaint();
			try {
				blinker.sleep(speed);
			} catch (InterruptedException e)
			{}
		}
	}
}
