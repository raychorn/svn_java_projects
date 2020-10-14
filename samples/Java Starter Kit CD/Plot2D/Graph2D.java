import java.awt.*;
import java.applet.*;
import java.util.*;
import java.lang.*;
import java.io.StreamTokenizer;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;

/*************************************************************************
**
**    Class  Graph2D
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
**    This class is the main interface for plotting 2D data sets.
**
*************************************************************************/



/*
** class Graph2D extends Canvas 
**
** The main entry point and interface for the 2D graphing package.
** This class keeps track of the DataSets and the Axes.
** It has the main drawing engine that positions axis etc.
*/

public class Graph2D extends Canvas {

/*********************
**
** Protected Variables
**
*********************/

/*
**  The axis we are controlling
*/
    protected Vector axis          = new Vector(4);
/*
**  The datasets we are controlling
*/
    protected Vector dataset       = new Vector(10);
/*
**  The markers that have been loaded
*/
    protected Markers marker;

/*********************
**
** Public Variables
**
*********************/

/*
**  The border around the entire plot. Allows slopover from axis labels
**  legends etc.
*/
    public int     border       = 40;
/*
**  Do you want a frame around the data window. Axis will overlay this frame
**  Not forgetting the frame color
*/
    public boolean frame        = true;
    public Color   framecolor;
/*
**  Do you want a grid over the data window.
*/
    public boolean grid         = true; 
    public Color   gridcolor    = Color.pink;
/*
**  The rectangle where the data will be plotted
*/
    public Rectangle datarect   = new Rectangle();
/*
**  Blank the screen when the update method is called? Normally
**  only modified for special effects
*/
    public boolean clearAll     = true;
/*
**  Paint it All ie axis, data etc. Normally
**  only modified for special effects
*/
    public boolean paintAll     = true;

/******************
**
**  Public Methods
**
*******************/


/*
**  loadFile( URL file)  
**
**  Load a DataSet from a File. The dataset is assumed to consist
**  (at this stage) 2 ASCII columns of numbers x, y. As always blank lines
**  are ignored and a # at the beginning of a line signifies a comment.
**
**  NOTE: the streamtokenizer was used in the first version of this method
**        it was more trouble than it was worth for numbers and scientific
**        notation.
**
**  URL file:    The URL of the file.
*/
    public DataSet loadFile( URL file) {
           byte b[] = new byte[50];
           int nbytes = 0;
           int max  = 100;
           int inc  = 100;
           int n    = 0;
           double data[] = new double[max];
           InputStream is = null;
           boolean comment    = false;
           int c;

           try {
                 is = file.openStream();

                 while( (c=is.read()) > -1 ) {
                     
                     switch (c) {
                     
                         case '#':
                                    comment = true;
                                    break;
                         case '\r': case '\n':
                                    comment = false;
                         case ' ': case '\t':
                                if( nbytes > 0 ) {
                                   String s = new String(b,0,0,nbytes);
                                   data[n] = Double.valueOf(s).doubleValue();
                                   n++;
                                   if( n >= max ) {
                                       max += inc;
                                       double d[] = new double[max];
                                       System.arraycopy(data, 0, d, 0, n);
                                       data = d;
                                   }

                                   nbytes = 0;
                                }
                                break;
                         default:
                                   if( !comment ) {
                                        b[nbytes] = (byte)c;
                                        nbytes++;
                                   }
                                   break;
                      }

                  }

                if (is != null) is.close();
        } catch(Exception e) {
          System.out.println("Failed to load Data set from file ");
          e.printStackTrace();
          if (is != null) try { is.close(); } catch (Exception ev) { }
          return null;
        }

        return loadDataSet(data,n/2);
        
    }
/*
**  loadDataSet( double data[], int n )  
**
**  Load a DataSet from an Array. The data is assumed to be stored
**  in the form  x,y,x,y,x,y.... A local copy of the data is made.
**
**  If everything goes well it returns 'true'
**
**  double data[]: The data to plot stored in the form x,y,x,y...
**  int   n:      The number of data points. This means that the
**                minimum length of the data array is 2*n.
*/

    public DataSet loadDataSet( double data[], int n ) {
       DataSet d;
       try { 
             d =  new DataSet(data, n);
             dataset.addElement( d );
             d.g2d = this;
            }
       catch (Exception e) { 
         System.out.println("Failed to load Data set ");
         e.printStackTrace();
         return null;
       }
       return d;
    }

/*
** deleteDataSet( DataSet d ) 
**        Delete all pointers to the local copy for the data set.
*/

    public void deleteDataSet( DataSet d ) {
       if(d != null) {
                      if(d.xaxis != null) d.xaxis.detachDataSet(d);
                      if(d.yaxis != null) d.yaxis.detachDataSet(d);
                      dataset.removeElement(d);
       }
    }
/*
** createAxis( int position )
**            create an axis
*/
    public Axis createAxis( int position ) {
       Axis a;

       try { 
             a =  new Axis(position);
             axis.addElement( a );
             a.g2d = this;
            }
       catch (Exception e) { 
         System.out.println("Failed to create Axis");
         e.printStackTrace();
         return null;
       }

       return a;

    }
/*
** deleteAxis( Axis a )
**            delete an axis
*/
    public void deleteAxis( Axis a ) {
       
       if(a != null) {
                   a.detachAllDataSets();
                   axis.removeElement(a);
       }
    }
/*
** Vector getMarkerVector(int m)
**         Given the marker index (m > 0 ) return the vector containing the
**         points to stroke the marker
*/
    public Vector getMarkerVector(int m) {

        if( marker == null) return null;


        if( marker.number > 0 && m <  marker.number ) {

              return marker.vert[m-1];
        }

        return null;
    }
/*
** boolean loadMarkerFile(URL name)
**        Load the marker file given the files URL. Markers are NOT
** predefined but must be loaded from a Marker definition file
*/
    public boolean loadMarkerFile(URL name) {

        marker = new Markers(name);

        if(marker.number <= 0) return false;

        return true;

    }
/*
** paint(Graphics g)
**                  Paint it ALL.
*/
    public void paint(Graphics g) {
        Graphics lg  = g.create();
        Rectangle r = bounds();
        DataSet d;
        Enumeration e;

        r.x      += border;
        r.y      += border;
        r.width  -= 2*border;
        r.height -= 2*border;

        if( dataset.isEmpty() ) return;

        if( !axis.isEmpty() ) r = drawAxis(lg, r);
        else                  drawFrame(lg,r.x,r.y,r.width,r.height);

        for (e = dataset.elements() ; e.hasMoreElements() ;) {
          d = (DataSet)e.nextElement();
          if( d.legend_text != null) d.draw_legend(lg);
        }

        lg.clipRect(r.x, r.y, r.width, r.height);

        datarect.x      = r.x;
        datarect.y      = r.y;
        datarect.width  = r.width;
        datarect.height = r.height;

        for (e = dataset.elements() ; e.hasMoreElements() ;) {
          ((DataSet)e.nextElement()).draw_data(lg);
        }


        lg.dispose();

    }
/*
** update(Graphics g)
**                   This method is called through the repaint() method.
** All it does is blank the canvas before calling paint.
*/
    public void update(Graphics g) {
          Rectangle r = bounds();
          Color c = g.getColor();

          if( clearAll ) {
             g.setColor(getBackground());
             g.fillRect(r.x,r.y,r.width,r.height);
             g.setColor(c);
          }
          if( paintAll ) paint(g);
    }
/******************
**
** Protected Methods
**
*******************/
/*
**  Rectangle drawAxis(Graphics g, Rectangle r)
**
**  Draw the Axis. As each axis is drawn and aligned less of the canvas
**  is avaliable to plot the data. The returned Rectangle is the canvas
**  area that the data is plotted in.
*/
        protected Rectangle drawAxis(Graphics g, Rectangle r) {
            Axis a;
            Enumeration e;
            int waxis;
            int x      = r.x;
            int y      = r.y;
            int width  = r.width;
            int height = r.height;

// Calculate the available area for the data
            for ( e = axis.elements() ; e.hasMoreElements() ;) {
               a = ((Axis)e.nextElement());
               waxis = a.getAxisWidth(g);

               switch (a.getAxisPos()) {
               case Axis.LEFT:
                          x += waxis;
                          width -= waxis;
                          break;
               case Axis.RIGHT:
                          width -= waxis;
                          break;
               case Axis.TOP:
                          y += waxis;
                          height -= waxis;
                          break;
               case Axis.BOTTOM:
                          height -= waxis;
                          break;
               }
            }
// Draw a frame around the data area (If requested)
            drawFrame(g,x,y,width,height);

// Now draw the axis in the order specified aligning them with the final
// data area.
            for ( e = axis.elements() ; e.hasMoreElements() ;) {
               a = ((Axis)e.nextElement());

               switch (a.getAxisPos()) {
               case Axis.LEFT:
                          r.x += a.width;
                          r.width -= a.width;
                          a.positionAxis(r.x,r.x,y,y+height);
                          if(r.x == x && grid) {
                             a.gridcolor = gridcolor;
                             a.gridlength = width;
                             a.drawAxis(g);
                             a.gridlength = 0;
                          } else {
                             a.drawAxis(g);
                          }
                          break;
               case Axis.RIGHT:
                          r.width -= a.width;
                          a.positionAxis(r.x+r.width,r.x+r.width,y,y+height);
                          if(r.x+r.width == x+width && grid) {
                             a.gridcolor = gridcolor;
                             a.gridlength = width;
                             a.drawAxis(g);
                             a.gridlength = 0;
                          } else {
                             a.drawAxis(g);
                          }
                           break;
               case Axis.TOP:
                          r.y += a.width;
                          r.height -= a.width;
                          a.positionAxis(x,x+width,r.y,r.y);
                          if(r.y == y && grid) {
                             a.gridcolor = gridcolor;
                             a.gridlength = height;
                             a.drawAxis(g);
                             a.gridlength = 0;
                          } else {
                             a.drawAxis(g);
                          }
                          break;
               case Axis.BOTTOM:
                          r.height -= a.width;
                          a.positionAxis(x,x+width,r.y+r.height,r.y+r.height);
                          if(r.y +r.height == y+height && grid) {
                             a.gridcolor = gridcolor;
                             a.gridlength = height;
                             a.drawAxis(g);
                             a.gridlength = 0;
                          } else {
                             a.drawAxis(g);
                          }

                          break;
               }
            }

           return r;
      }
/*
** drawFrame(Graphics g, int x, int y, int width, int height)
**    If requested draw a frame around the data area.
*/
      protected void drawFrame(Graphics g, int x, int y, int width, int height) {
        Color c = g.getColor();

        if(!frame) return;

        if( framecolor != null ) g.setColor(framecolor);

        g.drawRect(x,y,width,height);

        g.setColor(c);


     }



}

/*
** class FileFormatException
** This should be thrown if the fileloader encounters a format error
** not being thrown at the moment. Is being thown by the getMarker method.
*/
class FileFormatException extends Exception {
    public FileFormatException(String s) {
        super(s);
    }
}

/*
** class MVertex
**       This is a structure to contian the Marker points with the 
** drawing command
*/

class MVertex extends Object {
      boolean draw;
      int     x;
      int     y;
}
/*
** class Markers
**              This class is used to load and store the marker definitions.
** The marker definitions are not compiled in but are loaded from a file.
*/

class Markers extends Object {

/*
**    Number of markers loaded
*/
      public int number = 0;
/*
**    An array of vectors. Each element in the array contains the vertex
**    vectors for a marker. Marker 1 is at element vert[0].
*/
      public Vector vert[] = new Vector[10];

/*
**    Markers(URL file)
**         The marker contructor. Loads the markers from the givern URL
*/
      public Markers(URL file) {

          try {
                getMarkers(file);
          } catch ( Exception e ) {
            System.out.println("Error loading marker file!");
          }

      }
/*
**   getMarkers(URL file)
**        This method read the marker file and loads the marker definitions
**   into the vert array. The format of the file is simple
**   The keword "start" starts a new marker definition.
**   The keword "end"   ends a marker definition.
**   m x y means move to position x,y
**   l x y means line to x,y from previous position.
*/
      private void getMarkers(URL file)
                    throws IOException, FileFormatException {
         InputStream is;
         StreamTokenizer st;
         MVertex v;

         try {
            is = file.openStream();
         } catch (Exception e) {
            return;
         }

         st = new StreamTokenizer(is);
         st.eolIsSignificant(true);
         st.commentChar('#');

scan:
        while (true) {
            switch (st.nextToken()) {
              default:
                break scan;
              case StreamTokenizer.TT_EOL:
                break;
              case StreamTokenizer.TT_WORD:

                   if ("start".equals(st.sval)) {
                        vert[number] = new Vector();
                   } else
                   if ("end".equals(st.sval)) {
                        number++;
                   } else
                   if ("m".equals(st.sval)) {
                        v = new MVertex();
                        v.draw = false;
                        if (st.nextToken() == StreamTokenizer.TT_NUMBER) {
                           v.x = (int)st.nval;
                           if (st.nextToken() == StreamTokenizer.TT_NUMBER) {
                               v.y = (int)st.nval;
                               vert[number].addElement(v);
                           }
                        }
                   } else
                   if ("l".equals(st.sval)) {
                        v = new MVertex();
                        v.draw = true;
                        if (st.nextToken() == StreamTokenizer.TT_NUMBER) {
                           v.x = (int)st.nval;
                           if (st.nextToken() == StreamTokenizer.TT_NUMBER) {
                               v.y = (int)st.nval;
                               vert[number].addElement(v);
                           }
                        }
                    }
                 break;
             }


        }
        try {
            if (is != null)
                is.close();
        } catch(Exception e) {
        }
}

}


