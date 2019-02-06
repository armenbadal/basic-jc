
package basic.ast;

public class Variable extends Expression {
	public String name;

	public Variable( String nm )
	{
		name = nm;
	}

	@Override
	public String toString()
	{
		return name;
	}
}

