import java.awt.*;
import java.applet.*;
import java.net.URL;
import java.util.*;

/*************************************************************************
**
**    Class  G2Dint
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
**    This class is an extension of Graph2D class.
**    It adds interactive selection of the plotting range.
**
**    MouseDown: Starts the range selection
**    MouseDrag: Drag out a rectangular range selection
**    MouseUp:   Replot with modified plotting range.
**
**    Shift MouseUp: reset back to default range.
**
*************************************************************************/


class G2Dint extends Graph2D {

/*
**    Set to true when a rectangle is being dragged out by the mouse
*/
      private boolean drag = false;
/*
**    Button Down position
*/
      private int x0,y0;
/*
**    Button Drag position
*/
      private int  x1,y1;
/*
**    Previous Button Drag position
*/
      private int x1old, y1old;

/*
**    Attached Axis which must be registered with this class
**    These are the axis used to find the drag range.
**    If no axis are registered no mouse drag.
*/
      private Axis xaxis;
      private Axis yaxis;




/*
**    Attach xaxis to be used for the drag scaling
*/
      public Axis createXAxis() {
         xaxis = super.createAxis(Axis.BOTTOM);
         return xaxis;
      }
/*
**    Attach yaxis to be used for the drag scaling
*/
      public Axis createYAxis() {
         yaxis = super.createAxis(Axis.LEFT);
         return yaxis;
      }

/*
**  Override the normal Graph2D update method with this new one
*/
    public void update(Graphics g) {
          Rectangle r = bounds();
          Color c = g.getColor();

          if(drag) {
/*
**         Drag out the new box.
**         Use drawLine instead of drawRect to avoid problems
**         when width and heights become negative. Seems drawRect
**         can't handle it!
*/

           g.setColor(getBackground());
           g.drawLine(x0, y0, x1old, y0);
           g.drawLine(x1old, y0, x1old, y1old);
           g.drawLine(x1old, y1old, x0, y1old);
           g.drawLine(x0, y1old, x0, y0);
           g.setColor(getForeground());
           g.drawLine(x0, y0, x1, y0);
           g.drawLine(x1, y0, x1, y1);
           g.drawLine(x1, y1, x0, y1);
           g.drawLine(x0, y1, x0, y0);


           g.setColor(c);

           x1old = x1;
           y1old = y1;

           return;
           }

          if( clearAll ) {
             g.setColor(getBackground());
             g.fillRect(r.x,r.y,r.width,r.height);
             g.setColor(c);
          }
          if( paintAll ) paint(g);
    }

/*
** Handle the mouse events
*/
    public boolean handleEvent(Event e) {

        if( xaxis == null || yaxis == null ) return false;

        switch (e.id) {          
          case Event.MOUSE_DOWN:

                x0 = e.x;
                y0 = e.y;

                if( !e.shiftDown() ) { 
                          drag = true; 
                          x1old = x0;
                          y1old = y0;
                }

                if(x0 < datarect.x) x0 = datarect.x;
                else
                if(x0 > datarect.x + datarect.width ) 
                    x0 = datarect.x + datarect.width;

                if(y0 < datarect.y) y0 = datarect.y;
                else
                if(y0 > datarect.y + datarect.height ) 
                    y0 = datarect.y + datarect.height;


                return true;
          case Event.MOUSE_UP:
                x1   = e.x;
                y1   = e.y;

                drag = false;

                if( e.shiftDown() ) {
                    xaxis.minimum = xaxis.getDataMin();
                    xaxis.maximum = xaxis.getDataMax();
                    yaxis.minimum = yaxis.getDataMin();
                    yaxis.maximum = yaxis.getDataMax();
                    repaint();
                    return true;
                }

                if(x1 < datarect.x) x1 = datarect.x;
                else
                if(x1 > datarect.x + datarect.width ) 
                    x1 = datarect.x + datarect.width;

                if(y1 < datarect.y) y1 = datarect.y;
                else
                if(y1 > datarect.y + datarect.height ) 
                    y1 = datarect.y + datarect.height;


                if( Math.abs(x0-x1) > 5 &&  Math.abs(y0-y1) > 5 ) {
                   if(x0 < x1 ) {                
                      xaxis.minimum = xaxis.getDouble(x0);
                      xaxis.maximum = xaxis.getDouble(x1);
                   } else {
                      xaxis.maximum = xaxis.getDouble(x0);
                      xaxis.minimum = xaxis.getDouble(x1);
                   }

                   if(y0 >y1 ) {                
                      yaxis.minimum = yaxis.getDouble(y0);
                      yaxis.maximum = yaxis.getDouble(y1);
                   } else {
                      yaxis.maximum = yaxis.getDouble(y0);
                      yaxis.minimum = yaxis.getDouble(y1);
                   }

                   repaint();
                 }
                return true;

           case Event.MOUSE_DRAG:
                

                x1   = e.x;
                y1   = e.y;

                if(x1 < datarect.x) x1 = datarect.x;
                else
                if(x1 > datarect.x + datarect.width ) 
                    x1 = datarect.x + datarect.width;

                if(y1 < datarect.y) y1 = datarect.y;
                else
                if(y1 > datarect.y + datarect.height ) 
                    y1 = datarect.y + datarect.height;

                repaint();

                return true;
           }

           return false;
       
         }



}
