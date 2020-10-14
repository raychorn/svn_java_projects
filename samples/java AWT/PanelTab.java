import java.awt.*;
import java.util.*;

public class PanelTab extends Panel {
  TabParent parent;
  
  public PanelTab(TabParent parent){
    super();
    this.parent=parent;
  }
  
  public Component add(int tabIndex, Component comp){
    parent.attach(tabIndex,comp);
    return add(comp);
  }
  public synchronized Component add(int tabIndex, Component comp, int pos) {
    parent.attach(tabIndex,comp);
    return add(comp,pos);
  }
  public synchronized Component add(int tabIndex, String name, Component comp) {
    parent.attach(tabIndex,comp);
    return add(name,comp);
  }
  
  public synchronized void remove(Component comp) {
    parent.detach(comp);
    super.remove(comp);
  }
}
