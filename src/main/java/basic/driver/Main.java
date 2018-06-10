
package basic.driver;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import basic.parser.*;

public class Main {
	///
	public static void compile( String source ) throws Exception
	{
		CharStream input = CharStreams.fromFileName(source);
        BasicLexer lexer = new BasicLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        BasicParser parser = new BasicParser(tokens);
        ParseTree parseTree = parser.program();
        System.out.println(parseTree.toStringTree(parser));

		AstBuilder astBuilder = new AstBuilder();
		basic.ast.Node ast = astBuilder.visit(parseTree);
		System.out.println(ast);
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

