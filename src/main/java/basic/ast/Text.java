
package basic.ast;

public class Text extends Expression {
	public String value = "";

	public Text( String vl )
	{
		value = vl.substring(1, vl.length()-1);
        type = Node.Type.Text;
	}

	@Override
	public String toString()
	{
		return "\"" + value + "\"";
	}
}

