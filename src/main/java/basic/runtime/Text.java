
package basic.runtime;

public class Text {
    public static String _concatenate( String s0, String s1 )
    {
        return s0 + s1;
    }

    public static boolean _eq( String s0, String s1 )
    {
        return s0.equals(s1);
    }
    
    public static boolean _ne( String s0, String s1 )
    {
        return !s0.equals(s1);
    }
    
    public static boolean _gt( String s0, String s1 )
    {
        return s0.compareTo(s1) > 0;
    }
    
    public static boolean _ge( String s0, String s1 )
    {
        return s0.compareTo(s1) >= 0;
    }
    
    public static boolean _lt( String s0, String s1 )
    {
        return s0.compareTo(s1) < 0;
    }
    
    public static boolean _le( String s0, String s1 )
    {
        return s0.compareTo(s1) <= 0;
    }

    @BasicIntrinsic(name="MID$")
    public static String Mid( String sr, double from, double count )
    {
        return sr.substring((int)from, (int)count);
    }
}
