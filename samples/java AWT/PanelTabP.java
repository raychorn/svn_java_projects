import java.awt.*;
import java.util.*;
import TabParent;

public class PanelTabP extends Panel implements TabParent {
  Vector tabOrder;
  
  public PanelTabP(){tabOrder=new Vector(6,4);}
  
  public Component add(int tabIndex,Component comp) {
    try{
      tabOrder.insertElementAt(comp,tabIndex);
    } catch(ArrayIndexOutOfBoundsException e) {
      tabOrder.addElement(comp);
    }
    return add(comp);
  }
  
  public synchronized Component add(int tabIndex,Component comp,int pos) {
    try{
      tabOrder.insertElementAt(comp,tabIndex);
    } catch(ArrayIndexOutOfBoundsException e) {
      tabOrder.addElement(comp);
    }
    return add(comp,pos);
  }
  
  public synchronized Component add(int tabIndex, String name, Component comp) {
    try{
      tabOrder.insertElementAt(comp,tabIndex);
    } catch(ArrayIndexOutOfBoundsException e) {
      tabOrder.addElement(comp);
    }
    return add(name,comp);
  }
  
  public synchronized void remove(Component comp){
    tabOrder.removeElement(comp);
    super.remove(comp);
  }
  
  public synchronized void removeAll(){
    tabOrder.removeAllElements();
    super.removeAll();
  }

  public void listTabs(){
    for(int i=0;i<tabOrder.size();i++) System.out.println("Tab "+i+": "+(Component)tabOrder.elementAt(i));
  }
  
  public boolean handleEvent(Event evt){
    int pos;
    if(evt.id==Event.KEY_PRESS)
      if(evt.key==9) {
        pos=tabOrder.indexOf(evt.target);
        if(evt.target instanceof TextArea) pos=-2;
        if(pos>-1){
          if(evt.shiftDown()){
            if(pos==0) ((Component)tabOrder.lastElement()).requestFocus();
              else ((Component)tabOrder.elementAt(--pos)).requestFocus();
          }
          else {
            if(pos==tabOrder.size()-1) ((Component)tabOrder.firstElement()).requestFocus();
              else ((Component)tabOrder.elementAt(++pos)).requestFocus();
          }
          return true;
        }
      }
    return super.handleEvent(evt);
  }
  
  public synchronized void attach(int tabIndex, Component comp){
    try{
      tabOrder.insertElementAt(comp,tabIndex);
    } catch(ArrayIndexOutOfBoundsException e) {
      tabOrder.addElement(comp);
    }
  }
  
  public synchronized void detach(Component comp){
    tabOrder.removeElement(comp);
  }  
}
