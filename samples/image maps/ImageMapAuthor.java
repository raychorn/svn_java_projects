package UK.ac.bris.ets.image.hot;

import java.applet.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.util.Vector;
import java.util.Enumeration;
import java.awt.image.*;
import java.lang.Math;
import UK.ac.bris.ets.util.DataFormatException;
import UK.ac.bris.ets.util.General;
import UK.ac.bris.ets.net.*;


public class ImageMapAuthor extends Frame implements URLDialogCallback
  {
  MappableImage theImage;
  String theMap;
  String dispatch;
  Checkbox editBox;
  Checkbox testBox;
  Checkbox hiddenBox;
  Label URLDisplay;

  ImageMapAuthor(String Title,Image rawImage,String rawMap,String dispatchURL)
    {
    super(Title);
    reshape(10,10,400,300);


    CheckboxGroup modes=new CheckboxGroup();

    GridBagLayout bag=new GridBagLayout();
    GridBagConstraints c=new GridBagConstraints();

    URLDisplay=new Label("No URL");


    setLayout(bag);
    theImage=new MappableImage(rawImage,URLDisplay);
    theImage.setEditable(true);
    
    c.weightx=1.0;
    c.weighty=1.0;
    c.ipadx=2;
    c.ipady=2;
    c.gridwidth=GridBagConstraints.REMAINDER;
    bag.setConstraints(theImage,c);
    add(theImage);


    MenuBar menus; 
    menus=new MenuBar();
    Menu file=new Menu("File");
    file.add(new MenuItem("Load Map"));
    file.add(new MenuItem("Save Map"));
    file.add(new MenuItem("-"));
    file.add(new MenuItem("Set Background URL"));
    file.add(new MenuItem("-"));
    file.add(new MenuItem("Exit"));
    menus.add(file);
    setMenuBar(menus);

    dispatch=new String(dispatchURL);
    hiddenBox=new Checkbox("Hidden");
    hiddenBox.setState(true);
    editBox=new Checkbox("Drawing",modes,true);
    testBox=new Checkbox("Testing",modes,false);

    c.fill=GridBagConstraints.HORIZONTAL;
    bag.setConstraints(URLDisplay,c);
    add(URLDisplay);
    c.fill=GridBagConstraints.NONE;

    c.gridwidth=1;
 //   c.fill=GridBagConstraints.HORIZONTAL;

    bag.setConstraints(hiddenBox,c);
    add(hiddenBox);
    bag.setConstraints(editBox,c);
    add(editBox);
    c.gridwidth=GridBagConstraints.REMAINDER;
    bag.setConstraints(testBox,c);
    add(testBox);
    
    pack();
    setSize();

    MappableImageTest.showMsg("Defined GUI, displaying frame..");
    MappableImageTest.showMsg("Frame Info"+size()+location());
    show();
    MappableImageTest.showMsg("Frame displayed..");
    }
  
  public void setURL(String theURL,String tag) throws DataFormatException
    {
    if(tag.equals("Load"))
      {
      theMap=theURL;
      URL mapURL=null;
      MappableImageTest.showMsg("Loading map..."+theMap);
      try 
        {
        mapURL=new URL(theMap);
        }
      catch(MalformedURLException e)
        {
        MappableImageTest.showMsg("Errrk. Can't parse map URLe :"+e);
        return;
        }
      URLConnection mapConnection;
      try
        {
        mapConnection=mapURL.openConnection();
        }
      catch(IOException e3)
        {
        MappableImageTest.showMsg("Errk. Failed to get connection :"+e3);
        return;
        }

      MappableImageTest.showMsg(""+mapConnection);
      MappableImageTest.showMsg("Content-Type : "+mapConnection.getContentType());
      
     mapConnection.setContentHandlerFactory(ETSNetManager.getManager());
      
      String mapData="";
      
      try
        {
        mapData=(String)mapURL.getContent();
        }
      catch(IOException e2)
        {
        MappableImageTest.showMsg("Errrk. Can't load map URL :"+e2);
        return;
        }
      
      theImage.setMap(mapData);
      }
    if(tag.equals("Background")) theImage.setBackgroundURL(theURL);
    }
  
   public boolean action(Event e,Object what)   
     {
//     System.err.println("Action "+e+" "+what);
     
     if(e.target==editBox || e.target==testBox)
        {
        if(editBox.getState()) theImage.setNormalMode();
        if(testBox.getState()) 
           {
           theImage.setTestMode();
           hiddenBox.disable();
           }
        else hiddenBox.enable();
//        System.err.println("Editable set to "+editBox.getState());
        theImage.repaint();
        return(true);
        }
     if(e.target==hiddenBox)
        {
        theImage.setHidden(hiddenBox.getState());
        theImage.repaint();
        }
     if("Load Map".equals(what))
       {
       theImage.clearMap();
       URLDialog theDialog=new URLDialog("Enter URL for Map",this,"Load",screenLocation(),"http://");
       }

if("Set Background URL".equals(what))
  {
       URLDialog theDialog=new URLDialog("Enter URL for Background",this,"Background",screenLocation(),"http://");
  
  }

     if("Exit".equals(what)) 
       {
       exit();
       return(true);
       }
     return(false);
     }

   public void exit() {
        System.err.println("Exiting....");
        dispose();   
        System.exit(0);
    }

  public Point screenLocation()
    {
    return(General.screenLocation(this,location()));
    }
  
   public void setSize()
     {
     layout();
     Dimension newSize=preferredSize();
     reshape(10,10,newSize.width,newSize.height);
     layout();
     repaint();
     }
}
