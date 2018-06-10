
package basic.ast;

public class If extends Statement {
	public Expression condition;
	public Statement decision;
	public Statement alternative;

	public If( Expression c, Statement d, Statement a )
	{
		condition = c;
		decision = d;
		alternative = a;
	}
}

