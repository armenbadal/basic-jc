
package basic.runtime;

import java.util.Scanner;

public class IO {
    private static Scanner scanner = new Scanner(System.in);
    
    public static String inputText( String prompt )
    {
        System.out.printf("%s ", prompt);
        String val = scanner.nextLine();
        return val;
    }

    public static double inputReal( String prompt )
    {
        System.out.printf("%s ", prompt);
        double val = scanner.nextDouble();
        scanner.nextLine();
        return val;
    }

    public static boolean inputLogic( String prompt )
    {
        System.out.printf("%s ", prompt);
        String val = scanner.nextLine();
        // TODO: accept only 'TRUE' and 'FALSE'
        return val.equals("TRUE");
    }

    public static void printText( String str )
    {
        System.out.println(str);
    }

    public static void printReal( double rv )
    {
        System.out.println(rv);
    }

    public static void printLogic( boolean bv )
    {
        System.out.println(bv ? "TRUE" : "FALSE");
    }
}
