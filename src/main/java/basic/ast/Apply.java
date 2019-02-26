/*************************************
 * Basic-JC կոմպիլյատոր
 *************************************/

package basic.ast;

import java.util.List;

/**
 * {@code Apply} դասը ֆունկցիա-ենթածրագիր կիրառության մոդելն է։
 *
 * @author Արմեն Բադալյան
 */
public class Apply extends Expression {
	/**
	 * Կիրառվելիք ենթածրագիրը։
	 */
	public Subroutine callee;
	/**
	 * Կիրառման արգումենտները։
	 */
	public List<Expression> arguments;

	public Apply( Subroutine cl, List<Expression> args )
	{
		callee = cl;
		arguments = args;
	}
}

