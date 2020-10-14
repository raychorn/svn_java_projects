import java.io.*;   // the one time in the chapter we'll say this

public class  Cat {
    public static void  main(String args[]) {
        DataInput  d = new DataInputStream(System.in);
        String     line;

        try { while ((line = d.readLine()) != null)
                  System.out.println(line);
        } catch (IOException  ignored) { }
    }
}
