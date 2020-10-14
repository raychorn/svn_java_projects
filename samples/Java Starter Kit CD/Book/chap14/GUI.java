import java.awt.*;

public class GUI extends java.applet.Applet {
    Frame window;

    public void init() {
      add(new Button("Open Window"));
      add(new Button("Close Window"));

      window = new MyFrame("A Popup Window");
      window.resize(150,150);
      window.show();
    }

    public boolean action(Event evt, Object arg) {
      if (evt.target instanceof Button) {
	String label = (String)arg;
	if (label.equals("Open Window")) {
	  if (!window.isShowing()) 
	    window.show();
	}
	else if (label.equals("Close Window")) {
	  if (window.isShowing())
	    window.hide();
	}
      }
      return true;
    }
}

class MyFrame extends Frame {
  Dialog dl;
  Label l;
  TextField tf;

  MyFrame(String title) {
    super(title);
    MenuBar mb = new MenuBar();
    Menu m = new Menu("Colors");
    m.add(new MenuItem("Red"));
    m.add(new MenuItem("Blue"));
    m.add(new MenuItem("Green"));
    m.add(new MenuItem("-"));
    m.add(new CheckboxMenuItem("Reverse Text"));
    m.add(new MenuItem("Set Text..."));
    mb.add(m);
    setMenuBar(mb);
    
    setLayout(new GridLayout(1,1));

    l = new Label("This is a Window", Label.CENTER);
    add(l);

    // make dialog for this window
    dl = new Dialog(this, "Enter Text",true);
    dl.setLayout(new GridLayout(2,1,30,30));
    tf = new TextField(l.getText(),20);
    dl.add(tf);
    dl.add(new Button("OK"));
    dl.resize(150,75);
  }

  public boolean action(Event evt, Object arg) {
    String label = (String)arg;
    if (evt.target instanceof MenuItem) {
      if (label.equals("Red")) setBackground(Color.red);
      else if (label.equals("Blue")) setBackground(Color.blue);
      else if (label.equals("Green")) setBackground(Color.green);
      else if (label.equals("Set Text...")) dl.show();
    }
    if (evt.target instanceof CheckboxMenuItem) {
      if (getForeground() == Color.black)
	setForeground(Color.white);
      else setForeground(Color.black);
    }
    if (evt.target instanceof Button) {
      if (label == "OK") {
	System.out.println(tf.getText());
	l.setText(tf.getText()); 
	dl.hide();
      }
    }
    return true;
  } 
}

