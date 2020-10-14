/* choice menus */

import java.awt.*;

public class TextFieldTest extends java.applet.Applet {

  public void init() {
    add(new Label("Enter your Name"));
    add(new TextField("your name here",45));
    add(new Label("Enter your phone number"));
    add(new TextField(12));
    add(new Label("Enter your password"));
    TextField t = new TextField(20);
    t.setEchoCharacter('*');
    add(t);

  }

  public boolean action(Event evt, Object arg) {
    System.out.println(arg);
    return true;
  }
}
