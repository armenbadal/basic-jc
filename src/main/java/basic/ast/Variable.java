
package basic.ast;

public class Variable extends Expression {
	public String name;

	public Variable( String nm )
	{
		name = nm;
        type = Node.Type.of(name);
	}

	@Override
	public String toString()
	{
		return name;
	}
}

