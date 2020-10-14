/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
 *
 *     ImageSet.java
 *     ~~~~~~~~~~~~~
 *     Kenji Kita, 1996.
 *
 *     Department of Information Science and Intelligent Systems
 *     Faculty of Engineering, Tokushima University
 *     Tokushima 770, Japan
 *     Tel: 0886-56-7496,  Fax: 0886-56-7492
 *     E-mail: kita@is.tokushima-u.ac.jp
 *
 *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/

import java.applet.*;
import java.awt.*;

public class ImageSet extends Applet implements Runnable
{
	int	width, height;
	int	current_image = 0;
	Image	image[];
	Thread	thread;

	public void init()
	{
		int	i, N;
		String	s, imagefile;

		width = size().width;
		height = size().height;

		s = getParameter("images");
		N = Integer.parseInt(s);

		image = new Image[N];

		for(i = 0; i < N; i++)
		{
			s = "image" + i;
			imagefile = getParameter(s);
			image[i] = getImage(getDocumentBase(), imagefile);
		}
	}

	public void start()
	{
		if(thread == null)
		{
			thread = new Thread(this);
			thread.start();
		}
	}

	public void stop()
	{
		if(thread != null)
		{
			thread.stop();
			thread = null;
		}
	}

	public void paint(Graphics g)
	{
		update(g);
	}

	public void update(Graphics g)
	{
		g.drawImage(image[current_image], 0, 0, width, height, this);
	}

	public void run()
	{
		while(true)
		{
			try{ thread.sleep(150); }
			catch(InterruptedException e)
			{
				break;
			}

			current_image = GlobalValue.get();

			repaint();
		}
	}
}
