
package basic.ast;

import java.util.List;
import java.util.ArrayList;

public class Apply extends Expression {
	public Subroutine callee;
	public List<Expression> arguments;

	public Apply( Subroutine cl )
	{
		callee = cl;
		arguments = new ArrayList<>();
	}
}

