
package basic.parser;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;

public class BuiltIns {
    private Map<String,String> signatures = null;
        
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

                System.out.println("Class: " + jcl.getClassName()); // D
                for( Method me : jcl.getMethods() ) {
                    if( !me.isPublic() || !me.isStatic() )
                        continue;
                    
                    Type rt = me.getReturnType();
                    Type[] ats = me.getArgumentTypes();
                    
                    System.out.println("    " + me);
                    System.out.println("    < " + rt);
                    for( Type t : ats )
                        System.out.println("    > " + t);
                }
            }
        }
        catch( IOException ex ) {
            ex.printStackTrace();
        }
    }

    public Object getByName( String nm )
    {
        for( Map.Entry<String,String> si : signatures.entries() )
            if( si.getKey().equals(nm) )
                return si.getValue();
        return null;
    }
}
