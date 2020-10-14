import java.awt.*;

public interface TabParent {
  public void attach(int tabIndex, Component comp);
  public void detach(Component comp);
}
