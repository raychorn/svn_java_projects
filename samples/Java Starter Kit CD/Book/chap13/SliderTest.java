/* text areas */

import java.awt.*;

public class SliderTest extends java.applet.Applet {
  Label l;

  public void init() {
    l = new Label("0");
    add(l);
    add(new Scrollbar(Scrollbar.HORIZONTAL,0,0,1,100));
  }

  public boolean handleEvent(Event evt) {
    if (evt.target instanceof Scrollbar) {
      int v = ((Scrollbar)evt.target).getValue();
      l.setText(String.valueOf(v));
    }
    return true;
  }

}
