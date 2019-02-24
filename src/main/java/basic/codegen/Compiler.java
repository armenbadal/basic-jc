
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
		
		classGen = new ClassGen(program.name, "java.lang.Object",
                                program.path.toString(),
								Const.ACC_PUBLIC | Const.ACC_SUPER,
                                new String[] {});
		constPool = classGen.getConstantPool();
		instrFactory = new InstructionFactory(classGen, constPool);
	}

	//
	public void compile()
	{
        try {
            createConstructor();
            createMain();
            
            compile(program);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }

        // DEBUG
        try {
            classGen.getJavaClass().dump(new java.io.FileOutputStream(program.name + ".class"));
        }
        catch(java.io.IOException ex) {
            ex.printStackTrace();
        }
	}

	// 
	private void createConstructor()
	{
		InstructionList il = new InstructionList();
		MethodGen method = new MethodGen(Const.ACC_PUBLIC, Type.VOID, Type.NO_ARGS,
										 new String[] {}, "<init>", program.name,
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
                                         "main", program.name, il, constPool);
        
        il.append(instrFactory.createReturn(Type.VOID));
        method.setMaxStack();
        method.setMaxLocals();
        classGen.addMethod(method.getMethod());
        il.dispose();
    }

	private void compile( Program pr )
	{
		for( Subroutine subr : pr.members )
            if( !subr.isBuiltIn )
                compile(subr);
	}

	private void compile( Subroutine subr )
	{
        // հաշվարկել լոկալ փոփոխականների ինդեքսները
        nameMap = new HashMap<>();
        int inx = 0;
        for( Variable vi : subr.locals ) {
            nameMap.put(vi.name, inx);
            inx += vi.type == basic.ast.Node.Type.Real ? 2 : 1;
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
                                         program.name, currentInstrList, constPool);

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
        else if( e instanceof Logic )
            compile((Logic)e);
	}

    private void compile( Binary e )
    {
        if( e.oper.kind == 'L' ) {
            compile(e.left);
            short bropc = 0;
            if( e.oper.equals(Operation.And) )
                bropc = Const.IFEQ;
            else if( e.oper.equals(Operation.Or) )
                bropc = Const.IFNE;
            BranchInstruction bri = instrFactory.createBranchInstruction(bropc, null);
            currentInstrList.append(bri);
            compile(e.right);
            BranchInstruction ifeq = instrFactory.createBranchInstruction(Const.IFEQ, null);
            currentInstrList.append(ifeq);
            InstructionHandle one = currentInstrList.append(new PUSH(constPool, 1));
            BranchInstruction go = instrFactory.createBranchInstruction(Const.GOTO, null);
            currentInstrList.append(go);
            InstructionHandle zero = currentInstrList.append(new PUSH(constPool, 0));
            InstructionHandle nop = currentInstrList.append(new NOP());
            ifeq.setTarget(zero);
            if( bropc == Const.IFEQ )
                bri.setTarget(zero);
            else if( bropc == Const.IFNE )
                bri.setTarget(one);
            go.setTarget(nop);
        }
        else if( e.oper.kind == 'A' ) {
            compile(e.left);
            compile(e.right);

            if( e.oper.equals(Operation.Add) )
                currentInstrList.append(InstructionConst.DADD);
            else if( e.oper.equals(Operation.Sub) )
                currentInstrList.append(InstructionConst.DSUB);
            else if( e.oper.equals(Operation.Mul) )
                currentInstrList.append(InstructionConst.DMUL);
            else if( e.oper.equals(Operation.Div) )
                currentInstrList.append(InstructionConst.DDIV);
            else if( e.oper.equals(Operation.Pow) ) {
                InvokeInstruction pwf =
                    instrFactory.createInvoke("java.lang.Math", "pow", Type.DOUBLE,
                                              new Type[] { Type.DOUBLE, Type.DOUBLE },
                                              Const.INVOKESTATIC);
                currentInstrList.append(pwf);
            }
        }
        else if( e.oper.kind == 'C' ) {
            compile(e.left);
            compile(e.right);
            currentInstrList.append(InstructionConst.DCMPL); // ICMPL or ...
            
            short bropc = 0;
            if( e.oper.equals(Operation.Eq) )
                bropc = Const.IFNE;
            else if( e.oper.equals(Operation.Ne) )
                bropc = Const.IFEQ;
            else if( e.oper.equals(Operation.Gt) )
                bropc = Const.IFLE;
            else if( e.oper.equals(Operation.Ge) )
                bropc = Const.IFLT;
            else if( e.oper.equals(Operation.Lt) )
                bropc = Const.IFGE;
            else if( e.oper.equals(Operation.Le) )
                bropc = Const.IFGT;

            BranchInstruction bri = instrFactory.createBranchInstruction(bropc, null);
            currentInstrList.append(bri);
            currentInstrList.append(new PUSH(constPool, 1));
            BranchInstruction go = instrFactory.createBranchInstruction(Const.GOTO, null);
            currentInstrList.append(go);
            InstructionHandle zero = currentInstrList.append(new PUSH(constPool, 0));
            InstructionHandle nop = currentInstrList.append(new NOP());
            bri.setTarget(zero);
            go.setTarget(nop);
        }
        else if( e.oper.kind == 'T' ) {
            compile(e.left);
            compile(e.right);

            InvokeInstruction srop = null;
            if( e.oper.equals(Operation.Conc) )
                srop = instrFactory.createInvoke("basic.runtime.Text", "_concatenate",
                                                 Type.STRING,
                                                 new Type[] { Type.STRING, Type.STRING },
                                                 Const.INVOKESTATIC);

            currentInstrList.append(srop);
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
        Type[] ats = new Type[e.arguments.size()];
        int i = 0;
        for( Expression a : e.arguments ) {
            compile(a);
            ats[i++] = typeMap.get(a.type);
        }

        Subroutine cl = e.callee;
        Type rt = typeMap.get(basic.ast.Node.Type.of(cl.name));
        InvokeInstruction icl =
            instrFactory.createInvoke(cl.module,
                                      normalize(cl.name),
                                      rt,
                                      ats, // TODO: see Type.NO_ARGS case
                                      Const.INVOKESTATIC);
        currentInstrList.append(icl);
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

    private void compile( Logic e )
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
            return nm.replace("#", "_R");

        return nm + "_R";
    }
}

