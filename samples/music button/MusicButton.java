///////////////////////////////////////////////////////////////////////////
//
// Applet:      MusicButton.java (MusicButton.class)
//
// Version:     1.0
//
// Author:      Allen Luong
//
// E-mail:     allenl@cats.ucsc.edu
//
// Date:        03/23/1996
//
// Purpose:     Two buttons --
//              "Play" button to play the background music
//              "Stop" button to stop the background music
//
// JDK version:   1.0
//
// See also:      mbutton.html
//
// Disclaminer:
//
// YOU ARE FREE TO USE, COPY, MODIFY THIS JAVA APPLET.
// THIS SOURCE CODE IS PROVIDED "AS IS".  USE IT AT YOUR OWN RISK.
// I'M NOT LIABLE FOR ANY DAMAGES SUFFERED AS A RESULT OF USING,
// MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
//
///////////////////////////////////////////////////////////////////////////


import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;

public class MusicButton extends java.applet.Applet implements Runnable {


    Thread musicThread;			//// thread for the MusicButton applet

    String audiodir;			//// directory where audio files are for background music

    String audiofile[];			//// two audio files for background music

    AudioClip music = null;		//// background music

    int x = 0, y = 0;			//// x and y coordinates for drawing



	public String getAppletInfo() {
		return "MusicButton Version 1.0 by Allen Luong";
    }



    public String[][] getParameterInfo() {

    	String[][] info = {

		    {"audiodir",       "String",    "path of the audio file"},
    		{"audiofile",      "String",    "name of the audio file"},
		};

	    return info;
    }



    public void init() {

    	showStatus("Initialize...");

		//////////////////////////////// get audio directory and audio files

		String at = getParameter("audiodir");
		audiodir = (at != null) ? at : "audio/mbutton";

		audiofile = new String[1];	// create one string for the audio file name

		at = getParameter("audiofile");
		audiofile[0] = (at != null) ? at : "mbutton.au";

		music = getAudioClip(getDocumentBase(), audiodir + "/" + audiofile[0]);


		musicThread = new Thread(this);
		musicThread.setPriority(Thread.NORM_PRIORITY);

        add(new Button("Play"));
        add(new Button("Stop"));

		showStatus("");

    } //of init()



    public boolean action(Event evt, Object arg) {

        if (evt.target instanceof Button)
            backgroundMusic((String)arg);

        return true;

    } //of action()



    void backgroundMusic(String bname) {

        if (bname.equals("Play")) start();
        else if (bname.equals("Stop")) stop();
        else stop();

    } //of backgroundMusic()



    public void start()
    {
		if (musicThread.isAlive()) {
			musicThread.resume();
		}
		else {
			musicThread.start();
		}

		music = getAudioClip(getDocumentBase(), audiodir + "/" + audiofile[0]);

	    music.loop();		//// starting looping background music

	} //of start()



    public void stop()
    {
		if (musicThread != null) {
			musicThread.suspend();
		}

		if (music != null) {
			music.stop();
			music = null;
		}

    } //of stop()



    public void run()
    {
		while (musicThread != null) {

			try	{ Thread.currentThread().sleep(300); }
			catch (InterruptedException e) { break;	}

		}

    } //of run()



	public void destroy()
	{
		if (musicThread != null) {
			musicThread.stop();
			musicThread = null;
		}

		if (music != null) {
			music.stop();
			music = null;
		}

	} //of destroy()


} //of public class MusicButton
