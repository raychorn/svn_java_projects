import java.awt.*;
import java.util.*;
import java.lang.*;
import FrameClose;

public class Slider extends Panel {
  Scrollbar scroll;
  TextField field;
  int current;
  boolean done=false;

  public Slider(int a,int b,int c,int d) {this(Scrollbar.HORIZONTAL,a,b,c,d);}
  public Slider(int a,int b,int c,int d,int e) {
    super();
    setLayout(new BorderLayout());
    field = new TextField();
    scroll = new Scrollbar(a,b,c,d,e);
    setValue(b);
    add("South", scroll);
    add("Center", field);
  }
  public Slider(int a,int b,int c,int d,String s) {this(Scrollbar.HORIZONTAL,a,b,c,d,s);}
  public Slider(int a,int b,int c,int d,int e,String s) {
    super();
    setLayout(new GridLayout(3,1));
    field = new TextField();
    scroll = new Scrollbar(a,b,c,d,e);
    setValue(b);
    add(new Label(s,Label.CENTER));
    add(field);
    add(scroll);
  }

  public Insets insets() {
    return new Insets(1,1,1,1);
  }

  public void paint(Graphics g) {
    Rectangle rect = bounds();
    g.drawLine(rect.x, rect.y, rect.height-1, rect.width-1);
  }

  public boolean handleEvent(Event evt) {
    int in;

    if (evt.target == scroll) {
      in = ((Integer) evt.arg).intValue();
      setValue(in,true);
      return false;
    } else if (evt.target == field) {
      if(evt.id==Event.KEY_RELEASE) {
        try {in = Integer.parseInt(field.getText());
        } catch(NumberFormatException e){
          if(scroll.getMinimum()>0) setValue(scroll.getMinimum(),false);
          else setValue(0,false);
          return false;
        }
        setValue(in,false);
        return false;
      } else if (evt.id==Event.KEY_PRESS) {
        if(keyBad(evt.key)) return true;
        int temp=0;
        try {temp=Integer.parseInt(field.getText().substring(0,field.getSelectionStart())+(char)evt.key+field.getText().substring(field.getSelectionStart()));
        } catch(NumberFormatException e) {return false;}
        if((temp<scroll.getMinimum())||(temp>scroll.getMaximum())) return true;
      }
      return false;
    } else {
      return super.handleEvent(evt);
    }
  }

  public int getValue() {return current;}
  public int value(){return current;}

  public String toString() {
    return String.valueOf(current);
  }

  public void setValue(int val) {
    if(val<scroll.getMinimum()) val=scroll.getMinimum();
    if(val>scroll.getMaximum()) val=scroll.getMaximum();
    current = val;
    field.setText(String.valueOf(val));
    scroll.setValue(val);
  }
  void setValue(int val, boolean affectField) {
    if(val<scroll.getMinimum()) val=scroll.getMinimum();
    if(val>scroll.getMaximum()) val=scroll.getMaximum();
    current=val;
    if(affectField) field.setText(String.valueOf(val));
    else scroll.setValue(val);
  }

  public void setValues(int value, int visible, int minimum, int maximum) {
    scroll.setValues(value,visible,minimum,maximum);
    setValue(value);
  }

  public void enable(boolean doit) {
    scroll.enable(doit);
    field.enable(doit);
  }

  public boolean isEnabled(){return scroll.isEnabled();}

  public void requestFocus(){scroll.requestFocus();}

  public boolean keyBad(int key){
    switch(key){
      case 48: case 49: case 50: case 51: case 52: case 53: case 54:
      case 55: case 56: case 57: case 1006: case 1007: case 1000:
      case 1001: case 127: case 8:
        return false;
      default: return true;;
    }
  }
}
