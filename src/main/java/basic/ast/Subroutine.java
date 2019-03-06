/*************************************
 * Basic-JC կոմպիլյատոր
 *************************************/

package basic.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@code Subroutine} դասը Բեյսիկ-Փ լեզվի ենթածրագրերի մոդելն է։
 *
 * Այս դասով ներկայանում են և՛ ծրագրավորողի սահմանած, և՛ լեզվում
 * ներդրված ենթածրագրերը։ Ներդրված ենթածրագրերի դեպքում {@code body}
 * անդամը {@code null} է, իսկ {@code mappedTo} անդամը պարունակում է
 * {@basic.runtime} փաթեթի այն ստատիկ մեթոդի անունը, որն իրականացնում
 * է տվյալ ներդրված ենթածրագիրը։ Ծրագրավորողի սահմանած ենթածրագրերի
 * դեպքում  {@code mappedTo}-ն {@code null} է։
 */
public class Subroutine extends Node {
    public String module;
	public String name;
	public List<String> parameters;
	public List<Variable> locals = null;
	public Statement body = null;
	public String mappedTo = null;
    
	public Subroutine( String mo, String nm, List<String> pars )
	{
        module = mo;
		name = nm;
		parameters = new ArrayList<>(pars);
     	locals = new ArrayList<>();
	}

	public Subroutine( String mo, String nm, List<String> pars, String mp )
	{
        module = mo;
		name = nm;
		parameters = new ArrayList<>(pars);
		mappedTo = mp;
	}

	public boolean isBuiltIn()
	{
		return body == null && mappedTo != null;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\nSUB ").append(module).append("->").append(name).append("(");
		sb.append(parameters.stream().collect(Collectors.joining(", ")));
		sb.append(")\n");

		if( locals != null )
			locals.forEach(vi -> sb.append(String.format("|\t%s\n", vi)));
        
		if( body != null )
            sb.append(body.toString());
		sb.append("END SUB");
		return sb.toString();
	}
}

