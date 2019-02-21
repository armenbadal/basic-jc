
package basic.ast;

public abstract class Node {
	public enum Type {
		Real,
		Text,
		Logic;

		public static final Type of( String nm )
		{
			char c = nm.charAt(nm.length()-1);
			if( c == '$' )
				return Type.Text;
			if( c == '?' )
				return Type.Logic;
			//if( c == '#' )
			//	return Type.Real;
			return Type.Real;
		}
	}
	
	public int line = 0;
}

