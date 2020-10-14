/***********************************************************************
Copyright Notice: This source code is (C) Copyright 1995-96, EarthWeb LLC.,
All Rights Reserved. Distribution of this document or it's resulting
compiled code is granted for non-commercial use, with prior approval of
EarthWeb LLC. Distribution of this document or its resulting compiled
code, for commercial use, is granted only with prior written approval of
EarthWeb, LLC. For information, send email to info@earthweb.com.
***********************************************************************/

// $Id: Lineset.java,v 1.3 1996/04/16 21:00:52 greg Exp $

/*

This is a struct which describes a bunch of line segments.

*/

class Lineset
{
    int lines[];
    int n_lines;
    double points[];
    int n_points;
    public Lineset( int lines[], int n_lines, double points[], int n_points ) {
	this.lines = lines;
	this.n_lines = n_lines;
	this.points = points;
	this.n_points = n_points;
    }
}
