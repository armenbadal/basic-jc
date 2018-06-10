
package basic.ast;

public class Unary extends Expression {
	public Operation oper;
	public Expression expr;

	public Unary( Operation op, Expression ex )
	{
		oper = op;
		expr = ex;
	}
}

