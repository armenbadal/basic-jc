
package basic.ast;

public class Let extends Statement {
	public Variable place;
	public Expression expr;

	public Let( Variable pl, Expression ex )
	{
		place = pl;
		expr = ex;
	}
}

