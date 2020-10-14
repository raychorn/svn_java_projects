/* choice menus */

import java.awt.*;

public class ChoiceTest extends java.applet.Applet {

  public void init() {
    Choice c = new Choice();

    c.addItem("Apples");
    c.addItem("Oranges");
    c.addItem("Strawberries");
    c.addItem("Blueberries");
    c.addItem("Bananas");

    add(c);
  }

  public boolean action(Event evt, Object arg) {
    System.out.println(arg);
    return true;
  }
}
