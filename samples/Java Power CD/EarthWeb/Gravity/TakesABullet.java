/***********************************************************************
Copyright Notice: This source code is (C) Copyright 1995-96, EarthWeb LLC.,
All Rights Reserved. Distribution of this document or it's resulting
compiled code is granted for non-commercial use, with prior approval of
EarthWeb LLC. Distribution of this document or its resulting compiled
code, for commercial use, is granted only with prior written approval of
EarthWeb, LLC. For information, send email to info@earthweb.com.
***********************************************************************/

// $Id: TakesABullet.java,v 1.3 1996/04/16 20:55:23 greg Exp $

/*

Interface for an object which is notified that a bullet has been
fired.

*/

public interface TakesABullet
{
    public void take( double x, double y, double vx, double vy );
}
