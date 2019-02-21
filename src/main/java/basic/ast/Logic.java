
package basic.ast;

public class Logic extends Expression {
	public boolean value = false;

	public Logic( boolean vl )
	{
        value = vl;
        type = Node.Type.Logic;
	}

	@Override
	public String toString()
	{
		return value ? "TRUE" : "FALSE";
	}
}
