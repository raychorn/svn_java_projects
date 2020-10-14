/* ----------------------------------------------------------------
 * Needle 1.0, Copyright (c) 1995 Nils Hedstrom, All Rights Reserved.
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
 * For documentation look at http://www-und.ida.liu.se/~d94nilhe/java/applets.html 
 * 
 * This class creates and manages Needles.
 * */

import java.awt.Graphics;
import java.awt.Color;
import java.lang.Math;

/** 
This maintain a Needle (or a hand).
@version 	1.0 29 November 1995
@author         <A HREF="http://www.edu.isy.liu.se/~d94nilhe/">Nils Hedström</A>
*/

public class Needle
{
  private int centerx, centery, lengthx, lengthy,width,type;
  private double angle;
  private Color color,border;
  
  /**
   * Constructs a needle.
   * @param in_color          The color
   * @param in_border         The color of the border (the border is 1 pixel wide)
   * @param in_type           The type (0=no Needle, 1=Triangle, 2=Rectangle, 3=Romb)
   * @param in_width The witdh
   * @param in_centerx The x-cord of the center
   * @param in_centery The y-cord of the center
   * @param in_lengthx The length in the x-axis
   * @param in_lengthy The lenght in the y-axis
   * @param in_angle The angle (0=To the right, pi=To the left)
  */
  public Needle(Color in_color, Color in_border,int in_type,int in_width,int in_centerx, int in_centery, int in_lengthx, int in_lengthy, double in_angle)
    {
      color=in_color;                 // The color
      border=in_border;               // The color of the border (1 pixel width)
      type=in_type;                   // The type (0=no Needle, 1=Triangle, 2=Rectangle, 3=Romb)
      width=in_width;                 // The witdh
      centerx=in_centerx;             // The x-cord of the center
      centery=in_centery;             // The y-cord of the center
      lengthx=in_lengthx;             // The length in the x-axis
      lengthy=in_lengthy;             // The lenght in the y-axis
      angle=in_angle;                 // The angle (0=To the right, pi=To the left)
      
    }
  /**
   * Sets the angle of the needle.
   * @param in_angle          The needle's new angle. (0=To the right, pi/2 Up, pi=Left, 3pi/2 Down)
   */
  
  public void setAngle(double in_angle)
    {
      angle=in_angle;
    }
  /**
   * Draws the needle on a graphics object.
   * @param g         The graphics object which the needle will be drawn upon.
   */
  public void drawNeedle(Graphics g)
    {
      int x,y,dx,dy;
      double len;
      x=centerx+(int)(lengthx*Math.cos(angle));
      y=centery+(int)(lengthy*Math.sin(angle));
      len=Math.sqrt(Math.pow(x-centerx,2)+Math.pow(y-centery,2));
      dx=(int)(width*(centery-y)/len);
      dy=(int)(width*(x-centerx)/len);
      if(width==1) g.drawLine(centerx,centery,x,y);
      else 
	{
	  switch (type)
	    {
	    case 0:
	      break;
	    case 1:
	      draw_poly(centerx-dx,centerx+dx,x,x,centery-dy,centery+dy,y,y,g);
	      break;
	    case 2:
	      draw_poly(centerx-dx,centerx+dx,x+dx,x-dx,centery-dy,centery+dy,y+dy,y-dy,g);
	      break;
	    case 3:
	      draw_poly(centerx,(centerx+x)/2+dx,x,(centerx+x)/2-dx,centery,(centery+y)/2+dy,y,(centery+y)/2-dy,g);
	      break;
	    }
	} 
    }
  
  private void draw_poly(int x1,int x2,int x3,int x4,int y1,int y2,int y3,int y4,Graphics g)
    {
      int x[]={x1,x2,x3,x4,x1},y[]={y1,y2,y3,y4,y1};
      g.setColor(color);
      g.fillPolygon(x,y,5);
      g.setColor(border);
      g.drawPolygon(x,y,5);
      
    }  
}








