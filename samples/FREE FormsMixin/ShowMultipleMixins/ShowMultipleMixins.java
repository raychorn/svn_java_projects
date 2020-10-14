import controls.*;
import java.awt.*;    
/**
 *	This applet demonstrates the use of AllMixins objects on a single background
  *	(c) 1996 Inductive Solutions, Inc.
*/ 

public class ShowMultipleMixins extends java.applet.Applet { 
	AllMixins	all;
	MixinData	a,b,c,d,e;
        String  af = new String ("earth1t.gif|earth2t.gif|earth3t.gif|earth4t.gif|earth5t.gif|earth6t.gif|earth7t.gif|earth8t.gif");
  	public void init() {
		all = new AllMixins(640,480);
                all.setBackgroundFileName(this,getDocumentBase(),"images/nasa71.gif");

		a = all.createAnimator(640,480);
                a.setAnimationFileNames(this,getDocumentBase(),"images",af,8);
                a.setAnimationFilePositions(this,"100@0|100@10|100@20|100@30|100@40|100@50|100@70|100@80");

		b = all.createAnimator(640,480);
                b.setAnimationFileNames(this,getDocumentBase(),"images",af,8);
                b.setAnimationFilePositions(this,"200@0|200@10|200@20|200@30|200@40|200@50|200@70|200@80");

		c = all.createAnimator(640,480);
                c.setAnimationFileNames(this,getDocumentBase(),"images",af,8);
                c.setAnimationFilePositions(this,"300@0|300@10|300@20|300@30|300@40|300@50|300@70|300@80");

		d = all.createAnimator(640,480);
                d.setAnimationFileNames(this,getDocumentBase(),"images",af,8);
                d.setAnimationFilePositions(this,"400@0|400@10|400@20|400@30|400@40|400@50|400@70|400@80");
                d.setAnimationScaleFactors(this,"10@10|20@20|40@40|50@50|100@50|50@100|100@100|0@0");

		e = all.createBlinker(640,480);
                e.setBlinkerFileName(this,getDocumentBase(),"images/earth1t.gif");

		add ( all );
  	}  // end init
	
    	public void start() {
		all.start();
    	}

	public boolean mouseDown(java.awt.Event evt, int x, int y) { 
		a.startTheEffect();
		b.startTheEffect();
		c.startTheEffect();
		d.startTheEffect();
		e.startTheEffect();
		return (true);
    	}
	
	public boolean keyDown(java.awt.Event evt, int k) { 
		a.stopTheEffect();
		b.stopTheEffect();
		c.stopTheEffect();
		d.stopTheEffect();
		e.stopTheEffect();
		return (true);
	}

	public void stop() {
		all.stop();
	}

	public boolean mouseEnter(java.awt.Event evt, int x, int y) { 
		showStatus("(c) 1996 Inductive Solutions, Inc.");
		return (true);
    	}

}
