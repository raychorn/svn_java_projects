/* ----------------------------------------------------------------
 * Clock 2.0 Beta version, Copyright (c) 1995 Nils Hedstrom, All Rights Reserved.
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for NON-COMMERCIAL purposes and without fee is hereby
 * granted provided that this copyright notice and appropiate documention
 * appears in all copies.
 * 
 * NILS HEDSTROM MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. NILS HEDSTROM SHALL NOT BE LIABLE
 * FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * For documentation look at http://www-und.ida.liu.se/~d94nilhe/java/applets.html */

import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Color;
import java.awt.Image;
import java.util.Date;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.StringTokenizer;
import java.lang.Math;
import Needle;


public class Clock extends java.applet.Applet implements Runnable
{
  int width,height,num_lines,sleep,timezone,backgroundType;
  Polygon clockBackground;
  URL homepage;
  private Needle hour,minute,second;
  double pi=3.1415926535f;
  Color clockBackground_col,clockBackgroundBorder_col,backgroundBorder_col,background_col;
  Thread animate=null;
  Image backBuffer;
  Image backgroundImage;
  Graphics backGC;
  
  public void init() // Init all the variables and classes
    {
      try {
	homepage=new URL("http://www-und.ida.liu.se/~d94nilhe/java/applets.html");
      } catch (MalformedURLException e) {}
      hour=new Needle(readColor(getStringAttribute("hour_col",null),Color.red),
		      readColor(getStringAttribute("hour_border_col",null),Color.black),
		      getIntAttribute("hour_type",0,3,3),
		      getIntAttribute("hour_width",0,7,100),
		      size().width/2,size().height/2,
		      (size().width*getIntAttribute("hour_len",0,90,100))/200,
		      (size().height*getIntAttribute("hour_len",0,70,100))/200,0.);
      minute=new Needle(readColor(getStringAttribute("minute_col",null),Color.green),
			readColor(getStringAttribute("minute_border_col",null),Color.black),
			getIntAttribute("minute_type",0,3,3),
			getIntAttribute("minute_width",0,5,100),
			size().width/2,size().height/2,(size().width*getIntAttribute("minute_len",0,75,100))/200,
			(size().height*getIntAttribute("minute_len",0,90,100))/200,0.);
      second=new Needle(readColor(getStringAttribute("second_col",null),Color.blue),
			readColor(getStringAttribute("second_border_col",null),Color.black),
			getIntAttribute("second_type",0,3,3),
			getIntAttribute("second_width",0,3,100),
			size().width/2,size().height/2,(size().width*getIntAttribute("second_len",0,90,100))/200,
			(size().height*getIntAttribute("second_len",0,90,100))/200,0.);
      updateNeedles();
      backgroundType=getIntAttribute("background_type",0,0,2);
      if(backgroundType!=0) backgroundImage = getImage(getCodeBase(), getParameter("background_image"));
      num_lines=getIntAttribute("num_lines",3,12,1000);
      makeClockBackground(); // creating clockbackground with num_lines corners
      sleep=getIntAttribute("sleep",5,1000,20000);
      clockBackground_col=readColor(getStringAttribute("clock_background_col",null),Color.gray);
      clockBackgroundBorder_col=readColor(getStringAttribute("clock_background_border_col",null),Color.gray);
      background_col=readColor(getStringAttribute("background_col",null),Color.lightGray);
      backgroundBorder_col=readColor(getStringAttribute("background_border_col",null),Color.lightGray);
      timezone=getIntAttribute("timezone",-12,-13,12);
      resize(size().width,size().height);
      try
	{
	  backBuffer = createImage(size().width, size().height);
	  backGC = backBuffer.getGraphics();
	}
      catch (Exception e) backGC=null;
    }
  
  public void updateNeedles() // This updates the angle of each needle.
    {
      Date today= new Date();
      if(timezone!=-13)
	{
	  int hours=today.getHours();
	  int minutes=today.getMinutes();
	  hours+=timezone+today.getTimezoneOffset()/60;
	  minutes+=today.getTimezoneOffset()%60;
	  today.setMinutes(minutes);
	  today.setHours(hours);
	}
      second.setAngle(2.*pi*(today.getSeconds()-15)/60.);
      minute.setAngle(2.*pi*(today.getMinutes()-15)/60.);
      hour.setAngle(2.*pi*((today.getHours()%12-3)/12.+today.getMinutes()/720.));
    }
  
  public void paintApplet(Graphics g)  // Paint the applet 
    {
      drawClockBackground(g);
      hour.drawNeedle(g);
      minute.drawNeedle(g);
      second.drawNeedle(g);
    }      
  
  
  public void update(Graphics g) // When update is called
    {
      if (backBuffer != null) 
	{ 
	  // double-buffering available
	  backGC.setColor(background_col);
	  backGC.fillRect(0,0,size().width,size().height);
	  backGC.setColor(backgroundBorder_col);
	  backGC.drawRect(0,0,size().width-1,size().height-1);
	  paintApplet(backGC);
	  g.drawImage(backBuffer, 0, 0, this);
	} else 
	  {
	    // no double-buffering
	    g.clearRect(0,0,size().width,size().height);
	    paintApplet(g);
	  }
    }
  public void run() //Run the applet
    {
      while (true) 
	{
	  updateNeedles();
	  repaint();
	  try {Thread.sleep(sleep);} catch (InterruptedException e){}
	  
	}
    }
  
  public boolean mouseEnter(java.awt.Event evt) // When the mouse enters the applet
    {
      getAppletContext().showStatus("Who made this clock?");
	    return true;
    }
  
  public boolean mouseExit(java.awt.Event evt) // When the mouse leaves the applet
    {
      getAppletContext().showStatus("");
      return true;
    }
  
  public boolean mouseDown(java.awt.Event evt, int x,int y) // When a mouse button is pressed 
    {
      getAppletContext().showDocument(homepage);
      return true;
    }

  public void makeClockBackground() // Creates a polygon-background with num_lines-corners
    {
      double add,count;
      clockBackground=new Polygon();
      add=2.*pi/num_lines;
      for(count=0;count<=2.*pi;count+=add)
	{
	  clockBackground.addPoint(size().width/2+(int)(size().width*Math.cos(count)/2.),
			  size().height/2+(int)(size().height*Math.sin(count)/2.));
	}
    }
      
  public void drawClockBackground(Graphics g) // Draws the background of the Clock
    {
      if(backgroundType!=1)
	{
	  g.setColor(clockBackground_col);
	  g.fillPolygon(clockBackground);
	  g.setColor(clockBackgroundBorder_col);
	  g.drawPolygon(clockBackground);
	}
      if(backgroundType!=0)
	{
	  int img_width=backgroundImage.getWidth(null);
	  int img_height=backgroundImage.getHeight(null);
	  int x=(size().width-img_width)/2;
	  int y=(size().height-img_height)/2;
	  if (x<0) x=0;
	  if(y<0) y=0;
	  if((img_width!=-1) && (img_height!=-1))
	    g.drawImage(backgroundImage,x,y,null);
	}

    }
public void start()  // When the applet is started 
    {
      if (animate == null) {
	animate = new Thread(this);
	animate.start();
      }
    }
	
  public void stop() // When the applet is stopped 
    {
      if (animate != null) {
	animate.stop();
	animate=null;
      }
    }
  
  
  public String getStringAttribute (String att, String def) // Get a string parameter 
    {
      String ret;
      
      try {
	ret = getParameter(att);
	if (ret.length() < 1)
	  return def;
	return ret;
	    } catch(Exception e) {
	      return def;
	    }
      
    }   
  
  public Color readColor(String aColor, Color aDefault) // Creates a color from a string
    {
      if (aColor == null) { return aDefault; }
      
      int r;
      int g;
      int b;
      StringTokenizer st = new StringTokenizer(aColor, ",");
      
      try {
	r = Integer.valueOf(st.nextToken()).intValue();
	g = Integer.valueOf(st.nextToken()).intValue();
      b = Integer.valueOf(st.nextToken()).intValue();
	return new Color(r,g,b);
      }
      catch (Exception e) {
	return aDefault;
      }
    }
  
  public int getIntAttribute (String att, int min, int def, int max) // Get a integer parameter 
    {
	    try {
	      int ret = Integer.parseInt(getParameter(att));
	      if (ret < min)
		return min;
	      if (ret > max)
		return max;
	      return ret;
	    } catch(Exception e) {
	      return def;
	    }
	    
	  } 
  
}





