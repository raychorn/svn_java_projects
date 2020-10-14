// CrazyText.java
// Version 2.0
// Works with JDK 1.0 beta2.
// Patrick Taylor (taylor@us.net)
// Based on Daniel Wyszynski's NervousText applet from the JDK 1.0 beta1.
// See accompanying HTML documentation for detailed information.
// Requires CrazyLabel class.

import java.awt.Font;
import java.awt.Event;
import java.awt.Dimension;
import java.awt.BorderLayout;

public class CrazyText extends java.applet.Applet {

   protected CrazyLabel label;

   protected static String[][] paramInfo = {
      {"text",		"string",	"text to be displayed"},
      {"delay", 	"int",		"milliseconds between updates"},
      {"delta", 	"int",		"craziness factor; max pixel offset"},
      {"hgap",		"int",		"horizontal spacing between chars"},
      {"hspace",	"int",		"extra spacing on left and right"},
      {"vspace",	"int",		"extra spacing on top and bottom"},
      {"clear", 	"boolean", 	"clear background on update"},
      {"cycle", 	"string", 	"color changes: whole|char|none"},
      {"fontName", 	"string",	"font name"},
      {"fontSize", 	"int",		"font size"},
      {"fontBold", 	"boolean",	"font should be bold"},
      {"fontItalic", 	"boolean",	"font should be italic"},
   };

   public String[][] getParameterInfo() {
      return paramInfo;
   }

   protected String getParam(String name, String defaultVal) {
      String val = getParameter(name);
      return (val == null) ? defaultVal : val;
   }

   public void init() {
      // get font params
      String fontName = getParam("fontName","TimesRoman");
      int fontSize = Integer.parseInt(getParam("fontSize","36"));
      boolean fontBold = getParam("fontBold","true").equals("true");
      boolean fontItalic = getParam("fontItalic","false").equals("true");
      int fontStyle = (fontBold   ? Font.BOLD   : 0) + 
		      (fontItalic ? Font.ITALIC : 0);

      // create and configure label
      label = new CrazyLabel(getParam("text","CrazyText"));
      label.setDelay(Integer.parseInt(getParam("delay","100")));
      label.setDelta(Integer.parseInt(getParam("delta","5")));
      label.setHgap(Integer.parseInt(getParam("hgap","0")));
      label.setHspace(Integer.parseInt(getParam("hspace","0")));
      label.setVspace(Integer.parseInt(getParam("vspace","0")));
      label.setClear(getParam("clear","false").equals("true"));
      label.setCycle(getParam("cycle","whole"));
      label.setFont(new Font(fontName, fontStyle, fontSize));

      // add label to applet container
      setLayout(new BorderLayout());
      add("Center", label);

      // this doesn't work now, but it might someday
      resize(minimumSize());
   }

   public void start() {
      label.start();
   }

   public void stop() {
      label.stop();
   }

   public boolean mouseDown(Event evt, int x, int y) {
      showStatus("I'd like my size to be " + 
		 minimumSize().width + "x" + minimumSize().height);
      return true;
   }
}
