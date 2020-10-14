/***********************************************************************
Copyright Notice: This source code is (C) Copyright 1995-96, EarthWeb LLC.,
All Rights Reserved. Distribution of this document or it's resulting
compiled code is granted for non-commercial use, with prior approval of
EarthWeb LLC. Distribution of this document or its resulting compiled
code, for commercial use, is granted only with prior written approval of
EarthWeb, LLC. For information, send email to info@earthweb.com.
***********************************************************************/

// $Id: Planet.java,v 1.3 1996/04/16 20:55:23 greg Exp $

import java.awt.*;
import java.util.*;
import java.io.*;

public class Planet extends Figure
{
    Color bloo = new Color( 0, 128, 255 );
    Random rand;
    Figure friends[];
    int n_friends;
    TakesABullet tab = null;

    // For some reason a square is added to the world.
    Planet( TakesABullet tab, int lines[][][] ) {
	super( lines );
	rand = new Random();
	this.tab = tab;
	friends = new Figure[4];
	n_friends = 0;
	for (int i=0; i<n_lines; ++i)
	  addFriend( lines[i][0][0], lines[i][0][1],
		    lines[i][1][0], lines[i][1][1] );
    }

    // When resetting, remove all friends.
    public void reset() {
	n_friends = 0;
	super.reset();
    }

    // Add the given Figure as a Friend
    public void addFriend( Figure f ) {
	if (n_friends >= friends.length) {
	    Figure newFriends[] = new Figure[friends.length*2];
	    for (int i=0; i<friends.length; ++i)
	      newFriends[i] = friends[i];
	    friends = newFriends;
	}
	friends[n_friends++] = f;
    }

    // Add a Friend on the given line.
    public void addFriend( int sx, int sy, int ex, int ey ) {
	Figure f = Friend.onEdge( sx, sy, ex, ey );
	addFriend( f );
    }

    // Add a friend on the line indexed by the given number.
    public void addFriend( int which ) {
	addFriend( lines[which][0][0], lines[which][0][1],
		  lines[which][1][0], lines[which][1][1] );
    }

    // Remove the friend on the line indexed by the given number.
    public void removeFriend( int which ) {
	friends[which]=friends[--n_friends];
    }

    // Remove the friend on the line that is nearest (x,y).
    public void removeNearbyFriend( int x, int y ) {
	for (int i=0; i<n_friends; ++i) {
	    int dx = x - friends[i].lines[0][0][0];
	    int dy = y - friends[i].lines[0][0][1];
	    double d = Math.sqrt(dx*dx+dy*dy);
	    if (d < 10.0)
	      removeFriend( i );
	}
    }

    // Draw the world and its Friends.
    public void render( Graphics g ) {
	g.setColor( bloo );
	super.render( g );
	g.setColor( Color.red );
	renderFriends( g );
    }

    // Render all Friends
    public void renderFriends( Graphics g ) {
	for (int i=0; i<n_friends; ++i)
	  friends[i].render( g );
    }

    // The Friends sometimes fire bullets at the ship, whose position is
    // passed in.
    void fireBulletsMaybe( double num_buls, double shipx, double shipy ) {
	if (n_friends == 0) return;
	if (rand.nextDouble() < num_buls) {
	    int r = Math.abs( rand.nextInt() ) % n_friends;
	    Figure f = friends[r];
	    double v = Gravity.bul_vel/4;
	    int mx = f.lines[0][0][0];
	    int my = f.lines[0][0][1];
	    double dispx = mx-(f.lines[1][0][0]+f.lines[2][0][0])/2;
	    double dispy = my-(f.lines[1][0][1]+f.lines[2][0][1])/2;
	    double dx = shipx-mx;
	    double dy = shipy-my;
	    if (dx*dispx+dy*dispy < 0)
	      return;
	    mx += dispx/3;
	    my += dispy/3;
	    double dist = Math.sqrt( dx*dx + dy*dy );
	    tab.take( mx, my, v*dx/dist, v*dy/dist );
	}
    }

    // Intersect each of the friends with the given segment.
    public boolean intersectFriends( int b[][] ) {
	for (int i=0; i<n_friends; ++i)
	  if (friends[i].intersectLines( b )) {
	      removeFriend( i );
	      return true;
	  }
	return false;
    }

    // Read in the Friend positions as well.
    public void read( DataInputStream in ) throws IOException {
	super.read( in );
	int n = in.readInt();
	for (int i=0; i<n; ++i) {
	    Figure f = new Figure();
	    f.read( in );
	    addFriend( f );
	}
    }

    // Write out the Friend positions as well.
    public void write( DataOutputStream out ) throws IOException {
	super.write( out );
	out.writeInt( n_friends );
	for (int i=0; i<n_friends; ++i) {
	    friends[i].write( out );
	}
    }
}
