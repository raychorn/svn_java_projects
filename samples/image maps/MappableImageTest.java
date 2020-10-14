package UK.ac.bris.ets.image.hot;

/*
** $Log: MappableImageTest.java $
*/

import java.applet.*;
import java.awt.*;
import java.net.*;
import java.util.Vector;
import java.util.Enumeration;
import java.awt.image.*;
import java.lang.Math;
import UK.ac.bris.ets.util.DataFormatException;
import UK.ac.bris.ets.util.General;

class MappableImage extends Canvas {
   static final String rcsID="$Id: MappableImageTest.java 1.2 1996/01/09 16:55:10 joel Exp joel $";
   Image rawImage=null;
   Vector regions=null;
   final int NOPOINT=1; // Have yet to select a start point 
   final int FIRSTPOINT=2; // Have selected a start point and am now drawing
   final int TEST=3;
   int drawMode=NOPOINT; 
   Point lastMousePoint=null;
   Point lastPoint=null;
   MappableImageRegion currentRegion=null;
//   MappableImageRegion URLPending=null;
   boolean hidden=true;
   boolean editable=false;
   int jheight=-1;
   int jwidth=-1;
   Label notifyURL=null;
   String defaultURL="http://www.ets.bris.ac.uk/";
   String backgroundURL="";

   public Dimension preferredSize()
     {
     // This is required to force the layout manager always to report
     // the true required size, even if this Component has previously
     // been told to be a different size.
     return(new Dimension(jwidth,jheight));
     }
   
   public Dimension minimumSize()
     {
     // This is required to force the layout manager always to report
     // the true required size, even if this Component has previously
     // been told to be a different size.
     return(new Dimension(jwidth,jheight));
     }
      
   public Point screenLocation()
	{
	return(General.screenLocation(this,location()));
	}

   String toFormattedString()
      {
      String Output;
      MappableImageRegion current;
      Output=new String();
      Enumeration activeRegions=regions.elements();
      while(activeRegions.hasMoreElements())
         {
         current=(MappableImageRegion)activeRegions.nextElement();
         Output+=current.toFormattedString()+"\n";
         }
      return(Output);
      }

   public void setDefaultURL(String newURL)
     {
     defaultURL=newURL;
     }
   
   private final void selfInit(Image newImage)
     {
     rawImage=newImage;
     
     regions=new Vector();
     lastMousePoint=new Point(-1,-1);
     lastPoint=new Point(-1,-1);
     checkSize();
     MappableImageTest.showMsg("Image Initialised "+jwidth+","+jheight);
     }
   
   MappableImage(Image newImage) 
     {
     super();
     selfInit(newImage);   
     }   
   
   MappableImage(Image newImage,Label notifyParentURL)
     {
     super();
     selfInit(newImage);
     notifyURL=notifyParentURL;
     }

   public void checkSize()
      { 
      jheight=rawImage.getHeight(this);
      jwidth=rawImage.getWidth(this);
      if(jwidth==-1 || jheight==-1)  
         {
         System.err.println("Width or height undefined on image "+rawImage+":"+jwidth+","+jheight);
         return;
         }
//      System.err.println("Width and height on image "+rawImage+":"+jwidth+","+jheight);

      resize(jwidth,jheight); 
      
      repaint();
      } 

   public synchronized void paint(Graphics g)
      { 
      g.drawImage(rawImage,0,0,this);
      Enumeration activeRegions=regions.elements();
      MappableImageRegion region;
      notifyURL.setText("[Background] "+backgroundURL);
      while(activeRegions.hasMoreElements())
         {
         region=(MappableImageRegion)activeRegions.nextElement();
         if((!hidden && drawMode!=TEST) || region.inside(lastMousePoint.x,lastMousePoint.y)) 
           {
           region.paint(g,false,lastMousePoint);
           if(notifyURL!=null) notifyURL.setText(region.getURL());
           if(drawMode==TEST) currentRegion=region;
           }
         }
      if(currentRegion!=null && drawMode!=TEST) currentRegion.paint(g,true,lastMousePoint);
      };
   
   public void update(Graphics g) 
     {
     paint(g);
     }
   
   public synchronized boolean mouseMove(Event e,int x,int y)
     {
     lastMousePoint.x=x;
     lastMousePoint.y=y;
     if(drawMode!=NOPOINT) repaint();
     return(true);
     }

   /*
      //   public synchronized boolean mouseEnter(Event e,int x,int y) 
      //     {
      //     System.err.println("Mouse enter");
      //     requestFocus();
      //     return(true);
      //     }  
      */
   //   public boolean gotFocus(Event e,Object what)
   //     {
   //     System.err.println("got focus");
   //     return(true);
   //     }
   
   public synchronized boolean keyDown(Event e,int key) 
     {   
     //     System.err.println("Got key "+key);
     
     switch (key) 
       {
       case Event.HOME:
         {
         repaint();
         return true;
         }
       case 27:
         {
         if(drawMode==FIRSTPOINT)
           {
           currentRegion=null;
           drawMode=NOPOINT;
           repaint();
           return(true);
           }
         }
       case 9:
         {
         nextFocus();
         return(true);
         }
       case ' ':
         {
         if(drawMode==FIRSTPOINT) 
           {
           drawMode=NOPOINT;   
           Point dialogPosition=General.screenLocation(this,currentRegion.firstPoint());
           
           URLDialog d=new URLDialog("Enter URL for Region",currentRegion,"",dialogPosition,defaultURL);
           regions.addElement(currentRegion);
           //           URLPending=currentRegion;
           currentRegion=null;
           repaint();
           return(true);
           }
         }
       }
     return false;
     }
   
   public static final boolean closePoint(Point a,Point b,int range)
     {
     return(Math.abs(a.x-b.x)<=range && Math.abs(a.y-b.y)<=range);
     }
   
   public synchronized boolean mouseUp(Event e,int x,int y)
     {
     //     System.err.println("Mouse up");
     if(!editable) return(false);
     if(drawMode==TEST) 
       {
       Point dialogPosition=General.screenLocation(this,currentRegion.firstPoint());
       URLDialog d=new URLDialog("Update URL for Region",currentRegion,"",dialogPosition,currentRegion.getURL());
       return(true); 
       }
     
     //     System.err.println("Editing...");
     if(drawMode==NOPOINT)
       {
       //       System.err.println("First point...");
       currentRegion=new MappableImageRegion(this);
       currentRegion.addPoint(x,y);
       lastPoint=new Point(x,y);
       drawMode=FIRSTPOINT; 
       repaint();
       return(true);
       }
     
     if(drawMode==FIRSTPOINT)
       {
       Point where=new Point(x,y);
       if(closePoint(lastPoint,where,3) || closePoint(currentRegion.firstPoint(),where,5))
         {
         // double click - maybe make it two pixel ?
         drawMode=NOPOINT;   
         
         repaint();
         Point dialogPosition=General.screenLocation(this,currentRegion.firstPoint());
         
         URLDialog d=new URLDialog("Enter URL for Region",currentRegion,"",dialogPosition,defaultURL);
         regions.addElement(currentRegion);
         //         URLPending=currentRegion;
         currentRegion=null;
         return(true);
         }
       currentRegion.addPoint(x,y);
       repaint();
       return(true);
       }
     return(false);
     }
   
   public boolean hasMap()
     {  
     return(regions.size()>0);
     }  
   
   public void clearMap()
     {
     regions=new Vector();
     if(drawMode!=TEST) drawMode=NOPOINT;
     currentRegion=null;
     }
   
   public void setMap(String theMap) throws DataFormatException
     {   
     MappableImageTest.showMsg("Setting map to "+theMap);
     if(hasMap()) clearMap();
     String firstLine=theMap.substring(0,theMap.indexOf("\n")-1).trim();
     String lineType=theMap.substring(0,7);
     if(lineType.equalsIgnoreCase("DEFAULT"))
       {
       setBackgroundURL(firstLine.substring(8).trim());
       theMap=theMap.substring(theMap.indexOf("\n")+1);
       }
     while(theMap.length()>0)
       {
       String theLine=theMap.substring(0,theMap.indexOf("\n")-1);
       regions.addElement(new MappableImageRegion(this,theLine));
       theMap=theMap.substring(theMap.indexOf("\n")+1);
       }
     
     return;
     }
   
  public void setBackgroundURL(String theURL) 
     {
     backgroundURL=theURL;
     }
   
   public synchronized void setTestMode() 
     {
     drawMode=TEST;
     currentRegion=null;
     }
   
   public synchronized void setNormalMode()
     {
     if(!editable) setTestMode();
     else 
       {
       drawMode=NOPOINT;
       currentRegion=null;
       }
     }
   
   public boolean isModeTest()
     {
     if(drawMode==TEST) return(true);
     return(false);
     }
   
   public void setHidden(boolean value)
     {
     hidden=value;
     }
   
   public synchronized void setEditable(boolean value)
     {
     editable=value;
     if(editable) 
       {
       setNormalMode();
       }
     else 
       {
       setTestMode();
       }
     }
   }

class MappableImageRegion extends Polygon implements URLDialogCallback 
  {
  static final String rcsID="$Id: MappableImageTest.java 1.2 1996/01/09 16:55:10 joel Exp joel $";
  String URL;
  MappableImage parent;
  
  public void setURL(String newURL,String theTag) 
    {
    URL=new String(newURL);
    parent.setDefaultURL(URL);
    parent.repaint();
    } 
  
 public MappableImageRegion(MappableImage myParent)
   {
   super();
   parent=myParent;
   }
  
  public MappableImageRegion(MappableImage myParent,String myMapLine) throws DataFormatException
  {
  super();
  parent=myParent;
//  myMapLine=myMapLine.toUpperCase();
  myMapLine=myMapLine.trim();
  String lineType=myMapLine.substring(0,4);
  if(!lineType.equalsIgnoreCase("POLY"))
    {
    throw new DataFormatException("Map line '"+myMapLine+"' does not start with POLY(GON)",0);
    }
  
  while(true)
    {  
//    if(myMapLine.indexOf(" ")==-1) return;
    myMapLine=myMapLine.substring(myMapLine.indexOf(" "));
    myMapLine=myMapLine.trim();
    if(!myMapLine.startsWith("(")) 
      {
      setURL(myMapLine,"");
      return;
      }
    myMapLine=myMapLine.substring(1).trim();
    int xpoint=new Integer(myMapLine).intValue();
    if(myMapLine.indexOf(",")==-1)
      {
      throw new DataFormatException("Parse error at '"+myMapLine+"' : ',' expected",0);
      }
    myMapLine=myMapLine.substring(myMapLine.indexOf(",")+1).trim();
    int ypoint=new Integer(myMapLine).intValue();
    if(myMapLine.indexOf(")")==-1)
      {
      throw new DataFormatException("Parse error at '"+myMapLine+"' : ')' expected",0);
      }
    addPoint(xpoint,ypoint);    
    myMapLine=myMapLine.substring(myMapLine.indexOf(")")+1);
    }
  } 

  public Point firstPoint()
    {
    if(npoints>0) 
      {
      return(new Point(xpoints[0],ypoints[0]));
      }
    else	
		{
		return(null);
		}
    }
  
  public String getURL() 
    {
    if(URL!=null) return(URL.toString());
    else return("");
    }
  
  public boolean isFirstPoint(Point where)
    {
    return(isFirstPoint(where.x,where.y));
    }
  
  public boolean isFirstPoint(int x,int y)
    {
    if(x==xpoints[0] && y==ypoints[0]) return(true);
    return(false);
    }
  
  public synchronized void paint(Graphics g,boolean rubberBand,Point mousePoint)
    {
    Point where=null;
    Point last=new Point(-1,-1);
    Point first=null;
    int I;
    
    if(inside(mousePoint.x,mousePoint.y) || rubberBand) g.setColor(Color.red);
    else g.setColor(Color.black);
    // Not using drawPolyon as we may want to rubberband last line 
    for(I=0;I<npoints;I++)
      {
      where=new Point(xpoints[I],ypoints[I]);
      if(last.x==-1) 
           {
           last=where;
           first=last;
           continue;
           }
      g.drawLine(last.x,last.y,where.x,where.y);
      last=where;
      }
    if(!rubberBand) g.drawLine(last.x,last.y,first.x,first.y);
    else 
      {
      //         g.setColor(Color.white);
      g.setXORMode(Color.white);
      g.drawLine(last.x,last.y,mousePoint.x,mousePoint.y);
      }
    }
  
  public void update()
    {
    return;
    }
  
  public String toFormattedString() 
    {
    String Output;
    
    Output=new String("polygon ");
    int index=0;
    while(index<npoints)
      {
      index++;
      Output+="("+xpoints[index]+","+ypoints[index]+") ";
      }
    Output+=URL.toString();
    return(Output);
    }
  }

public class MappableImageTest extends Applet implements Runnable {
  static final String rcsID="$Id: MappableImageTest.java 1.2 1996/01/09 16:55:10 joel Exp joel $";
   ImageMapAuthor theWidget=null;   
   Image testImage=null;
   Thread imageFetch;
   MediaTracker images; 


   public static void showMsg(String Message){
   System.err.println(Message);
   }

    public void init() {
      showMsg("MappableImageTest : Initialising...");
      testImage=getImage(getDocumentBase(),getParameter("image"));        

      images=new MediaTracker(this);
      images.addImage(testImage,1);

      imageFetch=new Thread(this);
      imageFetch.start();

      showMsg("MappableImageTest : Initialising...done.");
      }    

   public void run() {

      showMsg("MappableImageTest : Running MediaTracker...");
      try 
         {
         images.waitForID(1);
         }
      catch(InterruptedException e) 
         {
         System.err.println("Image error : "+e);
         System.exit(1);
         }
      showMsg("MappableImageTest : Got image ?"+testImage);
      imageFetch=null;
      images=null;
      theWidget=new ImageMapAuthor(getParameter("image"),testImage,"","");
    }

   public String getAppletInfo()
     {
     return("Client Side Image Map Creator. Version 0.9 Joel Crisp 1995 ( $Id: MappableImageTest.java 1.2 1996/01/09 16:55:10 joel Exp joel $ )");
     }

}



