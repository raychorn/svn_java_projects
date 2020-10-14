/*-
 * Copyright (c) 1995 by Georg Hessmann.
 * All Right Reserved.
 *
 * Step.java	1.0   25 Aug 1995
 *		1.1   13 Sep 1995
 *
 */


/**
 * Step stores the information of one step.
 *
 * @see Figur
 * @see Floor
 *
 * @version 1.1, 13 Sep 1995
 * @author Georg He&szlig;mann
 */

public class Step {
  float		time_step;	// wie lange benoetigt dieser Schritt (an Schlaegen)
  boolean	is_left;
  boolean	is_dotted;
  int		deg;
  float		x;
  float 	y;
  String        takt_str;	// slow/quick
  int           ht_id;		// heel/toes (def. in Figur.java)


  /**
   * Stores one step with time and heel/toe string.
   * @param t how much time is to wait until the next step can be set
   * @param l is this a left foot
   * @param dot is this a dotted foot
   * @param xp x-coordinate relative to the dance-floor grid
   * @param yp y-coordinate relative to the dance-floor grid
   * @param TStr time string (e.g. slow/quick)
   * @param HTid heel/toe id number (defined in Figur.java)
   */
  public Step(float t, boolean l, boolean dot, int d, float xp, float yp,
	      String Tstr, int HTid)
  {
    time_step = t;
    is_left   = l;
    is_dotted = dot;
    deg       = d;
    x         = xp;
    y         = yp;
    takt_str  = Tstr;
    ht_id     = HTid;
  }


  /**
   * Stores one step with time string.
   * @param t how much time is to wait until the next step can be set
   * @param l is this a left foot
   * @param dot is this a dotted foot
   * @param xp x-coordinate relative to the dance-floor grid
   * @param yp y-coordinate relative to the dance-floor grid
   * @param TStr time string (e.g. slow/quick)
   */
  public Step(float t, boolean l, boolean dot, int d, float xp, float yp, String Tstr)
  {
    time_step = t;
    is_left   = l;
    is_dotted = dot;
    deg       = d;
    x         = xp;
    y         = yp;
    takt_str  = Tstr;
    ht_id     = Figur.HT_n;
  }

  /**
   * Stores one step without time string.
   * @param t how much time is to wait until the next step can be set
   * @param l is this a left foot
   * @param dot is this a dotted foot
   * @param xp x-coordinate relative to the dance-floor grid
   * @param yp y-coordinate relative to the dance-floor grid
   */
  public Step(float t, boolean l, boolean dot, int d, float xp, float yp)
  {
    time_step = t;
    is_left   = l;
    is_dotted = dot;
    deg       = d;
    x         = xp;
    y         = yp;
    takt_str  = null;
    ht_id     = Figur.HT_n;
  }
}

