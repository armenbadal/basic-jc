
package basic.ast;

public class Real extends Expression {
	public double value = 0.0;

	public Real( double vl )
	{
		value = vl;
        type = Node.Type.Real;
	}
}

