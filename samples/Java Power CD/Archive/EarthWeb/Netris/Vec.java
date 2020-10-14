/***********************************************************************
Copyright Notice: This source code is (C) Copyright 1995-96, EarthWeb LLC.,
All Rights Reserved. Distribution of this document or it's resulting
compiled code is granted for non-commercial use, with prior approval of
EarthWeb LLC. Distribution of this document or its resulting compiled
code, for commercial use, is granted only with prior written approval of
EarthWeb, LLC. For information, send email to info@earthweb.com.
***********************************************************************/

// $Id: Vec.java,v 1.5 1996/04/16 21:00:52 greg Exp $

import java.lang.Math;

/*

Vec represents a point in 3-space, or a Vector in 3-space.

*/

class Vec
{
    // The coordinates.
    public double x, y, z;

    public Vec( double x, double y, double z ) {
	this.x = x; this.y = y; this.z = z;
    }

    public Vec( int x, int y, int z ) {
	this.x = (double)x;
	this.y = (double)y;
	this.z = (double)z;
    }

    public Vec( int x[] ) {
	this( x[0], x[1], x[2] );
    }

    public Vec( double x[] ) {
	this( x[0], x[1], x[2] );
    }

    public Vec( Vec v ) {
	x = v.x; y = v.y; z = v.z;
    }

    public Vec() {}

    // Flip point through the origin.
    public Vec neg() {
	return new Vec( -x, -y, -z );
    }

    // Add like vectors.
    public Vec add( Vec v ) {
	return new Vec( x+v.x, y+v.y, z+v.z );
    }

    // Subtract like vectors.
    public Vec sub( Vec v ) {
	return new Vec( x-v.x, y-v.y, z-v.z );
    }

    // Add like vectors.
    public void add( double d[], double s[] ) {
	d[0] = s[0] + x;
	d[1] = s[1] + y;
	d[2] = s[2] + z;
    }

    // Dot product.
    public double dot( Vec v ) {
	return x*v.x + y*v.y + z*v.z;
    }

    public String toString() {
	return "["+x+" "+y+" "+z+"]";
    }

    // Length of vector, or dist from origin.
    public double len() {
	return Math.sqrt( x*x+y*y+z*z );
    }

    // Scale like vector.
    public Vec scale( double s ) {
	return new Vec( x*s, y*s, z*s );
    }

    // Normalize like vector.
    public Vec norm() {
	return scale( 1/len() );
    }

    // Vector cross product.
    public Vec cross( Vec v ) {
	return new Vec( y*v.z-z*v.y,
		       z*v.x-x*v.z,
		       x*v.y-y*v.x );
    }
}
