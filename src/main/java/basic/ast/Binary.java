
package basic.ast;

public class Binary extends Expression {
	public Operation oper;
	public Expression left;
	public Expression right;

	public Binary( Operation op, Expression el, Expression er )
	{
		oper = op;
		left = el;
		right = er;
	}

	@Override
	public String toString()
	{
		return left.toString() + " " + oper.mnemonic + " " + right.toString();
	}
}

