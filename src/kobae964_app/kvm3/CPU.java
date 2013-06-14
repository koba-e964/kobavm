package kobae964_app.kvm3;

import kobae964_app.kvm3.inline.Init;
import kobae964_app.kvm3.inline.KString;
import static kobae964_app.kvm3.DataType.*;

public class CPU {
	Mem mem;
	int pc;
	CallStack stack;
	VariableTable vtable;
	ClassLoader loader;
	/**
	 * The ID of class which this CPU runs.
	 */
	int classID;
	public CPU(Mem mem)
	{
		this.mem=mem;
		this.pc=0;
		this.stack=new CallStack();
		this.vtable=new VariableTable();
		this.loader=new ClassLoader();
		this.classID=ClassLoader.getClassID(Init.CLASS_NAME);
		/*
		 * for tests
		 */
		final int N=10;
		this.vtable.allocate(N);
	}
	void run()
	{
		while(decode()>=0){}
	}
	int decode()
	{
		int code=mem.getDword(pc);
		pc+=4;
		//TODO build machine code
		switch(code%64)
		{
		case 0://LDC.im
		{
			int ar0=code>>8;//signed int
			stack.pushInt(ar0);
			break;
		}
		case 1://LDC.cp st0 ar0
		{
			VarEntry st0=stack.pop();
			String clzName=KString.getContent(st0.value);
			int ar0=code>>>8;//unsigned
			VarEntry result=ClassLoader.getConstant(ClassLoader.getClassID(clzName), ar0);
			stack.push(result);
			break;
		}
		case 2://LDC.cp.cur ar0
		{
			int ar0=code>>>8;//unsigned
			VarEntry result=ClassLoader.getConstant(classID, ar0);
			stack.push(result);
			break;
		}
		case 3://LDV
		{
			int ar0=code>>>8;//unsigned
			VarEntry cont=vtable.load(ar0);
			stack.push(cont);
			break;
		}
		case 4://GETFIELD
		{
			VarEntry st0=stack.pop();
			VarEntry st1=stack.pop();
			String name=KString.getContent(st1.value);
			st0.checkDataType(OBJECT);
			VarEntry res=ClassLoader.getField(st0.value,name);
			stack.push(res);
			break;
		}
		case 5://STV
		{
			VarEntry st0=stack.pop();
			int ar0=code>>>8;//unsigned
			vtable.store(ar0,st0);
			break;
		}
		case 6://SETFIELD
		{
			VarEntry st0=stack.pop();//object whose member is assigned to
			VarEntry st1=stack.pop();//name of member
			VarEntry st2=stack.pop();//value that is assigned to member
			st1.checkDataType(OBJECT);
			String name=KString.getContent(st1.value);
			st0.checkDataType(OBJECT);
			ClassLoader.setField(st0.value, name, st2);
			break;
		}
		case 7://DUP
		{
			int ar0=code>>>8;
			VarEntry ve=stack.getAt(ar0);
			stack.push(ve.clone());
			break;
		}
		case 8://SWAP
		{
			int ar0=(code>>>8)&0xfff;//unsigned
			int ar1=(code>>>20)&0xfff;
			VarEntry ve1=stack.getAt(ar0);
			VarEntry ve2=stack.getAt(ar1);
			stack.setAt(ar0,ve2);
			stack.setAt(ar1,ve1);
			break;
		}
		case 9://ADD
		{
			long val1=stack.popInt();
			long val2=stack.popInt();
			stack.pushInt(val1+val2);
			break;
		}
		case 10://SUB
		{
			long val1=stack.popInt();
			long val2=stack.popInt();
			stack.pushInt(val1-val2);
			break;
		}
		case 11://MUL
		{
			long val1=stack.popInt();
			long val2=stack.popInt();
			stack.pushInt(val1*val2);
			break;
		}
		case 12://DIV
		{
			long val1=stack.popInt();
			long val2=stack.popInt();
			stack.pushInt(val1/val2);
			stack.pushInt(val1%val2);
			break;
		}
		case 13://CALL ar0
		{
			int ar0=code>>8;//signed
			int dest=pc+ar0;
			call(dest);
			break;
		}
		case 16://JMP
		{
			int ar0=code>>8;//signed
			pc+=ar0;
			break;
		}
		case 17://ret
		{
			ret();
			break;
		}
		case 0x3f://EXIT
			return -1;
		default:
			throw new RuntimeException("Invalid Code");
		}
		return 0;
	}
	public void call(int addr)
	{
		stack.pushInt(pc);
		vtable.allocate(100);//actual value should be recalled from classData
		pc=addr;
	}
	public void ret()
	{
		long a=stack.popInt();
		vtable.deallocate();
		pc=(int)a;
	}
}
