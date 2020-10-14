/* check boxes */

import java.awt.*;

public class CheckboxTest extends java.applet.Applet {

  public void init() {
    add(new Checkbox("Shoes"));
    add(new Checkbox("Socks"));
    add(new Checkbox("Pants"));
    add(new Checkbox("Underwear", null, true));
    add(new Checkbox("Shirt"));
  }

  public boolean action(Event evt, Object arg) {
    System.out.println(arg);
    return true;
  }
}
