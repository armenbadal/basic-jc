/*************************************
 * Basic-JC կոմպիլյատոր
 *************************************/

package basic.parser;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.*;

import basic.ast.*;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import java.io.IOException;

/**
 * <em>Վերացական (աբստրակտ) քերականական ծառի</em> կառուցման մեթոդները։
 *
 *
 * @author Արմեն Բադալյան
 */
public class AstBuilder extends BasicBaseVisitor<Node> {
	/**
	 * Ծառի (AST) արմատը
	 */
	private Program program = null;
	/**
	 * Ընթացիկ վերլուծվող ենթածրագրի հղումը
	 */
	private Subroutine current = null;
	/**
	 * Ենթածրագրերի չլուծված կանչեր
	 */
	private Map<String,List<Apply>> unresolved = new HashMap<>();
    /**
	 * Ներդրված ենթածրագրերի գրադարան
	 */
    private BuiltIns builtins;
    
	///
	public AstBuilder()
	{
        try {
            builtins = new BuiltIns();
        }
        catch( IOException ex ) {
            ex.printStackTrace();
        }
	}
	
	@Override
	public Node visitProgram(BasicParser.ProgramContext ctx)
	{
		program = new Program(BasicParser.sourceFileName);

        // create subroutines
		for( BasicParser.SubroutineContext sc : ctx.subroutine() )
			program.members.add((Subroutine)visitSubroutine(sc));

        // resolve links
		for( Map.Entry<String,List<Apply>> mi : unresolved.entrySet() )
            for( Subroutine si : program.members )
                if( si.name.equals(mi.getKey()) ) {
                    for( Apply ai : mi.getValue() )
                        ai.callee = si;
                    unresolved.remove(mi.getKey());
                    break;
                }

		return program;
	}

	@Override
	public Node visitSubroutine(BasicParser.SubroutineContext ctx)
	{
		// անունը
		String sname = ctx.name.getText();
		// պարամետրերը
		List<String> pars = ctx.params.stream()
            .map(Token::getText)
            .collect(Collectors.toList());
		// ենթածրագիր տրված անունով
		current = new Subroutine(program.name, sname, pars);

		current.parameters.forEach(p -> current.locals.add(new Variable(p)));

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
		Variable place = searchVariable(ctx.IDENT().getText());
		Expression exp = (Expression)visit(ctx.expression());
		return new Let(place, exp);
	}

	@Override
	public Node visitStatInput(BasicParser.StatInputContext ctx)
	{
		// հրավերքը
		String prompt = "? ";
		if( ctx.TEXT() != null )
			prompt = ctx.TEXT().getText() + " ";
		// ներմուծվող փոփոխականը
		Variable place = searchVariable(ctx.IDENT().getText());
		return new Input(prompt, place);
	}

	@Override
	public Node visitStatPrint(BasicParser.StatPrintContext ctx)
	{
		// արտածվող արտահայտություն
		Expression exp = (Expression)visit(ctx.expression());
		return new Print(exp);
	}

	@Override
	public Node visitStatIf(BasicParser.StatIfContext ctx)
	{
		// պայմանը
		Expression cn = (Expression)visit(ctx.mcond);
		// հաստատված ճյուղը
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
		// ցիկլի պայմանը
		Expression c = (Expression)visit(ctx.expression());
		// ցիկլի մարմինը
		Statement b = (Statement)visitSequence(ctx.sequence());
		return new While(c, b);
	}

	@Override
	public Node visitStatFor(BasicParser.StatForContext ctx)
	{
		// ցիկլի պարամետրը
		Variable p = searchVariable(ctx.IDENT().getText());
		// սկզբնական արժեքը
		Expression f = (Expression)visit(ctx.from);
		// սահմանյին արժեքը
		Expression t = (Expression)visit(ctx.to);
		// քայլը
		double sv = 1.0;
		if( ctx.step != null )
			sv = Double.parseDouble(ctx.step.getText());
		Real s = new Real(sv);
		// մարմինը
		Statement b = (Statement)visitSequence(ctx.sequence());
		return new For(p, f, t, s, b);
	}

	@Override
	public Node visitStatCall(BasicParser.StatCallContext ctx)
	{
		String nm = ctx.IDENT().getText();

		List<Expression> args = new ArrayList<>();
		for( BasicParser.ExpressionContext ec : ctx.expression() )
			args.add((Expression)visit(ec));

		Subroutine sbr = searchSubroutine(nm);
		Apply apy = new Apply(sbr, args);

		if( sbr == null )
			addUnresolved(nm, apy);
		
		return new Call(apy);
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
		return new Binary(Operation.from(ctx.oper.getText()),
						  (Expression)visit(ctx.left),
						  (Expression)visit(ctx.right));
	}

	@Override
	public Node visitEquality(BasicParser.EqualityContext ctx)
	{
		return new Binary(Operation.from(ctx.oper.getText()),
						  (Expression)visit(ctx.left),
						  (Expression)visit(ctx.right));
	}

	@Override
	public Node visitAddition(BasicParser.AdditionContext ctx)
	{
		return new Binary(Operation.from(ctx.oper.getText()),
						  (Expression)visit(ctx.left),
						  (Expression)visit(ctx.right));
	}

	@Override
	public Node visitMultiply(BasicParser.MultiplyContext ctx)
	{
		return new Binary(Operation.from(ctx.oper.getText()),
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
		return new Unary(Operation.from(ctx.oper.getText()),
						 (Expression)visit(ctx.expression()));
	}

	@Override
	public Node visitApply(BasicParser.ApplyContext ctx)
	{
		// կանչվող ենթածրագրի անունը
		String nm = ctx.IDENT().getText();

		// արգումենտների արտահայտությունները
		List<Expression> args = new ArrayList<>();
		for( BasicParser.ExpressionContext ec : ctx.expression() )
			args.add((Expression)visit(ec));

		// որոնել ենթածրագիրը
		Subroutine sbr = searchSubroutine(nm);
		// կիրառման օբյեկտը
		Apply apy = new Apply(sbr, args);
		if( sbr == null )
			addUnresolved(nm, apy);

		return apy;
	}

	@Override
	public Node visitPriority(BasicParser.PriorityContext ctx)
	{
		return visit(ctx.expression());
	}

	@Override
	public Node visitLogic(BasicParser.LogicContext ctx)
	{
		boolean val = ctx.value.getText().equals("TRUE");
		return new Logic(val);
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
		TerminalNode reno = ctx.REAL();
		double nval = Double.parseDouble(reno.getText());
		return new Real(nval);
	}

	@Override
	public Node visitVariable(BasicParser.VariableContext ctx)
	{
		String vnm = ctx.IDENT().getText();
		return searchVariable(vnm);
	}

	/**
	 * Տրված անունով փոփոխական
	 *
	 * @param vnm Փոփոխականի անունը
	 * @return Նոր ստեղծված կամ գոյություն ունեցող փոփոխականի հողումը 
	 */
	private Variable searchVariable( String vnm )
	{
		// փնտրել ընթացիկ ենթածրագրի լոկալ անունների ցուցակում
		for( Variable vi : current.locals )
			if( vi.name.equals(vnm) )
				return vi;

		// ստեղցծել նոր օբյեկտ ...
		Variable vr = new Variable(vnm);
		// ... և ավելացնել ընթացիկ ենթածրագրի լոկալ անունների ցուցակում
		current.locals.add(vr);
		
		return vr;
	}

	///
	private Subroutine searchSubroutine( String snm )
	{
		for( Subroutine si : program.members )
			if( si.name.equals(snm) )
				return si;

        for( Subroutine si : builtins.subroutines )
            if( si.name.equalsIgnoreCase(snm) )
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
}

