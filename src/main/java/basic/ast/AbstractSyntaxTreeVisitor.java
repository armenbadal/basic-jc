
package basic.ast;

public interface AbstractSyntaxTreeVisitor<R> {
	default R visit( Node n )
	{
		if( n instanceof Program )
			return visit((Program)n);
		
		if( n instanceof Subroutine )
			return visit((Subroutine)n);
		
		if( n instanceof Statement )
			return visit((Statement)n);
		
		if( n instanceof Expression )
			return visit((Expression)n);

		return null;
	}

	default R visit( Statement s )
	{
		if( s instanceof Sequence )
			return visit((Sequence)s);
		
		if( s instanceof Let )
			return visit((Let)s);
		
		if( s instanceof Input )
			return visit((Input)s);
		
		if( s instanceof Print )
			return visit((Print)s);
		
		if( s instanceof If )
			return visit((If)s);
		
		if( s instanceof While )
			return visit((While)s);
		
		if( s instanceof For )
			return visit((For)s);
		
		if( s instanceof Call )
			return visit((Call)s);

		return null;
	}

	default R visit( Expression e )
	{
		if( e instanceof Binary )
            	return visit((Binary)e);
        
		if( e instanceof Unary )
			return visit((Unary)e);
		
		if( e instanceof Apply )
			return visit((Apply)e);
		
		if( e instanceof Variable )
			return visit((Variable)e);
		
		if( e instanceof Real )
			return visit((Real)e);
		
		if( e instanceof Text )
			return visit((Text)e);

		return null;
	}
	
	R visit( Program p );
	R visit( Subroutine s );

	R visit( Let s );
	R visit( Input s );
	R visit( Print s );
	R visit( If s );
	R visit( While s );
	R visit( For s );
	R visit( Call s );

	R visit( Binary e );
	R visit( Unary e );
	R visit( Apply e );
	R visit( Variable e );
	R visit( Real e );
	R visit( Text e );
}
