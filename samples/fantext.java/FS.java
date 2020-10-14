/***************************************************************************

 Copyright (c) 1996 by Wong Ting Sieong 
 
 Permission to use, copy, and distribute this software for NON-COMMERCIAL
 and COMMERCIAL purposes and without fee is hereby granted provided that
 this copyright notice appears in all copies.

 The FlexScroll class is written to allow implementation either through html
 document or to be imported(hard-coded) to run in any program. The later
 option allows a more flexible and feature-rich implementation.

***************************************************************************/
 
import java.util.*;
import java.awt.*;
import java.awt.Event;
import java.applet.Applet;
import java.awt.MediaTracker;

class FlexScroll 
{
   FS     appl;     // Application using the class FS                      
   String label;    // The message

   // States in each instantiation of FlexScroll permit an instance to start
   // based on the states of other instances.
   static final int START = 0;     
   static final int STOP  = 1;
   int state              = STOP;            
                                       // Scrolling types for the message
   static final int LEFT      = 0;     // Going left
   static final int RIGHT     = 1;     //   "   right
   static final int UP        = 2;     //   "   up
   static final int DOWN      = 3;     //   "   down
   static final int DIAGONAL  = 4;     //   "   top-left to bottom-right
   static final int BOUNCE    = 5;     // Bouncing effect
   int scrolltype             = LEFT;  // The selected scrolling type

   Color       fground;                // Message foreground colour     
   boolean     toChangeColor=false;    // Want to change colour?
   Font        font;                   // Message font
   FontMetrics fm;                     // Message font metrics
      
   int      x,      y;        // x and y position of the message     
   int   dx=1,   dy=1;        // Distance of x/y to change for every step    
   int startx, starty;        // The starting x and y position of the message
   int  stopx,  stopy;        // The x and y position for the message to stop
   boolean toResetXY=false;   // Want to reset after the message stopped


   // ---------- Bounce Section ----------
   // Bouncing sequence with changing value for y simulating a sinusodial
   // function
   int bounceSequence[] = { 2, 4, 5, 7, 9,10,11,13,14,15,
                           13,11, 9, 7, 4, 3, 2, 1, 0,
                            1, 2, 4, 5, 6, 7, 9,10,11,12,
                            9, 8, 7, 6, 5, 4, 3, 2, 0,
                            1, 2, 3, 4, 5, 6, 7, 8,
                            7, 6, 5, 4, 3, 2, 1, 0,
                            1, 2, 3, 4, 5, 6,
                            5, 4, 3, 2, 1, 0,
                            1, 2, 3, 4,
                            3, 2, 1, 0,
                            1, 2, 3,
                            2, 1, 0,
                            0, 0, 0, 0, 0
                          };
   int numSequence    = 85;    // Number of bouncing sequence
   int sequence       = 0;     // The current sequence
   boolean toBounce   = false; // Want to bounce?
   boolean doneBounce = true;  // Flag to signal end of bounce sequence

   // Constructor
   public FlexScroll(FS appl, String label, int scrolltype, Font font) 
   {
      this.appl       = appl;
      this.label      = label;
      this.scrolltype = scrolltype;
      this.font       = font;
      fm              = appl.getFontMetrics(font);
      SetDefaultStartPoint(scrolltype);
      SetDefaultStopPoint(scrolltype);
   }

   // Setting default starting point of a particular scrolling type
   void SetDefaultStartPoint(int scrolltype)
   {
      switch(scrolltype)
      {
         case LEFT:
            SetStartPoint(appl.size().width+fm.stringWidth(label),
                          appl.size().height/2);
            break;
         case RIGHT:
            SetStartPoint(-fm.stringWidth(label),appl.size().height/2);
            break;
         case UP:
            SetStartPoint(appl.size().width/2,
                          appl.size().height+fm.getHeight());
            break;
         case DOWN:
            SetStartPoint(appl.size().width/2,0);
            break;
         case DIAGONAL:
            SetStartPoint(0,0);
            break;
      }
   }

   // Setting the starting point for the message
   void SetStartPoint(int startx, int starty)
   {
      x = this.startx = startx;
      y = this.starty = starty;
   }

   // Setting the default stopping point for a particular scrolling type
   void SetDefaultStopPoint(int scrolltype)
   {
      switch(scrolltype)
      {
         case LEFT:
            SetStopPoint(-fm.stringWidth(label),
                         appl.size().height/2);
            break;
         case RIGHT:
            SetStopPoint(appl.size().width+fm.stringWidth(label),
                         appl.size().height/2);
            break;
         case UP:
            SetStopPoint(appl.size().width/2,-fm.getHeight());
            break;
         case DOWN:
            SetStopPoint(appl.size().width/2,
                         appl.size().height);
            break;
         case DIAGONAL:
            SetStopPoint(appl.size().width,appl.size().height);
            break;
      }
   }

   // Setting the message starting point on the x axis
   void SetStartX(int startx)
   {
      x = this.startx = startx;
   }

   // Setting the message starting point on the y axis
   void SetStartY(int starty)
   {
      y = this.starty = starty;
   }

   // Setting the message stopping value for a particular scroll type
   void SetStopPoint(int stopx, int stopy)
   {
      switch (this.scrolltype) {
         case LEFT:
            this.stopx = stopx;
	    break;
         case RIGHT:
            this.stopx = stopx;
         case UP:
            this.stopy = stopy;
            break;
         case DOWN:
            this.stopy = stopy;
	    break;
         case DIAGONAL:
            this.stopx = stopx;
            this.stopy = stopy;
            break;
      }
   }

   // Setting the message stopping value on the y axis
   void SetStopX(int stopx)
   {
      this.stopx = stopx;
   }

   // Setting the message stopping value on the y axis
   void SetStopY(int stopy)
   {
      this.stopy = stopy;
   }

   // Setting the change on the x-axis for every repaint
   void SetDX(int dx)
   {
      this.dx = dx;
   }

   // Setting the change on the y-axis for every repaint
   void SetDY(int dy)
   {
      this.dy = dy;
   }

   // Setting the message to start scrolling
   void Start()
   {   state = START;  }

   // Setting the message to stop scrolling
   void Stop()
   {   state = STOP;   }

   // Procedure to set font for the message 
   void SetFont(String typeface, int style, int ptsize)
   {
      font = new Font(typeface, style, ptsize);
   }

   // Overloading the above procedure
   void SetFont(String typeface, int ptsize)
   {
      font = new Font(typeface, Font.PLAIN, ptsize);
   }

   // Setting the foreground colour for the message
   void SetColor(int fred, int fgreen, int fblue)
   {
      fground       = new Color(fred,fgreen,fblue);
      toChangeColor = true;
   }

   // Flagging to signal the resetting of the scrolling
   void SetResetOn()
   {
      toResetXY = true;
   }

   // Resetting the message starting position after reaching its stopping position
   void ResetXY()
   {
      if(toResetXY)
      {
         SetStartPoint(startx,starty);
      }  
   }

   // Changing the x and y position to display a message for a particular
   // scroll type
   void MoveText()
   {
      switch(scrolltype)
      {
         case LEFT:
            x -= dx;
            break;
         case RIGHT:
            x += dx;
            break;
         case UP:
            y -= dy;
            break;
         case DOWN:
            y += dy;
            break;
         case DIAGONAL:
            x += dx;
            y += dy;
            break;
      }
   }

   // Flagging to signal the bouncing of a going DOWN message when it hits
   // the stopping point
   void SetBounce()
   {
      toBounce   = true;
      doneBounce = false;
      SetResetOn();
   }

   // Changing the y position of the message through the bouncing sequence
   // to stimulate a bouncing effect
   int BounceText(int pos)
   {
      if(sequence<numSequence)
      {
         pos = pos - bounceSequence[sequence];
         sequence++;
      }
      else
      {
         doneBounce = true;
         sequence = 0;
      }
      return pos;
   }

   // Checking for the stopping point
   boolean isStopPoint()
   {
      boolean stop=false;
      switch(this.scrolltype)
      {
         case LEFT:
            if(x <= stopx)
               stop = true;
            break;
         case RIGHT:
            if(x >= stopx)
               stop = true;
            break;
         case UP:
            if(y <= stopy)
               stop = true;
            break;
         case DOWN:
            if(y >= stopy)
               stop = true;
            break;
         case DIAGONAL:
            if((x>=stopx) && (y>=stopy))
               stop = true;
            break;
      }
      return stop;
   }

   // Paint the message
   public void paint(Graphics g)
   {
      if(toChangeColor)
      {
         g.setColor(fground);
      }
      g.setFont(font);

      if (state == START)
      {
         if(!isStopPoint())
         {
            MoveText();
            g.drawString(label,x,y);
         }
         else
         {
            if(!doneBounce)
            {
               g.drawString(label,x,BounceText(y));
            }
            else
            {
               if(toBounce)
               {
                 doneBounce=false;
               }
               ResetXY();
               g.drawString(label,x,y);
            }
         }
      }
   }
}

/*-------------------------------------------------------------------*/
public class FS extends Applet implements Runnable {
    // Objects for updating graphics off-screen
    private Image     osImage;
    private Graphics  osGraphics; 
    private Dimension osSize;   

    Font   font;                                // Default font 
    int    ptsize=25;                           // Default font point size
    int    fred=0,   fblue=0,   fgreen=0;       // Default foreground colour
    int    bred=255, bblue=255, bgreen=255;     // Default background colour

    int     delay     =10;                      // Default delay time between each repaint
    Thread  scroll    =null;                    
    boolean suspended =false;

    Vector Flex = null;                         // Vector to store pointers to multiple FlexScroll instances
    static final int MaxFlexScroll = 20;        // Maximum vector size
    int numFlex;                                // Number of FlexScroll instances
    FlexScroll tmp;                             // A temporary FlexScroll

    MediaTracker tracker;                       // MediaTracker to maintain a proper sequence in loading of images 
    Image bgimage;                              // An optional background image


    public void init()
    {
        initParameters();
        font = new Font("TimesRoman",Font.PLAIN,ptsize);  // setting the default font
    }


    // Getting parameters from the html document calling this applet.
    // Simplicity is of utmost importance in this section to guarantee future
    // extensibility.
    void initParameters()
    {
      String s, parname;
      String label;
      int    scrolltype;
      String typeface;
      int    style;
      int    ptsize;
      int    fred, fgreen, fblue;
      int    startx, starty;
      int    stopx, stopy;
      int    dx, dy;

      tracker = new MediaTracker(this);
      s = getParameter("bgimage");
      if(s!=null)
      {
         bgimage = getImage(getDocumentBase(),s);
         tracker.addImage(bgimage,0);
      }

      s = getParameter("delay");
      delay = (s == null) ? 100 : (Integer.valueOf(s).intValue());

      numFlex = 0;
      Flex = new Vector(MaxFlexScroll);
      for (int i=1; i<=MaxFlexScroll; i++)
      {
         parname = "label" + i;
         s = getParameter(parname);
         label = (s==null) ? "FlexText" : s;
         if(s==null) break;

         parname = "scrolltype" + i;
         s = getParameter(parname);
         if (s == null)
            scrolltype = FlexScroll.LEFT;
         else if (s.equalsIgnoreCase("LEFT"))
            scrolltype = FlexScroll.LEFT;
         else if (s.equalsIgnoreCase("RIGHT"))
            scrolltype = FlexScroll.RIGHT;
         else if (s.equalsIgnoreCase("UP"))
            scrolltype = FlexScroll.UP;
         else if (s.equalsIgnoreCase("DOWN"))
            scrolltype = FlexScroll.DOWN;
         else if (s.equalsIgnoreCase("DIAGONAL"))
            scrolltype = FlexScroll.DIAGONAL;
         else
            scrolltype = FlexScroll.LEFT;

         parname = "typeface" + i;
         s = getParameter(parname);
         if (s == null)
            typeface = "TimesRoman";
         else if (s.equalsIgnoreCase("TimesRoman"))
            typeface = "TimesRoman";
         else if (s.equalsIgnoreCase("Courier"))
            typeface = "Courier";
         else if (s.equalsIgnoreCase("Helvetica"))
            typeface = "Helvetica";
         else if (s.equalsIgnoreCase("Dialog"))
            typeface = "Dialog";
         else
            typeface = "TimesRoman";

         parname = "style" + i;
         s = getParameter(parname);
         if (s == null)
            style = Font.PLAIN;
         else if (s.equalsIgnoreCase("PLAIN"))
            style = Font.PLAIN;
         else if (s.equalsIgnoreCase("BOLD"))
            style = Font.BOLD;
         else if (s.equalsIgnoreCase("ITALIC"))
            style = Font.ITALIC;
         else
            style = Font.PLAIN;

         parname = "ptsize" + i;
         s = getParameter(parname);
         ptsize = (s == null) ? 25 : (Integer.valueOf(s).intValue());

         parname = "fred" + i;
         s = getParameter(parname);
         fred = (s == null) ? 255 : (Integer.valueOf(s).intValue());

         parname = "fgreen" + i;
         s = getParameter(parname);
         fgreen = (s == null) ? 0 : (Integer.valueOf(s).intValue());

         parname = "fblue" + i;
         s = getParameter(parname);
         fblue = (s == null) ? 0 : (Integer.valueOf(s).intValue());

         Flex.addElement(new FlexScroll(this, label,scrolltype,
                         new Font(typeface,style,ptsize)));
         tmp = (FlexScroll)(Flex.elementAt(numFlex));
         tmp.Start();
         tmp.SetColor(fred,fgreen,fblue);

         parname = "bounce" + i;
         s = getParameter(parname);
         if (s!=null)
         {
            tmp.SetBounce();
         }

         parname = "reset" + i;
         s = getParameter(parname);
         if (s!=null)
         {
            tmp.SetResetOn();
         }

         parname = "startx" + i;
         s = getParameter(parname);
         if (s != null)
         {
            startx = Integer.valueOf(s).intValue();
            tmp.SetStartX(startx);
         }

         parname = "starty" + i;
         s = getParameter(parname);
         if (s != null)
         {
            starty = Integer.valueOf(s).intValue();
            tmp.SetStartY(starty);
         }

         parname = "stopx" + i;
         s = getParameter(parname);
         if (s != null)
         {
            stopx = Integer.valueOf(s).intValue();
            tmp.SetStopX(stopx);
         }

         parname = "stopy" + i;
         s = getParameter(parname);
         if (s != null)
         {
            stopy = Integer.valueOf(s).intValue();
            tmp.SetStopY(stopy);
         }

         parname = "dx" + i;
         s = getParameter(parname);
         if (s != null)
         {
            dx = Integer.valueOf(s).intValue();
            tmp.SetDX(dx);
         }

         parname = "dy" + i;
         s = getParameter(parname);
         if (s != null)
         {
            dy = Integer.valueOf(s).intValue();
            tmp.SetDY(dy);
         }

         numFlex++;
      }

      // The default message if none is included in the html document.
      if(numFlex==0)   
      {
         Flex.addElement(new FlexScroll(this,"Thank you for using FlexScroll",
                         FlexScroll.DOWN,new Font("Arial",Font.BOLD,30)));
         tmp = (FlexScroll)(Flex.elementAt(numFlex));
         tmp.Start();
         tmp.SetColor(0,0,255);
         tmp.SetBounce();
         tmp.SetStartX(0);
         numFlex++;
      }
   } 


   public void paint(Graphics g)
   {
       g.setColor(new Color(bred,bgreen,bblue));
       g.fillRect(0,0,size().width,size().height);
       
       g.setFont(font);

       if(bgimage!=null)
       {
          g.drawImage(bgimage,0,0,this);
       }
       if(tracker.checkID(0))
       {
          for(int i=0; i<numFlex;i++)
          {
             tmp = (FlexScroll)(Flex.elementAt(i));
             tmp.paint(g);
          }
       }
   }


   public final synchronized void update (Graphics theG)
   {
      Dimension d = size();
      if((osImage == null) || (d.width != osSize.width) ||
         (d.height != osSize.height))
      {
         osImage = createImage(d.width, d.height);
         osSize = d;
         osGraphics = osImage.getGraphics();
         osGraphics.setFont(getFont());
      }
      osGraphics.setColor(getBackground());
      osGraphics.fillRect(0,0,d.width, d.height);
      paint(osGraphics);
      theG.drawImage(osImage, 0, 0, null);
   }


   public void start()
   {
       if(scroll==null)  // Starts the Thread if it is null.
       {
          scroll = new Thread(this);
          scroll.start();
       }
   }


   public void run()
   {
      try
      {
         tracker.waitForID(0);        // Wait for the image to finish loading
      } catch(InterruptedException e)
      {
         return;
      }
      Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
      while(scroll!=null)
      {
         try {
            Thread.sleep(delay);
         }
         catch (InterruptedException e) {}
         repaint();
      }
   }


   public boolean handleEvent (Event evt)
   {
       if(evt.id == Event.MOUSE_DOWN)
       {
          if(suspended)
          {
             scroll.resume();
          }
          else
          {
             scroll.suspend();
          }
          suspended = !suspended;
       }
       return true;
   }


   public void stop()
   {
       if(scroll != null)
          scroll.stop();
      scroll = null;
   }
}
