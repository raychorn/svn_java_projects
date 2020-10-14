/* create a string and muck with it using methods.  For chapter 4,
   on calling methods */

class TestString {
  
  public static void main (String args[]) {
    String str = "Now is the winter of our discontent";

    System.out.println("The string is: " + str);
    System.out.println("Length of this string: " +str.length());
    System.out.println("The character at position 5: " + str.charAt(5));
    System.out.println("The substring from positions 11 to 18: " + 
		       str.substring(11,18));
    System.out.println("The index of the character d: " +str.indexOf('d'));
    System.out.println("The index of the beginning of the substring \"winter\": " 
		       + str.indexOf("winter"));
    System.out.println("The string in upper case: " + str.toUpperCase());
  }
}


