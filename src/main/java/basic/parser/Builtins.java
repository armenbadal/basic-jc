
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
    	String classpath = System.getProperty("java.class.path");
		String[] classpathEntries = classpath.split(System.getProperty("path.separator"));
        String rjarname = "basic-rt.jar";
		for( String s : classpathEntries )
			if(s.endsWith(rjarname)) {
                rjarname = s;
                break;
            }
        System.out.printf("------> %s\n", rjarname);

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
