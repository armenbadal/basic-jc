
package basic.parser;

import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

public class Builtins
{
    public Builtins()
    {
        try {
            JarFile jar  = new JarFile("basic-rt.jar");
            Enumeration<JarEntry> entries = jar.entries();
            while( entries.hasMoreElements() ) {
                JarEntry je = entries.nextElement();
                if( !je.getName().endsWith(".class") )
                    continue;
                ClassParser cps = new ClassParser("basic-rt.jar", je.getName());
                JavaClass jcl = cps.parse();

                System.out.println("Class: " + jcl.getClassName()); // D
                for( Method me : jcl.getMethods() )
                {
                    System.out.println("    " + me);
                }
            }
        }
        catch( IOException ex ) {
            ex.printStackTrace();
        }
    }
}
