
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
	
    private Map<basic.ast.Node.Type,Type> typeMap;

	//
	public Compiler( Program prog )
	{
        // տիպերի ձևափոխման աղյուսակ
        typeMap = new HashMap<>();
        typeMap.put(basic.ast.Node.Type.Text, Type.STRING);
        typeMap.put(basic.ast.Node.Type.Real, Type.DOUBLE);
        typeMap.put(basic.ast.Node.Type.Logic, Type.BOOLEAN);

        //
		program = prog;

		final int sb = program.fileName.lastIndexOf('/');
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

        // DEBUG
        try {
            classGen.getJavaClass().dump(new java.io.FileOutputStream(progName + ".class"));
        }
        catch(java.io.IOException ex) {}
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
        Type retype = typeMap.get(basic.ast.Node.Type.of(subr.name));
        
        final int parcount = subr.parameters.size();
        Type partypes[] = new Type[parcount];
        String parnames[] = new String[parcount];
        for( int i = 0; i < parcount; ++i ) {
            String pn = subr.parameters.get(i);
            parnames[i] = normalize(pn);;
            partypes[i] = typeMap.get(basic.ast.Node.Type.of(pn));
        }

		MethodGen method = new MethodGen(Const.ACC_PUBLIC | Const.ACC_STATIC,
										 retype, partypes, parnames, normalize(subr.name),
                                         progName, currentInstrList, constPool);

        // ենթածրագրի մարմինը
        compile(subr.body);

        // վերադարձվող արժեքը
        Integer rvi = nameMap.get(subr.name);
        if( rvi != null )
            currentInstrList.append(instrFactory.createLoad(retype, rvi));
        else {
            CompoundInstruction cip = new PUSH(constPool, 0.0);
            if( retype.equals(Type.STRING) )
                cip = new PUSH(constPool, "");
            else if( retype.equals(Type.BOOLEAN) )
                cip = new PUSH(constPool, false);
            currentInstrList.append(cip);
        }
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
            Type yp = typeMap.get(s.place.type);
            currentInstrList.append(instrFactory.createStore(yp, ix));
        }
    }
    
    private void compile( Input s )
    {
        // TODO: call basic.runtime.read<Real|Text>
    }

    private void compile( Print s )
    {
        currentInstrList.append(instrFactory.createFieldAccess("java.lang.System",
            "out", new ObjectType("java.io.PrintStream"), Const.GETSTATIC));

        compile(s.expr);
        // TODO: call basic.runtime.print<Real|Text>
        Type ety = typeMap.get(s.expr.type);
        InvokeInstruction pln =
            instrFactory.createInvoke("java.io.PrintStream", "println", Type.VOID,
                                      new Type[] { ety }, Const.INVOKEVIRTUAL);
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
            case Conc:
                // TODO: call basic.runtime.Concatenate
                break;
        }
    }

    private void compile( Unary e )
    {
        compile(e.expr);

        if( e.oper == Operation.Sub )
            currentInstrList.append(InstructionConst.DNEG);
        else if( e.oper == Operation.Not ) {
            BranchInstruction ifne = instrFactory.createBranchInstruction(Const.IFNE, null);
            currentInstrList.append(ifne);
            currentInstrList.append(new PUSH(constPool, 1));
            BranchInstruction go = instrFactory.createBranchInstruction(Const.GOTO, null);
            currentInstrList.append(go);
            InstructionHandle zero = currentInstrList.append(new PUSH(constPool, 0));
            InstructionHandle nop = currentInstrList.append(new NOP());
            ifne.setTarget(zero);
            go.setTarget(nop);
        }
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
            Type y = typeMap.get(e.type);
            currentInstrList.append(instrFactory.createLoad(y, ix));
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

    private String normalize( String nm )
    {
        if( nm.endsWith("$") )
            return nm.replace("$", "_T");
        
        if( nm.endsWith("?") )
            return nm.replace("?", "_B");

        if( nm.endsWith("#") )
            return nm.replace("$", "_R");

        return nm + "_R";
    }
}

