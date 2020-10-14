/***********************************************************************
Copyright Notice: This source code is (C) Copyright 1995-96, EarthWeb LLC.,
All Rights Reserved. Distribution of this document or it's resulting
compiled code is granted for non-commercial use, with prior approval of
EarthWeb LLC. Distribution of this document or its resulting compiled
code, for commercial use, is granted only with prior written approval of
EarthWeb, LLC. For information, send email to info@earthweb.com.
***********************************************************************/

// $Id: Ship.java,v 1.3 1996/04/16 20:55:23 greg Exp $

import java.lang.*;
import java.awt.*;
import java.util.*;

public class Ship extends Figure
{
    // A scaling factor for the ship shape.
    public static int size = 8;

    // The transformed ship
    public Figure transshape;

    // The shape of the exploded ship.
    static int bamsz = 10;
    static public int bamshape[][][] =
      {{{0,0},{bamsz,0}},{{0,0},{bamsz,bamsz}},
       {{0,0},{0,bamsz}}, {{0,0},{-bamsz,bamsz}},
       {{0,0},{-bamsz,0}},{{0,0},{-bamsz,-bamsz}},
       {{0,0},{0,-bamsz}},{{0,0},{bamsz,-bamsz}}};
    static Figure bam;
    static {
	bam = new Figure( bamshape );
    }

    // The untransformed shape of the ship.
    static public int shipshape[][][] =
        {{{size,0},{-size,size}},{{-size,size},{-size,-size}},
				  {{-size,-size},{size,0}}};

    // The untransformed shape of the afterburner.
    static public int afterburner[][][] = {{{-size,(size*3)/4},{-size*2,0}},
					   {{-size,-(size*3)/4},{-size*2,0}}};

    Ship() {
	super( shipshape );
    }

    // Render the ship; render the afterburner and shield if they're on,
    // and render the explosion instead if the ship has exploded.
    public void render( int ox, int oy, double angle, Graphics g,
		       boolean engine, boolean exploded, boolean shield ) {
	if (exploded) {
	    transshape = bam.transform( ox, oy, angle );
	    transshape.render( g );
	} else {
	    g.setColor( Color.green );
	    transshape = transform( ox, oy, angle );
	    transshape.render( g );
	    if (engine) {
		g.setColor( Color.yellow );
		(new Figure( afterburner )).transform( ox, oy, angle )
                                           .render( g );
	    }
	    if (shield) {
		g.setColor( Color.green );
		g.drawOval( ox-(size*4)/2, oy-(size*4)/2, size*4, size*4 );
	    }
	}
    }

    // Intersection is with the transformed shape.
    public boolean intersectLines( int b[][] ) {
	if (transshape != null)
	  return transshape.intersectLines( b );
	else return false;
    }
}
