/* check boxes */

import java.awt.*;

public class CheckboxGroupTest extends java.applet.Applet {

  public void init() {
    CheckboxGroup cbg = new CheckboxGroup();

    add(new Checkbox("Red", cbg, true));
    add(new Checkbox("Blue", cbg, false));
    add(new Checkbox("Yellow", cbg, false));
    add(new Checkbox("Green", cbg, false));
    add(new Checkbox("Orange", cbg, false));
    add(new Checkbox("Purple", cbg, false));
  }

  public boolean action(Event evt, Object arg) {
    System.out.println(arg);
    return true;
  }
}
