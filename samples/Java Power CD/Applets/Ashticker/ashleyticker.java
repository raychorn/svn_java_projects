/* Ashleyticker.class */
//      File: Ashleyticker.java
//      Author: Ashley Cheng  (ackheng@ha.org.hk,  http://www.iohk.com/UserPages/acheng/)
//      Date: Mar 01 96
//      Version: 2.5  (for JDK API 1.0)
//
// Copyright (c) 1995-96 Ashley Cheng. All Rights Reserved.
//

import java.awt.*;
import java.lang.*;
import java.util.*;
import java.net.URL; 
import java.applet.Applet;

public class Ashleyticker extends Applet implements Runnable {
   
/*Declaration and initiation of variables*/

Thread ticker = null;
String label, typeface;
String mousedown, mouseup, mouseright, mouseleft, mouseoutb;
Image image, imborder, imscroll = null;
int restart, counter = 0;
int mcount, icount, index, imageidx, repeat, random;
int step, sensitive, offset, position;
int fontsize, style;
int xpos = -5000;
int ypos;
int x1,x2,y1,y2,scroll;
Vector message, imageUrl;
Color txtCo,bgCo,shadowCo;
Font font;
Image offScreenImage;
Graphics offScreenGraphics;
Dimension offScreenSize;


/*Information*/
public String getAppletInfo() {
return "Yet another Ticker by Ashley Cheng";
}

/*Initialization*/
public void init() {

typeface   = (getParameter("typeface")== null)?   "Arial" : getParameter("typeface");
fontsize = (getParameter("fontsize") == null) ? 24 : (Integer.valueOf(getParameter("fontsize")).intValue());
style = (getParameter("style") == null) ? 0 : (Integer.valueOf(getParameter("style")).intValue());
  if (style >=0 && style <= 2) {
    font = new java.awt.Font(typeface, style, fontsize);
  }
  else {
    font = new java.awt.Font(typeface, Font.PLAIN, fontsize);
  }
mcount =  (getParameter("count") == null) ? 1 : (Integer.valueOf(getParameter("count")).intValue());
repeat =  (getParameter("repeat") == null) ? 5 : (Integer.valueOf(getParameter("repeat")).intValue());
random =  (getParameter("random") == null) ? 0 : (Integer.valueOf(getParameter("random")).intValue()); 
message = new Vector(mcount);
for( int i=0 ; i< mcount ; ++i )
	{
	   if  (getParameter("message" + i)==null)	{
	       message.addElement( "Applet written by Ashley Cheng Feb 07 96" );
	   }
	   else {
	      message.addElement( getParameter("message" + i) );	
		 }	
	 }
mouseup    = (getParameter("mouseup")==null)?     "Thank you"                                : getParameter("mouseup");
mousedown  = (getParameter("mousedown")==null)?   "Oh, don't tickle me!!"                    : getParameter("mousedown");
mouseright = (getParameter("mouseright")==null)?  "Let me out!!"                             : getParameter("mouseright");
mouseleft  = (getParameter("mouseleft")==null)?   "Don't be so impatient"                    : getParameter("mouseleft");
mouseoutb  = (getParameter("mouseoutb")==null)?   "Let go your mouse please"                 : getParameter("mouseoutb");
step = (getParameter("step")==null)? 2 : (Integer.valueOf(getParameter("step")).intValue());
sensitive = (getParameter("sensitive")==null)? 2 : (Integer.valueOf(getParameter("sensitive")).intValue());
offset = (getParameter("offset")==null)? 0 : (Integer.valueOf(getParameter("offset")).intValue());
position = (getParameter("position")==null)? 1 : (Integer.valueOf(getParameter("position")).intValue());
txtCo = readColor(getParameter("text"), Color.black);
bgCo = readColor(getParameter("backgd"), getBackground());
shadowCo = readColor(getParameter("shadow"), Color.white);
icount =  (getParameter("icount") == null) ? 1 : (Integer.valueOf(getParameter("icount")).intValue());
imageUrl = new Vector(icount);
for( int i=0 ; i< icount ; ++i )
	{
           if  (getParameter("image" + i) !=null)      {
              imageUrl.addElement( getParameter("image" + i) );
           }
}

if (getParameter("image") != null) {
    image = getImage(getDocumentBase(), getParameter("image"));
 }
if (getParameter("imborder") != null) {
    imborder = getImage(getDocumentBase(), getParameter("imborder"));
 }
}



/*Painting the text or image*/
public void paint(Graphics g) {

g.setColor(bgCo);
g.fillRect(0,0,size().width,size().height);

if (image != null) {
g.drawImage(image, 0,0,size().width, size().height, this);
}

if (getParameter("image0") == null) {
    g.setFont(font);                    
    FontMetrics fm = g.getFontMetrics();
                                        
    if (position == 0) {  
      ypos = fm.getHeight()/2;               
            
    }                                                    
    else if (position == 1) {                            
      ypos = size().height/2;                             
    }                                                    
    else if (position == 2) {                            
       ypos = size().height - fm.getHeight()/2 ;                        
    }                                                    
    else {                                               
      ypos = position;                                   
    }                                                    
    if (random == 0) {                                   
      index = (index < mcount)? index : 0;                 
    }                                                    
    
    label = (String)(message.elementAt(index));          
    scroll = fm.stringWidth(label);  

    restart = scroll + 10;              
    if (xpos <= 0 - restart) {          
    	xpos = size().width - 10 ;  
    	counter = counter + 1;          
    }                                   
    else {                              
    	xpos = xpos - step;         
    }                                   
   
    if (offset > 0) {
      g.setColor(shadowCo);
      g.drawString(label, xpos+offset, ypos+offset);
    }
    g.setColor(txtCo);
    g.drawString(label, xpos, ypos);
           
}                                               

else {
    
    if (random == 0) {
    imageidx = (imageidx < icount)? imageidx : 0;
    }
                                           
    imscroll = getImage(getDocumentBase(), (String)imageUrl.elementAt(imageidx)); 
    Dimension r=getImageDimensions(imscroll);       
    if (position == 1) {                            
      ypos = (size().height/2)-(r.height/2);        
                                
    }                                               
    else if (position == 2) {                       
       ypos = size().height - r.height;        
    }                                               
    else {                                          
     ypos = position;                               
    }                                               
    scroll = r.width;   
         
    restart = scroll + 10; 
    if (xpos <= 0 - restart) {
    	xpos = size().width - 10 ;
    	counter = counter + 1;                       
    }
    else {
    	xpos = xpos - step;
    }                       
 
    g.drawImage(imscroll, xpos, ypos, this);                      
 
                                   
}                                  
      
  if (imborder != null) {                                            
  g.drawImage(imborder, 0,0,size().width, size().height, this);    
  }                                                                  
  
  if (random == 0 && counter > repeat) {
       restart = 0;
       index = index + 1;
       imageidx = imageidx +1;
       counter = 1;
   }

}


/*Starting the thread*/
public void start() {   

if (random != 0) { 
 imageidx = (int)((Math.random()*1000)%icount);
 index = (int)((Math.random()*1000)%mcount);
}

ticker = new Thread(this);
ticker.start();
}

/*Stopping the thread*/
public void stop() {
 ticker.stop();
}

/*Running the thread*/
public void run() {                                                  
 showStatus ("Try clicking and dragging your mouse on the ticker!");  
 while(ticker !=null){                                                
   try {Thread.sleep(10);}catch(InterruptedException e){}              
   repaint();                                                          
 }                                                                   
}                                                                    


/* Avoiding flickers */

 
public void update (Graphics g) {
             
     Dimension d = size();   
     if ((offScreenImage == null) 
      || (d.width != offScreenSize.width) 
      || (d.height != offScreenSize.height)) {
	  offScreenSize = d;
	  offScreenImage = createImage(d.width, d.height);
          offScreenGraphics = offScreenImage.getGraphics();
	  
      }
      offScreenGraphics.setFont(getFont());
      offScreenGraphics.fillRect(0,0,d.width, d.height);

      paint(offScreenGraphics);
      g.drawImage(offScreenImage, 0, 0, null);

}



/*Catching mouse events*/
public boolean handleEvent(Event evt) {

 if (evt.id == Event.MOUSE_DOWN){
         x1=evt.x;
         y1=evt.y;
         showStatus(mousedown);
         ticker.suspend();
         return true;
 }
 if (evt.id == Event.MOUSE_DRAG) {
      x2=evt.x;
      y2=evt.y;

      if (y2 >0 && y2 < size().height) {
             
              if (x2>x1) {
              xpos = xpos + sensitive;
              repaint();
              showStatus(mouseright);
                  x1=x2;
             }
             if (x1>x2) {
             xpos = xpos - sensitive;
                  repaint();
              showStatus(mouseleft);
                  x1=x2;
             }
          }
          else {
            showStatus(mouseoutb);
          }
      return true;
   }
   if (evt.id == Event.MOUSE_UP) {
       ticker.resume();
           showStatus(mouseup);
               return true;
   }
   else        {
   return super.handleEvent(evt);
   }
 }

/*Getting Dimensions of Images*/
public Dimension getImageDimensions(Image im) {
	int width;
	int height;
	
        width = im.getWidth(this);
        height = im.getHeight(this);
	return new Dimension(width, height);
}

/*Reading color*/
public Color readColor(String aColor, Color aDefault) {
if (aColor == null) { return aDefault; }
int r, g, b;
StringTokenizer st = new StringTokenizer(aColor, ",");
try {
  r = Integer.valueOf(st.nextToken()).intValue();
  g = Integer.valueOf(st.nextToken()).intValue();
  b = Integer.valueOf(st.nextToken()).intValue();
  return new Color(r,g,b);
}
catch (Exception e) { return aDefault; }
}


}
/* The End */
