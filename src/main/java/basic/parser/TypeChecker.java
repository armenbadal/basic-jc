
package basic.parser;

import basic.ast.*;

public class TypeChecker {
    // void check( Node n ) throws TypeError
	// {
	// 	if( n instanceof Program )
	// 		return check((Program)n);
	// 	if( n instanceof Subroutine )
	// 		return check((Subroutine)n);
	// 	if( n instanceof Statement )
	// 		return check((Statement)n);
	// 	if( n instanceof Expression )
	// 		return check((Expression)n);
	// 	return null;
	// }

    public void check( Program p ) throws TypeError
    {
        for( Subroutine m : p.members)
            check(m);
    }

	public void check( Subroutine s ) throws TypeError
    {
        check(s.body);
    }

	public void check( Statement s ) throws TypeError
	{
		if( s instanceof Sequence )
			check((Sequence)s);
		else if( s instanceof Let )
			check((Let)s);
		else if( s instanceof Input )
			check((Input)s);
		else if( s instanceof Print )
			check((Print)s);
		else if( s instanceof If )
			check((If)s);
		else if( s instanceof While )
			check((While)s);
		else if( s instanceof For )
			check((For)s);
		else if( s instanceof Call )
			check((Call)s);
	}

	public void check( Let s ) throws TypeError
    {
        check(s.place);
        check(s.expr);
        // check
    }

	public void check( Input s ) throws TypeError
    {
        check(s.place);
    }

	public void check( Print s ) throws TypeError
    {
        check(s.expr);
    }

	public void check( If s ) throws TypeError
    {
        check(s.condition);
        check(s.decision);
        check(s.alternative);
    }

	public void check( While s ) throws TypeError
    {
        check(s.condition);
        check(s.body);
    }

	public void check( For s ) throws TypeError
    {
        check(s.param);
        check(s.from);
        check(s.to);
        check(s.step);
        check(s.body);
    }

	public void check( Call s ) throws TypeError
    {
        check(s.caller);
    }

    public void check( Expression e ) throws TypeError
	{
		if( e instanceof Binary )
            check((Binary)e);
        else if( e instanceof Unary )
			check((Unary)e);
		else if( e instanceof Apply )
			check((Apply)e);
		else if( e instanceof Variable )
			check((Variable)e);
		else if( e instanceof Real )
			check((Real)e);
		else if( e instanceof Text )
			check((Text)e);
    }

	public void check( Binary e ) throws TypeError
    {
        check(e.left);
        check(e.right);
    }

	public void check( Unary e ) throws TypeError
    {
        check(e.expr);
        if( e.oper == Operation.Not && e.expr.type != Node.Type.Boolean )
            throw new TypeError();
        if( (e.oper == Operation.Add || e.oper == Operation.Sub) && e.expr.type != Node.Type.Real )
            throw new TypeError();
    }

	public void check( Apply e ) throws TypeError
    {
        check(e.callee);
        for( Expression a : e.arguments)
            check(a);
    }

	public void check( Variable e )
    {}

	public void check( Real e )
    {}

	public void check( Text e )
    {}
}
