
package basic.parser;

import basic.ast.*;

public class TypeChecker {
    public void check( Program p ) throws TypeError
    {
        for( Subroutine m : p.members )
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

        if( s.expr.type != s.place.type )
            throw new TypeError();
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
        if( s.condition.type != Node.Type.Logic )
            throw new TypeError();
        
        check(s.decision);
        check(s.alternative);
    }

	public void check( While s ) throws TypeError
    {
        check(s.condition);
        if( s.condition.type != Node.Type.Logic )
            throw new TypeError();
        
        check(s.body);
    }

	public void check( For s ) throws TypeError
    {
        check(s.param);
        if( s.param.type != Node.Type.Real )
            throw new TypeError();
        
        check(s.from);
        if( s.from.type != Node.Type.Real )
            throw new TypeError();
        
        check(s.to);
        if( s.to.type != Node.Type.Real )
            throw new TypeError();
        
        check(s.step);
        if( s.step.type != Node.Type.Real )
            throw new TypeError();
        
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

        if( e.left.type == Node.Type.Real && e.right.type == Node.Type.Real ) {
            boolean allowed = e.oper == Operation.Add;
            allowed = allowed || e.oper == Operation.Sub;
            allowed = allowed || e.oper == Operation.Mul;
            allowed = allowed || e.oper == Operation.Div;
            allowed = allowed || e.oper == Operation.Pow;
            if( !allowed )
                throw new TypeError();
        }
        else if( e.left.type == Node.Type.Text && e.right.type == Node.Type.Text ) {
            if( e.oper != Operation.Conc )
                throw new TypeError();
        }
        else if( e.left.type == Node.Type.Logic && e.right.type == Node.Type.Logic ) {
            if( e.oper != Operation.And || e.oper != Operation.Or )
                throw new TypeError();
        }

        if( e.left.type == e.right.type ) {
            boolean allowed = e.oper == Operation.Eq;
            allowed = allowed || e.oper == Operation.Ne;
            allowed = allowed || e.oper == Operation.Gt;
            allowed = allowed || e.oper == Operation.Ge;
            allowed = allowed || e.oper == Operation.Lt;
            allowed = allowed || e.oper == Operation.Le;
            if( !allowed )
                throw new TypeError();
        }
    }

	public void check( Unary e ) throws TypeError
    {
        check(e.expr);
        if( e.oper == Operation.Not && e.expr.type != Node.Type.Logic )
            throw new TypeError();
        if( (e.oper == Operation.Add || e.oper == Operation.Sub) && e.expr.type != Node.Type.Real )
            throw new TypeError();
    }

	public void check( Apply e ) throws TypeError
    {
        // կիրառվող ենթածրագրի պարամետրերի քանակը պետք է հավասար
        // լինի տրված արգումենտների քանակին
        if( e.callee.parameters.size() != e.arguments.size() )
            throw new TypeError();
        
        // կիրառման տիպը կիրառվող ենթածրագրի տիպն է
        type = Node.Type.of(e.callee.name);

        // պարամետրերի և արգումենտների տիպերի համապատասխանություն
        Iterator<String> pari = e.callee.parameters.iterator();
        for( Expression a : e.arguments ) {
            check(a);
            if( a.type != Node.Type.of(pari.next()) )
                throw new TypeError();
        }
    }

	public void check( Variable e )
    {}

	public void check( Real e )
    {}

	public void check( Text e )
    {}
}

