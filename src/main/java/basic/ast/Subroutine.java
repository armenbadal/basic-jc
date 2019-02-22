
package basic.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//
public class Subroutine extends Node {
    public String module;
	public String name;
	public List<String> parameters;
	public List<Variable> locals = null;
	public Statement body = null;
    public boolean isBuiltIn = false;
    
	public Subroutine( String mo, String nm, List<String> pars )
	{
		name = nm;
		parameters = new ArrayList<>(pars);
     	locals = new ArrayList<>();
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("SUB ").append(name).append("( ");
		sb.append(parameters.stream().collect(Collectors.joining(", ")));
		sb.append(" )\n");

		locals.forEach(vi -> sb.append(String.format("|\t%s\n", vi)));
        
		sb.append(body.toString());
		sb.append("END SUB\n");
		return sb.toString();
	}
}

