
package basic.ast;

public enum Operation {
	None("None"),
	Or("OR"),
	And("AND"),
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
	Pow("^"),
	Not("NOT");

	public String mnemonic = "None";

	Operation( String mn )
	{
		mnemonic = mn;
	}
}

