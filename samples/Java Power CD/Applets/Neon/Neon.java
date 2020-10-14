 
/*
 *
 * Written By: Scott Clark
 * Date: Jan 16, 96
 * Description: Neon class
 * Copyright 1996 Scott Clark
 *
 */

import java.awt.Graphics;
import java.awt.Image;
import java.lang.Math;

public class Neon extends java.applet.Applet implements Runnable {
        Image mAge[];
        int iMagNdx=0;
        Thread endit;
         

        public void init() {
                resize(500,250);
                
                                }

        public void Paint(Graphics g) {
                update(g);
                }
        public void update(Graphics g) {
                if(mAge[iMagNdx]==null)
                        g.drawString("Error loading pic", 0, 170);
                g.drawImage(mAge[iMagNdx],0,0, this);
                }
        public void start() {
                if(endit == null) {
                        endit=new Thread(this);
                        endit.start();
                        }
                }

/** Parameter Info. */
  public String[][] getParameterInfo() {
    String[][] info = {
      {"picture1",    "String",  "First image to be displayed." },
      {"picture2",    "String",  "Second image to be displayed"},
    };
    return info;
  }



 /** Applet Info. */
  public String getAppletInfo() {
    return "Neon.java, V 1, 1/16/96 by Scott Clark";
  }    


   
        public void stop() {
                endit=null;
                }
        
        public void run() {
                mAge=new Image[2];
                String m1 = getParameter( "picture1" );
                String m2 = getParameter( "picture2" );
                mAge[0]=getImage(getDocumentBase(), m1);
                mAge[1]=getImage(getDocumentBase(), m2);
                for(;;) {
                        repaint();
                        iMagNdx=iMagNdx==0 ? 1 : 0;
try {Thread.sleep( (int) (Math.random()*500));} catch (InterruptedException e){}
                        }
                }
        }

