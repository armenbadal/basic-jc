
package basic.parser;

import basic.ast.*;

import java.util.Iterator;

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

    public void check( Sequence s ) throws TypeError
    {
        for( Statement si : s.items )
            check(si);
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

        if( e.left.type != e.right.type )
            throw new TypeError();

        if( e.left.type == Node.Type.Real ) {
            // իրական արժեքների թույլատրելի գործողությունները
            switch( e.oper ) {
                case Add:
                case Sub:
                case Mul:
                case Div:
                case Pow:
                    e.type = Node.Type.Real;
                    break;
                case Eq:
                case Ne:
                case Gt:
                case Ge:
                case Lt:
                case Le:
                    e.type = Node.Type.Logic;
                    break;
                default:
                    throw new TypeError();                
            }
        }
        else if( e.left.type == Node.Type.Text ) {
            // տեքստային արժեքների թույլատրելի գործողությունները
            switch( e.oper ) {
                case Conc:
                    e.type = Node.Type.Text;
                    break;
                case Eq:
                case Ne:
                case Gt:
                case Ge:
                case Lt:
                case Le:
                    e.type = Node.Type.Logic;
                    break;
                default:
                    throw new TypeError();                
            }
        }
        else if( e.left.type == Node.Type.Logic ) {
            // տրամաբանական արժեքների թույլատրելի գործողությունները
            switch( e.oper ) {
                case And:
                case Or:
                case Eq:
                case Ne:
                    e.type = Node.Type.Logic;
                    break;
                default:
                    throw new TypeError();                
            }
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
        e.type = Node.Type.of(e.callee.name);

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

