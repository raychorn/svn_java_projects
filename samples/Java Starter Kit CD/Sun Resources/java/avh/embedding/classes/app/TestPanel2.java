package app;

/**
 * An sample applet panel.
 *
 * @author Arthur van Hoff
 */

public
class TestPanel2 extends AppletPanel {
    AppSlider slider;
    
    public void init() {
	panel = new AppPanel(this);
	panel.reshape(0, 0, width, height);
	panel.add(new AppButton(this, "10"), 5, 5, 50, 20);
	panel.add(new AppButton(this, "20"), 60, 5, 50, 20);
	panel.add(new AppButton(this, "30"), 115, 5, 50, 20);
	panel.add(slider = new AppSlider(this, 0, 100), 5, 30, 160, 20);
	slider.setValue(40);
    }
    public void action(AppButton but) {
	slider.setValue(Integer.valueOf(but.label).intValue());
    }
}
