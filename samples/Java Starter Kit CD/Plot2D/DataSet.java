import java.awt.*;
import java.applet.*;
import java.util.*;
import java.lang.*;


/*************************************************************************
**
**    Class  DataSet
**                                              Version 1.0   October 1995
**
**************************************************************************
**    Copyright (C) 1995 Leigh Brookshaw
**
**    This program is free software; you can redistribute it and/or modify
**    it under the terms of the GNU General Public License as published by
**    the Free Software Foundation; either version 2 of the License, or
**    (at your option) any later version.
**
**    This program is distributed in the hope that it will be useful,
**    but WITHOUT ANY WARRANTY; without even the implied warranty of
**    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
**    GNU General Public License for more details.
**
**    You should have received a copy of the GNU General Public License
**    along with this program; if not, write to the Free Software
**    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
**************************************************************************
**
**    This class is designed to be used in conjunction with 
**    the Graph2D class and Axis class for plotting 2D graphs.
**
*************************************************************************/


/*
**  Class DataSet
**               This object is designed to hold the data to be plotted
**               The data is stored in an array with the x,y data
**               stored adjacently. The x data in even indices, y data in odd.
**               Along with the data are a number of flags associated
**               with the data defining how this data is to be plotted.
*/


public class DataSet extends Object {


/**************************
** Public Static Values     
**************************/
/*
**    The static value specifying that no straight line segment
**    is to join the points.
*/
      public final static int NOLINE    =  0;
/*
**    The static value specifying that a straight line segment
**    is to join the points.
*/
      public final static int LINE      =  1;



/**********************
** Public Variables      
**********************/

/* 
** g2d
**    The Graphics canvas that is driving the whole show
**    See class Graph2D
*/
      public Graph2D g2d;

/*
** linestyle
**    The linestyle to employ when joining the data points with
**    straight line segments. Currently only solid and no line
**    are supported. Dashed lines will be added
*/
      public int   linestyle     = LINE;
/*
** linecolor
**    The color of the straight line segments
*/
      public Color linecolor     = null;
/*
** marker
**    The index of the marker to use at the data points
**    markers are stroked.
*/
      public int    marker       = 0;
/*
** markercolor
**    The marker color
*/
      public Color  markercolor  = null;
/*
** markerscale
**    The scaling factor for the marker
*/
      public double markerscale  = 1.0;
/*
** xaxis
**    The Axis object the X data is attached to. From the Axis obeject
**    the scaling for the data can be calculated.
*/
      public Axis xaxis;
/*
** yaxis
**    The Axis object the Y data is attached to.
*/
      public Axis yaxis;
/*
** xmax, xmin, ymax, ymin
**    The plottable range of the data. This can be very different from
**    true data range. The data is clipped when plotted.
*/
      public double xmax, xmin, ymax, ymin;

/*
** legend_length
**    The length of the line in the legend
*/
      int legend_length = 20;
/*
** legend_color
**    The color of the text in the legend
*/
      Color legend_color;
/*
** legend_text
**    The legend text
*/
      String legend_text;
/*
** legend_font
**    The font to use for the legend text
*/
      Font legend_font;
/*
** legend_x, legend_y
**    
*/
      int legend_x, legend_y;


/**********************
** Protected Variables      
**********************/
/*
** dxmax, dxmin, dymax, dymin
**    The true data range. Once the data is loaded this will never change.
*/
      protected double dxmax, dxmin, dymax, dymin;

/*
** data[]
**    The actual data
*/
      protected double data[];
/*
** xrange, yrange
**    The range of the clipped data
*/
      protected double xrange, yrange;


/********************
** Constructors
********************/

/*
** DataSet(double[], int)
**   The double array contains the data. The X data is expected in
**   the even indices, the y data in the odd. The integer n is the
**   number of data Points. This manes that the length of the data
**   array is 2*n.
*/
      public DataSet ( double d[], int n ) throws Exception {
           int i;
           int k = 0;

           if ( d  == null || d.length == 0 || n <= 0 ) {
              throw new Exception("DataSet: Error in parsed data!");
           }

//     Copy the data locally.

           data = new double[n*2];

           System.arraycopy(d, 0, data, 0, n*2);


//     Calculate the data range.   

           dxmax = data[0];
           dymax = data[1];
           dxmin = dxmax;
           dymin = dymax;


           for(i=1; i<data.length-1; ) {

             i++;
             if( dxmax < data[i] ) { dxmax = data[i]; }
             else
             if( dxmin > data[i] ) { dxmin = data[i]; }

             i++;
             if( dymax < data[i] ) { dymax = data[i]; }
             else
             if( dymin > data[i] ) { dymin = data[i]; }
           }

           xmin = dxmin;
           xmax = dxmax;
           ymin = dymin;
           ymax = dymax;

      }



/******************
** Public Methods
******************/


/*
** draw_data(Graphics)
**    Actually draw the straight line segments and/or the markers at the
**    data points.
**
**    If this data has been attached to an Axis then scale the data
**    based on the axis maximum/minimum otherwise scale using
**    the data's maximum/minimum
**
**    Use the clipRect of the graphics object to define the window we
**    have to draw into.
*/

      public void draw_data(Graphics g) {
           Color c;

           if ( xaxis != null ) {
                xmax = xaxis.maximum;
                xmin = xaxis.minimum;
           }

           if ( yaxis != null ) {
                ymax = yaxis.maximum;
                ymin = yaxis.minimum;
           }

           
           xrange = xmax - xmin;
           yrange = ymax - ymin;

           c = g.getColor();

           if( linestyle != DataSet.NOLINE ) {
               if ( linecolor != null) g.setColor(linecolor);
               else                    g.setColor(c);
               draw_lines(g);
           }    


           if( marker > 0 ) {
               if(markercolor != null) g.setColor(markercolor);
               else                    g.setColor(c);
               draw_markers(g);
           }    





           g.setColor(c);
      }


      public double getXmax() {  return dxmax; } 
      public double getXmin() {  return dxmin; } 
      public double getYmax() {  return dymax; } 
      public double getYmin() {  return dymin; }


      public void dataLegend(int x, int y, String text) {
           if(text == null) { legend_text = null;  return; }
           legend_text = text;
           legend_x    = x;
           legend_y    = y;
      }


/******************
** Protected Methods
******************/

/*
** draw_lines(Graphics)
**
** Actually draw into the clipRect the straight line segments
** At some future date this method will be extended to draw dashed lines.
**
** To save time don't bother to try and draw segments that will be 
** completely clipped. Only Draw segments that are not clipped or partially
** clipped. 
*/

      protected void draw_lines(Graphics g) {
          int i;
          int j;
          boolean inside0 = false;
          boolean inside1 = false;
          Rectangle w = g.getClipRect();
          double x,y;
          int x0 = 0 , y0 = 0;
          int x1 = 0 , y1 = 0;


//    Is the first point inside the clipping region ?
          if( inside(data[0], data[1]) ) {

              x0 = (int)(w.x + ((data[0]-xmin)/xrange)*w.width);
              y0 = (int)(w.y + (1.0 - (data[1]-ymin)/yrange)*w.height);

                    inside0 = true;
          } else {
                    inside0 = false;
          }


          for(i=2; i<data.length; i+=2) {

//        Is this point inside the clipping region?
              if( inside( data[i], data[i+1]) ) {
                    inside1 = true;
              } else {
                    inside1 = false;
              }
             
//        If one point is inside the clipping region calculate the second point
              if ( inside1 || inside0 ) {

               x1 = (int)(w.x + ((data[i]-xmin)/xrange)*w.width);
               y1 = (int)(w.y + (1.0 - (data[i+1]-ymin)/yrange)*w.height);

              }
//        If the second point is inside calculate the first point if it
//        was outside
              if ( !inside0 && inside1 ) {

                x0 = (int)(w.x + ((data[i-2]-xmin)/xrange)*w.width);
                y0 = (int)(w.y + (1.0 - (data[i-1]-ymin)/yrange)*w.height);

              }
//        If either point is inside draw the segment
              if ( inside0 || inside1 )  {
                      g.drawLine(x0,y0,x1,y1);
              }

/*
**        The reason for the convolution above is to avoid calculating
**        the points over and over. Now just copy the second point to the
**        first and grab the next point
*/
              inside0 = inside1;
              x0 = x1;
              y0 = y1;

          }

      }


/*
** boolean inside(double, double)
**         
**    return true if the point(x,y) is inside the alowed range.
*/

      protected boolean inside(double x, double y) {
          if( x >= xmin && x <= xmax && 
              y >= ymin && y <= ymax )  return true;
          
          return false;
      }

/*
**  draw_markers(Graphics)
**
**    Draw the markers. The markers have been preloaded from a file.
**    (see the method Graph2D.loadMarker(URL)).
**    Only markers inside the specified range will be drawn. Also markers
**    close the edge of the clipping region will be clipped.
**
**    The markers are stroked based on the preloaded marker definitions.
*/
      protected void draw_markers(Graphics g) {
          int x1,y1;
          int i;
          Rectangle w = g.getClipRect();
/*
**        Load the marker specified for this data
*/
          Vector m = g2d.getMarkerVector(marker);


          if( m == null) return;


          for(i=0; i<data.length; i+=2) {
              if( inside( data[i], data[i+1]) ) {

                x1 = (int)(w.x + ((data[i]-xmin)/xrange)*w.width);
                y1 = (int)(w.y + (1.0 - (data[i+1]-ymin)/yrange)*w.height);

                    stroke_marker(m, g, x1, y1);
                }
          }


      }
/*
** stroke_marker(Vector, Graphics, int, int)
**
**         Stroke the marker centered at the point x,y.
**   The vector contains the definition of the marker. Marker definitions
**   are centered on (0,0) and specify move and draw commands for each
**   point.
*/


      protected void stroke_marker(Vector m, Graphics g, int x, int y) {
          int x0 = x, x1 = x, y0 = y, y1 = y;
          MVertex v;

          for (Enumeration e = m.elements() ; e.hasMoreElements() ;) {
             v = (MVertex)e.nextElement();

             if( v.draw ) {
                 x1 = x + (int)(v.x*markerscale);
                 y1 = y + (int)(v.y*markerscale);

                 g.drawLine(x0,y0,x1,y1);

                 x0 = x1;
                 y0 = y1;
             } else {
                 x0 = x + (int)(v.x*markerscale);
                 y0 = y + (int)(v.y*markerscale);

             }



          }

     }

/*
** draw_legend( Graphics g )
**             Draw a legend for this data set using the data colors and the
**             supplied text
*/

      protected void draw_legend(Graphics g) {
          Color c = g.getColor();
          Font  f = g.getFont();
          FontMetrics fm;
          Vector m;

          if( legend_text == null ) return;

          if( linestyle != DataSet.NOLINE ) {
              if ( linecolor != null) g.setColor(linecolor);
              g.drawLine(legend_x,legend_y,legend_x+legend_length,legend_y);
          }

          if( marker > 0 ) {
               m = g2d.getMarkerVector(marker);
               if( m != null) {
                  if(markercolor != null) g.setColor(markercolor);
                  else                    g.setColor(c);

                  stroke_marker(m, g, legend_x+legend_length/2, legend_y);
               }
          }

          if( legend_color != null)   g.setColor(legend_color);
          else                        g.setColor(c);
          if( legend_font != null)    g.setFont(legend_font);

          fm = g.getFontMetrics();
          g.drawString( legend_text,
                        legend_x+legend_length+fm.charWidth('x'),
                        legend_y+fm.getAscent()/3);

          g.setColor(c);
          g.setFont(f);


      }

}


