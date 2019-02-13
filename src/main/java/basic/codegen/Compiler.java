
package basic.codegen;

import basic.ast.*;

import org.apache.bcel.*;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;

import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

/**/
public class Compiler {
	private ClassGen classGen = null;
	private ConstantPoolGen constPool = null;
	private InstructionFactory instrFactory = null;
    private InstructionList currentInstrList = null;

    
	private Program program;
    private String progName;
    private Map<String,Integer> nameMap;
	
	//
	public Compiler( Program prog )
	{
		program = prog;

		int sb = program.fileName.lastIndexOf('/');
        String fileName = program.fileName.substring(sb+1);
        progName = fileName.substring(0, fileName.indexOf('.'));
		
		classGen = new ClassGen(progName, "java.lang.Object", fileName,
								Const.ACC_PUBLIC | Const.ACC_SUPER,
                                new String[] {});
		constPool = classGen.getConstantPool();
		instrFactory = new InstructionFactory(classGen, constPool);
	}

	//
	public void compile()
	{
		createConstructor();
        createMain();

		compile(program);

        //// DEBUG
        //try {
        //    classGen.getJavaClass().dump(new java.io.FileOutputStream("/home/pi/Projects/b4/Ex0g.class"));
        //}
        //catch(java.io.IOException ex) {}
	}

	// 
	private void createConstructor()
	{
		InstructionList il = new InstructionList();
		MethodGen method = new MethodGen(Const.ACC_PUBLIC, Type.VOID, Type.NO_ARGS,
										 new String[] {}, "<init>", progName,
										 il, constPool);

		il.append(instrFactory.createLoad(Type.OBJECT, 0));
		il.append(instrFactory.createInvoke("java.lang.Object", "<init>", Type.VOID,
											Type.NO_ARGS, Const.INVOKESPECIAL));
		il.append(instrFactory.createReturn(Type.VOID));
		method.setMaxStack();
		method.setMaxLocals();
		classGen.addMethod(method.getMethod());
		il.dispose();
	}

    //
    private void createMain()
    {
        InstructionList il = new InstructionList();
        MethodGen method = new MethodGen(Const.ACC_PUBLIC | Const.ACC_STATIC,
                                         Type.VOID,
                                         new Type[] { new ArrayType(Type.STRING, 1) },
                                         new String[] { "args" },
                                         "main", progName, il, constPool);
        
        il.append(instrFactory.createReturn(Type.VOID));
        method.setMaxStack();
        method.setMaxLocals();
        classGen.addMethod(method.getMethod());
        il.dispose();
    }

	private void compile( Program pr )
	{
		for( Subroutine subr : pr.members )
			compile(subr);
	}

	private void compile( Subroutine subr )
	{
        // հաշվարկել լոկալ փոփոխականների ինդեքսները
        nameMap = new HashMap<>();
        int inx = 0;
        for( Variable vi : subr.locals ) {
            nameMap.put(vi.name, inx);
            inx += vi.type == basic.ast.Node.Type.Text ? 1 : 2;
        }
        
        // հրամանների ցուցակ
		currentInstrList = new InstructionList();

        // վերադարձվող արժեքի տիպն ըստ ֆունկցիայի անվան
        Type retype = Type.DOUBLE;
        if( basic.ast.Node.Type.of(subr.name) == basic.ast.Node.Type.Text )
            retype = Type.STRING;
        
        int parcount = subr.parameters.size();
        Type partypes[] = new Type[parcount];
        String parnames[] = new String[parcount];
        for( int i = 0; i < parcount; ++i ) {
            parnames[i] = subr.parameters.get(i);
            partypes[i] = basic.ast.Node.Type.of(parnames[i]) == basic.ast.Node.Type.Text ? Type.STRING : Type.DOUBLE;
        }
		MethodGen method = new MethodGen(Const.ACC_PUBLIC | Const.ACC_STATIC,
										 retype, partypes, parnames, subr.name,
                                         progName, currentInstrList, constPool);

        // ենթածրագրի մարմինը
        compile(subr.body);

        // վերադարձվող արժեքը
        Integer rvi = nameMap.get(subr.name);
        if( rvi != null )
            currentInstrList.append(instrFactory.createLoad(retype, rvi));
        else
            currentInstrList.append(new PUSH(constPool, 0));
		currentInstrList.append(instrFactory.createReturn(retype));
        
		method.setMaxStack();
		method.setMaxLocals();
		classGen.addMethod(method.getMethod());

		currentInstrList.dispose();
        currentInstrList = null;

        nameMap = null;
	}

	private void compile( Statement s )
	{
		if( s instanceof Sequence )
			compile((Sequence)s);
		else if( s instanceof Let )
			compile((Let)s);
		else if( s instanceof Input )
			compile((Input)s);
		else if( s instanceof Print )
			compile((Print)s);
		else if( s instanceof If )
			compile((If)s);
		else if( s instanceof While )
			compile((While)s);
		else if( s instanceof For )
			compile((For)s);
		else if( s instanceof Call )
			compile((Call)s);
	}

    private void compile( Sequence s )
    {
        for( Statement si : s.items )
            compile(si);
    }
    
    private void compile( Let s )
    {
        compile(s.expr);

        Integer ix = nameMap.get(s.place.name);
        if( ix != null ) {
            Type y = s.place.type == basic.ast.Node.Type.Text ? Type.OBJECT : Type.DOUBLE;
            currentInstrList.append(instrFactory.createStore(y, ix));
        }
    }
    
    private void compile( Input s )
    {
    }

    private void compile( Print s )
    {
        compile(s.expr);

        Type ety = s.expr.type == basic.ast.Node.Type.Text ? Type.STRING : Type.DOUBLE;
        InvokeInstruction pln =
            instrFactory.createInvoke("java.io.PrintStream",
                                      "println", Type.VOID,
                                      new Type[] { ety },
                                      Const.INVOKEVIRTUAL);
        currentInstrList.append(pln);
    }

    private void compile( If s )
    {}

    private void compile( While s )
    {}

    private void compile( For s )
    {}

    private void compile( Call s )
    {}

    private void compile( Expression e )
	{
        if( e instanceof Binary )
            compile((Binary)e);
        else if( e instanceof Unary )
            compile((Unary)e);
        else if( e instanceof Apply )
            compile((Apply)e);
        else if( e instanceof Variable )
            compile((Variable)e);
        else if( e instanceof Real )
            compile((Real)e);
        else if( e instanceof Text )
            compile((Text)e);
	}

    private void compile( Binary e )
    {
        compile(e.left);
        compile(e.right);
		
        switch( e.oper ) {
            case Add:
                currentInstrList.append(InstructionConst.DADD);
                break;
            case Sub:
                currentInstrList.append(InstructionConst.DSUB);
                break;
            case Mul:
                currentInstrList.append(InstructionConst.DMUL);
                break;
        }
    }

    private void compile( Unary e )
    {
        compile(e.expr);
    }

    private void compile( Apply e )
    {
        for( Expression a : e.arguments )
            compile(a);
        // TODO: call
    }
    
    private void compile( Variable e )
    {
        Integer ix = nameMap.get(e.name);
        if( ix != null ) {
            Type y = e.type == basic.ast.Node.Type.Text ? Type.OBJECT : Type.DOUBLE;
            currentInstrList.append(instrFactory.createStore(y, ix));
        }
    }
    
    private void compile( Real e )
    {
        currentInstrList.append(new PUSH(constPool, e.value));
    }
        
    private void compile( Text e )
    {
        currentInstrList.append(new PUSH(constPool, e.value));
    }
}

