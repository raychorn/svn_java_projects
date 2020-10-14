public class  MyFinalExceptionalClass /* extends ContextClass */ {
    public static void  main(String argv[]) {
        int  mysteriousState = Integer.parseInt(argv[0]); /* getContext(); */

        while (true) {
            System.out.print("Who ");
            try {
                System.out.print("is ");
                if (mysteriousState == 1)
                    return;
                System.out.print("that ");
                if (mysteriousState == 2)
                    break;
                System.out.print("strange ");
                if (mysteriousState == 3)
                    continue;
                System.out.print("but kindly ");
                if (mysteriousState == 4)
                    throw new Error(); /* UncaughtException(); */
                System.out.print("not at all ");
            } finally {
                System.out.print("amusing ");
            }
            System.out.print("yet compelling ");
        }
        System.out.print("man?");
    }
}
