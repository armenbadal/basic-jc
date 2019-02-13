
package basic.runtime;

public class BasicIO {
    public static String readString( String prompt )
    {
        System.out.printf("%s ", prompt);
        return "Read string from console."; // TODO: implement this
    }

    public static Double readReal( String prompt )
    {
        System.out.printf("%s ", prompt);
        return 3.1415;
    }

    public static void printString( String str )
    {
        System.out.println(str);
    }

    public static void printString( Double rv )
    {
        System.out.println(rv);
    }
}
