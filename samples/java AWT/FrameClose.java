import java.awt.*;

public class FrameClose extends Frame {
  public FrameClose() {
    super();
  }
  public FrameClose(String s) {
    super(s);
  }
  public boolean handleEvent(Event e) {
    if(e.id==Event.WINDOW_DESTROY) {
	  System.exit(0);
	  return true;
	}
	else return super.handleEvent(e);
  }
}
