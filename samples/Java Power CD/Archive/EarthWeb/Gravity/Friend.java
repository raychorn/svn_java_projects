/***********************************************************************
Copyright Notice: This source code is (C) Copyright 1995-96, EarthWeb LLC.,
All Rights Reserved. Distribution of this document or it's resulting
compiled code is granted for non-commercial use, with prior approval of
EarthWeb LLC. Distribution of this document or its resulting compiled
code, for commercial use, is granted only with prior written approval of
EarthWeb, LLC. For information, send email to info@earthweb.com.
***********************************************************************/

// $Id: Friend.java,v 1.3 1996/04/16 20:55:23 greg Exp $

import java.lang.Math;
import java.awt.*;

/*

Strangely enough, a Friend is really one of the enemies that are always
shooting at your ship.  See comments in 'Figure' for more details.

*/

public class Friend extends Figure
{
    static int fs = 8;
    static int shape[][][] =
        {{{0,fs},{fs,0}},{{fs,0},{-fs,0}},{{-fs,0},{0,fs}}};
    Friend() {
	super( shape );
    }

    // Create a Friend which is placed sitting on the given line.
    static Figure onEdge( int sx, int sy, int ex, int ey ) {
	Friend f = new Friend();
	double angle = Math.atan2( sy-ey, sx-ex );
	return f.transform( (sx+ex)/2, (sy+ey)/2, angle );
    }
}
