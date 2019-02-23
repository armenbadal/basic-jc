
package basic.ast;

import java.nio.file.*;
import java.util.List;
import java.util.ArrayList;

public class Program extends Node {
	public Path path;
    public String name;
	public List<Subroutine> members;

	public Program( String ph )
	{
		path = Paths.get(ph);
        name = path.getFileName().toString();
        name = name.substring(0, name.indexOf('.'));
		members = new ArrayList<>();
	}

	@Override
	public String toString()
	{
		String res = "' - - - - - - - - - -\n";
        res += "' File: " + path.toString() + "\n";
        res += "' Module: " + name + "\n";
		for(Subroutine si : members) {
			res += si.toString();
			res += "\n";
		}
		return res;
	}
}

