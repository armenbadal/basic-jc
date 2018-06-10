
package basic.ast;

public class Call extends Statement {
	public Apply caller;

	public Call( Apply ay )
	{
		caller = ay;
	}
}


