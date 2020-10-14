/*
 * Permission to use, copy, modify and distribute this software and its
 * documentation without fee is hereby granted provided that:
 * a) this notice with a reference to the original source and 
 *    the author appears in all copies or derivatives of this software.
 * b) a reference to the original source and the author appears in all files
 *    generated using copies or derivatives of this software.
 *
 * THE AUTHOR MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THIS SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. THE AUTHOR SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY ANYBODY AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

import java.io.*;
import java.lang.*;

/**
 * doccnv - converts HTML-Dokuments generated using javadoc<br>
 * <p>
 * Parameters:<pre>
 * -in  afile   from javadoc
 * -out afile   to be generated
 * -api apath   to api-documentation, usually .../java/api/ or 
.../java/apidocs/
 * -i           replaces images with its alternatives
 * -u           unix-style output (lf only)
 *</pre><p>
 * Usage:<br>
 * If api is given, it prepends all references to java.* classes and all 
references to images.
 * Because the commandline must not contain '|', ':' is used instead and 
internally replaced, if it
 * is not part of the protocol. The flag i overrides the api parameter for 
images in such a
 * manner, that the text in the 'alt' parameter of the image is used instead 
of using the image
 * itself. in and out must be given, api and the flags are optional. If 
neither api or the
 * flag exist, out would be a copy of in. in must not equal out.<br>
 *
 * <p>Example: <br>
 * java doccnv -in doccnv.html -out doccnv.htm -api 
file://h:/www/java/apidocs/ -iu<br>
 * 
 * <p>V b1.00, 11/12/95: Creation by Thomas Wendt (thw)<br>
 * V b1.01, 11/18/95: Cleaning up the code by thw<br>
 *
 * <p><a 
href="http://www.uni-kassel.de/fb16/ipm/mt/java/javae.html"><b>origin</b></a> 
of doccnv.
 *
 * @author <a 
href="http://www.uni-kassel.de/fb16/ipm/mt/staff/thwendte.html">Thomas 
Wendt</a>
 * @version b1.01, 11/18/1995
 */
class doccnv {
  // Note: All needs to be static, because this class is never instantiated.

  /** The inputstream. */
  public static DataInputStream is = null;

  /** 
   * The outputstream.
   * Using <a href="java.io.PrintStream">PrintStream</a> would be nice,
   * but it does not allow handling of errors or setting EOL.
   */
  public static DataOutputStream os = null;

  /** EOL for the lines written. Default: cr+lf, if unix flag set: lf. */
  public static String crlf = "\r\n";
    
  /* The filenames */
  static String outputfile = "";
  static String inputfile = "";

  /**
   * Do the job.
   */
  public static void main(String args[]) {
    int i = 0, f, j, p;
    String s;
    boolean replImg = false;
    boolean replApi = false;
    String jPath = "";
    
    System.out.println("doccnv by Thomas Wendt");

    // while argument exist
    while (i < args.length && args[i].startsWith("-")) {
      s = args[i++];
      if (s.equals("-out")) {  // filename of output
        if (i < args.length)
          outputfile = args[i++];
        else
          System.err.println("-out requires a filename");
      } else if (s.equals("-in")) {  // filename of input
        if (i < args.length)
          inputfile = args[i++];
        else
          System.err.println("-in requires a filename");
      } else if (s.equals("-api")) {  // pathname or URL of api, with ending /
        if (i < args.length) {
          jPath = args[i++];
   // '|' cant be given at cmdline, so use and convert ':'
          if ((p = jPath.indexOf(':',6)) > -1) {
            jPath = jPath.substring(0,p)+'|'+jPath.substring(p+1);
          }
          replApi = true;
        } else
          System.err.println("-api requires a pathname or URL");
      } else {  // check options
        for (j = 1; j < s.length(); j++) {
          switch (s.charAt(j)) {
            case 'i':   // replace image with its alt text
              replImg = true;
              break;
            case 'u':   // use unix style output with lf only
              crlf = "\n";
              break;
            default:    // nothing matches: print err msg
              System.err.println("illegal option " + s.charAt(j));
              break;
          }
        }
      }
    }   

    // Quit, if param missing or wrong
    if (inputfile.equals("") || outputfile.equals("") || 
inputfile.equals(outputfile)) {
      quit("Usage: java doccnv [-iu] [-api aURL] -out afile -in afile, out != 
in");
      return;
    } else if (!(replApi || replImg)) {
      quit("Nothing to do!");
      return;
    }

    // Open streams and exit, if error
    try { is = new DataInputStream(new FileInputStream(inputfile)); }
    catch (java.io.IOException e) { quit("Error accessing "+inputfile); }
    try { os = new DataOutputStream(new FileOutputStream(outputfile)); }
    catch (java.io.IOException e) { quit("Error creating "+outputfile); }

    // Init some constants
    String d = ""+'"';  // silly construction, isn't it? (no constructor for 
single char)
    String im = "<img ";
    String src = " src="+d;
    String ja = " href="+d+"java.";
    String alt = " alt="+d;
    String bo = "</body>";
    boolean ok;

    // while not eof
    s = readln(); 
    while (s != null) {

      // Replace or extend img tags. Use while, because multiple img tags may 
exist.
      f = -1;
      while ((f = s.indexOf(im,f+1)) > -1) {
        if (replImg) {
          p = s.indexOf(d,s.indexOf(alt,f))+1;
          s = s.substring(p,s.indexOf(d,p));
        } else if (replApi) {
          p = s.indexOf(d, s.indexOf(src,f))+1;
          s = s.substring(0,p)+jPath+s.substring(p);
        }
      }

      // Extend refs to the API. Use while, because multiple refs may exist.
      f = -1;
      while (replApi && (f = s.indexOf(ja,f+1)) > -1) {
        p = s.indexOf(d,f)+1;
        s = s.substring(0,p)+jPath+s.substring(p);
      }

      // Last line? Then output some info about the program.
      if (s.indexOf(bo) > -1) {
        String fi = "<a href="+d+"http://www.uni-kassel.de/fb16/ipm/mt/";
        String la = ".html"+d+">";
        println("<p><hr><p>Converted using "+fi+"java/javae"+la+"doccnv</a> 
by");
        println(fi+"staff/thwendte"+la+"Thomas Wendt</a>");
      }

      // Output the converted line and read in the next one.
      println(s);
      s = readln();
    }

    // Flush and/or close the streams.
    try {
      os.flush();
      os.close();
    }
    catch (java.io.IOException e) { quit("Error flushing/closing 
"+outputfile); }
    try { is.close(); }
    catch (java.io.IOException e) { }
  }

  /**
   * Write a line to outputstream with error handling
   * Exit, if error occurs.
   * @see #os
   * @param s the line to write
   */
  public static void println(String s) {
    try { os.writeBytes(s+crlf); }
    catch (java.io.IOException e) { quit("Error writing "+outputfile); }
  }

  /**
   * Read a line from inputstream with error handling
   * @see #is
   * @return The line read or null, if eof or error.
   */
  public static String readln() {
    String s;
    try { s = is.readLine(); }
    catch (java.io.IOException e) { return null; }
    return s;
  }

  /**
   * Exit
   * Print an error message, if one is given.
   * @param s The text describing the Error. If null, exitcode = 0; 1 
otherwise.
   */
  public static void quit(String s) {
    Runtime r = Runtime.getRuntime();
    if (s != null) {
      System.out.println();
      System.err.println(s);
      r.exit(1);
    } else r.exit(0);
  }

}
----------
X-Sun-Data-Type: default-app
X-Sun-Data-Description: default
X-Sun-Data-Name: form.java
X-Sun-Charset: us-ascii
X-Sun-Content-Lines: 184
