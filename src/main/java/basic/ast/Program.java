/*************************************
 * Basic-JC կոմպիլյատոր
 *************************************/

package basic.ast;

import java.nio.file.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Ծրագրի մոդելը աբստրակտ քերականական ծառում։
 *
 * @author Արմեն Բադալյան
 */
public class Program extends Node {
	/**
	 * Ծրագրի տեքստի ֆայլի ճանապարհը։
	 */
	public Path path;
	/**
	 * Ծրագիր անունը։
	 */
    public String name;
	/**
	 * Ենթածրագրերի ցուցակը։
	 */
	public List<Subroutine> members;

	public Program( String ph )
	{
		path = Paths.get(ph);
        name = path.getFileName().toString();
        name = name.substring(0, name.indexOf('.'));
		members = new ArrayList<>();
	}

	@Override
	public String toString()
	{
		String res = "' - - - - - - - - - -\n";
        res += "' File: " + path.toString() + "\n";
        res += "' Module: " + name + "\n";
		for(Subroutine si : members) {
			res += si.toString();
			res += "\n";
		}
		res += "' - - - - - - - - - -\n";
		return res;
	}
}

