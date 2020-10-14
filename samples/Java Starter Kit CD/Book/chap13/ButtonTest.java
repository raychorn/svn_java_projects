/* create a few buttons */

import java.awt.*;

public class ButtonTest extends java.applet.Applet {

  public void init() {
    add(new Button("Rewind"));
    add(new Button("Play"));
    add(new Button("Fast Forward"));
    add(new Button("Stop"));
  }

  public boolean action(Event evt, Object arg) {
    System.out.println(arg);
    return true;
  }
}
