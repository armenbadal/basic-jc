
package basic.ast;

public enum Operation {
	None("None"),
	Or("OR"),
	And("AND"),
	Not("NOT"),
	Eq("="),
	Ne("<>"),
	Gt(">"),
	Ge(">="),
	Lt("<"),
	Le("<="),
	Add("+"),
	Sub("-"),
	Conc("&"),
	Mul("*"),
	Div("/"),
	Pow("^");

	public String mnemonic = "None";

	Operation( String mn )
	{
		mnemonic = mn;
	}

	///
	public static Operation from( String ops )
	{
        Operation code = Operation.None;
        switch( ops ) {
            case "OR":
                code = Operation.Or;
                break;
            case "AND":
                code = Operation.And;
                break;
            case "=":
                code = Operation.Eq;
                break;
            case "<>":
                code = Operation.Ne;
                break;
            case ">":
                code = Operation.Gt;
                break;
            case ">=":
                code = Operation.Ge;
                break;
            case "<":
                code = Operation.Lt;
                break;
            case "<=":
                code = Operation.Le;
                break;
            case "+":
                code =Operation.Add;
                break;
            case "-":
                code = Operation.Sub;
                break;
            case "&":
                code = Operation.Conc;
                break;
            case "*":
                code = Operation.Mul;
                break;
            case "/":
                code = Operation.Div;
                break;
            case "^":
                code = Operation.Pow;
                break;
            case "NOT":
                code = Operation.Not;
                break;
        }
        
		return code;
	}
}

