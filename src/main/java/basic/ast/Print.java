
package basic.ast;

public class Print extends Statement {
	public Expression expr;

	public Print( Expression ex )
	{
		expr = ex;
	}
}

