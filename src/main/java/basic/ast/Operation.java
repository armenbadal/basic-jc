
package basic.ast;

public enum Operation {
	None("None", 'N'),
	Or("OR", 'L'),
	And("AND", 'L'),
	Eq("=", 'C'),
	Ne("<>", 'C'),
	Gt(">", 'C'),
	Ge(">=", 'C'),
	Lt("<", 'C'),
	Le("<=", 'C'),
	Add("+", 'A'),
	Sub("-", 'A'),
	Conc("&", 'T'),
	Mul("*", 'A'),
	Div("/", 'A'),
	Pow("^", 'A'),
	Not("NOT", 'L');

	public String mnemonic = "None";
    public char kind = 'N';

	Operation( String mn, char k )
	{
		mnemonic = mn;
        kind = k;
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

