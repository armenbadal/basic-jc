
package basic.parser;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.*;

import basic.ast.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

///
public class AstBuilder extends BasicBaseVisitor<Node> {
	// AST֊ի արմատը
	private Program program = null;
	// ընթացիկ վերլուծվող ենթածրագիրը
	private Subroutine current = null;
	// ենթածրագրերի չլուծված կանչեր
	private Map<String,List<Apply>> unresolved;

	///
	public AstBuilder()
	{
		unresolved = new HashMap<>();
	}
	
	@Override
	public Node visitProgram(BasicParser.ProgramContext ctx)
	{
		program = new Program();
		program.fileName = BasicParser.fileName;
			
		for( BasicParser.SubroutineContext sc : ctx.subroutine() )
			program.members.add((Subroutine)visitSubroutine(sc));

		// for( String nm : unresolved.keySet() )
		// 	System.out.println(nm);

		return program;
	}

	@Override
	public Node visitSubroutine(BasicParser.SubroutineContext ctx)
	{
		// ենթածրագիր տրված անունով
		current = new Subroutine(ctx.name.getText());
		
		// պարամետրերը
		for( Token tk : ctx.params ) {
			Variable pr = new Variable(tk.getText());
			current.parameters.add(pr);
            current.locals.add(pr);
		}
		
		current.body = (Statement)visitSequence(ctx.sequence());
		return current;
	}

	@Override
	public Node visitSequence(BasicParser.SequenceContext ctx)
	{
		Sequence sequ = new Sequence();
		for( BasicParser.StatementContext sc : ctx.statement() )
			sequ.items.add((Statement)visit(sc));
		
		return sequ;
	}

	@Override
	public Node visitStatLet(BasicParser.StatLetContext ctx)
	{
		return new Let(searchVariable(ctx.IDENT().getText()),
					   (Expression)visit(ctx.expression()));
	}

	@Override
	public Node visitStatInput(BasicParser.StatInputContext ctx)
	{
		String prompt = "? ";
		if( ctx.TEXT() != null )
			prompt = ctx.TEXT().getText() + " ";
		return new Input(prompt, searchVariable(ctx.IDENT().getText()));
	}

	@Override
	public Node visitStatPrint(BasicParser.StatPrintContext ctx)
	{
		return new Print((Expression)visit(ctx.expression()));
	}

	@Override
	public Node visitStatIf(BasicParser.StatIfContext ctx)
	{
		Expression cn = (Expression)visit(ctx.mcond);
		Statement de = (Statement)visitSequence(ctx.mseq);
		If sif = new If(cn, de, null);
		
		If si = sif;
		for( int i = 1; i < ctx.scond.size(); ++i ) {
			cn = (Expression)visit(ctx.scond.get(i));
			de = (Statement)visitSequence(ctx.sseq.get(i));
			si.alternative = new If(cn, de, null);
			si = (If)si.alternative;
		}

		if( ctx.aseq != null )
			si.alternative = (Statement)visitSequence(ctx.aseq);
		
		return sif;
	}

	@Override
	public Node visitStatWhile(BasicParser.StatWhileContext ctx)
	{
		Expression c = (Expression)visit(ctx.expression());
		Statement b = (Statement)visitSequence(ctx.sequence());
		return new While(c, b);
	}

	@Override
	public Node visitStatFor(BasicParser.StatForContext ctx)
	{
		Variable p = searchVariable(ctx.IDENT().getText());
		Expression f = (Expression)visit(ctx.from);
		Expression t = (Expression)visit(ctx.to);
		double sv = 1.0;
		if( ctx.step != null )
			sv = Double.parseDouble(ctx.step.getText());
		Real s = new Real(sv);
		Statement b = (Statement)visitSequence(ctx.sequence());
		return new For(p, f, t, s, b);
	}

	@Override
	public Node visitStatCall(BasicParser.StatCallContext ctx)
	{
		String nm = ctx.IDENT().getText();
		Subroutine sbr = searchSubroutine(nm);
		Apply ay = new Apply(sbr);
		for( BasicParser.ExpressionContext ec : ctx.expression() )
			ay.arguments.add((Expression)visit(ec));

		if( sbr == null )
			addUnresolved(nm, ay);
		
		return new Call(ay);
	}

	
	@Override
	public Node visitDisjunction(BasicParser.DisjunctionContext ctx)
	{
		return new Binary(Operation.Or,
						  (Expression)visit(ctx.left),
						  (Expression)visit(ctx.right));
	}

	@Override
	public Node visitConjunction(BasicParser.ConjunctionContext ctx)
	{
		return new Binary(Operation.And,
						  (Expression)visit(ctx.left),
						  (Expression)visit(ctx.right));
	}

	@Override
	public Node visitComparison(BasicParser.ComparisonContext ctx)
	{
		return new Binary(opcode(ctx.oper.getText()),
						  (Expression)visit(ctx.left),
						  (Expression)visit(ctx.right));
	}

	@Override
	public Node visitEquality(BasicParser.EqualityContext ctx)
	{
		return new Binary(opcode(ctx.oper.getText()),
						  (Expression)visit(ctx.left),
						  (Expression)visit(ctx.right));
	}

	@Override
	public Node visitAddition(BasicParser.AdditionContext ctx)
	{
		return new Binary(opcode(ctx.oper.getText()),
						  (Expression)visit(ctx.left),
						  (Expression)visit(ctx.right));
	}

	@Override
	public Node visitMultiply(BasicParser.MultiplyContext ctx)
	{
		return new Binary(opcode(ctx.oper.getText()),
						  (Expression)visit(ctx.left),
						  (Expression)visit(ctx.right));
	}

	@Override
	public Node visitPower(BasicParser.PowerContext ctx)
	{
		return new Binary(Operation.Pow,
						  (Expression)visit(ctx.left),
						  (Expression)visit(ctx.right));
	}
	
	@Override
	public Node visitUnary(BasicParser.UnaryContext ctx)
	{
		return new Unary(opcode(ctx.oper.getText()),
						 (Expression)visit(ctx.expression()));
	}

	@Override
	public Node visitApply(BasicParser.ApplyContext ctx)
	{
		String nm = ctx.IDENT().getText();
		Subroutine sbr = searchSubroutine(nm);
		Apply ay = new Apply(sbr);
		for( BasicParser.ExpressionContext ec : ctx.expression() )
			ay.arguments.add((Expression)visit(ec));

		if( sbr == null )
			addUnresolved(nm, ay);

		return ay;
	}

	@Override
	public Node visitPriority(BasicParser.PriorityContext ctx)
	{
		return visit(ctx.expression());
	}

	@Override
	public Node visitText(BasicParser.TextContext ctx)
	{
		String lex = ctx.TEXT().getText();
		return new Text(lex);
	}

	@Override
	public Node visitReal(BasicParser.RealContext ctx)
	{
		String lex = ctx.REAL().getText();
		double nval = Double.parseDouble(lex);
		return new Real(nval);
	}

	@Override
	public Node visitVariable(BasicParser.VariableContext ctx)
	{
		String vnm = ctx.IDENT().getText();
		return searchVariable(vnm);
	}

	///
	private Variable searchVariable( String vnm )
	{
		for( Variable vi : current.locals )
			if( vi.name.equals(vnm) )
				return vi;

		Variable vr = new Variable(vnm);
		current.locals.add(vr);
		
		return vr;
	}

	///
	private Subroutine searchSubroutine( String snm )
	{
		for( Subroutine si : program.members )
			if( si.name.equals(snm) )
				return si;

		return null;
	}

	///
	private void addUnresolved( String snm, Apply cr )
	{
		if( !unresolved.containsKey(snm) )
			unresolved.put(snm, new ArrayList<>());
		unresolved.get(snm).add(cr);								
	}
	
	///
	private Operation opcode( String ops )
	{
		if( ops.equals("OR") )
			return Operation.Or;

		if( ops.equals("AND") )
			return Operation.And;

		if( ops.equals("=") )
			return Operation.Eq;

		if( ops.equals("<>") )
			return Operation.Ne;

		if( ops.equals(">") )
			return Operation.Gt;

		if( ops.equals(">=") )
			return Operation.Ge;

		if( ops.equals("<") )
			return Operation.Lt;

		if( ops.equals("<=") )
			return Operation.Le;

		if( ops.equals("+") )
			return Operation.Add;

		if( ops.equals("-") )
			return Operation.Sub;

		if( ops.equals("=") )
			return Operation.Conc;

		if( ops.equals("*") )
			return Operation.Mul;

		if( ops.equals("/") )
			return Operation.Div;

		if( ops.equals("NOT") )
			return Operation.Not;

		return Operation.None;
	}
}

