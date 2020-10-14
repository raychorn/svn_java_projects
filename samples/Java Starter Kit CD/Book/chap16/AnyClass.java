/* public */ interface  PresumablyUsefulConstants {
    public static final int     oneOfThem   = 1234;
    public static final float   another     = 1.234F;
    public static final String  yetAnother  = "1234";

}

public class  AnyClass implements PresumablyUsefulConstants {
    public static void  main(String argv[]) {
        double  calculation = oneOfThem * another;

        System.out.println("hello " + yetAnother + calculation);

    }
}
