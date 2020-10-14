/* acNeilson.java  bugly@interserv.com March 1996
 *
 *                 Changed name from CrazyCounter.java to acNeilson.java
 *                 Improved readability (because I'm so Alden Bugly) maybe
 *
 *                 Note, number is not a parem
 *
 *                 Calling syntax that works for me is:
 *
 *   <applet codebase="file:///c|/Html/AldenBugly/Class"
 *           code=acNeilson.class 
 *           width=75 
 *           height=20
 *           >
 *           <param name=NUMBER_FRAMES value=10>
 *           <param name=NUMBER_WIDTH value=15>
 *           <param name=NUMBER_HEIGHT value=20>
 *           <param name=HOW_MANY value=5>
 *           <param name=DELAY value=1000>
 *           <parem name=VALUE value=777777>
 *   </applet>
 *
 *   Passing VALUE (priming the pump) doesn't seem to work.
 *
 *   Renamed Numbers.gif to acNeilson.gif and PSP'd it so the counter
 *   and my schtick are harmonious.
*/
/* CrazyCounter.java */
/* 
 * Copyright (C) 1996 Mark Boyns <boyns@sdsu.edu>
 *
 * CrazyCounter
 * <URL:http://www.sdsu.edu/~boyns/java/CrazyCounter/>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

import java.applet.*;
import java.awt.*;
import java.net.*;

public class acNeilson extends java.applet.Applet implements Runnable
{
    Thread thread = null;
    MediaTracker tracker = null;
    Image numbers;
    int number_frames;
    int number_width;
    int number_height;
    int how_many;
    int delay;
    String value;
    boolean first = true;
    int state = 0;

    public void init ()
    {
	String param;
	
	tracker = new MediaTracker (this);

	try
	{
	    numbers = getImage(getCodeBase(),"../Images/acNeilson.gif");
	    tracker.addImage (numbers, 0);
	    
	    param = getParameter ("NUMBER_FRAMES");
	    number_frames = Integer.parseInt (param);
	    
	    param = getParameter ("NUMBER_WIDTH");
	    number_width = Integer.parseInt (param);
	    
	    param = getParameter ("NUMBER_HEIGHT");
	    number_height = Integer.parseInt (param);

	    param = getParameter ("HOW_MANY");
	    how_many = Integer.parseInt (param);

	    param = getParameter ("DELAY");
	    delay = Integer.parseInt (param);

	    value = getParameter ("VALUE");
	    if (value == null)
	    {
		value = String.valueOf ((long)(Math.random () * (long)(Math.pow (10, how_many))));
	    }
	}
	catch (Exception e)
	{
	    return;
	}

	resize ((how_many * number_width), number_height);

	thread = new Thread (this);
	thread.start ();
    }

    public void stop ()
    {
	if (thread != null)
	{
	    thread.stop ();
	    thread = null;
	}
    }

    public void run ()
    {
	tracker.checkAll (true);

	for (;;)
	{
	    repaint ();

	    try
	    {
		Thread.sleep (delay);
	    }
	    catch (Exception e)
	    {
	    }
	}
    }

    /* Handle mouse events. */
    public boolean mouseDown (Event e, int x, int y)
    {
	if (thread != null)
        {
            thread.stop ();
            thread = null;
        }
        else
        {
            thread = new Thread (this);
            thread.start ();
        }
	return true;
    }
    
    /* Handle keyboard events. */
    public boolean keyDown (Event e, int key)
    {
	switch (key)
	{
	case '+':		// faster
	    delay -= 100;
	    if (delay < 10)
	    {
		delay = 10;
	    }
	    break;

	case '-':		// slower
	    delay += 100;
	    break;

	case '0':
	    value = String.valueOf (0);
	    state = 0;
	    break;

	default:
	    value = String.valueOf ((long)(Math.random () * (long)(Math.pow (10, how_many))));
	    state = 0;
	    break;
	}
	
	return true;
    }

    /* Don't clear the screen; just call paint. */
    public void update (Graphics g)
    {
	paint (g);
    }

    /* Paint the screen. */
    public void paint (Graphics g)
    {
	int i;
	int digit;
	boolean scroll;
	
	if (tracker.checkAll () == false)
	{
	    g.setColor (Color.black);
	    g.fillRect (0, 0, (how_many * number_width), number_height);
	    return;
	}

	int zeros = how_many - value.length ();
	for (i = 0; i < zeros; i++)
	{
	    Graphics gc = g.create (i * number_width, 0, number_width, number_height);
	    gc.drawImage (numbers, 0, 0, this);
	    gc.dispose ();
	}

	scroll = true;
	
	for (i = value.length () - 1; i >= 0; i--)
	{
	    digit = value.charAt (i) - '0';
	    
	    Graphics gc = g.create ((zeros+i) * number_width, 0, number_width, number_height);

	    if (scroll)
	    {
		switch (state)
		{
		case 0:
		    if (digit == 0)
		    {
			gc.drawImage (numbers, 0, -((9 * number_height) + number_height/2), this);
		    }
		    gc.drawImage (numbers, 0, -((digit * number_height) - number_height/2), this);
		    break;

		case 1:
		    gc.drawImage (numbers, 0, -(digit * number_height), this);
		    break;

		case 2:
		    gc.drawImage (numbers, 0, -((digit * number_height) + number_height/2), this);
		    if (digit == 9)
		    {
			gc.drawImage (numbers, 0, -((0 * number_height) - number_height/2), this);
		    }
		    break;
		}
	    }
	    else
	    {
		gc.drawImage (numbers, 0, -(digit * number_height), this);
	    }
	    
	    gc.dispose ();

	    if (digit != 9)
	    {
		scroll = false;
	    }
	}

	if (state >= 2)
	{
	    state = 0;
	    long l = Long.parseLong (value);
	    l++;
	    value = String.valueOf (l);
	}
	state++;
    }
}

/*
Local variables:
eval: (progn (make-local-variable 'compile-command) (setq compile-command (concat "javac " buffer-file-name)))
End:
*/
