import java.awt.*;    
import controls.*;
/**
*	Two Animators at different points. 
 *	(c) 1996 Inductive Solutions, Inc.
 *
*/ 
public class ShowTwoAnimsLocus extends java.applet.Applet { 
	AnimatorMixin	a,b;
  	public void init() {
		setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
		a = new AnimatorMixin(200,180);
		b = new AnimatorMixin(100,200);

		String	s1 = new String ("earth1t.gif | earth2t.gif | earth3t.gif | earth4t.gif|");
		String  af = new String( s1 + " earth5t.gif|earth6t.gif|earth7t.gif|earth8t.gif") ;
	
		a.setAnimationFileNames(this,getDocumentBase(),"images",af,8);
		b.setAnimationFileNames(this,getDocumentBase(),"images",af,8);
		
		a.setAnimationRate(100);
		b.setAnimationRate(200);

		a.setAnimationFilePositions(this,"0@10 |0@20|0@30|0@40|0@50|0@60|0@70|0@80");
		b.setAnimationFilePositions(this,"1@0 |2@0|3@0|4@0|5@0|6@0|7@0|8@0");


		a.setAnimationScaleFactors(this,"100@100|100@100|100@100|100@100|100@100|100@100| 100@100|100@100");
		b.setAnimationScaleFactors(this,"10@10 | 20@20 | 30@30 | 40@40 | 50@50 | 40@40 | 30@30 | 20@20");

		a.setBackgroundColor(Color.black);
		b.setBackgroundColor(Color.gray);

		add(a);
		add(b);

		a.init();
		b.init();
  	}  // end init

    	public void start() {
		add(a);
		add(b);
		a.start();
		b.start();

		a.startTheEffect();
		b.startTheEffect();

    	}

	public boolean mouseDown(java.awt.Event evt, int x, int y) { 
		a.startTheEffect();
		b.startTheEffect();
		return (true);
    	}

	public boolean keyDown(java.awt.Event evt, int k) { 
		a.stopTheEffect();
		b.stopTheEffect();
		return (true);
	}

	public void stop() {
		remove(a);
		remove(b);
		a.stop();
		b.stop();
	}

	public boolean mouseEnter(java.awt.Event evt, int x, int y) { 
		showStatus("(c) 1996 Inductive Solutions, Inc.");
		return (true);
    	}
}
