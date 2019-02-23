
package basic.parser;

import basic.ast.Subroutine;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;

public class BuiltIns {
    public List<Subroutine> subroutines = new ArrayList<>();
        
    public BuiltIns()
    {
        // որոշել basic-rt.jar ֆայլի տեղը; այն պիտի լինի classpath֊ի մեջ
        final String sep = System.getProperty("path.separator");
    	String[] clspath = System.getProperty("java.class.path").split(sep);
        String rjarname = "basic-rt.jar";
		for( String s : clspath )
			if(s.endsWith(rjarname)) {
                rjarname = s;
                break;
            }

        //
        Map<Type,String> suffix = new HashMap<>();
        suffix.put(Type.STRING, "$");
        suffix.put(Type.DOUBLE, "#");
        suffix.put(Type.BOOLEAN, "?");
        
        // գտնել basic-rt֊ի բոլոր public-static մեթոդները
        try {
            JarFile jar  = new JarFile(rjarname);
            Enumeration<JarEntry> entries = jar.entries();
            while( entries.hasMoreElements() ) {
                JarEntry je = entries.nextElement();
                if( !je.getName().endsWith(".class") )
                    continue;
                if( je.getName().endsWith("IO.class") )
                    continue;

                ClassParser cps = new ClassParser(rjarname, je.getName());
                JavaClass jcl = cps.parse();

                String module = jcl.getClassName();
                for( Method me : jcl.getMethods() ) {
                    if( !me.isPublic() || !me.isStatic() )
                        continue;

                    final Type rt = me.getReturnType();
                    String name = me.getName() + suffix.get(rt);

                    List<String> args = new ArrayList<>();
                    int i = 0;
                    for( Type t : me.getArgumentTypes() )
                        args.add(String.format("a%d%s", i++, suffix.get(t)));

                    Subroutine subr = new Subroutine(module, name, args);
                    subr.isBuiltIn = true;
                    subroutines.add(subr);
                }
            }
        }
        catch( IOException ex ) {
            ex.printStackTrace();
        }
        catch( Exception ex ) {
            ex.printStackTrace();
        }
    }
}
