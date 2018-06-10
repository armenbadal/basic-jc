
package basic.ast;

public class Input extends Statement {
	public String prompt;
	public Variable place;

	public Input( String pr, Variable vr )
	{
		prompt = pr;
		place = vr;
	}
}

