
package basic.ast;

import java.util.List;
import java.util.ArrayList;

public class Sequence extends Statement {
	public List<Statement> items = new ArrayList<>();

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for( Statement si : items )
			sb.append(si.toString()).append("\n");
		return sb.toString();
	}
}

