/***********************************************************************
Copyright Notice: This source code is (C) Copyright 1995-96, EarthWeb LLC.,
All Rights Reserved. Distribution of this document or it's resulting
compiled code is granted for non-commercial use, with prior approval of
EarthWeb LLC. Distribution of this document or its resulting compiled
code, for commercial use, is granted only with prior written approval of
EarthWeb, LLC. For information, send email to info@earthweb.com.
***********************************************************************/

// $Id: Transform.java,v 1.6 1996/04/16 21:00:52 greg Exp $

import java.lang.Math;

/*

This class represents a transformation matrix.

[a b c][x]
[d e f][y]
[g h i][z]
bx =
[a]
[d]
[g]

X right
Y down
Z in

*/

class Transform
{
    // The basis of the transformed space.
    Vec bx, by, bz;

    // The origin of the transformed space.
    Vec translate;

    static public double pi=3.141592653589793f;

    // I don't like typing Math.cos().
    public static double cos( double a ) { return Math.cos( a ); }
    public static double sin( double a ) { return Math.sin( a ); }

    // Make a transform from the individual values.
    public Transform( double mxx, double myx, double mzx,
		      double mxy, double myy, double mzy,
		      double mxz, double myz, double mzz ) {
	bx = new Vec(); by = new Vec(); bz = new Vec();
	bx.x = mxx; by.x = myx; bz.x = mzx;
	bx.y = mxy; by.y = myy; bz.y = mzy;
	bx.z = mxz; by.z = myz; bz.z = mzz;
	translate = new Vec( 0, 0, 0 );
    }

    // Generate a transform from a basis.
    public Transform( Vec x, Vec y, Vec z ) {
	bx = new Vec( x );
	by = new Vec( y );
	bz = new Vec( z );
	translate = new Vec( 0, 0, 0 );
    }

    // Duplicate a Transform.
    public Transform( Transform t ) {
	bx = new Vec( t.bx );
	by = new Vec( t.by );
	bz = new Vec( t.bz );
	translate = new Vec( t.translate );
    }

    // Generate a transform that only translates.
    static Transform translateTransform( Vec translate ) {
	Transform t = new Transform( identity );
	t.translate = new Vec( translate );
	return t;
    }

    // The identity Transform.
    public static Transform identity = new Transform( 1, 0, 0,
						      0, 1, 0,
						      0, 0, 1 );

    // A Transform which rotates around the X axis.
    public static Transform rotx( double ang ) {
	double c = cos( ang ), s = sin( ang );
	return new Transform( 1, 0, 0,
			      0, c,-s,
			      0, s, c );
    }

    // A Transform which rotates around the Y axis.
    public static Transform roty( double ang ) {
	double c = cos( ang ), s = sin( ang );
	return new Transform( c, 0, s,
			      0, 1, 0,
			     -s, 0, c );
    }

    // A Transform which rotates around the Z axis.
    public static Transform rotz( double ang ) {
	double c = cos( ang ), s = sin( ang );
	return new Transform( c,-s, 0,
			      s, c, 0,
			      0, 0, 1 );
    }

    // Transpose a Transform -- exchange the X and Y
    // axes of the matrix.
    public Transform transposed() {
	Transform t = new Transform( bx.x, bx.y, bx.z,
				    by.x, by.y, by.z,
				    bz.x, bz.y, bz.z );
	t.translate = translate;
	return t;
    }

    // Multiply a vector only by the main part, not
    // the translation part.
    Vec mul_only_matrix( Vec v ) {
	double x, y, z;
	x = bx.x * v.x + by.x * v.y + bz.x * v.z;
	y = bx.y * v.x + by.y * v.y + bz.y * v.z;
	z = bx.z * v.x + by.z * v.y + bz.z * v.z;
	return new Vec( x, y, z );
    }

    // Same function, different form for args.
    void mul_only_matrix( double d[], double s[] ) {
	d[0] = bx.x * s[0] + by.x * s[1] + bz.x * s[2];
	d[1] = bx.y * s[0] + by.y * s[1] + bz.y * s[2];
	d[2] = bx.z * s[0] + by.z * s[1] + bz.z * s[2];
    }

    // Multiply a vector by this matrix.
    public Vec mul( Vec v ) {
	return translate.add( mul_only_matrix( v ) );
    }

    // Same function, different from.
    public void mul( double d[][], double s[][], int n ) {
	double t[] = new double[3];
	for (int i=0; i<n; ++i) {
	    mul_only_matrix( t, s[i] );
	    translate.add( d[i], t );
	}
    }

    // Concatenate (multiply) two transforms.
    public Transform mul( Transform t ) {
	Transform ans = new Transform( t.mul_only_matrix( bx ),
				       t.mul_only_matrix( by ),
				       t.mul_only_matrix( bz ) );
	ans.translate = t.translate.add( t.mul_only_matrix( translate ) );
	return ans;
    }

    public String toString() {
	Transform tr = transposed();
	return tr.bx.toString() + "\n"
	     + tr.by.toString() + "\n"
	     + tr.bz.toString() + "\n"
	     + tr.translate.toString() + "\n";
    }
}
