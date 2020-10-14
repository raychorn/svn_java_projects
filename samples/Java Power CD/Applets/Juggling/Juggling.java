/*
 * @(#)Juggling.java                          1.0f 95/05/01 Chris Seguin
 * E-mail:  seguin@uiuc.edu
 *
 * Copyright (c) 1995 University of Illinois (UIUC)
 *
 * I MAKE NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. UIUC SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * To be fair, I used code created by James Gosling and extended it 
 * to juggle objects, rather than just animate a gif image.
 *
 */

import java.io.InputStream;
import java.awt.*;
import java.net.*;

/**
 * JugglingImages class. This is a container for a list
 * of images that are animated.
 *
 * @author      Chris Seguin
 * @version     1.0f, May 1, 1995
 */
class JugglingImages {
    /**
     * The images.
     */
    Image imgs[];

    /**
     * The number of images actually loaded.
     */
    int nImagesCount = 0;

    /**
     * Load the images, from dir. The images are assumed to be
     * named T1.gif, T2.gif...
     */
    JugglingImages(URL context, String dir, Juggling parent) {

        nImagesCount = 0;
        imgs = new Image[10];
        int nWidth = 0;

        for (int i = 1; i < imgs.length; i++) {

            Image im = parent.getImage(parent.getDocumentBase(), 
                                       dir + "T" + i + ".gif");
            
            imgs[nImagesCount++] = im;
        }
    }
}

/**
 * BallPaths class. This is a container for a paths
 * of juggling balls
 *
 * @author      Chris Seguin
 * @version     1.0f, May 1, 1995
 */
class BallPaths {
    
    /**
     * Arrays containing the path of the balls
     */
    int pnX[] = {0};
    int pnY[] = {0};

    int nLength = 1;

    /**
     * LookupX - looks up the appropriate value of X
     */
    public int LookupX (int nIndex) 
    {
    if ((nIndex > nLength) || (nIndex < 0))
        return 0;

    return pnX[nIndex];
    }

    /**
     * LookupY - looks up the appropriate value of Y
     */
    public int LookupY (int nIndex) 
    {
    if ((nIndex > nLength) || (nIndex < 0))
        return 0;

    return pnY[nIndex];
    }

    /**
     * Length - the number of data points stored in the path
     */
    public int Length ()
    {
    return nLength;
    } 
}


/**
 * CascadeBallPaths class. This is a container for a paths
 * of juggling balls, the balls are moving in a standard 
 * cascade pattern
 *
 * @author      Chris Seguin
 * @version     1.0f, May 1, 1995
 */
class CascadeBallPaths extends BallPaths {
    
    /**
     * Arrays containing the path of the balls
     */
    int pnX[] = {     20,   24,   27,   31,   35, 
                      40,   45,   50,   55,   60, 
                      65,   70,   75,   80,   85, 
                      90,   95,  100,  105,  110, 
                     115,  120,  125,  130,  135, 
                     143,  144,  141,  138,  134, 
                     130,  126,  123,  119,  115, 
                     110,  105,  100,   95,   90, 
                      85,   80,   75,   70,   65, 
                      60,   55,   50,   45,   40, 
                      35,   30,   25,   20,   15, 
                       7,    6,    9,   12,   16
                     };

    int pnY[] = {     76,   78,   76,   70,   60, 
                      60,   50,   42,   34,   28, 
                      22,   18,   14,   12,   10, 
                      10,   10,   12,   14,   18, 
                      22,   28,   34,   42,   50, 
                      66,   68,   70,   72,   74, 
                      76,   78,   76,   70,   60, 
                      60,   50,   42,   34,   28, 
                      22,   18,   14,   12,   10, 
                      10,   10,   12,   14,   18, 
                      22,   28,   34,   42,   50, 
                      66,   68,   70,   72,   74
                     };

    /**
     *  The length of the arrays
     */
    int nLength = 60;

    /**
     * LookupX - looks up the appropriate value of X
     */
    public int LookupX (int nIndex) 
    {
    if ((nIndex >= nLength) || (nIndex < 0))
        return 0;

    return pnX[nIndex];
    }

    /**
     * LookupY - looks up the appropriate value of Y
     */
    public int LookupY (int nIndex) 
    {
    if ((nIndex >= nLength) || (nIndex < 0))
        return 0;

    return pnY[nIndex];
    }

    /**
     * Length - the number of data points stored in the path
     */
    public int Length ()
    {
    return nLength;
    } 
}


/**
 * ReverseCascadeBallPaths class. This is a container 
 * for a paths of juggling balls, the balls are moving 
 * in a reverse cascade pattern
 *
 * @author      Chris Seguin
 * @version     1.0f, May 1, 1995
 */
class ReverseCascadeBallPaths extends BallPaths {
    
    /**
     * Arrays containing the path of the balls
     */
    int pnX[] = {     12,    9,    6,    3,    0, 
                       0,    5,   10,   15,   20, 
                      25,   30,   35,   40,   45, 
                      50,   55,   60,   65,   70, 
                      75,   80,   85,   90,   95, 
                     100,  103,  106,  109,  112, 
                     115,  118,  121,  124,  127, 
                     130,  125,  120,  115,  110, 
                     105,  100,   95,   90,   85, 
                      80,   75,   70,   65,   60, 
                      55,   50,   45,   40,   35, 
                      27,   24,   21,   18,   15  };
    int pnY[] = {     60,   60,   60,   60,   60, 
                      60,   51,   42,   35,   28, 
                      23,   18,   15,   12,   11, 
                      10,   11,   12,   15,   18, 
                      23,   28,   35,   42,   51, 
                      60,   60,   60,   60,   60, 
                      60,   60,   60,   60,   60, 
                      60,   51,   42,   35,   28, 
                      23,   18,   15,   12,   11, 
                      10,   11,   12,   15,   18, 
                      23,   28,   35,   42,   51, 
                      60,   60,   60,   60,   60 };

    /**
     *  The length of the arrays
     */
    int nLength = 60;

    /**
     * LookupX - looks up the appropriate value of X
     */
    public int LookupX (int nIndex) 
    {
    if ((nIndex >= nLength) || (nIndex < 0))
        return 0;

    return pnX[nIndex];
    }

    /**
     * LookupY - looks up the appropriate value of Y
     */
    public int LookupY (int nIndex) 
    {
    if ((nIndex >= nLength) || (nIndex < 0))
        return 0;

    return pnY[nIndex];
    }

    /**
     * Length - the number of data points stored in the path
     */
    public int Length ()
    {
    return nLength;
    } 
}

/**
 * JugglingBall class. This is a juggling ball
 *
 * @author      Chris Seguin
 * @version     1.0f, May 1, 1995
 */
class JugglingBall {

    /**
     * The location on the ball's path
     */
    int nCycleSlot;

    /**
     * The color of the ball - specified by an index into the ball array
     */
    int nBallColor;

    /**
     * The current location of the ball
     */
    int nX;
    int nY;

    /**
     * The path to follow
     */
    BallPaths ptbpPath;

    /**
     * JugglingBall - creates a juggling ball
     */
    public JugglingBall (int nStartPos, int nStartColor, BallPaths ptbpThePath)
    {
    nCycleSlot = nStartPos;
    nBallColor = nStartColor;

    ptbpPath = ptbpThePath;

    nX = ptbpPath.LookupX(nStartPos);
    nY = ptbpPath.LookupY(nStartPos);
    }

    /**
     * Move - moves the ball to the next location
     */
    public void Move ()
    {
    nCycleSlot++;
    if ((nCycleSlot >= ptbpPath.Length ()) || (nCycleSlot <= 0)) {
        nCycleSlot = 0;
        }

    nX = ptbpPath.LookupX(nCycleSlot);
    nY = ptbpPath.LookupY(nCycleSlot);
    }

    /**
     * XLoc - returns the x location
     */
    public int XLoc ()
    {
    return nX;
    }

    /**
     * YLoc - returns the Y location
     */
    public int YLoc ()
    {
    return nY;
    }

    /**
     * Color - returns the color
     */
    public int Color ()
    {
    return nBallColor;
    }
}


/**
 * HandPath class. This is a container for the paths of the hands
 *
 * @author      Chris Seguin
 * @version     1.0f, May 3, 1995
 */
class HandPath {
    
    /**
     * Arrays containing the path of the hands
     */
    int pnLeftHandX[] = {
                       7,    6,    9,   12,   16, 
                      20,   24,   27,   31,   35, 
                      35,   31,   27,   24,   20, 
                      16,   12,    9,    6,    7
                     };

    int pnRightHandX[] = {
                     143,  144,  141,  138,  134, 
                     130,  126,  123,  119,  115, 
                     115,  119,  123,  126,  130, 
                     134,  138,  141,  144,  143
                     };

    int pnHandY[] = {
                      73,   75,   77,   79,   81, 
                      83,   85,   83,   77,   67, 
                      67,   57,   51,   49,   51, 
                      53,   55,   57,   59,   61
                     };


    /**
     *  The length of the arrays
     */
    int nLength = 60;
    int nBalls = 0;

    /**
     * HandPath - creates a hand path
     */
    public HandPath (int nStartBalls) 
    {
    nBalls = nStartBalls;
    }


    /**
     * LookupX - looks up the appropriate value of X
     */
    public int LookupX (int nIndex, boolean bLeft) 
    {
    if ((nIndex >= nLength) || (nIndex < 0))
        return 0;

    //  Limit the lookup to the range
    if (nIndex >= 20 * nBalls)
         nIndex = 19;

    while (nIndex >= 20)
         nIndex -= 20;

    //  Look up the value
    if (bLeft)
        return pnLeftHandX[nIndex];
    else
        return pnRightHandX[nIndex];
    }

    /**
     * LookupY - looks up the appropriate value of Y
     */
    public int LookupY (int nIndex) 
    {
    if ((nIndex >= nLength) || (nIndex < 0))
        return 0;

    //  Limit the lookup to the range
    if (nIndex >= 20 * nBalls)
         nIndex = 19;

    while (nIndex >= 20)
         nIndex -= 20;

    //  Look up the value
    return pnHandY[nIndex];
    }

    /**
     * Length - the number of data points stored in the path
     */
    public int Length ()
    {
    return nLength;
    } 
}


/**
 * Hand class. This is a hand 
 *
 * @author      Chris Seguin
 * @version     1.0f, May 3, 1995
 */
class Hand {

    /**
     * The location on the ball's path
     */
    int nCycleSlot;

    /**
     * Whether this is the left hand
     */
    boolean bLeft;

    /**
     * The current location of the ball
     */
    int nX;
    int nY;

    /**
     * The path to follow
     */
    HandPath phPath;

    /**
     * Hand - creates a hand
     */
    public Hand (int nStartPos, HandPath phThePath, boolean bStartLeft)
    {
    nCycleSlot = nStartPos;
    bLeft = bStartLeft;

    phPath = phThePath;

    nX = phPath.LookupX(nStartPos, bLeft);
    nY = phPath.LookupY(nStartPos);
    }

    /**
     * Move - moves the ball to the next location
     */
    public void Move ()
    {
    nCycleSlot++;
    if ((nCycleSlot >= phPath.Length ()) || (nCycleSlot <= 0)) {
        nCycleSlot = 0;
        }

    nX = phPath.LookupX(nCycleSlot, bLeft);
    nY = phPath.LookupY(nCycleSlot);
    }

    /**
     * XLoc - returns the x location
     */
    public int XLoc ()
    {
    return nX;
    }

    /**
     * YLoc - returns the Y location
     */
    public int YLoc ()
    {
    return nY;
    }
}


/**
 * A juggling demonstration program
 *
 * @author      Chris Seguin
 * @version     1.0f, May 1, 1995
 */
public class Juggling extends java.applet.Applet implements Runnable {

    /**
     * The path of the juggling balls
     */
    BallPaths pjbPaths;

    /**
     * The juggling balls
     */
    JugglingBall pjbBalls[] = {null, null, null};

    /**
     * The paths that the hands trace out
     */
    HandPath phHandPaths;
    /**
     * The hands
     */
    Hand phLeft;
    Hand phRight;

    /**
     * The directory or URL from which the images are loaded
     */
    String dir;

    /**
     * The images used.
     */
    JugglingImages jbiImages;

    /**
     * The thread animating the images.
     */
    Thread kicker = null;

    /**
     * The delay between animation frames
     */
    int nSpeed;

    /**
     * Shape of the window
     */
    int nHeight = 0;
    int nWidth = 0;

    /**
     * The number of balls in the demonstration
     */
    int nBalls = 0;

    /**
     * Parameter info.
     */
    public String[][] getParameterInfo() {
        String[][] info = {
            {"balls",  "int",  "the number of balls to animate"},
            {"speed",  "int",  "the speed the balls move at"},
            {"img",    "urls", "the directory where the images are located"},
        };
        return info;
    }

    /**
     * Initialize the applet.  Get attributes.
     */
    public void init() {
        //  Load the parameters from the HTML file

        String at = getParameter("img");
        dir = (at != null) ? (at + "/") : "";

        at = getParameter("speed");
        nSpeed = (at != null) ? Integer.valueOf (at).intValue() : 20;

        at = getParameter("height");
        nHeight = (at != null) ? Integer.valueOf (at).intValue() : 100;
        at = getParameter("width");
        nWidth = (at != null) ? Integer.valueOf (at).intValue() : 170;

        at = getParameter("balls");
        nBalls = (at != null) ? Integer.valueOf (at).intValue() : 3;
        if ((nBalls > 3) || (nBalls < 1))
            nBalls = 3;

        //  Initialize the Ball variables
        pjbPaths = new CascadeBallPaths ();
        pjbBalls[0] = new JugglingBall ( 0, 0, pjbPaths);
        pjbBalls[1] = new JugglingBall (40, 2, pjbPaths);
        pjbBalls[2] = new JugglingBall (20, 4, pjbPaths);
        
        //  Initialize the hand variables
        phHandPaths = new HandPath (nBalls);
        phLeft = new Hand (5, phHandPaths, true);
        phRight = new Hand (35, phHandPaths, false);

        resize(nWidth, nHeight);
    }

    /**
     * Run the image loop. This methods is called by class Thread.
     * @see java.lang.Thread
     */
    public void run() {
        //  Create the thread
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

        //  Load the images
        jbiImages = new JugglingImages(getDocumentBase(), dir, this);
        
        //  Do the animation
        int ndx = 0;
        while (size().width > 0 && size().height > 0 && kicker != null) {
            for (ndx = 0; ndx < nBalls; ndx++) {
                (pjbBalls[ndx]).Move();
            }
            
            phLeft.Move();
            phRight.Move();
            
            repaint();
            try {Thread.sleep(nSpeed);} catch (InterruptedException e){}
        }
    }
    
    /**
     * Paint the current frame.
     */
    public void paint(Graphics g) {
        update (g);
    }
    public void update(Graphics g) {
        if ((jbiImages != null) && (jbiImages.imgs != null)) {
            //  Erase the background
            g.setColor(java.awt.Color.lightGray);
            g.fillRect(0, 0, nWidth, nHeight);

            int ndx = 0;
            for (ndx = 0; ndx < nBalls; ndx++) {
                if (jbiImages.imgs[pjbBalls[ndx].Color ()] == null) {
                    System.out.print ("ERROR::No Image ");
                    System.out.println (ndx);
                }

                g.drawImage(jbiImages.imgs[pjbBalls[ndx].Color ()], 
                      (pjbBalls[ndx]).XLoc(), (pjbBalls[ndx]).YLoc(), this);
            }
            
            //  Draw the hands
            g.drawImage(jbiImages.imgs[7], 
                        phLeft.XLoc(), phLeft.YLoc(), this);
            g.drawImage(jbiImages.imgs[7], 
                        phRight.XLoc(), phRight.YLoc(), this);
        }
    }

    /**
     * Start the applet by forking an animation thread.
     */
    public void start() {
        if (kicker == null) {
            kicker = new Thread(this);
            kicker.start();
        }
    }

    /**
     * Stop the applet. The thread will exit because kicker is set to null.
     */
    public void stop() {
        kicker = null;
    }
}

