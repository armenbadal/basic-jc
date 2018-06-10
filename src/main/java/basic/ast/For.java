
package basic.ast;

public class For extends Statement {
	public Variable param;
	public Expression from;
	public Expression to;
	public Real step;
	public Statement body;

	public For( Variable p, Expression f, Expression t, Real s, Statement b )
	{
		param = p;
		from = f;
		to = t;
		step = s;
		body = b;
	}
}

