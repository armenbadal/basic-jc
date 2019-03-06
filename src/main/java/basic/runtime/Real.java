
package basic.runtime;

public class Real {
    @BasicIntrinsic(supports="ABS")
    public static double Abs( double vl )
    {
        return Math.abs(vl);
    }

    @BasicIntrinsic(supports="SQR")
    public static double Sqr( double vl )
    {
        return Math.sqrt(vl);
    }
}

