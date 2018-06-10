
package basic.codegen;

import basic.ast.*;

public class Printer extends Visitor {

	private static void print( String s )
	{
		System.out.print(s);
	}

	private static void nl()
	{
		System.out.println();
	}

	private int indent = 0;

	
	///
	@Override
	public void visit( Program p )
	{
		for( Subroutine s : p.members )
			visit(s);
	}

	///
	@Override
	public void visit( Subroutine s )
	{
		print("SUB"); print(" "); print(s.name); nl();
		print("END SUB"); nl();
	}

	
}

