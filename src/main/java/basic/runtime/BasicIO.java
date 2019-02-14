
package basic.runtime;

public class BasicIO {
    public static String readText( String prompt )
    {
        System.out.printf("%s ", prompt);
        return "Read string from console."; // TODO: implement this
    }

    public static double readReal( String prompt )
    {
        System.out.printf("%s ", prompt);
        return 3.1415;
    }

    public static void printText( String str )
    {
        System.out.println(str);
    }

    public static void printReal( double rv )
    {
        System.out.println(rv);
    }
}
