
package basic.parser;

import basic.ast.Subroutine;

import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.*;

import java.io.IOException;

import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.ElementValuePair;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;

public class BuiltIns {
    public List<Subroutine> subroutines = new ArrayList<>();
        
    public BuiltIns() throws IOException
    {
        // որոշել basic-rt.jar ֆայլի տեղը; այն պիտի լինի classpath֊ի մեջ
        final String sep = System.getProperty("path.separator");
    	String[] clspath = System.getProperty("java.class.path").split(sep);
        String rjarname = Stream.of(clspath)
            .filter(cpi -> cpi.endsWith("basic-rt.jar"))
            .findAny().orElse(null);
        if( rjarname == null )
            throw new IOException("Cannot find runtime JAR in classpath.");

        // գտնել basic-rt֊ի բոլոր public-static մեթոդները
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

                // որոնել մեթոդի նշագրումը
                String supports = null;
                for( AnnotationEntry ae : me.getAnnotationEntries() )
                    for( ElementValuePair evp : ae.getElementValuePairs() )
                        if( evp.getNameString().equals("supports") )
                            supports = evp.getValue().toString();
                if( supports == null )
                    continue;

                // կազմակերպել արգումենտների ցուցակը                
                List<String> args = new ArrayList<>();
                int i = 0;
                for( Type t : me.getArgumentTypes() ) {
                    char c = '~';
                    if( Type.STRING.equals(t) )
                        c = '$';
                    else if( Type.DOUBLE.equals(t) )
                        c = '#';
                    else if( Type.BOOLEAN.equals(t) )
                        c = '?';
                    args.add(String.format("a%d%c", i++, c));
                }
                
                Subroutine subr = new Subroutine(module, supports, args, me.getName());
                subroutines.add(subr);
            }
        }
    }
}
