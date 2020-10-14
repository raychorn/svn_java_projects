/**
 * Copyright (c) 1995, 1996 THOUGHT Inc. All Rights Reserved.<p>
 *
 */

import java.util.*;
import java.lang.*;
import java.lang.*;
import CinnaMoney;

public class TestCinnaMoney {
	public static void main(String args[]) {
	String strn1 = new String("12000.00");
	CinnaMoney m1 = new CinnaMoney(strn1);
	String strn2 = new String("-1200.00");
	CinnaMoney m2 = new CinnaMoney(strn2);
	CinnaMoney m3;
	m3 = m1.add(m2);
	System.out.print(m1.toString());
	System.out.print(" + ");
	System.out.print(m2.toString());
	System.out.print(" = ");
	System.out.println(m3.toString());
	m3 = m1.subtract(m2);
	System.out.print(m1.toString());
	System.out.print(" - ");
	System.out.print(m2.toString());
	System.out.print(" = ");
	System.out.println(m3.toString());
	m3 = m1.divide(m2);
	System.out.print(m1.toString());
	System.out.print(" / ");
	System.out.print(m2.toString());
	System.out.print(" = ");
	System.out.println(m3.toString());
	System.out.println("Result printed to two precision = " + m3.toString(2));
	m3 = m1.multiply(m2);
	System.out.print(m1.toString());
	System.out.print(" * ");
	System.out.print(m2.toString());
	System.out.print(" = ");
	System.out.println(m3.toString());
	System.out.println("Result printed to two precision = " + m3.toString(2));
	}
}


