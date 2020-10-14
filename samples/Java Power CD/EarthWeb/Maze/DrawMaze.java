/***********************************************************************
Copyright Notice: This source code is (C) Copyright 1995-96, EarthWeb LLC.,
All Rights Reserved. Distribution of this document or it's resulting
compiled code is granted for non-commercial use, with prior approval of
EarthWeb LLC. Distribution of this document or its resulting compiled
code, for commercial use, is granted only with prior written approval of
EarthWeb, LLC. For information, send email to info@earthweb.com.
***********************************************************************/

import java.applet.*;
import java.awt.*;

/*<applet codebase= "." code=DrawMaze.class width=200 height=200>
<param name=gap value=10>
<param name=turn value=0.01>
<param name=punt value=0.05>
<param name=autostart value=1>
</applet>*/

class VisibleSolver extends Thread implements Callback
{
    Maze m;
    DrawMaze a;

    VisibleSolver( Maze m, DrawMaze a ) {
	this.m = m;
	this.a = a;
    }
    public void run() {
	m.solve( this );
	a.done();
	stop();
    }
    public void callback() {
	a.repaint();
	try {
	    sleep( 20 );
	} catch( Exception e ) { System.out.println( e.toString() ); }
    }
}

public class DrawMaze extends Applet
{
    Maze m = null;
    int gap=10;
    float turn=0.3f, punt=0.3f;
    Image offscreenImage = null;
    Graphics offscreen = null;
    public boolean youWin = false;
    int tx=0, ty=0;
    int state;
    Thread solver = null;

    void build() {
	m = new Maze( (size().width-1)/gap, (size().height-1)/gap, turn, punt );
	tx = ((size().width-1)%gap)/2;
	ty = ((size().height-1)%gap)/2;
	System.out.println( "AHA "+ tx+" "+ty );
    }

    public void init () {
	try {
	    offscreenImage = createImage( size().width, size().height );
	    offscreen = offscreenImage.getGraphics();
	    offscreen.setColor( Color.black );
	} catch( Exception e ) {
	    offscreen = null;
	    offscreenImage = null;
	}
	if (getParameter( "gap" ) != null)
	  gap = Integer.parseInt(getParameter( "gap" ));
	gap = gap==0?10:gap;
	if (getParameter( "turn" ) != null)
	  turn = Float.valueOf(getParameter( "turn" )).floatValue();
	if (getParameter( "punt" ) != null)
	  punt = Float.valueOf(getParameter( "punt" )).floatValue();
	System.out.println( "" + turn + " " + punt + " " );
	build();
	render( offscreen );
	if (getParameter( "autostart" ) != null)
	  click();
    }

    public void update(Graphics g) {
	paint(g);
    }

    public void render( Graphics g ) {
	g.setColor( Color.red );
	for (int y=0; y<m.sy; ++y) {
	    for (int x=0; x<m.sx; ++x) {

		if (m.getWall( x, y, 2 ))
		  g.drawLine( x*gap, y*gap, x*gap, (y+1)*gap );
		if (m.getWall( x, y, 3 ))
		  g.drawLine( x*gap, y*gap, (x+1)*gap, y*gap );
	    }
	    if (m.getWall( m.sx-1, y, 0 ))
	      g.drawLine( m.sx*gap, y*gap, m.sx*gap, (y+1)*gap );
	}
	for (int x=0; x<m.sx; ++x)
	  if (m.getWall( x, m.sy-1, 1 ))
	    g.drawLine( x*gap, m.sy*gap, (x+1)*gap, m.sy*gap );
	if (m.stackp > 0) {
	    g.setColor( Color.blue );
	    int lx = m.stackx[0], ly = m.stacky[0];
	    g.fillOval( lx*gap, ly*gap, gap, gap );
	    for (int i=1; i<m.stackp; ++i) {
		g.drawLine( lx*gap+gap/2, ly*gap+gap/2, m.stackx[i]*gap+gap/2, m.stacky[i]*gap+gap/2 );
		lx = m.stackx[i];
		ly = m.stacky[i];
	    }
	    g.fillOval( lx*gap, ly*gap, gap, gap );
	    if (youWin) {
		g.setColor( Color.black );
		centerText( g, "I Win!" );
	    }
	}
    }

    Font font=null;
    public void centerText( Graphics g, String s ) {
	if (font==null) font = new Font( "Times", Font.BOLD, 24 );
	g.setFont( font );
	FontMetrics fm = g.getFontMetrics();
	int left = (size().width - fm.stringWidth( s ))/2;
	int top = (size().height - fm.getHeight())/2;
	g.drawString( s, left, top );
    }

    public void paint( Graphics g ) {
	Graphics draw = ((offscreen == null) ? g : offscreen);
	draw.clearRect( 0, 0, size().width, size().height );
	draw.translate( tx, ty );
	render( draw );
	if (offscreen != null) {
	    g.drawImage( offscreenImage, 0, 0, this );
	    draw.translate( -tx, -ty );
	}
    }

    public void done() {
	youWin = true;
	repaint();
    }

    public boolean mouseDown( Event evt, int x, int y ) {
	click();
	return true;
    }

    void click() {
	if (solver != null) {
	    solver.stop();
	    solver = null;
	    youWin = false;
	}
	if ((state++&1)==0) {
	    solver = new VisibleSolver( m, this );
	    solver.start();
	}
	else {
	    build();
	    repaint();
	}
    }
}
