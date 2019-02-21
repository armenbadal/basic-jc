
package basic.parser;

import java.util.stream.*;

///
public class Signature {
    public String module;
    public String name;
    public String[] args;

    public Signature( String m, String n, String[] as )
    {
        module = m;
        name = n;
        args = as;
    }

    @Override
    public String toString()
    {
        String as = Stream.of(args).collect(Collectors.joining(", "));
        return String.format("| %s:%s(%s)", module, name, as);
    }
}

