/* scrolling lists */

import java.awt.*;

public class ListsTest extends java.applet.Applet {

  public void init() {
    List lst = new List(5, true);

    lst.addItem("Hamlet");
    lst.addItem("Claudius");
    lst.addItem("Gertrude");
    lst.addItem("Polonius");
    lst.addItem("Horatio");
    lst.addItem("Laertes");
    lst.addItem("Ophelia");
    
    add(lst);
  }
  public boolean action(Event evt, Object arg) {
    System.out.println("arg: " + arg);
    return true;
  }

}
