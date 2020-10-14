package UK.ac.bris.ets.image.hot;

import java.lang.*;
import java.util.*;
import java.awt.*;

import UK.ac.bris.ets.util.ETSException;


class URLDialog extends Frame
{
   static final String rcsID="$Id: MappableImageTest.java 1.9 1995/10/27 09:20:27 joel Exp joel $";

   static Vector oldURLs=new Vector();
   
   String URL;
   String theTag;
   TextField tf;
   URLDialogCallback theCallback;
   Button OK;
  
   URLDialog(String title,URLDialogCallback callback,String tag,Point where,String defaultText)
      {
      super(title);
      GridBagLayout bag=new GridBagLayout();
      GridBagConstraints c=new GridBagConstraints();

      resize(800,600);

      setLayout(bag);
    
      c.weightx=1.0; 
      c.ipadx=2;
      c.ipady=2;
//      Label l=new Label("Enter URL");
//	bag.setConstraints(l,c);
//     add(l);
      tf=new TextField(70);
      tf.setText(defaultText);
      c.gridwidth=GridBagConstraints.REMAINDER;
      bag.setConstraints(tf,c);
      add(tf);

      if(oldURLs.size()>0)
        {
        c.fill=GridBagConstraints.HORIZONTAL;
        List l=new List(5,false);
        Enumeration urls=oldURLs.elements();
        while(urls.hasMoreElements())
          {
          l.addItem((String)urls.nextElement());
          }
        bag.setConstraints(l,c);
        add(l);
//        l.resize(l.minimumSize());
        c.fill=GridBagConstraints.NONE;
        }
      
      c.gridwidth=1;
      OK=new Button("OK"); 
      c.gridwidth=GridBagConstraints.HORIZONTAL;
      bag.setConstraints(OK,c);
      add(OK);
      c.gridwidth=GridBagConstraints.REMAINDER;
      Button b=new Button("Cancel"); 
      bag.setConstraints(b,c);
      add(b);
      
      pack();
      Dimension newSize=preferredSize();
      where.translate(-tf.location().x,-tf.location().y);
      if(where.x<1) where.x=1;
      if(where.y<1) where.y=1;
      reshape(where.x,where.y,newSize.width,newSize.height);
      show();
	tf.requestFocus();
      theCallback=callback;
      theTag=tag;
      }

   public boolean keyUp(Event e, int key)
     {
     if(key==13)
       {	
       OK.requestFocus();	
       }
     return(false);
     }
   
   public boolean action(Event e,Object what)   
     {
     if(e.id==Event.WINDOW_DESTROY) 
       {
       hide();
       dispose();
       return(true);
       }
     //     if(locate(e.x,e.y)!=b) return(false);  
     if("Cancel".equals(e.arg))
       {
       hide();
       dispose();
       return(true);
       }
     
     if("OK".equals(e.arg)) 
       {
       URL=tf.getText();
       hide();
       dispose();
       try
         {
         if(!oldURLs.contains(URL))
           {
           oldURLs.addElement(URL);
           }
         theCallback.setURL(URL,theTag);
         }
       catch(ETSException e1)
         {
         // Ignore it.
         }
       
       return(true);
       }
     return(false);
     }
   }


