
package basic.codegen;

import basic.ast.*;

import org.apache.bcel.*;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
//import java.io.*;

/**/
public class Compiler /*extends Visitor*/ {
	private ClassGen classGen;
	private ConstantPoolGen constPool;
	private InstructionFactory instrFactory;

	private Program program;
	
	//
	public Compiler( Program prog )
	{
		program = prog;
		// TODO: ֆայլի անունից անջատել վերջավորությունը
		
		classGen = new ClassGen("դասիԱնունը", "java.lang.Object", "ֆայլիԱնունը",
								Const.ACC_PUBLIC | Const.ACC_SUPER, new String[] {  });
		constPool = classGen.getConstantPool();
		instrFactory = new InstructionFactory(classGen, constPool);
	}

	//
	public void compile()
	{
		createConstructor();
		
		for( Subroutine subr : program.members )
			compile(subr);
	}

	// 
	private void createConstructor()
	{
		InstructionList il = new InstructionList();
		MethodGen method = new MethodGen(Const.ACC_PUBLIC, Type.VOID, Type.NO_ARGS,
										 new String[] {  }, "<init>", "դասիԱնունը",
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
	private void compile( Subroutine subr )
	{
		InstructionList il = new InstructionList();
		MethodGen method = new MethodGen(Const.ACC_PUBLIC | Const.ACC_STATIC,
										 Type.VOID, new Type[] { new ArrayType(Type.STRING, 1) },
										 new String[] { "arg0" }, "main", "Ex0", il, _cp);
		
		InstructionHandle ih_0 = il.append(_factory.createReturn(Type.VOID));
		method.setMaxStack();
		method.setMaxLocals();
		_cg.addMethod(method.getMethod());
		il.dispose();
	}
}

