import java.net.* ;
import java.io.* ;

public class ServerCounter {

  static public void main (String argv[] ) {
     String filename = ""  ;
     ServerSocket sock = null ;
     DataInputStream input = null ;
     DataOutputStream output = null ;
     Socket s = null ;
     char c ; 
     String counter = "" ;
     try {
      sock = new ServerSocket( 9999, 8 ) ;
      while (true) {
	 filename = "" ;
         s = sock.accept() ;
         // System.out.println(s.getInetAddress().getHostName()) ;
	 input = new DataInputStream(s.getInputStream()) ;
	 output = new DataOutputStream(s.getOutputStream()) ;
         
	 while (true) {
	     synchronized (filename) {
                c = (char) input.readByte() ;
		if ( c == '\n' ) break;
		filename = filename + c ; 
		}
         }
        // System.out.println("OK, the filename is:") ; 
        // System.out.println(filename) ;
        counter = getcounter(filename) ; // Get counter value 
        output.writeBytes(counter) ;
        s.close() ;
      }
    } catch (EOFException e) {
      e.printStackTrace() ;
      System.err.println(e.getMessage()) ;
    } catch (IOException e) {
      e.printStackTrace() ;
      System.err.println(e.getMessage()) ;
    }
  }

  static public  String getcounter(String name) {
   String counter = "" ;
   String filename = "" ;
   String line = ""  ;
   int num = 0 ;
   
   FileInputStream in = null ;
   FileOutputStream out = null ;
  
   if ( name.endsWith(".html") || name.endsWith(".html/") ) {
        filename = "/home/hefei2/duw/www/counter" + name ;
       }
     else if ( name.endsWith("/")) { 
             filename = "/home/hefei2/duw/www/counter" + name + "index.html" ; 
             }
          else {
             filename = "/home/hefei2/duw/www/counter" + name + "/index.html" ;
             }
   try
   { 
       in = new FileInputStream(filename) ;
   }
   catch (Throwable e )
   {
      e.printStackTrace() ;
      System.err.println(e.getMessage()) ;
      // System.exit(1) ;
      return ("-1") ;  // if no such file, return -1
   }

   DataInputStream dis_in = new DataInputStream(in) ;
   try 
   {
      line = dis_in.readLine() ;
      in.close() ;
      // System.out.println(line) ;
   } 
   catch (IOException e ) 
   { 
      e.printStackTrace() ; 
      System.err.println(e.getMessage()) ;
      System.exit(1) ;
    }

   counter = line ;
   num = (Integer.valueOf(line)).intValue() ;
   num ++ ;
   line = Integer.toString(num) ;

   // Save new counter 
   try
   { 
       out = new FileOutputStream(filename) ;
   }
   catch (Throwable e )
   {
      e.printStackTrace() ; 
      System.err.println(e.getMessage()) ;
      System.exit(1) ;
   }
 
   DataOutputStream dis_out = new DataOutputStream(out) ;
   try
   {
      dis_out.writeBytes(line) ;
      out.close() ;
      // System.out.println(line) ;
   }
   catch (IOException e )
   {
      e.printStackTrace() ;
      System.err.println(e.getMessage()) ;
      //   System.exit(1) ;
      return("-1") ;
    }


   return ( counter) ;   
  }  

}
