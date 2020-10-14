import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Font;

public class CrazyLabelExample extends java.applet.Applet {

   CrazyLabel label;

   public void init() {
      setLayout( new BorderLayout());
      label = new CrazyLabel("Center");
      label.setFont(new Font("TimesRoman", Font.BOLD, 36));
      label.setClear(true);
      add( "Center", label);
      add( "North", new Button( "North"));
      add( "South", new Button( "South"));
      add( "East", new Button( "East"));
      add( "West", new Button( "West"));
   }

   public void start() {
      label.start();
   }

   public void stop() {
      label.stop();
   }
}
