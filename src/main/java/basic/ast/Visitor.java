
package basic.ast;

public abstract class Visitor {
	
	public void visit( Node n )
	{
		if( n instanceof Program )
			visit((Program)n);
		else if( n instanceof Subroutine )
			visit((Subroutine)n);
		else if( n instanceof Statement )
			visit((Statement)n);
		else if( n instanceof Expression )
			visit((Expression)n);
	}

	public void visit( Statement s )
	{
		if( s instanceof Sequence )
			visit((Sequence)s);
		else if( s instanceof Let )
			visit((Let)s);
		else if( s instanceof Input )
			visit((Input)s);
		else if( s instanceof Print )
			visit((Print)s);
		else if( s instanceof If )
			visit((If)s);
		else if( s instanceof While )
			visit((While)s);
		else if( s instanceof For )
			visit((For)s);
		else if( s instanceof Call )
			visit((Call)s);
	}

	public void visit( Expression e )
	{
		
	}
	
	public void visit( Program p ) {}
	public void visit( Subroutine s ) {}
}

