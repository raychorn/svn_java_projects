/* UI applet to convert RGB to HSB, and vice versa */

import java.awt.*;

public class ColorTest extends java.applet.Applet {
  ColorControls RGBcontrols, HSBcontrols;
  Canvas swatch;

  public void init() {
    Color theColor = new Color(0,0,0);
    float[] HSB = Color.RGBtoHSB(theColor.getRed(),theColor.getGreen(),
			       theColor.getBlue(), (new float[3]));
   
    setLayout(new GridLayout(1,3,10,10));
    
    // The color swatch
    swatch = new Canvas();
    swatch.setBackground(theColor);
    
    // the control panels
    RGBcontrols = new ColorControls(this,
	  "Red", "Green", "Blue", 
           theColor.getRed(), theColor.getGreen(),theColor.getBlue());

    HSBcontrols = new ColorControls(this,
	  "Hue", "Saturation", "Brightness", 
	   (int)(HSB[0] * 360), (int)(HSB[1] * 100), (int)(HSB[2] * 100));
	  
    add(swatch);
    add(RGBcontrols);
    add(HSBcontrols);

  }

  public Insets insets() {
    return new Insets(10,10,10,10);
  }

  void update(ColorControls in) {
    Color c;
    String v1 = in.f1.getText();
    String v2 = in.f2.getText();
    String v3 = in.f3.getText();

    if (in == RGBcontrols) {  // change to RGB
      c = new Color(Integer.parseInt(v1),Integer.parseInt(v2),
		    Integer.parseInt(v3));
      swatch.setBackground(c);

      float[] HSB = Color.RGBtoHSB(c.getRed(),c.getGreen(),
			       c.getBlue(), (new float[3]));
      HSB[0] *= 360;
      HSB[1] *= 100;
      HSB[2] *= 100;
      HSBcontrols.f1.setText(String.valueOf((int)HSB[0]));
      HSBcontrols.f2.setText(String.valueOf((int)HSB[1]));
      HSBcontrols.f3.setText(String.valueOf((int)HSB[2]));
    }
    else {  // change to HSB
      int f1 = Integer.parseInt(v1);
      int f2 = Integer.parseInt(v2);
      int f3 = Integer.parseInt(v3);
      c = Color.getHSBColor((float)f1 / 360, (float)f2 / 100, (float)f3/100);
      swatch.setBackground(c);    
      RGBcontrols.f1.setText(String.valueOf(c.getRed()));
      RGBcontrols.f2.setText(String.valueOf(c.getGreen()));
      RGBcontrols.f3.setText(String.valueOf(c.getBlue()));

    }
  }
}

class ColorControls extends Panel {
  TextField f1, f2, f3;
  ColorTest outerparent;

  ColorControls(ColorTest target, String l1, String l2, String l3,
		int v1, int v2, int v3) {

    this.outerparent = target;
    setLayout(new GridLayout(3,2,10,10));
    
    f1 = new TextField(String.valueOf(v1),10);
    f2 = new TextField(String.valueOf(v2),10);    
    f3 = new TextField(String.valueOf(v3),10);    

    add(new Label(l1, Label.RIGHT));
    add(f1);
    add(new Label(l2, Label.RIGHT));
    add(f2);
    add(new Label(l3, Label.RIGHT));
    add(f3);

  }
  public Insets insets() {
    return new Insets(10,10,0,0);
  }

  public boolean action(Event evt, Object arg) {
    if (evt.target instanceof TextField) {
      this.outerparent.update(this);
      return true;
    }
    else return false;
  }
}


