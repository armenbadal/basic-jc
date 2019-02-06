
package basic.ast;

import java.util.List;
import java.util.ArrayList;

public class Program extends Node {
	public String fileName;
	public List<Subroutine> members;

	public Program()
	{
		fileName = "";
		members = new ArrayList<>();
	}

	@Override
	public String toString()
	{
		String res = "' " + fileName + "\n";
		for(Subroutine si : members) {
			res += si.toString();
			res += "\n";
		}
		return res;
	}
}

