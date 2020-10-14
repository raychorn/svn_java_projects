/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
 *
 *     Java Music Box
 *     ~~~~~~~~~~~~~~
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

public class MusicBox extends Applet implements Runnable
{
	boolean		audio_loaded;
	boolean		playing;
	int		appletID;
	String		audiofile;
	Image		image_play, image_stop;
	AudioClip	audio;
	Thread		thread;

	public void init()
	{
		String	s;

		s = getParameter("appletID");
		appletID = Integer.parseInt(s);

		s = getParameter("image_play");
		image_play = getImage(getDocumentBase(), s);

		s = getParameter("image_stop");
		image_stop = getImage(getDocumentBase(), s);

		audiofile = getParameter("audio");
		audio = getAudioClip(getDocumentBase(), audiofile);
	}

	public void start()
	{
		if(thread == null)
		{
			thread = new Thread(this);
			thread.start();
		}
		playing = false;
	}

	public void stop()
	{
		if(thread != null)
		{
			thread.stop();
			thread = null;
		}
		audio_stop();
		GlobalValue.set(0);
	}

	public void paint(Graphics g)
	{
		update(g);
	}

	public void update(Graphics g)
	{
		if(playing)
			g.drawImage(image_play, 0, 0, this);
		else
			g.drawImage(image_stop, 0, 0, this);
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
			repaint();

			if(GlobalValue.get() != appletID)
				audio_stop();
		}
	}

	public boolean mouseDown(Event e, int x, int y)
	{
		int	current_appletID;

		current_appletID = GlobalValue.get();
		if(current_appletID == 0)
			audio_start();
		else
			audio_toggle();
		return(true);
	}

	void audio_start()
	{
		if(playing == false)
		{
			playing = true;
			audio.loop();
			GlobalValue.set(appletID);
		}
	}

	void audio_stop()
	{
		if(playing == true)
		{
			playing = false;
			audio.stop();
		}
	}

	void audio_toggle()
	{
		if(playing == true)
		{
			playing = false;
			audio.stop();
			GlobalValue.set(0);
		}
		else
		{
			playing = true;
			audio.loop();
			GlobalValue.set(appletID);
		}
	}
}
