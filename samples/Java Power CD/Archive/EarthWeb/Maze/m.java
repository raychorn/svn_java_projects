/***********************************************************************
Copyright Notice: This source code is (C) Copyright 1995-96, EarthWeb LLC.,
All Rights Reserved. Distribution of this document or it's resulting
compiled code is granted for non-commercial use, with prior approval of
EarthWeb LLC. Distribution of this document or its resulting compiled
code, for commercial use, is granted only with prior written approval of
EarthWeb, LLC. For information, send email to info@earthweb.com.
***********************************************************************/

public class m
{
    static public void main( String args[] ) {
	int mx = Integer.parseInt(args[0]), my = Integer.parseInt(args[1]);
	float turn = Float.valueOf(args[2]).floatValue();
	float punt = Float.valueOf(args[3]).floatValue();
	Maze m = new Maze( mx, my, turn, punt );
	m.solve( null );
	m.asciiDraw( System.out );
    }
}
