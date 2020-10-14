import java.awt.*;    
import java.awt.image.*;    
import controls.*;
/**
 *	Demonstrates BasicImageMixin, SliderMixin,  ShapesMixin
 * 	Implements an RGB color selection tool.
 *	(c) 1996 Inductive Solutions, Inc.
*/ 
public class ShowRGBPanel extends java.applet.Applet { 
	BasicImageMixin	rgbBi;
	SliderMixin	redS,greenS,blueS;
	ShapesMixin	shapes = new ShapesMixin();
	int		thisImagea;
	FadeFilter	ff;
	Image		myGrabbedFadeImage,myNewGrabbedFadeImage;
	Panel		redP,greenP,blueP,selectionP,combinedP; 
  	public void init() {

		selectionP = new Panel();
		selectionP.setLayout ( new GridLayout(3,2)); 
		redS = new SliderMixin(100, 20, 0, 255, Color.red, true);
		selectionP.add( new Label("RED RGB VALUE", Label.CENTER)); 
		selectionP.add(redS);
		
		greenS = new SliderMixin(100, 20, 0, 255, Color.green, true);
		selectionP.add( new Label("GREEN RGB VALUE", Label.CENTER)); 
		selectionP.add(greenS);

		blueS = new SliderMixin(100, 20, 0, 255, Color.blue, true);
		selectionP.add( new Label("BLUE RGB VALUE", Label.CENTER)); 
		selectionP.add(blueS);

		rgbBi = new BasicImageMixin(200,60);
		setLayout(new BorderLayout() );
		add ( "West",selectionP);
		add ( "Center",rgbBi);
		rgbBi.init();	
  	}  // end init
	
    	public void start() {
		rgbBi.start();	
 		shapes.pyramid(rgbBi.getDrawable(),0,0,200,60,5);
		Color nc = new Color(redS.getValue(),greenS.getValue(),blueS.getValue());
		rgbBi.getDrawable().setColor( nc );
		rgbBi.getDrawable().fillRect(5,5,190,50);
		nc = new Color(255-redS.getValue(),255-greenS.getValue(),255-blueS.getValue());
		rgbBi.getDrawable().setColor( nc );
		rgbBi.getDrawable().drawRect(5,5,190,50);
		rgbBi.getDrawable().drawString("Combined RGB Values",20,20);
    	}
	
        public boolean action(Event evt, Object arg) {
		if ( evt.target == redS ) {
			redS.setColor(new Color(((Integer)arg).intValue(),0,0) );
		} else if ( evt.target == greenS ) {
			greenS.setColor(new Color ( 0,((Integer)arg).intValue(),0 ) );
		} else if ( evt.target == blueS ) {
			blueS.setColor(new Color ( 0,0,((Integer)arg).intValue() ) );
		}
 		shapes.pyramid(rgbBi.getDrawable(),0,0,200,60,5);
		Color nc = new Color(redS.getValue(),greenS.getValue(),blueS.getValue());
		rgbBi.getDrawable().setColor( nc );
		rgbBi.getDrawable().fillRect(5,5,190,50);
		nc = new Color(255-redS.getValue(),255-greenS.getValue(),255-blueS.getValue());
		rgbBi.getDrawable().setColor( nc );
		rgbBi.getDrawable().drawRect(5,5,190,50);
		rgbBi.getDrawable().drawString("Combined RGB Values",20,20);
		return ( true );
	}

	public boolean mouseEnter(java.awt.Event evt, int x, int y) { 
		showStatus("(c) 1996 Inductive Solutions, Inc.");
		return (true);
    	}


}
