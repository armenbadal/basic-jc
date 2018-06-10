
package basic.ast;

import java.util.List;
import java.util.ArrayList;

public class Subroutine extends Node {
	public String name;
	public List<Variable> parameters;
	public List<Variable> locals;
	public Statement body = null;
	
	public Subroutine( String nm )
	{
		name = nm;
		
		parameters = new ArrayList<>();
		locals = new ArrayList<>();
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("SUB ").append(name).append("( ");
		for( Variable vr : parameters )
			sb.append(vr.name).append(" ");
		sb.append(")\n");
		for( Variable vr : locals )
			sb.append("|\t").append(vr.name).append("\n");
		sb.append(body.toString());
		sb.append("END SUB\n");
		return sb.toString();
	}
}

