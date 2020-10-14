/* 
SpectrumApplet.java, copyright 1996, Brian Smith
Thanks to Sun Microsystems' Java engineers for suggesting
I use the DirectColorModel to work around the default color
model bug in Win95.

*/

import java.awt.*;
import java.awt.image.*;
import java.applet.*;

/** Applet to display spectrum of colors and allow display of a color's
    RGB components.
  * @author Brian T. Smith
  * @version 1.02 25 Jun 1996
  */
    
public class SpectrumApplet extends Applet 
{

   /** called when Applet is started, adds Spectrum and fields to the 
       Applet's panel
     */
   public void init ()
   {        
           
       Panel p1=new Panel();
       p1.resize(Spectrum.mapWidth,Spectrum.mapHeight);
       p1.setLayout(new FlowLayout());
       p1.add(colorSpectrum);
       
       add("North",p1);
       
       Panel p2=new Panel();
       p2.setLayout(new FlowLayout());
       
       p2.add(new Label("Red:"));
       p2.add(redField=new TextField(5));
       redField.setEditable(false);
      
       p2.add(new Label("Green:"));
       p2.add(greenField=new TextField(5));
       greenField.setEditable(false);
       
       p2.add(new Label("Blue:"));
       p2.add(blueField=new TextField(5));
       blueField.setEditable(false);
       
       add("South",p2);            
   }
   
   /** returns string identifying this applet 
     */
   public String getAppletInfo()
   {
      return "SpectrumApplet.java\nCopyright 1996, Brian T. Smith";
   }
   
  
   
   /** event handler for mouse button hit.  If the target is the Spectrum,
       Spectrum has already processed this event and passed it back to 
       this Applet so it can show the components of the selected color
     * @param e the event
     * @param x the x position of the mouse 
     * @param y the y position of the mouse     
     */
   public boolean mouseDown(Event e, int x, int y)
   {
      int color[]=new int[3];
        
      if(e.target==colorSpectrum)
         {
          int colors[]=new int[3];
          colorSpectrum.reportPixel(colors);
          redField.setText(""+colors[0]);
          greenField.setText(""+colors[1]);
          blueField.setText(""+colors[2]);
          
          return true;
          }
      return super.mouseDown(e,x,y);
   }
   
   
   
  
   private TextField redField;
   private TextField greenField;
   private TextField blueField;
   private Spectrum colorSpectrum=new Spectrum();
}

/** Class to display a spectrum of colors on the screen, and report a color
    that is selected
  * @author Brian T. Smith
  * @version 1.01 25 Jun 1996
    */

class Spectrum extends Canvas
{
   /** mouse hit event handler, saves the color that was selected and
       tells the parent in the window heriarchy that a color was selected
     * @param e the event
     * @param x the x position of the mouse 
     * @param y the y position of the mouse     
     */
   public boolean mouseDown(Event e, int x, int y)
   {
     
      queryPixel=colorPix[(x+(y*mapWidth))];
        // this class records the pixel value where the mouse was hit,
        // then tells its parent in the window heirarchy to process the
        // button event
      return false;
   }
 
   /** Reports the integer value of the last color that was selected
       G=bits 23-16  R=bits 15-8  B=bits 7-0  
     * @param color array of integer, color.length>=3.  After return,
         color[0]=red component
         color[1]=green component
         color[2]=blue component
     */
   public void reportPixel(int color[])
   {
     
     color[0]=(queryPixel%0x10000)/0x100;
     color[1]=queryPixel/0x10000;
     color[2]=queryPixel%0x100;
          
   }
   
   /** Creates an image contaning a spectrum of colors
     */
   public  Spectrum() 
   {
    resize(mapWidth,mapHeight);
    if(colorMap==null) /* compute a spectrum of colors */
      {
     
      int pixIndex=0;
      int red=0;
      int green=0;
      int blue=0;
      int redIncrem=2;
      int greenIncrem=0;
      int rgColor;
      
      System.out.println("Computing color map... "+mapWidth+"x"+mapHeight);     
 
      for(int x=0;x<mapWidth;x++)
         {
   
         rgColor=(red<<8)|(green<<16);
          
          pixIndex=x;
          for(blue=0;blue<mapHeight;blue++)
            {            /* put computed color in the 
                            image source */
            colorPix[pixIndex]=rgColor|blue;
                  /* blue is incremented along the y axis */
            pixIndex+=mapWidth;
            }
        
         red+=redIncrem;
         green+=greenIncrem;
         if(red>255)      /* red maxed out, increment green */
            {
            red=255;
            redIncrem=0;
            greenIncrem=2;
            }
         else if (red<0) /* red min'ed out, decrement green */
            {
            red=0;
            redIncrem=0;
            greenIncrem=-2;
            } 
                   
         if(green>255)  /* green maxed out, decrement red */
            {
            green=255;
            greenIncrem=0;
            redIncrem=-2;
            }
         else if(green<0) /* green min'ed out, no more green or
                             red changes in color */
            {
            green=0;
            greenIncrem=0;
            } 
         
         }
     
            /* create an image from the array of int.  we have
               to create a DirectColorModel and specify the masks
               to prevent an 'invalid page access' under win95
               AWT */
        colorMap=createImage(new MemoryImageSource(mapWidth,mapHeight,
            new DirectColorModel(24,0xff00,0xff0000,0xff),colorPix,0,mapWidth));
         
       System.out.println("Done computing color map.");
      }
     
   }
   
   /** display the spectrum
     * @param g the graphics context
     */
   public void paint(Graphics g)
   {
        
          g.drawImage(colorMap,0,0,null);
    
   }
   
  
   
   
   public static final int mapWidth=256*4/2;
   public static final int mapHeight=256;
   private int colorPix[]=new int[mapWidth*mapHeight];
   private int queryPixel=0;
   private Image colorMap=null;  
}

