
package basic.driver;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import basic.parser.*;
import basic.codegen.*;

public class Main {
	///
	public static void compile( String source ) throws Exception
	{
		CharStream input = CharStreams.fromFileName(source);
        BasicLexer lexer = new BasicLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        BasicParser parser = new BasicParser(source, tokens);
        ParseTree parseTree = parser.program();
        System.out.println(parseTree.toStringTree(parser));

		AstBuilder astBuilder = new AstBuilder();
		basic.ast.Node ast = astBuilder.visit(parseTree);
		System.out.println(ast);
		try{
			TypeChecker checker = new TypeChecker();
			checker.check((basic.ast.Program)ast);
		}
		catch( TypeError er ) {
			System.err.println(er.getMessage());
		}

        basic.codegen.Compiler cr = new basic.codegen.Compiler((basic.ast.Program)ast);
        cr.compile();
	}

	///
	public static void main( String[] args )
	{
		try {
			if( args.length > 0 ) 
				compile(args[0]);
		}
		catch( Exception ex ) {
			System.err.println(ex.getMessage());
		}
		
		System.out.println("Ok!");
	}
}

