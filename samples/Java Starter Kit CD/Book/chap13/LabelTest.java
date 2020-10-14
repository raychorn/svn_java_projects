/* Labels */

import java.awt.*;

public class LabelTest extends java.applet.Applet {

  public void init() {
    add(new Label("aligned left  "));
    add(new Label("aligned center", Label.CENTER));
    add(new Label(" aligned right", Label.RIGHT));
  }
}
