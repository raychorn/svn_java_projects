public class  AFoolishClass {
    public String  aUsefulString;
}


/* public */ class  AFoolishClassUse {
    public static void main(String argv[]) {

        AFoolishClass  aFC = new AFoolishClass();

        aFC.aUsefulString = "oops!";

    }
}
