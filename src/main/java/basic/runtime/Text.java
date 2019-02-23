
package basic.runtime;

public class Text {
    public static String Concatenate( String s0, String s1 )
    {
        return s0 + s1;
    }
    
    public static String Mid( String sr, double from, double count )
    {
        return sr.substring((int)from, (int)count);
    }
}
