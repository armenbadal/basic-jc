
package basic.ast;

public class While extends Statement {
	public Expression condition;
	public Statement body;

	public While( Expression c, Statement b )
	{
		condition = c;
		body = b;
	}
}

