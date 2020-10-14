import java.awt.*;
import java.applet.*;
import java.util.Vector;
import java.util.Enumeration;
import java.lang.*;


/*************************************************************************
**
**    Class  Axis            
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
**    the Graph2D class and DataSet class for plotting 2D graphs.
**
*************************************************************************/





class Axis extends Object {
    
/**************************
** Public Static Values     
**************************/

/*
**    Horizontal Axis or Vertical ?
*/
      static final int  HORIZONTAL = 0;
      static final int  VERTICAL   = 1;
/*
**    The axis will be on the lleft/right border or top/bottom border?
*/
      static final int  LEFT       = 2;
      static final int  RIGHT      = 3;
      static final int  TOP        = 4;
      static final int  BOTTOM     = 5;
/*
**    The first guess on the number of Major tic marks with labels
*/
      static final int  NUMBER_OF_TICS = 4;

/**********************
** Public Variables      
**********************/

      public int     gridlength       = 0;
      public Color   gridcolor        = null;
      public boolean redraw           = true;
      public boolean force_end_labels = false;

      public boolean major_tics     = true;
      public int     major_tic_size = 10;

      public int     minor_tic_size  = 5;
      public int     minor_tic_count = 1;

      public Color   axis_color;

      public int width = 0;

      public Graph2D g2d;

      public double minimum;
      public double maximum;


      public String      title;
      public Dimension   title_offset  = new Dimension(0,0);
      public Font        title_font;
      public Color       title_color;

      public Dimension   exponent_offset = new Dimension(0,0);
      public int         exp_title_padding = 5;

      public int    label_Vpadding  = 2;     
      public int    label_Hpadding  = 2;      

      public Font   label_font;
      public Color  label_color;
/**********************
** Protected Variables      
**********************/
      protected Point amin;
      protected Point amax;
      protected int orientation;
      protected int position;
      

      protected int exponent_height = 0;
      protected int exponent_width  = 0;
      protected int label_height    = 0;
      protected int label_width     = 0;
      protected int title_width     = 0;
      protected int title_height    = 0;


      protected Vector dataset = new Vector();
       

      protected String label_string[]     = null;
      protected double label_start        = 0.0;
      protected double label_step         = 0.0;
      protected int    label_exponent     = 0;
      protected int    label_count        = 0;
      protected int    guess_label_number = 4;
/*
**    StringKludge ONLY appears to work around a Major Deficiency in
**    Netscape 2.0b1. It should dissappear when String.valueOf
**    works
*/           
      StringKludge sk = new StringKludge();

/********************
** Constructors
********************/

      public Axis(int p) {

           position = p;

           switch (position) {
              case LEFT:
                          orientation = VERTICAL;
                          break;
              case RIGHT:
                          orientation = VERTICAL;
                          break;

              case TOP:
                          orientation = HORIZONTAL;
                          break;
              case BOTTOM:
                          orientation = HORIZONTAL;
                          break;
              default:
                          orientation = HORIZONTAL;
                          position    = BOTTOM;
                          break;
           }

      }


/******************
** Public Methods
******************/

      public void attachDataSet( DataSet d ) {
            if( orientation == HORIZONTAL )   attachXdata( d );
            else                              attachYdata( d );
      }

      public void detachDataSet( DataSet d ) {
           int i = 0;

           if( d == null ) return;

           if( orientation == HORIZONTAL ) {
               d.xaxis = null;
           } else {
               d.yaxis = null;
           }
           dataset.removeElement(d);

           minimum = getDataMin();
           maximum = getDataMax();
      }

      public void detachAllDataSets() {
            Enumeration e;
            DataSet d;

            if( dataset.isEmpty() ) return;


            if( orientation == HORIZONTAL ) {
                 for (e = dataset.elements() ; e.hasMoreElements() ;) {
                     d = (DataSet)e.nextElement();
                     d.xaxis = null;
                 }
            } else {
                 for (e = dataset.elements() ; e.hasMoreElements() ;) {
                     d = (DataSet)e.nextElement();
                     d.yaxis = null;
                 }
            }

            minimum = 0.0;
            maximum = 0.0;
      }
      public double getDataMin() {
            double m;
            Enumeration e;
            DataSet d;

            if( dataset.isEmpty() ) return 0.0;

            d = (DataSet)(dataset.firstElement());
            if(d == null) return 0.0;

            if( orientation == HORIZONTAL ) {
                 m = d.getXmin();
                 for (e = dataset.elements() ; e.hasMoreElements() ;) {

                     d = (DataSet)e.nextElement();
                     m = Math.min(d.getXmin(),m);
                     
                 }
            } else {
                 m = d.getYmin();
                 for (e = dataset.elements() ; e.hasMoreElements() ;) {

                     d = (DataSet)e.nextElement();
                     m = Math.min(d.getYmin(),m);
                     
                 }
            }

            return m;
      }
      public double getDataMax() {
            double m;
            Enumeration e;
            DataSet d;

            if( dataset.isEmpty() ) return 0.0;

            d = (DataSet)(dataset.firstElement());

            if(d == null) return 0.0;

          
            if( orientation == HORIZONTAL ) {
                 m = d.getXmax();
                 for (e = dataset.elements() ; e.hasMoreElements() ;) {

                     d = (DataSet)e.nextElement();
                     m = Math.max(d.getXmax(),m);
                 }
            } else {
                 m = d.getYmax();
                 for (e = dataset.elements() ; e.hasMoreElements() ;) {

                     d = (DataSet)e.nextElement();

                     m = Math.max(d.getYmax(),m);
                 }
            }

            return m;
      }


      public int getInteger(double v) {
          double scale;
          
          if( orientation == HORIZONTAL ) {
               scale  = (amax.x - amin.x)/(maximum - minimum);
               return amin.x + (int)( ( v - minimum ) * scale);
          } else {
               scale  = (amax.y - amin.y)/(maximum - minimum);
               return amax.y - (int)( ( v - minimum ) * scale);

          }

      }

      public double getDouble(int i) {
            double scale;

          if( orientation == HORIZONTAL ) {
               scale  = (maximum - minimum)/(amax.x - amin.x);
               return minimum + (i - amin.x)*scale;
          } else {
               scale  = (maximum - minimum)/(amax.y - amin.y);
               return maximum - (i - amin.y)*scale;
          }
      }

     public int getAxisPos() { return position; }      

     public int getAxisWidth(Graphics g) {
          FontMetrics fm;
          int i;
          width = 0;

          if( minimum == maximum )    return 0;
          if( dataset.size() == 0 )   return 0;


          calculateGridLabels();

          if( label_font != null)  fm = g.getFontMetrics(label_font);
          else                     fm = g.getFontMetrics();

          label_height =  fm.getHeight();
          label_width  = 0;       
          for(i=0; i<label_string.length; i++) {
               label_width = Math.max(
                             fm.stringWidth(label_string[i]),label_width);
          }

          exponent_width  = 0;
          exponent_height = 0;
          if(label_exponent != 0) {
              exponent_width = fm.stringWidth("x10");
              exponent_width += fm.stringWidth(String.valueOf(label_exponent));
              exponent_height = fm.getHeight() + (int)(fm.getAscent()*0.5);
          }

          title_width  = 0;
          title_height = 0;
          if(title != null) {
               if(title_font != null) fm = g.getFontMetrics(title_font);
               else                   fm = g.getFontMetrics();

               title_height = fm.getHeight();
               title_width = fm.stringWidth(title);
          }

           if( orientation == HORIZONTAL ) {

               width = label_height + label_Vpadding; 
               width += Math.max(title_height,exponent_height);

           } else {

               width = label_width + label_Hpadding;
               if( title_width != 0 && exponent_width != 0 ) {
                   width = Math.max(width, 
                      (title_width+exponent_width+exp_title_padding));
               } else
               if( title_width != 0 ) {
                  width = Math.max(width,title_width);
               } else
               if ( exponent_width != 0 ) {
                  width = Math.max(width,exponent_width);
               }
           }


           return width;
      }

      public boolean positionAxis(int xmin, int xmax, int ymin, int ymax ){
           amin = null;
           amax = null;

           if( orientation == HORIZONTAL && ymin != ymax ) return false;
           if( orientation == VERTICAL   && xmin != xmax ) return false;

           amin = new Point(xmin,ymin);
           amax = new Point(xmax,ymax);


           return true;
      }         



      public void drawAxis(Graphics g) {
          Graphics lg;

          if( !redraw            ) return;
          if( minimum == maximum ) return;
          if( amin.equals(amax) ) return;
          if( dataset.size() == 0 ) return;
          if( width == 0 ) return;

          lg = g.create();

          if( force_end_labels ) {
              minimum = label_start;
              maximum = minimum + (label_count-1)*label_step;
          }

          if( orientation == HORIZONTAL) {
               drawHAxis(lg);
          } else {
               drawVAxis(lg);
          }


     }


/******************
** Protected Methods
******************/

     protected void drawHAxis(Graphics g) {
          Graphics lg;
          int i;
          int j;
          int x0,y0,x1,y1;
          int direction;
          double minor_step;
          
          FontMetrics fm_label;
          FontMetrics fm_title;
          Color c;

          double vmin = minimum*1.001;
          double vmax = maximum*1.001;

          double scale  = (amax.x - amin.x)/(maximum - minimum);
          double val;
          double minor;


          if( axis_color != null) g.setColor(axis_color);

          g.drawLine(amin.x,amin.y,amax.x,amax.y);
          
          if(position == TOP )     direction =  1;
          else                     direction = -1;

          minor_step = label_step/(minor_tic_count+1);
          val = label_start;
          for(i=0; i<label_count; i++) {
              if( val >= vmin && val <= vmax ) {
                 y0 = amin.y;
                 x0 = amin.x + (int)( ( val - minimum ) * scale);
                 if(gridlength > 0) {
                      c = g.getColor();
                      if(gridcolor != null) g.setColor(gridcolor);
                      g.drawLine(x0,y0,x0,y0+gridlength*direction);
                      g.setColor(c);
                 }
                 x1 = x0;
                 y1 = y0 + major_tic_size*direction;
                 g.drawLine(x0,y0,x1,y1);
              }

              minor = val + minor_step;
              for(j=0; j<minor_tic_count; j++) {
                 if( minor >= vmin && minor <= vmax ) {
                    y0 = amin.y;
                    x0 = amin.x + (int)( ( minor - minimum ) * scale);
                    if(gridlength > 0) {
                      c = g.getColor();
                      if(gridcolor != null) g.setColor(gridcolor);
                      g.drawLine(x0,y0,x0,y0+gridlength*direction);
                      g.setColor(c);
                    }
                    x1 = x0;
                    y1 = y0 + minor_tic_size*direction;
                    g.drawLine(x0,y0,x1,y1);
                 }
                minor += minor_step;
              }

              val += label_step;
          }

          if( label_font != null) g.setFont(label_font);
          fm_label = g.getFontMetrics();
          if( label_color != null) g.setColor(label_color);

          if(position == TOP ) {
             direction = - label_Vpadding - fm_label.getDescent();
          } else {
             direction = + label_Vpadding + fm_label.getAscent();
          }


          val = label_start;
          for(i=0; i<label_count; i++) {


              if( val >= vmin && val <= vmax ) {
                 y0 = amin.y + direction;
                 x0 = amin.x + (int)(( val - minimum ) * scale) - 
                             fm_label.stringWidth(label_string[i])/2;

                 g.drawString(label_string[i],x0,y0);

              }
              val += label_step;
          }
          if( label_exponent != 0 ) {

              x0 = amax.x - exponent_width/2 + exponent_offset.width;
              if( position == TOP) {
                y0 = amin.y - label_Vpadding  -
                   fm_label.getDescent() - fm_label.getAscent() -
                   fm_label.getDescent() + exponent_offset.height;
              } else {
                y0 = amax.y + label_Vpadding +
                   fm_label.getDescent() + fm_label.getAscent() +
                   (int)(1.5*fm_label.getAscent()) + exponent_offset.height;
              }
              
              g.drawString("x10",x0,y0);

              x0 += fm_label.stringWidth("x10");
              y0 -= fm_label.getAscent()/2;

              g.drawString(String.valueOf(label_exponent),x0,y0);



          }


          if( title != null) {

             if(title_color != null ) g.setColor(title_color);

             if(title_font != null) g.setFont(title_font);
             fm_title = g.getFontMetrics();

             x0 = amin.x + ( amax.x - amin.x - fm_title.stringWidth(title) )/2
                  + title_offset.width;
             if( position == TOP) {
               y0 = amin.y - label_Vpadding  -
                   fm_label.getDescent() - fm_label.getAscent() -
                   fm_title.getDescent() + title_offset.height;
             } else {
               y0 = amax.y + label_Vpadding +
                   fm_label.getDescent() + fm_label.getAscent() +
                   fm_title.getAscent() + title_offset.height;
             }

             g.drawString(title,x0,y0);
          }


     }


     protected void drawVAxis(Graphics g) {
          Graphics lg;
          int i;
          int j;
          int x0,y0,x1,y1;
          int direction;
          double minor_step;
          double minor;
          Color c;
          
          FontMetrics fm;
          Color gc = g.getColor();
          Font  gf = g.getFont();

          double vmin = minimum*1.001;
          double vmax = maximum*1.001;

          double scale  = (amax.y - amin.y)/(maximum - minimum);
          double val;


          if( axis_color != null) g.setColor(axis_color);

          g.drawLine(amin.x,amin.y,amax.x,amax.y);

          if(position == RIGHT )     direction = -1;
          else                       direction =  1;

          minor_step = label_step/(minor_tic_count+1);
          val = label_start;
          for(i=0; i<label_count; i++) {
              if( val >= vmin && val <= vmax ) {
                 x0 = amin.x;
                 y0 = amax.y - (int)( ( val - minimum ) * scale);
                 if(gridlength > 0) {
                      c = g.getColor();
                      if(gridcolor != null) g.setColor(gridcolor);
                      g.drawLine(x0,y0,x0+gridlength*direction,y0);
                      g.setColor(c);
                 }
                 x1 = x0 + major_tic_size*direction;
                 y1 = y0;
                 g.drawLine(x0,y0,x1,y1);
              }

              minor = val + minor_step;
              for(j=0; j<minor_tic_count; j++) {
                 if( minor >= vmin && minor <= vmax ) {
                    x0 = amin.x;
                    y0 = amax.y - (int)( ( minor - minimum ) * scale);
                    if(gridlength > 0) {
                      c = g.getColor();
                      if(gridcolor != null) g.setColor(gridcolor);
                      g.drawLine(x0,y0,x0+gridlength*direction,y0);
                      g.setColor(c);
                    }
                    x1 = x0 + minor_tic_size*direction;
                    y1 = y0;
                    g.drawLine(x0,y0,x1,y1);
                 }
                minor += minor_step;
              }
              val += label_step;
          }

          if( label_font != null) g.setFont(label_font);
          else                    g.setFont(gf);
          fm = g.getFontMetrics();
          if( label_color != null) g.setColor(label_color);
          else                     g.setColor(gc);

          if(position == RIGHT ) {
             direction = label_Hpadding + fm.getDescent();
          } else {
             direction = - label_Hpadding - fm.getAscent();
          }


          val = label_start;
          for(i=0; i<label_count; i++) {
              if( val >= vmin && val <= vmax ) {
                 if(position == RIGHT ) {
                     x0 = amin.x + label_Hpadding;
                 } else {
                     x0 = amin.x - label_Hpadding - 
                          fm.stringWidth(label_string[i]);
                 }
                 y0 = amax.y - (int)(( val - minimum ) * scale) + 
                             fm.getAscent()/2;

                 g.drawString(label_string[i],x0,y0);
              }
              val += label_step;
          }

          if( title == null && label_exponent==0 ) return;


          if(position == RIGHT ) {
              x0 = amin.x + label_Hpadding;
          } else {
              x0 = amin.x  - label_Hpadding - exponent_width - title_width;
              if( exponent_width != 0 && title_width != 0 )
                      x0 -= exp_title_padding;
          }
          y0 = amin.y;

          if( title != null) {

             if(title_color != null ) g.setColor(title_color);
             else                     g.setColor(gc);

             if(title_font != null) g.setFont(title_font);
             else                   g.setFont(gf);
 
             g.drawString(title,x0+title_offset.width,y0+title_offset.height);

             
             x0 += title_width + exp_title_padding;
          }

          if( label_exponent != 0) {

              if( label_font != null) g.setFont(label_font);
              else                    g.setFont(gf);

              fm = g.getFontMetrics();
              if( label_color != null) g.setColor(label_color);
              else                     g.setColor(gc);

              x0 += exponent_offset.width;
              y0 += exponent_offset.height;

              g.drawString("x10",x0,y0);

              x0 += fm.stringWidth("x10");
              y0 -= fm.getAscent()/2;

              g.drawString(String.valueOf(label_exponent),x0,y0);

          }

 }








      protected void attachXdata( DataSet d ) {

            dataset.addElement(d);
            d.xaxis = this;

            if( dataset.size() == 1 ) {
                  minimum = d.xmin;
                  maximum = d.xmax;
            } else {
               if(minimum > d.xmin) minimum = d.xmin;
               if(maximum < d.xmax) maximum = d.xmax;
            }

      }

      protected void attachYdata( DataSet d ) {

            dataset.addElement(d);
            d.yaxis = this;

            if( dataset.size() == 1 ) {
                  minimum = d.ymin;
                  maximum = d.ymax;
            } else {
               if(minimum > d.ymin) minimum = d.ymin;
               if(maximum < d.ymax) maximum = d.ymax;
            }


      }


      protected void calculateGridLabels() {
        double val;
        int i;
        int j;

        
        if (Math.abs(minimum) > Math.abs(maximum) ) 
         label_exponent = ((int)Math.floor(log10(Math.abs(minimum))/3.0) )*3;
        else
         label_exponent = ((int)Math.floor(log10(Math.abs(maximum))/3.0) )*3;

        label_step = RoundUp( (maximum-minimum)/guess_label_number );
        label_start = Math.floor( minimum/label_step )*label_step;

        val = label_start;
        label_count = 1;
        while(val < maximum) { val += label_step; label_count++; }

        label_string = new String[label_count];


//      System.out.println("label_step="+label_step);
//      System.out.println("label_start="+label_start);
//      System.out.println("label_count="+label_count);
//      System.out.println("label_exponent"+label_exponent);

           
        for(i=0; i<label_count; i++) {
            val = label_start + i*label_step;

            if( label_exponent< 0 ) {
                  for(j=label_exponent; j<0;j++) { val *= 10; }
            } else {
                  for(j=0; j<label_exponent;j++) { val /= 10; }
            }

//            label_string[i] = String.valueOf(val);
            label_string[i] = sk.valueOf(val);
        }

      }

/******************
** Private Methods
******************/

      private double RoundUp( double val ) {
          int exponent;
          int i;

          exponent = (int)(Math.floor( log10(val) ) );

          if( exponent < 0 ) {
             for(i=exponent; i<0; i++) { val *= 10.0; }
          } else {
             for(i=0; i<exponent; i++) { val /= 10.0; }
          }

          if( val > 5.0 )     val = 10.0;
          else
          if( val > 2.0 )     val = 5.0;
          else
          if( val > 1.0 )     val = 2.0;
          else
                              val = 1.0;

          if( exponent < 0 ) {
             for(i=exponent; i<0; i++) { val /= 10.0; }
          } else {
             for(i=0; i<exponent; i++) { val *= 10.0; }
          }
          
          return val;

      }                  


      private double log10( double a ) throws ArithmeticException {
           return Math.log(a)/2.30258509299404568401;
      }



}


