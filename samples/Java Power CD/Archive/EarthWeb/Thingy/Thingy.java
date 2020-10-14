/***********************************************************************
Copyright Notice: This source code is (C) Copyright 1995-96, EarthWeb LLC.,
All Rights Reserved. Distribution of this document or it's resulting
compiled code is granted for non-commercial use, with prior approval of
EarthWeb LLC. Distribution of this document or its resulting compiled
code, for commercial use, is granted only with prior written approval of
EarthWeb, LLC. For information, send email to info@earthweb.com.
***********************************************************************/

// $Id: Thingy.java,v 1.2 1996/04/16 21:04:45 greg Exp $

import java.applet.*;
import java.awt.*;

public class Thingy extends java.applet.Applet {
    int mx, my;

    public void init() {
        mx = my = 50;
    }

    public void paint(Graphics g) {
        for (int i=0; i<size().width-1; i+=8) {
            g.drawLine( mx, my, i, 0 );
            g.drawLine( mx, my, i, size().width-1 );
        }
        for (int i=0; i<size().height-1; i+=8) {
            g.drawLine( mx, my, 0, i );
            g.drawLine( mx, my, size().width-1, i );
        }
        g.drawRect(0, 0, size().width - 1, size().height - 1);
    }

    public void Action( int x, int y ) {
        mx = x; my = y;
        repaint();
    }

    public boolean mouseDown(java.awt.Event evt, int x, int y) {
        Action( x, y );
        return true;
    }

    public boolean mouseDrag(java.awt.Event evt, int x, int y) {
        Action( x, y );
        return true;
    }
}
