
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

	
	@Override
	public void visit( Program p )
	{
		for( Subroutine s : p.members )
			visit(s);
	}

	@Override
	public void visit( Subroutine s )
	{
		print("SUB"); print(" "); print(s.name); nl();
		//visit(s.body());
		print("END SUB"); nl();
	}

	@Override
	public void visit( Let s )
	{
		print("LET"); print(" ");
	}

	@Override
	public void visit( Input s )
	{
	}

	@Override
	public void visit( Print s )
	{
	}

	@Override
	public void visit( If s )
	{
	}

	@Override
	public void visit( While s )
	{
	}

	@Override
	public void visit( For s )
	{
	}

	@Override
	public void visit( Call s )
	{
	}


	@Override
	public void visit( Unary e )
	{}

	@Override
	public void visit( Real e )
	{
		System.out.print(e.value);
	}

	@Override
	public void visit( Text e )
	{
		System.out.print(e.value)
	}

	@Override
	public void visit( Variable e )
	{
		System.out.print(e.name);
	}
}


