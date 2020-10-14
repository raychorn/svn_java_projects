/* scrolling lists */

import java.awt.*;
import java.net.URL;
import java.net.MalformedURLException;

public class ButtonLink extends java.applet.Applet {
  
  Bookmark bmlist[] = new Bookmark[3];

  public void init() {
    bmlist[0] = new Bookmark("Laura's Home Page",
	"http://www.lne.com/lemay/");
    bmlist[1] = new Bookmark("Yahoo", "http://www.yahoo.com");
    bmlist[2]= new Bookmark("Java Home Page", "http://java.sun.com");

    setLayout(new GridLayout(bmlist.length,1,10,10));

    for (int i = 0; i < bmlist.length; i++) {
      add(new Button(bmlist[i].name));
    }
  }

  public boolean action(Event evt, Object arg) {
    if (evt.target instanceof Button) {
      LinkTo((String)arg);
      return true;	
    }
    else return false;
  }

  void LinkTo(String name) {
    URL theURL = null;
    
    for (int i = 0; i < bmlist.length; i++) {
      if (name.equals(bmlist[i].name)) {
	theURL = bmlist[i].url;
      }
    }
    if (theURL != null)
      System.out.println("now loading: " + theURL);
      getAppletContext().showDocument(theURL);
  }
}

      
class Bookmark {
  String name;
  URL url;

  Bookmark(String name, String theURL) {
    this.name = name;
    try { this.url = new URL(theURL); }
    catch ( MalformedURLException e) {
      System.out.println("Bad URL: " + theURL);
    }

  }
}
