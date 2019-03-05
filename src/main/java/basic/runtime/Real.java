
package basic.runtime;

public class Real {
    @BasicIntrinsic(name="ABS")
    public static double Abs( double vl )
    {
        return Math.abs(vl);
    }
}

