
package basic.parser;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;

public class BuiltIns {
    private List<Signature> signatures = new ArrayList<>();
        
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

        // գտնել basic-rt֊ի բոլոր public-static մեթոդները
        try {
            JarFile jar  = new JarFile(rjarname);
            Enumeration<JarEntry> entries = jar.entries();
            while( entries.hasMoreElements() ) {
                JarEntry je = entries.nextElement();
                if( !je.getName().endsWith(".class") )
                    continue;

                ClassParser cps = new ClassParser(rjarname, je.getName());
                JavaClass jcl = cps.parse();

                String module = jcl.getClassName();
                for( Method me : jcl.getMethods() ) {
                    if( !me.isPublic() || !me.isStatic() )
                        continue;

                    String name = me.getName();
                    
                    Type rt = me.getReturnType();
                    if( rt.equals(Type.DOUBLE) )
                        name += "#";
                    else if( rt.equals(Type.STRING) )
                        name += "$";
                    else if( rt.equals(Type.BOOLEAN) )
                        name += "?";

                    Type[] ats = me.getArgumentTypes();
                    String[] args = new String[ats.length];
                    int i = 0;
                    for( Type t : ats ) {
                        if( t.equals(Type.DOUBLE) )
                            args[i] = String.format("a%d#", i);
                        else if( t.equals(Type.STRING) )
                            args[i] = String.format("a%d$", i);
                        else if( t.equals(Type.BOOLEAN) )
                            args[i] = String.format("a%d?", i);
                        ++i;
                    }

                    Signature sig = new Signature(module, name, args);
                    signatures.add(sig);
                    System.out.println(sig);
                }
            }
        }
        catch( IOException ex ) {
            ex.printStackTrace();
        }
    }

    public Object getByName( String nm )
    {
        return null;
    }
}
