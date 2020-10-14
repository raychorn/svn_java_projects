import java.lang.*;

/*
** StringKludge
**              A quick hack to convert float and double to
** a string to overcome a Major deficiency in Netscape 2.0b1
**************************************************************************
**    Copyright (C) 1995 Leigh Brookshaw
**
**    This program is free software; you can redistribute it and/or modify
**    it under the terms of the GNU General Public License as published by
**    the Free Software Foundation; either version 2 of the License, or
**    (at your option) any later version.
**
**    This program is distributed in the hope that it will be useful,
**    but WITHOUT ANY WARRANTY; without even the implied warranty of
**    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
**    GNU General Public License for more details.
**
**    You should have received a copy of the GNU General Public License
**    along with this program; if not, write to the Free Software
**    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
**************************************************************************
*/


class StringKludge extends Object {

/*
**   Maximum number of digits allowed in the ordinal before we go to
**   scientific notation
*/
     public int MaxOrdinal = 4;
/*
**   Number of digits in the fraction before we round up.
*/
     public int MaxFraction = 4;   



     StringKludge() {
           super();
     }

     StringKludge(int o, int f) {
          MaxOrdinal  = o;
          MaxFraction = f;
     }





     public String valueOf(float f) {
        return valueOf( (double)f );
     }

     public String valueOf(double d) {

          int i;
          double fraction;
          double v = d;
          int ordinal  = 0;
          int exponent = 0;
          String s;
          StringBuffer sb = new StringBuffer();

          if(v != 0.0) exponent = (int)(Math.floor( log10(Math.abs(v)) ) );

          if(exponent > 0 && exponent <= MaxOrdinal ) {
               exponent = 0;
          } else
          if(exponent < 0 && Math.abs(exponent) <= MaxFraction ) {
               exponent = 0;
          } else {
             if( exponent < 0 ) {
                for(i=exponent; i<0; i++) { v *= 10.0; }
             } else {
                for(i=0; i<exponent; i++) { v /= 10.0; }
             }
          }

          if(v < 0 ) v -= 0.5*Math.pow(10,-MaxFraction);
          else       v += 0.5*Math.pow(10,-MaxFraction);
          ordinal  = (int)v;
          fraction = Math.abs(v - ordinal);

          sb.append(ordinal);
          if(fraction != 0.0 ) {
               s = stringfraction(fraction);
               if( s != null) sb.append(s);
          } 
          if(exponent != 0 )  {
                 s = stringexponent(exponent);
                 if( s != null) sb.append(s); 
          }
          return sb.toString();


     }
    

     private String stringfraction(double f) {
           double d = f;
           int j;
           int i;
           int k;
           double c;

           d *= Math.pow(10,MaxFraction);
           i = (int)d;

           if(i == 0) return null;

//         Strip trailing zeros
           for(j=0;j<MaxFraction;j++) {
               k = ( (int)(i/10) )*10;
               if( i == k ) i /= 10;
               else break;
           }
           
           if(i == 0) return null;
 

           return ".".concat(String.valueOf(i));

     }

     private String stringexponent(int exponent) {
          StringBuffer s = new StringBuffer("e");
          s.append(String.valueOf(exponent));
          return s.toString();
     }




      private double log10( double a ) throws ArithmeticException {
           return Math.log(a)/2.30258509299404568401;
      }







}
