import java.awt.*;
import java.util.*;

class Popup extends Window {
  Vector items;
  Dimension mySize;
  Point target;
  Frame parent;

  public Popup(Frame parent,Point target){
    super(parent);
    this.parent=parent;
    items=new Vector(4,2);
    mySize=new Dimension(0,0);
    setLayout(null);
    setTarget(target);
  }
  public CWPopup(Frame parent){
    super(parent);
    this.parent=parent;
    items=new Vector(4,2);
    mySize=new Dimension(0,0);
    setLayout(null);
  }

  public void setTarget(Point target) {
    this.target=new Point(target.x+parent.location().x-insets().left,target.y+parent.location().y-insets().top);
  }

  public void add(String s) {
    Rectangle r;
    r=new Rectangle(5,sHeight()*items.size()+5+insets().top,0,sHeight());
    items.addElement(new PopupItem(s,false,r));
    int w=sWidth(s);
    mySize.width=Math.max(mySize.width,w);
    mySize.height+=sHeight();
    for(int i=0;i<items.size();i++)
      ((PopupItem)items.elementAt(i)).pos.width=mySize.width;
  }

  public int sWidth(String s) {
    return getFontMetrics(getFont()).stringWidth(s);
  }
  public int sHeight(){
    return getFontMetrics(getFont()).getHeight();
  }

  public void update(Graphics g) {paint(g);}
  public void paint(Graphics g) {
    Insets in=insets();
    Dimension d=new Dimension(mySize.width+in.left+in.right+10,mySize.height+in.top+in.bottom+10);
    resize(d);
    move(target.x,target.y);
    System.out.println("Sized to: "+d.width+","+d.height+".");
    Color old=g.getColor();
    g.setColor(getBackground());
    g.fillRect(1,1,d.width-3,d.height-3);
    g.setColor(old);
    g.drawRect(0,0,d.width-1,d.height-1);
    for(int i=0;i<items.size();i++) {
      PopupItem p=(PopupItem)items.elementAt(i);
      if(p.over) {
        g.setColor(new Color(0,0,255));
        g.fillRect(p.pos.x-1,p.pos.y,p.pos.width+1,p.pos.height);
        g.setColor(new Color(255,255,0));
      }
      p.changed=false;
      g.drawString(p.item,5+in.left,5+in.top+sHeight()*(i+1)-getFontMetrics(getFont()).getDescent());
      g.setColor(old);
    }
  }

  public boolean handleEvent(Event evt) {
    if((evt.id==Event.MOUSE_EXIT)||(!new Rectangle(1,1,size().width-2,size().height-2).inside(evt.x,evt.y))) {
      hide();
      return true;
    }
    if(evt.id==Event.MOUSE_MOVE) {
      PopupItem p;
      boolean changed=false;
      for(int i=0;i<items.size();i++) {
        p=(PopupItem)items.elementAt(i);
        if(p.pos.inside(evt.x,evt.y)) {
          if(!p.over) {
            p.over=true;
            p.changed=changed=true;
          }
        }
        else {
          if(p.over) {
            p.over=false;
            p.changed=changed=true;
          }
        }
      }
      if(changed)repaint();
      return true;
    }
    if(evt.id==Event.MOUSE_DOWN) {
      for(int i=0;i<items.size();i++)
        if(((PopupItem)items.elementAt(i)).pos.inside(evt.x,evt.y)) {
          hide();
          parent.deliverEvent(new Event(this,Event.LIST_SELECT,((PopupItem)items.elementAt(i)).item));
        }
      return true;
    }
    return super.handleEvent(evt);
  }
}

class PopupItem extends Object {
  public String item;
  public boolean over,changed;
  public Rectangle pos;

  public PopupItem(String item,boolean over,Rectangle pos) {
    this.item=item;
    this.over=over;
    this.pos=pos;
    changed=false;
  }
}
