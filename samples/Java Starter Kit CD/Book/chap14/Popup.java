/* windows */

import java.awt.*;

public class Popup extends java.applet.Applet  {

  Frame window;

  public void init() {
    
    add(new Button("Open Window"));
    add(new Button("Close Window"));

    window = new Frame("Colors");
    window.pack();
    window.add(new Canvas());
    window.setBackground(Color.blue);
    
    MenuBar mb = new MenuBar();
    window.setMenuBar(mb);
    Menu m = new Menu("Colors");
    mb.add(m);
    m.add(new MenuItem("Red"));
    m.add(new MenuItem("Blue"));
    m.add(new MenuItem("Green"));

    System.out.println(window.location());
    window.show();    
  }

}

      

